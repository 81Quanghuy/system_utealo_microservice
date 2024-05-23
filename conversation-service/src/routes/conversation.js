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

router.put('/update/:id/:type', getUser, ConversationController.updateMembers);
router.put('/:id/leave', getUser, ConversationController.leaveConversation);
router.put('/:id', getUser, ConversationController.update);
// update member of conv
router.put('/change/role', getUser, ConversationController.changeRole);
router.put('/member/delete', getUser, ConversationController.updateMember);
router.delete('/user-deleted/:id', isAuth, ConversationController.userDeletedAllMessages);
// delete conv
router.delete('/:id', getUser, ConversationController.delete);

//get file in conversation
router.post('/:id/files', isAuth, ConversationController.getFiles);

module.exports = router;
