var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var CommentSchema   = new Schema({
	to: String,
	author: String,
	comment: String,
	time: Number,
	ip: String
});

CommentSchema.static('findByMessage', function (message, callback) {
	return this.find({ to: message }, callback);
});

module.exports = mongoose.model('Comments', CommentSchema);
