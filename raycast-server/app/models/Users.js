var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var UserSchema   = new Schema({
	username: {type: String, lowercase: true, trim: true, required: true, unique: true},
	name: String,
	email: {type: String, lowercase: true, trim: true, required: true, unique: true},
	site: String,
	description: String,
	image: String
});

UserSchema.static('findByUsername', function (username, callback) {
    return this.find({ username: username }, callback);
});

UserSchema.static('findByEmail', function (email, callback) {
	return this.find({ email: email }, callback);
});

module.exports = mongoose.model('Users', UserSchema);
