const express = require('express');

const router = express.Router({ mergeParams: true });
const MessageController = require('./../app/controllers/User/MessageController');

// add
router.post('/', MessageController.add);

// chat with chatgpt
router.post('/chatbot', MessageController.chatWithChatgpt);

// get
router.get('/', MessageController.fetchMessages);
// router.get('/', MessageController.getAll);

// delete
router.delete('/:id', MessageController.delete);

// update reader
router.put('/:id', MessageController.update);

module.exports = router;
