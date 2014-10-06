'use strict';

var User = require('../models/User');
var userValidator = require('../validators/UserValidator');

//Add a new user
exports.addUser = function(req, res) {
    if(userValidator(req, res)){
        var usr = new User();
        usr.username = req.body.username;
        usr.password = req.body.password;
        usr.name = req.body.name;
        usr.email = req.body.email;
        usr.site = req.body.site;
        usr.description = req.body.description;
        usr.image = req.body.image;

        usr.save(function(err) {
            if (err) {
                res.send(err);
            }

            res.json({ message: 'Success' });
        });
    }
};

//Get all users
exports.getUsers = function(req, res) {
    User.find(function(err, users) {
        if (err) {
            res.send(err);
        }

        res.json(users);
    });
};

//Get a user by id
exports.getUserById = function(req, res) {
    User.findById(req.params.user_id, function(err, user) {
        if (err) {
            res.send(err);
        }

        res.json(user);
    });
};

//Update a user by id
exports.updateUser = function(req, res) {
    User.findById(req.params.user_id, function(err, user) {
        if (err) {
            res.send(err);
        }

        if(userValidator(req, res)){
            user.username = req.body.username;
            user.name = req.body.name;
            user.email = req.body.email;
            user.site = req.body.site;
            user.description = req.body.description;
            user.image = req.body.image;

            user.save(function(err) {
                if (err) {
                    res.send(err);
                }

                res.json({ message: 'Success' });
            });
        }
    });
};

//Delete a user by id
exports.removeUser = function(req, res) {
    User.remove({
        _id: req.params.user_id
    }, function(err, user) {
        if (err) {
            res.send(err);
        }

        res.json({ message: 'Success' });
    });
};

//Get a user by username
exports.getUserByUsername = function(req, res) {
    User.findByUsername(req.params.user_username, function(err, user) {
        if (err) {
            res.send(err);
        }

        res.json(user);
    });
};

//Get a user by username
exports.getUserByEmail = function(req, res) {
    User.findByEmail(req.params.user_email, function(err, user) {
        if (err) {
            res.send(err);
        }

        res.json(user);
    });
};


//Get current user info
exports.getUserInfo = function(req, res) {
    // req.authInfo is set using the `info` argument supplied by
    // `BearerStrategy`.  It is typically used to indicate scope of the token,
    // and used in access control checks.  For illustrative purposes, this
    // example simply returns the scope in the response.
    res.json({ _id: req.user.userId, name: req.user.username, scope: req.authInfo.scope });
};
