'use strict';

//var User = require('../models/User');
var Comment = require('../models/Comment');
var commentValidator = require('../validators/CommentValidator');

//Add a new comment
exports.addComment = function(req, res) {
    if(commentValidator(req, res)){
        var cmm = new Comment();
        cmm.messageId = req.body.messageId;
        cmm.author = req.user._id;
        cmm.comment = req.body.comment.substr(0, 160);
        cmm.time = Date.now();
        cmm.ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;

        cmm.save(function(err, comments) {
            if (err){
                res.send(err);
            }
            res.json(comments);
        });
    }
};

//Get all comments
exports.getAllComment = function(req, res) {
    Comment.find({}).populate('author').exec(function(err, comment) {
        if (err) {
            res.send(err);
        }

        res.json(comment);
    });
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
    Comment.findByMessage(req.params.message_id, function(err, message) {
        if (err){
            res.send(err);
        }

        res.json(message);
    });
};
