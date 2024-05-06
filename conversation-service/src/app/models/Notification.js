const mongoose = require('mongoose');
const mongooseDelete = require('mongoose-delete');
const mongoosePaginate = require('mongoose-paginate-v2');

const NotificationSchema = new mongoose.Schema(
    {
        userId: {
            type: String,
        },
        senderId: {
            type: String,
        },
        content: {
            type: String,
        },
        type: {
            type: String,
        },
        groupId: {
            type: String,
        },
        conversationId: {
            type: String,
        },
        postId: {
            type: String,
        },
        commentId: {
            type: String,
        },
        read: {
            type: Boolean,
            default: false,
        },
        photo: {
            type: String,
        },

        deleted: {
            type: Boolean,
            default: false,
        },
        isSystem: {
            type: Boolean,
            default: false,
        },
        createdAt: {
            type: Date,
            default: Date.now,
        },
        updatedAt: {
            type: Date,
        },
    },
    {
        timestamps: true,
        collection: 'notifications',
    }
);

// soft delete
NotificationSchema.plugin(mongooseDelete, {
    deletedAt: true,
    overrideMethods: 'all',
});

// paginate
NotificationSchema.plugin(mongoosePaginate);

module.exports = mongoose.model('Notification', NotificationSchema);
