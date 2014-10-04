/**
 * Created by lucas on 9/28/14.
 */
'use strict';

var mongoose = require('mongoose');

var Client = new mongoose.Schema({
    name: {
        type: String,
        unique: true,
        required: true
    },
    clientId: {
        type: String,
        unique: true,
        required: true
    },
    clientSecret: {
        type: String,
        required: true
    }
});

module.exports = mongoose.model('Client', Client);
