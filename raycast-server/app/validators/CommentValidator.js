'use strict';

module.exports = function (req, res){
    var validator = require('validator');
    var ok = true;
    var author =  typeof req.body.author == 'string' ? req.body.author : req.body.author._id;

    if(author != null && author.trim() != null){
        if(!validator.isAlphanumeric(author)){
            ok = false;
            res.send(412, { error: 'Not a valid author id' });
        }
    }else{
        ok = false;
        res.send(412, { error: 'No author set' });
    }

    if(req.body.to.trim() == ''){
        ok = false;
        res.send(412, { error: 'No message set' });
    }else{
        if(!validator.isAlphanumeric(req.body.to)){
            ok = false;
            res.send(412, { error: 'Not a valid message id' });
        }
    }

    if(req.body.comment.trim() == ''){
        ok = false;
        res.send(412, { error: 'The comment is empty' });
    }

    return ok;
};
