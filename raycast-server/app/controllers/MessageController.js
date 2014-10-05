'use strict';

var User = require('../models/User');
var Message = require('../models/Message');
var messageValidator = require('../validators/MessageValidator');

//Add a message
exports.addMessage = function(req, res) {
    if(messageValidator(req, res)){
        var message = new Message();
        message.author = req.user._id;
        message.message = req.body.message.substr(0, 160);
        message.time = Date.now();
        message.loc = {
            type : 'Point',
            coordinates : req.body.loc.coordinates
        };
        message.ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;

        message.save(function(err, message) {
            if (err){
                res.send(err);
            }
            res.json(message);
        });
    }
};

//Get all messages from a location
exports.getAllMessageByLocation = function(req, res) {
    var lat = Number(req.query.latitude) || null;
    var lon = Number(req.query.longitude) || null;
    var r = Number(req.query.radius) || null;

    if((lat === null) || (lon === null) || (r === null)){
        res.send(400, { error: 'Insufficient arguments' });
    }else{
        Message.findByRadius(r, lat, lon, null, null, null, function(err, message) {
            if (err){
                res.send(err);
            }
            res.json(message);
        });
    }
};

//Get all messages from a location with filters
exports.getAllMessageByFilter = function(req, res) {
    var lat = Number(req.query.latitude) || null;
    var lon = Number(req.query.longitude) || null;
    var r = Number(req.query.radius) || null;

    if((lat === null) || (lon === null) || (r === null)){
        res.send(400, { error: 'Insufficient arguments' });
    }else{
        Message.findByRadius(r, lat, lon, req.body.skip,
            req.body.limit, req.body.time, function(err, message) {
                if (err){
                    res.send(err);
                }

                res.json(message);
            });
    }
};

//Get all messages
exports.getAllMessage = function(req, res) {
    Message.find({}).populate('author').exec(function(err, message) {
        if (err) {
            res.send(err);
        }

        res.json(message);
    });
};

//Get a message by id
exports.getMessageById = function(req, res) {
    Message.findById(req.params.message_id).populate('author').exec(function(err, message) {
        if (err)
            res.send(err);

        if(message == null)
            res.json({error: 'No message found'});

        res.json(message);
    });
};

//Delete a message by id
exports.removeMessage = function(req, res) {
    Message.remove({
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
exports.getAllMessageByUser = function(req, res) {
    Message.findByAuthor(req.params.user_username, function(err, message) {
        if (err)
            res.send(err);

        if(message == null)
            res.json({error: 'No message found'});

        res.json(message);
    });
};
