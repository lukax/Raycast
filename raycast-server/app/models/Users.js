var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var BearSchema   = new Schema({
	username: {type: String, lowercase: true, trim: true, required: true, unique: true},
	lastToken: String,
	name: String,
	//For some reason the following line isn't working properly
	email: {type: String, lowercase: true, trim: true, required: true, unique: true},
	site: String,
	description: String,
	photo: String
});

BearSchema.static('findByUsername', function (username, callback) {
	return this.find({ username: username }, callback);
});

BearSchema.static('findByEmail', function (email, callback) {
	return this.find({ email: email }, callback);
});

module.exports = mongoose.model('Users', BearSchema);