'use strict';

var mongoose = require('mongoose');
var crypto = require('crypto');

var UserSchema   = new mongoose.Schema({
    username: {
        type: String,
        unique: true,
        required: true
    },
    hashedPassword: {
        type: String,
        required: true
    },
    salt: {
        type: String,
        required: true
    },
    created: {
        type: Date,
        default: Date.now
    },
    name: String,
	email: {type: String, lowercase: true, trim: true, required: true, unique: true},
	site: String,
	description: String,
	image: String
});


UserSchema.methods.encryptPassword = function(password) {
    return crypto.createHmac('sha1', this.salt).update(password).digest('hex');
};

UserSchema.virtual('userId')
    .get(function () {
        return this.id;
    });

UserSchema.virtual('password')
    .set(function(password) {
        this._plainPassword = password;
        this.salt = crypto.randomBytes(32).toString('base64');
        this.hashedPassword = this.encryptPassword(password);
    })
    .get(function() { return this._plainPassword; });


UserSchema.methods.checkPassword = function(password) {
    return this.encryptPassword(password) === this.hashedPassword;
};

UserSchema.methods.toJSON = function() {
  var obj = this.toObject();
  delete obj.salt;
  delete obj.hashedPassword;
  return obj;
};

UserSchema.static('findByUsername', function (username, callback) {
    return this.find({ username: username }, callback);
});

UserSchema.static('findByEmail', function (email, callback) {
	return this.find({ email: email }, callback);
});

module.exports = mongoose.model('User', UserSchema);
