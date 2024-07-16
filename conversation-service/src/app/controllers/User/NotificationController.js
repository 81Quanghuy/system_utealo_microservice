// noinspection DuplicatedCode

const axios = require('axios');
const createError = require('http-errors');
const Joi = require('joi');
const console = require('console');
// const IP = require('ip');
const crypto = require('crypto');
const { getPagination } = require('./../../../utils/Pagination');
const Message = require('./../../models/Message');
const mongoose = require('mongoose');
const { responseError } = require('./../../../utils/Response/error');
const {getListConversation} = require("../../../utils/Response/listData");
const {populateUser} = require("../../../utils/clients/userClient");
// set encryption algorithm
const algorithm = 'aes-256-cbc';
const Notification = require('./../../models/Notification');
const SocketManager = require("../../../socket/SocketManager");
const {eventName} = require("../../../socket/constant");

// private key
const key = process.env.DECODE_KEY; // must be of 32 characters
class NotificationController {
    // get all notification of user paginated
    async getAll(req, res,next) {
        try {
            const { page, size } = req.query;
            const { limit, offset } = getPagination(page, size);
            const userId = req.user.userId;
            const notifications = await Notification.find({ userId: userId })
                .limit(limit)
                .skip(offset)
                .sort({ updatedAt: -1 });
            return  getListConversation(res, true,
                'Get all notification successfully',
                notifications, 200);
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

    // add
    async addConversation(req, res, next) {
        try {
            const notification = new Notification();
            notification.userId = req.body.userId;
            if(req.user){
                notification.senderId = req.user.userId;
            }
            notification.content = req.body.content;
            notification.photo = req.body.photo;
            notification.link = req.body.link;
            if(req.body.groupId){
                notification.groupId = req.body.groupId;
                notification.type = 'group';
            }
            if(req.body.postId){
                notification.postId = req.body.postId;
                notification.type = 'post';
            }
            if(req.body.commentId){
                notification.commentId = req.body.commentId;
                notification.type = 'comment';
            }
            if(req.body.conversationId) {
                notification.conversationId = req.body.conversationId;
                notification.type = 'conversation';
            }
            await notification.save();
            console.log('notification', notification);
            console.log('req.body.userId', req.body.userId);
            SocketManager.send(req.body.userId, eventName.NOTIFICATION, notification);
            return  getListConversation(res, true,
                'Add notification successfully',
                notification, 200);
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

    //read notification
    async readNotification(req, res, next) {
        try {
            const notification = await Notification.findOne({ _id:  req.query.id});
            if (!notification) {
                return responseError(res, false, 'Notification not found', 404);
            }
            notification.read = true;
            await notification.save();
            return  getListConversation(res, true,
                'Read notification successfully',
                notification, 200);
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
    async deleteNotification(req, res, next) {
        try {
            const notification = await Notification.findOne({ _id: req.params.id });
           if (notification===null ) {
                return responseError(res, 404, 'Notification not found');
           }
           else{
                if(req.user.userId !== notification.userId){
                     return responseError(res, false, 'You are not authorized to delete this notification', 403);
                }
           }
            await notification.remove();
            return  getListConversation(res, true,
                'Delete notification successfully',
                notification, 200);
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

    //read all notification
    async readAllNotification(req, res, next) {
        try {
            const userId = req.user.userId;

            // Fetch notifications efficiently using updateMany
            const updatedNotifications = await Notification.updateMany(
                { userId },
                { read: true } // Set 'read' to true for all matching notifications
            );

            // Return a success response with the number of updated notifications
            return res.status(200).json({
                success: true,
                message: 'Read all notifications successfully.',
                updatedCount: updatedNotifications.modifiedCount,
                statusCode: 200,
            });
        } catch (err) {
            console.error(err); // Log the error for debugging
            return next(
                createError.InternalServerError(
                    'An error occurred while marking notifications as read.'
                )
            );
        }
    }

    //delete all notification
    async deleteAllNotification(req, res, next) {
        try {
            const userId = req.user.userId;
            const notifications = await Notification.find({ userId: userId });
            if (notifications.length === 0) {
                return res.status(404).json({
                    success: false,
                    message: 'No notifications found.',
                    statusCode: 404,
                });
            }
            await Notification.deleteMany({ userId });
            return res.status(200).json({
                success: true,
                message: 'All notifications deleted successfully.',
                statusCode: 200,
            });
        } catch (err) {
            console.error(err); // Log the error for debugging
            return next(
                createError.InternalServerError(
                    'An error occurred while deleting all notifications.'
                )
            );
        }
    }
}

module.exports = new NotificationController();
