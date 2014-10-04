'use strict';

var user = require('../models/User');
var userValidator = require('../validators/UserValidator');

//Add a new user
exports.addUser = function(req, res) {
    if(userValidator(req, res)){
        var usr = new user();
        usr.username = req.body.username;
        usr.password = req.body.password;
        usr.name = req.body.name;
        usr.email = req.body.email;
        usr.site = req.body.site;
        usr.description = req.body.description;
        usr.image = req.body.image;

        usr.save(function(err) {
            if (err)
                res.send(err);

            res.json({ message: 'Success' });
        });
    }
};

//Get all users
exports.getUsers = function(req, res) {
    user.find(function(err, users) {
        if (err)
            res.send(err);

        res.json(users);
    });
};

//Get a user by id
exports.getUserById = function(req, res) {
    user.findById(req.params.user_id, function(err, user) {
        if (err)
            res.send(err);
        res.json(user);
    });
};

//Update a user by id
exports.updateUser = function(req, res) {
    user.findById(req.params.user_id, function(err, user) {
        if (err)
            res.send(err);

        if(userValidator(req, res)){
            user.username = req.body.username;
            user.name = req.body.name;
            user.email = req.body.email;
            user.site = req.body.site;
            user.description = req.body.description;
            user.image = req.body.image;

            user.save(function(err) {
                if (err)
                    res.send(err);

                res.json({ message: 'Success' });
            });
        }
    });
};

//Delete a user by id
exports.removeUser = function(req, res) {
    user.remove({
        _id: req.params.user_id
    }, function(err, user) {
        if (err)
            res.send(err);

        res.json({ message: 'Success' });
    });
};

//Get a user by username
exports.getUserByUsername = function(req, res) {
    user.findByUsername(req.params.user_username, function(err, user) {
        if (err)
            res.send(err);
        res.json(user);
    });
};

//Get a user by username
exports.getUserByEmail = function(req, res) {
    user.findByEmail(req.params.user_email, function(err, user) {
        if (err)
            res.send(err);
        res.json(user);
    });
};
