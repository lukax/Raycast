'use strict';

module.exports = function (req, res){
    var validator = require('validator');
    var ok = true;

    if(req.body.username.trim() == ''){
        ok = false;
        res.send(412, { error: 'No username set' });
    }else{
        if(!validator.isAlphanumeric(req.body.username)){
            ok = false;
            res.send(412, { error: 'Not a valid username' });
        }
    }

    if(req.body.name.trim() == ''){
        ok = false;
        res.send(412, { error: 'No name set' });
    }

    if(req.body.email.trim() == ''){
        ok = false;
        res.send(412, { error: 'No email set' });
    }else{
        if(!validator.isEmail(req.body.email)){
            ok = false;
            res.send(412, { error: 'Not a valid email' });
        }
    }

    return ok;
};
