var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var BearSchema   = new Schema({
	author: String,
	message: String,
	time: Number,
	location: String,
	//latitude: Number,
	//longitude: Number,
	ip: String
});

BearSchema.static('findByAuthor', function (author, callback) {
	return this.find({ author: author }, callback);
});

module.exports = mongoose.model('Messages', BearSchema);