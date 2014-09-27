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

    if(req.body.message == null || req.body.message.trim() == ''){
        ok = false;
        res.send(412, { error: 'The message is empty' });
    }

    if(req.body.loc.coordinates == null){
        ok = false;
        res.send(412, { error: 'No coordinates set' });
    }else{
        if(!validator.isFloat(req.body.loc.coordinates[0]) || !validator.isFloat(req.body.loc.coordinates[0])){
            ok = false;
            res.send(412, { error: 'Not a valid coordinate' });
        }
    }

    return ok;
};
