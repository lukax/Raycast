'use strict';

module.exports = function (req, res){
    var validator = require('validator');
    var ok = true;

    if(!req.user || !req.user._id){
        ok = false;
        if(!validator.isAlphanumeric(req.user._id)){
            res.send(412, { error: 'Not a valid author' });
        }
        res.send(412, { error: 'No author set' });
    }

    if(!req.body.messageId || req.body.messageId.trim() === ''){
        ok = false;
        if(!validator.isAlphanumeric(req.body.to)){
            res.send(412, { error: 'Not a valid message id' });
        }
        res.send(412, { error: 'No message set' });
    }

    if(!req.body.comment || req.body.comment.trim() === ''){
        ok = false;
        res.send(412, { error: 'The comment is empty' });
    }

    return ok;
};
