// noinspection DuplicatedCode

const axios = require('axios');
const createError = require('http-errors');
const Joi = require('joi');
const console = require('console');
// const IP = require('ip');
const crypto = require('crypto');
const Conversation = require('./../../models/Conversation');
const {getPagination} = require('./../../../utils/Pagination');
const Message = require('./../../models/Message');
const {populateConversation} = require('./../../../utils/Populate/Conversation');
const mongoose = require('mongoose');
const {responseError} = require('./../../../utils/Response/error');
const {getListData} = require('./../../../utils/Response/listData');
const getLocationByIPAddress = require('./../../../configs/location');
const {getListConversation} = require("../../../utils/Response/listData");
const {populateUser} = require("../../../utils/clients/userClient");
const {populateMedia, populateListMedia} = require("../../../utils/clients/mediaClient");
const SocketManager = require("../../../socket/SocketManager");
const {eventName} = require("../../../socket/constant");
// set encryption algorithm
const algorithm = 'aes-256-cbc';

// private key
const key = process.env.DECODE_KEY; // must be of 32 characters
const apiKey = process.env.API_KEY_VIDEOCALL;

class ConversationController {
    // search conversation
    async search(req, res, next) {
        // tìm kiếm cuộc trò chuyện theo tên mà người dùng có trong cuộc trò chuyện bỏ qua các cuộc trò chuyện có 2 thành viên
        const {limit, offset} = getPagination(req.query.page, req.query.size, req.query.offset);
        const q = req.query.key ?? '';
        try {
            const conversations = await Conversation.find({
                name: {$regex: q, $options: 'i'},
                'members.userId': req.user.userId,
            })
                .populate({
                    path: 'lastest_message',
                    select: 'senderId text createdAt updatedAt readerId iv',
                })
                .sort({updatedAt: -1})
                .skip(offset)
                .limit(limit);
            conversations.forEach((item) => {
                if (item.lastest_message && item.lastest_message.iv) {
                    const iv = Buffer.from(item.lastest_message.iv, 'base64');
                    const decipher = crypto.createDecipheriv(algorithm, key, iv);
                    let decryptedData = decipher.update(item.lastest_message.text, 'hex', 'utf-8');
                    decryptedData += decipher.final('utf-8');
                    item.lastest_message.text = decryptedData;
                }
                // doi ten cuoc tro chuyen neu la cuoc tro chuyen 1-1
                if (item.members.length === 2) {
                    const member = item.members.find((member) => member.userId !== req.user.userId);
                    item.name = member.nickname;
                }
            });
            return getListConversation(res, true, 'Danh sách cuộc trò chuyện', conversations, 200);
        } catch (err) {
            console.log(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }

    }

    async leaveConversation(req, res, next) {
        try {
            const conversation = await Conversation.findById(req.params.id);
            if (!conversation) {
                return responseError(res, 404, 'Không tìm thấy cuộc trò chuyện');
            }
            let index = -1;
            index = conversation.members.findIndex((item) => item.userId === req.user.userId);

            console.log(conversation.members.length)
            if (index !== -1) {
                const adminOfConversation = conversation.members.filter((member) => member.role === 'admin');
                conversation.members.splice(index, 1);
                // ramdom admin
                if (adminOfConversation.length === 1) {
                    const newAdmin = conversation.members.find((member) => member.userId !== req.user.userId);
                    if (newAdmin) {
                        newAdmin.role = 'admin';
                    }

                }
                // create message system
                const messageSystem = await new Message({
                    conversation: conversation._id,
                    text: `<b>${req.user.userName}</b> đã rời khỏi cuộc hội thoại này`,
                    isSystem: true,
                }).save();

                // set lastest message
                conversation.lastest_message = messageSystem._id;
                if (conversation.members.length === 0) {
                    await Conversation.deleteOne({_id: req.params.id});
                } else {
                    await conversation.save();
                    const userIds = conversation.members
                        .filter((member) => member.userId !== req.user.userId)
                        .map((item) => item.userId);

                    // send socket
                    SocketManager.sendToList(userIds, eventName.SEND_MESSAGE, messageSystem);
                }


                return res.status(200).send('Bạn đã rời khỏi cuộc trò chuyện này');
            } else {
                return responseError(res, 404, 'Bạn không có cuộc hội thoại này');
            }

        } catch (err) {
            console.error(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    // Delete all messages of conversation with user
    async userDeletedAllMessages(req, res, next) {
        try {
            const conversation = await Conversation.findById(req.params.id);
            if (conversation.members.some((member) => member.user.toString() === req.user._id.toString())) {
                let index = -1;
                index = conversation.user_deleted.findIndex(
                    (item) => item.userId.toString() === req.user._id.toString()
                );
                if (index !== -1) {
                    conversation.user_deleted[index].deletedAt = Date.now();
                } else {
                    conversation.user_deleted.push({userId: req.user._id});
                }
                await conversation.save();
                return res.status(200).send('Đã xóa cuộc hội thoại cho User');
            } else {
                return responseError(res, 404, 'Bạn không có cuộc hội thoại này');
            }
        } catch (err) {
            console.error(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    // Create conversation with user
    async createConversationUser(req, res, next) {
        const user = await populateUser(req.body.userId);
        if (!user) {
            return responseError(res, 404, 'Không tìm thấy User');
        }
        try {
            if (req.body.userId === req.user.userId) {
                return responseError(res, 400, 'Không thể tạo cuộc trò chuyện với chính mình');
            }
            // lấy conversation dựa vào 2 id của user và conversation có type là direct
            const checkConversation = await Conversation.findOne({
                type: 'direct',
                members: {
                    $all: [
                        {$elemMatch: {userId: req.user.userId}},
                        {$elemMatch: {userId: req.body.userId}},
                    ],
                },

            });

            if (checkConversation) {
                return res.status(200).json(checkConversation);
            }

            const conversation = new Conversation({
                members: [
                    {
                        userId: req.body.userId,
                        role: 'member',
                        nickname: req.body.userName,
                        avatar: req.body.avatar,

                    },
                    {
                        userId: req.user.userId,
                        role: 'member',
                        nickname: req.user.userName,
                        avatar: req.user.avatar,
                    },
                ],
                creator: req.user.userId,
                type: 'direct',
            });
            conversation.name = `${req.user.userName} ${req.body.userName}`;
            await conversation.save();
            return res.status(200).json(conversation);
        } catch (err) {
            console.error(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );

        }

    }

    // [Post] add a new conversation
    async add(req, res, next) {
        // validate request
        const schema = Joi.object({
            members: Joi.array()
                .items(
                    Joi.object({
                        userId: Joi.string().required(),
                        nickname: Joi.string().min(3).max(100),
                        addedBy: Joi.string(),
                        avatar: Joi.string(),
                    })
                )
                .required(),
            name: Joi.string().when('members', {
                is: Joi.array().length(1),
                then: Joi.string().allow(null).default(null),
                otherwise: Joi.string().allow(null, '').default(''),
            }),
            avatar: Joi.string(),
        }).unknown();

        const {error} = schema.validate(req.body);
        if (error) {
            return next(createError.BadRequest(error.details[0].message));
        }

        try {
            if (req.body.members.length < 2) {
                return responseError(res, 400, 'Cuộc hội thoại phải có ít nhất 2 thành viên');
            } else {
                let newConversation = new Conversation({
                    members: req.body.members,
                    name: req.body.name,
                    avatar: req.body.avatar,
                    type: 'group',
                });
                newConversation.members.push({
                    userId: req.user.userId,
                    nickname: req.user.userName,
                    role: 'admin',
                    addedBy: req.user.userId,
                    avatar: req.user.avatar,
                });
                newConversation.creatorId = req.user.userId;
                newConversation.history.push({
                    editorId: req.user.userId,
                    content: `<b>${req.user.userName}</b> đã tạo cuộc hội thoại`,
                });
                // save the conversation
                const savedConversation = await newConversation.save();

                // create message system
                const messageSystem = new Message({
                    conversation: savedConversation._id,
                    text: `<b>${req.user.userName}</b> đã tạo cuộc hội thoại`,
                    isSystem: true,
                });
                await messageSystem.save();
                newConversation.lastest_message = messageSystem;
                await savedConversation.save();
                return res.status(200).json(savedConversation);
            }

        } catch (err) {
            console.log(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    // [Get] get conv of a user
    async getConversationOfUser(req, res, next) {
        const {limit, offset} = getPagination(req.query.page, req.query.size, req.query.offset);
        const q = req.query.key ?? '';

        try {
            // Tìm tất cả các cuộc trò chuyện mà có thành viên có userId là giá trị của tham số
            const conversations = await Conversation.find({'members.userId': req.user.userId}).populate({
                path: 'lastest_message',
                select: 'senderId text createdAt updatedAt readerId iv',
            });
            // kiểm tra consersation có type = drect hay không
            let checkChange = false;
            for (const item of conversations) {
                if ('direct' == (await item).type) {
                    // kiểm tra thông tin của thành viên trong conversation
                    for (const member of (await item).members) {
                        if (member.userId !== req.user.userId) {
                            const userMember = await populateUser(member.userId);
                            if (member.nickname !== userMember.userName) {
                                member.nickname = userMember.userName;
                                checkChange = true;
                            }
                            member.avatar = userMember.avatar;
                        } else {
                            if (member.nickname !== req.user.userName) {
                                member.nickname = req.user.userName;
                                checkChange = true;
                            }
                            member.avatar = req.user.avatar;
                        }
                    }
                }
                if (checkChange) {
                    item.name = `${item.members[0].nickname} ${item.members[1].nickname}`;
                    checkChange = false;
                    item.save();
                }
                if (item.lastest_message && item.lastest_message.iv) {
                    const iv = Buffer.from(item.lastest_message.iv, 'base64');
                    const decipher = crypto.createDecipheriv(algorithm, key, iv);
                    let decryptedData = decipher.update(item.lastest_message.text, 'hex', 'utf-8');
                    decryptedData += decipher.final('utf-8');
                    item.lastest_message.text = decryptedData;
                }
            }
            return getListConversation(res, true, 'Danh sách cuộc trò chuyện', conversations, 200);
        } catch (err) {
            console.log(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }


    // [Get] get all conversations
    async getAll(req, res) {
        const {limit, offset} = getPagination(req.query.page, req.query.size, req.query.offset);

        Conversation.paginate({}, {offset, limit, sort: {updatedAt: -1}})
            .then((data) => {
                getListData(res, data);
            })
            .catch((err) => {
                return responseError(res, 500, err.message ?? 'Some error occurred while retrieving tutorials.');
            });
    }

    // get conversation by id
    async getConversationById(req, res) {
        try {
            const conversation = await Conversation.findById(req.params.id);
            if (conversation && conversation.members.some((member) => member.userId === req.user.userId)) {
                // lay creatAt va UpdateAt cua lastest message
                if (conversation.lastest_message && conversation.lastest_message.iv) {
                    const iv = Buffer.from(conversation.lastest_message.iv, 'base64');
                    const decipher = crypto.createDecipheriv(algorithm, key, iv);
                    let decryptedData = decipher.update(conversation.lastest_message.text, 'hex', 'utf-8');
                    decryptedData += decipher.final('utf-8');
                    conversation.lastest_message.text = decryptedData;
                }

                // lấy trạng thái online của người trong cuộc trò chuyện
                for (let member of conversation.members) {
                    const user = await populateUser(member.userId);
                    member.isOnline = user.isOnline;
                    member.lastLogin = user.lastLogin;
                }
                return res.status(200).json(conversation);
            } else {
                return responseError(res, 401, 'Bạn không có trong conversation này');
            }
        } catch (err) {
            console.error(err);
            return responseError(res, 404, 'Không tìm thấy Conversation');
        }
    }

    // get conversation include 2 members by user id
    async getConversationByUserIds(req, res, next) {
        try {
            // check user with params id is exist
            const user = await User.findById(req.params.userId);
            if (!user) {
                return responseError(res, 404, 'Không tìm thấy User');
            }
            // const query = {
            // 	members: {
            // 		$elemMatch: {
            // 			user: req.user._id,
            // 		},
            // 		$elemMatch: {
            // 			user: req.params.userId,
            // 		},
            // 	},
            // 	members: { $size: 2 },
            // };
            const query = {
                members: {
                    $size: 2,
                    $all: [{$elemMatch: {user: req.user._id}}, {$elemMatch: {user: req.params.userId}}],
                },
            };
            const conversation = await Conversation.findOne(query)
                .populate({
                    path: 'lastest_message',
                    populate: {
                        path: 'sender',
                    },
                })
                .populate({
                    path: 'members.user',
                    select: '_id fullname profilePicture isOnline isOnline',
                    populate: {
                        path: 'profilePicture',
                        select: '_id link',
                    },
                })
                .populate({
                    path: 'avatar',
                    select: '_id link',
                })
                .populate({
                    path: 'members.addedBy',
                    select: '_id fullname profilePicture isOnline',
                    populate: {
                        path: 'profilePicture',
                        select: '_id link',
                    },
                })
                .populate({
                    path: 'members.changedNicknameBy',
                    select: '_id fullname profilePicture isOnline',
                    populate: {
                        path: 'profilePicture',
                        select: '_id link',
                    },
                });
            if (conversation) {
                res.status(200).json(conversation);
            } else {
                // create new conversation with 2 members is req.user._id and req.params.userId
                const newConversation = new Conversation({
                    members: [
                        {
                            user: req.user._id,
                            role: 'admin',
                            addedBy: req.user._id,
                            nickname: req.user.fullname,
                        },
                        {
                            user: req.params.userId,
                            role: 'member',
                            addedBy: req.user._id,
                            nickname: user.fullname,
                        },
                    ],
                    creator: req.user._id,
                });
                // name of conversation is name of 2 members
                const user1 = await User.findById(req.user._id);
                const user2 = await User.findById(req.params.userId);
                newConversation.name = `${user1.fullname}, ${user2.fullname}`;
                // save the conversation
                const savedConversation = await newConversation.save();
                // create message system
                const messageSystem = new Message({
                    conversation: savedConversation._id,
                    text: `<b>${user1.fullname}</b> đã tạo cuộc hội thoại`,
                    isSystem: true,
                });
                await messageSystem.save();

                // populate conversation
                const conversation = await populateConversation(savedConversation._id);

                res.status(200).json(conversation);
            }
        } catch (err) {
            console.error(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    // [PUT] update conversation
    async update(req, res, next) {
        try {

            // validate request
            const schema = Joi.object({
                name: Joi.string().min(3).max(255),
                avatar: Joi.string().min(3).max(255),
                user_deleted: Joi.string(),
            }).unknown();
            const {error} = schema.validate(req.body);
            if (error) {
                return responseError(res, 400, error.details[0].message);
            }

            const conversation = await Conversation.findById(req.params.id);
            if (conversation.members.some((member) => member.userId.toString() === req.user.userId.toString())) {
                let contentMessage = '';

                for (const key of Object.keys(req.body)) {
                    if (key === 'avatar') {
                        contentMessage += 'đã đổi avatar cho cuộc hội thoại này';
                        conversation[key] = req.body[key];
                    } else if (key === 'name' && conversation.members.length > 2) {
                        contentMessage += `đã đổi tên cuộc hội thoại này thành <b>${req.body[key]}</b>`;
                        conversation[key] = req.body[key];
                    } else if (key === 'name' && conversation.members.length == 2) {
                        // change nickname of orther member
                        const member = conversation.members.find(
                            (member) => member.userId !== req.user.userId.toString()
                        );

                        contentMessage += `đã đổi biệt danh của ${member.nickname} thành ${req.body[key]}`;

                        member.nickname = req.body[key];
                        member.changedNicknameBy = req.user.userName;
                    }
                }

                // Check update
                if (contentMessage) {
                    conversation.history.push({
                        editor: req.user.userId,
                        content: `<b>${req.user.userName}</b> ${contentMessage}`,
                    });

                    // create message system
                    const messageSystem = new Message({
                        conversation: req.params.id,
                        text: `<b>${req.user.userName}</b> ${contentMessage}`,
                        isSystem: true,
                    });
                    await messageSystem.save();

                    // update lastest message
                    conversation.lastest_message = messageSystem._id;

                    const userIds = conversation.members
                        .filter((member) => member.userId !== req.user.userId)
                        .map((item) => item.userId);
                    // send socket
                    SocketManager.sendToList(userIds, eventName.SEND_MESSAGE, messageSystem);
                    SocketManager.sendToList(userIds, eventName.CONVERSATION, conversation);
                }

                await conversation.save();

                // populate
                const savedConversation = await populateConversation(conversation._id);


                return res.status(200).json(savedConversation);
            } else {
                return responseError(res, 403, 'Bạn không nằm trong cuộc hội thoại này');
            }
        } catch (err) {
            console.error(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    // update members in conversation
    async updateMembers(req, res, next) {
        try {
            const conversation = await Conversation.findById(req.params.id);
            if (!conversation) {
                return responseError(res, 404, 'Không tìm thấy cuộc hội thoại');
            }
            if (conversation.members.some((member) => member.userId === req.user.userId)) {
                let contentMessage = '';
                // add members
                // validate request

                // check member is exist in conversation
                const membersOfConversation = conversation.members.map((member) => member.userId);
                const membersFromRequest = req.body.newMembers.map((member) => member.userId);
                const sameMembers = membersOfConversation.filter((member) => membersFromRequest.includes(member));
                if (sameMembers.length > 0) {
                    return responseError(res, 403, 'Thành viên đã tồn tại trong cuộc hội thoại');
                }
                // check user is admin of conversation
                contentMessage += `${req.user.userName} đã thêm `;
                req.body.newMembers.forEach((member) => {
                    contentMessage += `<b>${member.username}</b>, `;
                    const newMember = {
                        avatar: member.avatar,
                        addedAt: Date.now(),
                        isOnline: member.isOnline,
                        userId: member.userId,
                        role: 'member',
                        nickname: member.username,
                    }
                    conversation.members.push(newMember);
                });
                // create message system
                const messageSystem = new Message({
                    conversation: req.params.id,
                    text: contentMessage + 'vào cuộc hội thoại',
                    isSystem: true,
                });
                await messageSystem.save();
                conversation.lastest_message = messageSystem._id;
                await conversation.save();
                return res.status(200).json(conversation);
            } else {
                return responseError(res, 403, 'Bạn không có quyền thêm thành viên vào cuộc hội thoại này');
            }
        } catch (err) {
            console.error(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    async deleteConversation(req, res, next) {
        try {
            const conversation = await Conversation.findById(req.params.id);
            if (!conversation) {
                return res.status(404).json('Không tìm thấy cuộc hội thoại');
            }
            const adminOfConversation = conversation.members
                .filter((member) => member.role === 'admin')
                .map((member) => member.user.toString());
            if (adminOfConversation.includes(req.user._id.toString()) || req.user.role.name === 'ADMIN') {
                await conversation.delete();
                // delete all message in conversation
                await Message.deleteMany({conversation: req.params.id});
                return conversation;
            } else {
                return responseError(res, 403, 'Bạn không có quyền xóa cuộc hội thoại này');
            }
        } catch (error) {
            console.error(error);
            return next(
                createError.InternalServerError(
                    `${error.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    // [Delete] delete conversation
    async delete(req, res, next) {
        try {
            const conversation = await Conversation.findById(req.params.id);
            if (!conversation) {
                return res.status(404).json('Không tìm thấy cuộc hội thoại');
            }
            //check user is admin of conversation
            const userAdmin = conversation.members.filter((member) => member.role === 'admin' && member.userId === req.user.userId);
            if (userAdmin.length > 0) {
                await Conversation.deleteOne({_id: req.params.id});
                await Message.deleteOne({conversation: req.params.id});
                return res.status(200).json('Đã xóa cuộc hội thoại thành công');
            } else {
                return responseError(res, 403, 'Bạn không có quyền xóa cuộc hội thoại này');
            }
        } catch (err) {
            console.log(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    async getAllMedia(req, res, next) {
        try {
            const conversations = await Conversation.findById(req.params.id);
            if (!conversations) {
                return responseError(res, 404, 'Không tìm thấy cuộc hội thoại');
            }
            const {limit, offset} = getPagination(req.query.page, req.query.size, req.query.offset);
            const memberOfConversation = conversations.members.filter(
                (member) => member.user.toString() === req.user._id.toString()
            );
            if (memberOfConversation.length > 0) {
                const query = [{conversation: req.params.id}];
                const mimeTypeOfMedia = ['image/png', 'image/jpeg', 'video/mp4', 'video/x-matroska'];
                if (req.params.type === 'media') {
                    query.push({type: {$in: mimeTypeOfMedia}});
                } else if (req.params.type === 'other') {
                    query.push({type: {$nin: mimeTypeOfMedia}});
                } else {
                    return responseError(res, 404, 'Không tìm thấy');
                }

                File.paginate(
                    {$and: query},
                    {
                        limit,
                        offset,
                        sort: {createdAt: -1},
                        populate: {
                            path: 'creator',
                            select: '_id fullname profilePicture isOnline',
                            populate: {
                                path: 'profilePicture',
                                select: '_id link',
                            },
                        },
                    }
                )
                    .then((data) => {
                        getListData(res, data);
                    })
                    .catch((err) => {
                        return responseError(
                            res,
                            500,
                            err.message ?? 'Some error occurred while retrieving tutorials.'
                        );
                    });
            } else {
                return responseError(res, 403, 'Bạn không nằmm trong cuộc hội thoại này');
            }
        } catch (err) {
            console.log(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    // [Post] Create random conversation fit profile, hometown or place of residence and user isOnline
    async createRandomConversation(req, res, next) {
        try {
            // check user online and
            // const query = [
            //     { _id: { $ne: req.user._id } },
            //     {
            //         $or: [
            //         { "city": { $regex: req.query.key, $options: 'i' } },
            //         { "from": { $regex: req.query.key, $options: 'i' } },
            //         ]
            //     },
            //     { isOnline: true },
            // ];
            // User.aggregate(query).exec((err, data)=>{
            //     if (err) {
            //         console.log(err);
            //         return next(createError.InternalServerError(`${err.message} in method: ${req.method} of ${req.originalUrl}`));
            //     }

            //     User.populate(data, [
            //         { path: 'profilePicture', select: '_id link' },
            //         { path: 'coverPicture', select: '_id link' },
            //     ], (err, data) => {
            //         if (err) {
            //             console.log(err);
            //             return next(createError.InternalServerError(`${err.message} in method: ${req.method} of ${req.originalUrl}`));
            //         }

            //         const newConversation = new Conversation({
            //             members: [
            //                 {
            //                     user: req.user._id,
            //                     role: "admin",
            //                     addedBy: req.user._id,
            //                     nickname: req.user.fullname,
            //                 },
            //             ],
            //             creator: req.user._id,
            //         });
            //         //name of conversation is name of 2 members
            //         const user1 = req.user;
            //         const user2 = data[0];

            //         newConversation.name = `${user1.fullname}, ${user2.fullname}`;
            //         //save the conversation
            //         const savedConversation = newConversation.save();

            //         //create message system
            //         const messageSystem = new Message({
            //             conversation: savedConversation._id,
            //             text: `<b>${req.user.fullname}</b> đã tạo cuộc hội thoại`,
            //             isSystem: true,
            //             });
            //         messageSystem.save();

            //     });

            // });
            // const ipAddress = IP.address();
            const data = await getLocationByIPAddress('192.168.2.250');
            return res.json(data);
        } catch (error) {
            console.log(error);
            return next(
                createError.InternalServerError(`${error.message} in method: ${req.method} of ${req.originalUrl}`)
            );
        }
    }

    // Video call
    async createRoomIDVideoCall(req, res, next) {
        try {
            const options = {
                method: 'POST',
                headers: {
                    Authorization: apiKey,
                    'Content-Type': 'application/json',
                },
                // body: JSON.stringify({ "region": "sg001", "customRoomId": "aaa-bbb-ccc", "webhook": "see example", "autoCloseConfig": "see example" }),
            };
            const url = `https://api.videosdk.live/v2/rooms`;
            const response = await axios(url, options);
            return res.json(response.data);
        } catch (err) {
            console.log(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    async getFiles(req, res, next) {
        try {
            const listMessage = await Message.find({conversation: req.params.id})
            if (!listMessage) {
                return responseError(res, 404, 'Không tìm thấy cuộc hội thoại');
            }
            //Chi lay mediaId cua message co trong listMessage
            const listMediaId = listMessage.flatMap(message => message.mediaId);
            let listMedia = await populateListMedia(
                {mediaIds: listMediaId, type: req.body.type, page: req.query.page || 0, size: req.query.size || 20});
            return res.status(200).json(listMedia.data);
        } catch (err) {
            console.log(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    async changeRole(req, res, next) {
        try {
            const conversation = await Conversation.findById(req.body.conversationId);
            if (!conversation) {
                return responseError(res, 404, 'Không tìm thấy cuộc hội thoại');
            }
            const member = conversation.members.find((member) => member.userId === req.body.userId);
            if (!member) {
                return responseError(res, 404, 'Không tìm thấy thành viên trong cuộc hội thoại');
            }
            const adminMenber = conversation.members.find((member) => member.role === 'admin');
            if (adminMenber.userId !== req.user.userId) {
                return responseError(res, 403, 'Bạn không có quyền thay đổi vai trò');
            }
            member.role = req.body.role;
            const myMember = conversation.members.find((member) => member.userId === req.user.userId);
            myMember.role = 'member';
            const messageSystem = await new Message({
                conversation: conversation._id,
                text: `<b>${req.user.userName}</b> đã thay đổi vai trò của <b>${member.nickname}</b> thành ${req.body.role}`,
                isSystem: true,
            }).save();
            await conversation.save();
            conversation.lastest_message = messageSystem._id;
            const userIds = conversation.members
                .filter((member) => member.userId !== req.user.userId)
                .map((item) => item.userId);
            // send socket
            SocketManager.sendToList(userIds, eventName.SEND_MESSAGE, messageSystem);
            return res.status(200).json(conversation);
        } catch (err) {
            console.log(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }

    async updateMember(req, res, next) {
        try {
            const conversation = await Conversation.findById(req.body.conversationId);
            if (!conversation) {
                return responseError(res, 404, 'Không tìm thấy cuộc hội thoại');
            }
            const member = conversation.members.find((member) => member.userId === req.body.userId);
            if (!member) {
                return responseError(res, 404, 'Không tìm thấy thành viên trong cuộc hội thoại');
            }
            const adminMenber = conversation.members.find((member) => member.role === 'admin');
            if (adminMenber.userId !== req.user.userId) {
                return responseError(res, 403, 'Bạn không có quyền thay đổi vai trò');
            }
            conversation.members = conversation.members.filter((member) => member.userId !== req.body.userId);
            await conversation.save();
            const messageSystem = await new Message({
                conversation: conversation._id,
                text: `<b>${req.user.userName}</b> đã xóa <b>${member.nickname}</b> khỏi cuộc hội thoại`,
                isSystem: true,
            }).save();
            conversation.lastest_message = messageSystem._id;
            const userIds = conversation.members
                .filter((member) => member.userId !== req.user.userId)
                .map((item) => item.userId);
            // send socket
            console.log("messageSystem", messageSystem);
            SocketManager.sendToList(userIds, eventName.SEND_MESSAGE, messageSystem);
            return res.status(200).json(conversation);
        } catch (err) {
            console.log(err);
            return next(
                createError.InternalServerError(
                    `${err.message}\nin method: ${req.method} of ${req.originalUrl}\nwith body: ${JSON.stringify(
                        req.body,
                        null,
                        2
                    )}`
                )
            );
        }
    }
}

module.exports = new ConversationController();
