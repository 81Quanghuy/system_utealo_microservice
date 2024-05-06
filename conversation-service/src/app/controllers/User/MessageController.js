const Joi = require('joi');
const createError = require('http-errors');
const crypto = require('crypto');
const Message = require('./../../models/Message');
const ReactMessage = require('../../models/ReactMessage')
const { getPagination } = require('./../../../utils/Pagination');
const Conversation = require('./../../models/Conversation');
const { getListData } = require('./../../../utils/Response/listData');
const { openai } = require('./../../../configs/chatgpt');
const SocketManager = require('./../../../socket/SocketManager');
const { eventName } = require('./../../../socket/constant');

const { responseError } = require('./../../../utils/Response/error');
const ReactDTO = require('../../../utils/DTO/ReactDTO')
const {userId} = require("../../../utils/DTO/ReactDTO");
// set encryption algorithm
const algorithm = 'aes-256-cbc';

// private key
const key = process.env.DECODE_KEY; // must be of 32 characters

class MessageController {
    // [Post] add a new message
    async add(req, res, next) {
        try {
            // Validate request body not empty
            const schema = Joi.object({
                text: Joi.string().min(0),
                media: Joi.array().items(Joi.string()),
            })
                .or('text', 'media')
                .unknown();

            const { error } = schema.validate(req.body);
            if (error) {
                return next(createError(400, error.details[0].message));
            }

            const conversation = await Conversation.findById(req.params.conversationId || req.query.conversationId);

            if (conversation.members.some((mem) => mem.userId=== req.user.userId.toString())) {
                // random 16 digit initialization vector
                const iv = crypto.randomBytes(16);

                // encrypt the string using encryption algorithm, private key and initialization vector
                const cipher = crypto.createCipheriv(algorithm, key, iv);
                let encryptedData = cipher.update(req.body.text, 'utf-8', 'hex');
                encryptedData += cipher.final('hex');

                // convert the initialization vector to base64 string
                const base64data = Buffer.from(iv).toString('base64');

                const newMessage = new Message(req.body);
                newMessage.iv = base64data;
                newMessage.text = encryptedData;
                newMessage.conversation = conversation._id;
                newMessage.senderId = req.user.userId;
                const savedMessage = await newMessage.save();
                // populate sender
                const message = await Message.findById(savedMessage._id);
                conversation.lastest_message = savedMessage._id;
                await conversation.save();
                message.text = req.body.text;

                const userIds = conversation.members
                    .filter((member) => member.userId !== message.senderId)
                    .map((item) => item.userId);

                // send socket
                SocketManager.sendToList(userIds, eventName.SEND_MESSAGE, message);

                res.status(200).json({
                    success: true,
                    message: 'Tin nhắn đã được gửi',
                    result: message,
                    statusCode: 200,
                });
            } else {
                next(createError(403, 'Bạn không có trong cuộc hội thoại này!!!'));
            }
        } catch (err) {
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

    // [PUT] update reader message
    async update(req, res, next) {
        try {
            const message = await Message.findById(req.params.id);
            if (!message.reader.includes(req.user._id)) message.reader.push(req.user._id);
            await message.save();
            res.status(200).json(message);
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

    // [Delete] delete a message
    async delete(req, res, next) {
        try {
            const message = await Message.findById(req.body._id);
            if (message.senderId === req.user.userId) {
                // await Message.delete({ _id: req.params.id });
                await message.delete();
                res.status(200).json(message);
                const conversation = await Conversation.findById(message.conversation);

                const userIds = conversation.members
                    .filter((member) => member.userId !== req.user.userId)
                    .map((item) => item.userId);

                SocketManager.sendToList(userIds, eventName.DELETE_MESSAGE, message);
            } else {
                return responseError(res, 401, 'Bạn không có quyền xóa tin nhắn này');
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

    // [Get] get all messages
    async getAll(req, res) {
        const { limit, offset } = getPagination(req.query.page, req.query.size, req.query.offset);

        Message.paginate({}, { offset, limit })
            .then((data) => {
                getListData(res, data);
            })
            .catch((err) => responseError(res, 500, err.message ?? 'Some error occurred while retrieving tutorials.'));
    }

    // [Get] fetch messages from conversationId
    async fetchMessages(req, res, next) {
        try {
            const { limit, offset } = getPagination(req.query.page, req.query.size, req.query.offset);

            const conversation = await Conversation.findById(req.params.conversationId);
             if (!conversation) return res.status(404).send('Không tìm thấy cuộc hội thoại');

            // check user has existing user deleted conversation
            let index = -1;
            index = conversation.user_deleted.findIndex((item) => item.userId.toString() === req.user.userId);
            let deletedDate = new Date(-1); // date BC
            if (index !== -1) {
                deletedDate = conversation.user_deleted[index].deletedAt;
            }
            if (conversation.members.some((mem) => mem.userId === req.user.userId)) {
                Message.paginate(
                    {
                        conversation: req.params.conversationId,
                        createdAt: { $gt: deletedDate },
                    }, { offset, limit,sort: { createdAt: -1 },
                    populate: [
                    {
                        path: 'react',
                        select: '_id  react userId ',
                    },
                ],}

                )
                    .then((data) => {
                        data.docs.forEach((message) => {
                            if (message.iv) {
                                const iv = Buffer.from(message.iv, 'base64');
                                const decipher = crypto.createDecipheriv(algorithm, key, iv);
                                let decryptedData = decipher.update(message.text, 'hex', 'utf-8');
                                decryptedData += decipher.final('utf-8');
                                message.text = decryptedData;
                            }
                        });
                        getListData(res, data);
                    })
                    .catch((err) =>
                        responseError(res, 500, err.message ?? 'Some error occurred while retrieving tutorials.')
                    );
            } else {
                return responseError(res, 403, 'Bạn không có trong cuộc hội thoại này!!!');
            }
        } catch (error) {
            console.log(error);
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
    async reactMessage(req,res,next){
        const message = await Message.findById(req.body.messageId).populate('react','_id react userId');
        let newReact = new ReactMessage();
        let checkDetect = '';
        if(message.react.length >0){
            for (let i = 0; i < message.react.length; i++) {
                if(message.react[i].userId === req.body.userId){
                    const updateReact = await ReactMessage.findById(message.react[i]._id);
                    if(message.react[i].react === req.body.react){
                       newReact = updateReact;
                        checkDetect = "delete";
                    }
                    else {
                        updateReact.react = req.body.react;
                        newReact = updateReact;
                        checkDetect = "update";
                    }
                }
            }
            if(checkDetect ===""){
                newReact.react = req.body.react;
                newReact.userId = req.body.userId;
                checkDetect = "add";
            }
        } else {
            newReact.react = req.body.react;
            newReact.userId = req.body.userId;
            checkDetect = "add";
        }
        switch (checkDetect) {
            case "add":
                message.react.push(newReact);
                await newReact.save();
                break;
            case "update":
                message.react.forEach((item) => {
                    if(item.userId === req.body.userId){
                        item.react = req.body.react;
                    }
                });
                await newReact.save();
                break;
            case "delete":
              message.react =  message.react.filter((item) => item.userId !== req.body.userId);
              await newReact.deleteOne();
                break;
            default:
                break;

        }
        const savedMessage = await message.save();
        const conversation = await Conversation.findById(req.body.conversationId);
        const userIds = conversation.members
            .filter((member) => member.userId !== req.user.userId)
            .map((item) => item.userId);
        SocketManager.sendToList(userIds, eventName.REACT_MESSAGE, savedMessage);
        return res.status(200).json(savedMessage);
    }
    async removeMessage(req,res,next){
        const message = await Message.findById(req.body.messageId);
        return res.status(200).json("message");
    }
    // Chat with Chatgpt
    async chatWithChatgpt(req, res, next) {
        try {
            // TODO: token fail updated
            const completion = await openai.createChatCompletion({
                model: 'gpt-3.5-turbo',
                messages: [{ role: 'user', content: `${req.body.text}` }],
            });
            res.status(200).json(completion.data.choices[0].message);
        } catch (error) {
            console.log(error);
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
}

module.exports = new MessageController();
