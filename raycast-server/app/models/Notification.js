/**
 * Created by lucas on 10/27/14.
 */
'use strict';

var mongoose = require('mongoose');

var Notification = new mongoose.Schema({
    user: { type: String, ref: 'User' },
    messageId: { type: String, ref: 'Message' },
    type: String,
    description: String,
    time: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('Notification', Notification);
