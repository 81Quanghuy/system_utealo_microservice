const Conversation = require('../../app/models/Conversation');

exports.populateConversation = async (conversationID) => {
    return Conversation.findById(conversationID);
}

