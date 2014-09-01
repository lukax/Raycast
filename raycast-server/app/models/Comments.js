var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var CommentSchema   = new Schema({
	to: String,
    author: { type: String, ref: 'Users' },
	comment: String,
	time: Number,
	ip: String
});

CommentSchema.static('findByMessage', function (message, callback) {
    return this.find({ to: message }).populate('author').exec(callback);
});

module.exports = mongoose.model('Comments', CommentSchema);
