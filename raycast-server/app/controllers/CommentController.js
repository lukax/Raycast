'use strict';

var User = require('../models/User');
var Comment = require('../models/Comment');
var Message = require('../models/Message');
var Notification = require('../models/Notification');
var commentValidator = require('../validators/CommentValidator');

//Add a new comment
exports.addComment = function(req, res) {
    if(commentValidator(req, res)){
        Message.findById(req.params.message_id).exec(function(err, message) {
            if(err){
                res.send(err);
            }
            if(message === null){
                res.json({error: 'No message found'});
            }
            //Create comment
            var cmm = new Comment();
            cmm.messageId = message._id;
            cmm.author = req.user._id;
            cmm.comment = req.body.comment.substr(0, 160);
            cmm.ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
            cmm.save(function(err, comments) {
                if (err){
                    res.send(err);
                }
                res.json(comments);
            });
            //Create notification for comment
            //TODO: verify if user is posting on the message he created, then do not create the notification
            var not = new Notification();
            not.user = req.user._id;
            not.messageId = message._id;
            not.type = 'comment';
            not.save(function(err) {
                //if err log
            });
        });
    }
};

//Get a comment by id
exports.getCommentById = function(req, res) {
    Comment.findById(req.params.comment_id).populate('author').exec(function(err, comment) {
        if (err) {
            res.send(err);
        }

        if(comment === null) {
            res.json({error: 'No comment found'});
        }

        res.json(comment);
    });
};

//Delete a comment by id
exports.removeComment = function(req, res) {
    Comment.remove({
        _id: req.params.comment_id
    }, function(err) {
        if (err) {
            res.send(err);
        }

        res.json({ message: 'Success' });
    });
};

//Get all comments from a message
exports.getAllCommentByMessage = function(req, res) {
    Comment.findByMessage(req.params.message_id, function(err, comment) {
            if (err){
                res.send(err);
            }

            res.json(comment);
        });
};
