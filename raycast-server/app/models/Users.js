var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var BearSchema   = new Schema({
	username: String,
	password: String,
	name: String,
	email: String,
	site: String,
	description: String,
	photo: String
});

BearSchema.static('findByUsername', function (username, callback) {
	return this.find({ username: username }, callback);
});

module.exports = mongoose.model('Users', BearSchema);