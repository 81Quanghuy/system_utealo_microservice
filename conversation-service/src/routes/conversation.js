const express = require('express');

const router = express.Router();
const AuthorMiddleware = require('../app/middlewares/AuthMiddleware');
const ConversationController = require('../app/controllers/User/ConversationController');

const { isAuth , getUser} = AuthorMiddleware;
// new conv
router.post('/create', getUser, ConversationController.add);
router.post('/create/user', getUser, ConversationController.createConversationUser);
router.get('/random',isAuth, ConversationController.createRandomConversation);
router.get('/video-call', isAuth, ConversationController.createRoomIDVideoCall);
// get conv of a user
router.get('/getAll', ConversationController.getAll);
router.get('/search', isAuth, ConversationController.search);
router.get('/', getUser, ConversationController.getConversationOfUser);
router.get('/:id', getUser, ConversationController.getConversationById);
// get conv includes two userId
router.get('/find/:userId', isAuth, ConversationController.getConversationByUserIds);
// get media of a conversation
router.get('/:id/files/:type', isAuth, ConversationController.getAllMedia);

// update conv

router.patch('/:id/members/:type', isAuth, ConversationController.updateMembers);
router.put('/:id/leave', isAuth, ConversationController.leaveConversation);
router.put('/:id', isAuth, ConversationController.update);
// update member of conv

router.delete('/user-deleted/:id', isAuth, ConversationController.userDeletedAllMessages);
// delete conv
router.delete('/:id', isAuth, ConversationController.delete);

module.exports = router;
