'use strict';

var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var CommentSchema   = new Schema({
	messageId: { type: String, ref: 'Message' },
    author: { type: String, ref: 'User' },
	comment: String,
	time: Number,
	ip: String
});

CommentSchema.static('findByMessage', function (messageId, callback) {
    return this.find({ messageId: messageId }).populate('author').exec(callback);
});

module.exports = mongoose.model('Comment', CommentSchema);
