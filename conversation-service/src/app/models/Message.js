const mongoose = require('mongoose');
const mongooseDelete = require('mongoose-delete');
const mongoosePaginate = require('mongoose-paginate-v2');

const MessageSchema = new mongoose.Schema(
	{
		conversation: {
			type: mongoose.SchemaTypes.ObjectId,
			ref: 'Conversation',
		},
		senderId: {
			type: String,
		},
		react:[
			{
				type:mongoose.SchemaTypes.ObjectId,
				ref:'ReactMessage'
			},
		],
		readerId: [
			{
				type: String,
				default: [],
			},
		],
		text: {
			type: String,
		},
		iv: {
			type: String,
		},
		mediaId: [
			{
				type: String,
				default: [],
			},
		],
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
		collection: 'messages',
	}
);

// soft delete
MessageSchema.plugin(mongooseDelete, {
	deletedAt: true,
	overrideMethods: 'all',
});

// paginate
MessageSchema.plugin(mongoosePaginate);

module.exports = mongoose.model('Message', MessageSchema);
