'use strict';

var message = require('../models/Message');
var messageValidator = require('../validators/MessageValidator');
var user = require('../models/User');

//Add a message
exports.addMessage = function(req, res) {
    if(messageValidator(req, res)){
        var messages = new message();
        messages.author = typeof req.body.author == 'string' ? req.body.author : req.body.author._id;
        messages.message = req.body.message.substr(0, 160);
        messages.time = Date.now();
        messages.loc = {
            type : 'Point',
            coordinates : req.body.loc.coordinates
        };
        messages.ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;

        messages.save(function(err, messages) {
            if (err)
                res.send(err);

            var msg = messages.toObject();

            user.findById(messages.author, function(uerr, user) {
                if(uerr)
                    res.send(uerr);

                if(user != null){
                    msg.author = user.toObject();
                    res.json(msg);
                }
            });
        });
    }
};

//Get all messages from a location
exports.listMessageByLocation = function(req, res) {
    var lat = Number(req.query.latitude) || null;
    var lon = Number(req.query.longitude) || null;
    var r = Number(req.query.radius) || null;

    if((lat == null) || (lon == null) || (r == null)){
        res.send(400, { error: 'Insufficient arguments' });
    }else{
        message.findByRadius(r, lat, lon, null, null, null, function(err, message) {
            if (err)
                res.send(err);
            res.json(message);
        });
    }
};

//Get all messages from a location with filters
exports.listMessageByFilter = function(req, res) {
    var lat = Number(req.query.latitude) || null;
    var lon = Number(req.query.longitude) || null;
    var r = Number(req.query.radius) || null;

    if((lat == null) || (lon == null) || (r == null)){
        res.send(400, { error: 'Insufficient arguments' });
    }else{
        message.findByRadius(r, lat, lon, req.body.skip,
            req.body.limit, req.body.time, function(err, message) {
                if (err)
                    res.send(err);
                res.json(message);
            });
    }
};

//Get all messages
exports.listAllMessage = function(req, res) {
    message.find({}).populate('author').exec(function(err, message) {
        if (err)
            res.send(err);
        res.json(message);
    });
};

//Get a message by id
exports.getMessageById = function(req, res) {
    message.findById(req.params.message_id).populate('author').exec(function(err, message) {
        if (err)
            res.send(err);

        if(message == null)
            res.json({error: 'No message found'});

        res.json(message);
    });
};

//Delete a message by id
exports.removeMessage = function(req, res) {
    message.remove({
        _id: req.params.message_id
    }, function(err, message) {
        if (err)
            res.send(err);

        if(message == 0){
            res.json({error: 'No message found'});
        }else{
            res.json({ message: 'Success' });
        }
    });
};

//Get all messages from a user
exports.listMessageByUser = function(req, res) {
    message.findByAuthor(req.params.user_username, function(err, message) {
        if (err)
            res.send(err);

        if(message == null)
            res.json({error: 'No message found'});

        res.json(message);
    });
};
