var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var BearSchema   = new Schema({
	to: String,
	author: String,
	comment: String,
	time: Number,
	ip: String
});

BearSchema.static('findByMessage', function (message, callback) {
	return this.find({ to: message }, callback);
});

module.exports = mongoose.model('Comments', BearSchema);