const mongoose = require('mongoose');
const mongooseDelete = require('mongoose-delete');
const mongoosePaginate = require('mongoose-paginate-v2');

const userDeletedAllMessages = mongoose.Schema(
    {
        userId: {
           type: String,
        },
        deletedAt: {
            type: Date,
            default: Date.now(),
        },
    },
    { _id: false }
);

const history = mongoose.Schema(
    {
        editorId: {
            type: String,
        },
        content: {
            type: String,
        },
        updatedAt: {
            type: Date,
            default: Date.now(),
        },
    },
    { _id: false }
);

const member = mongoose.Schema(
    {
        userId: {
            type: String,
        },
        role: {
            type: String,
            enum: ['admin', 'member'],
            default: 'member',
        },
        nickname: {
            type: String,
            default: '',
        },
        changedNicknameBy: {
            type: String,
        },
        addedAt: {
            type: Date,
            default: Date.now(),
        },
        addedBy: {
            type: String,
        },
        isOnline: {
            type: Boolean,
            default: false,
        },
        lastLogin: {
            type: Date,
        },
        avatar: {
            type: String,
        },
    },
    { _id: false }
);

const ConversationSchema = new mongoose.Schema(
    {
        members: [member],
        name: {
            type: String,
        },
        creatorId: {
            type: String,
        },
        history: [history],
        user_deleted: [userDeletedAllMessages],
        type: {
            type: String,
            enum: ['direct', 'group'],
            default: 'direct',
        },
        avatar: {
           type: String,
        },
        lastest_message: {
            type: mongoose.SchemaTypes.ObjectId,
            ref: 'Message',
        },
        deleted: {
            type: Boolean,
            default: false,
        },
    },
    { timestamps: true }
);

// soft delete
ConversationSchema.plugin(mongooseDelete, {
    deletedAt: true,
    overrideMethods: 'all',
});

// paginate
ConversationSchema.plugin(mongoosePaginate);
//khai bao constructor

module.exports = mongoose.model('Conversation', ConversationSchema);
