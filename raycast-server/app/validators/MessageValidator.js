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

    if(!req.body.message || req.body.message.trim() == ''){
        ok = false;
        res.send(412, { error: 'The message is empty' });
    }

    if(!req.body.loc || !req.body.loc.coordinates) {
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
