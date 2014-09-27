'use strict';

var mongoose = require('mongoose');
var bcrypt = require('bcrypt-nodejs');

var UserSchema   = new mongoose.Schema({
	username: {type: String, lowercase: true, trim: true, required: true, unique: true},
    password: {type: String, required: true},
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

// Execute before each user.save() call
UserSchema.pre('save', function(callback) {
    var user = this;

    // Break out if the password hasn't changed
    if (!user.isModified('password')) return callback();

    // Password changed so we need to hash it
    bcrypt.genSalt(5, function(err, salt) {
        if (err) return callback(err);

        bcrypt.hash(user.password, salt, null, function(err, hash) {
            if (err) return callback(err);
            user.password = hash;
            callback();
        });
    });
});

UserSchema.methods.verifyPassword = function(password, cb) {
    bcrypt.compare(password, this.password, function(err, isMatch) {
        if (err) return cb(err);
        cb(null, isMatch);
    });
};

module.exports = mongoose.model('User', UserSchema);
