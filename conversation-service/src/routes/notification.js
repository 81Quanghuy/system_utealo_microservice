const express = require('express');

const router = express.Router({ mergeParams: true });
const NotificationController = require('./../app/controllers/User/NotificationController');
const AuthorMiddleware = require("../app/middlewares/AuthMiddleware");
const { isAuth , getUser} = AuthorMiddleware;

// add
router.post('/',getUser, NotificationController.addConversation);

// get all notification of user paginated
router.get('/',getUser, NotificationController.getAll);
//read notification
router.put('/read',getUser, NotificationController.readNotification);
//delete notification
router.delete('/:id',getUser, NotificationController.deleteNotification);
//delete all notification
router.delete('/',getUser, NotificationController.deleteAllNotification);
//read all notification
router.put('/read-all',getUser, NotificationController.readAllNotification);

module.exports = router;
