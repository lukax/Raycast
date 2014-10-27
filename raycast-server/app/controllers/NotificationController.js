'use strict';

var log          = require('../util/log')(module);
var Notification = require('../models/Notification');

// GET
// return list of Notifications
exports.getNotifications = function(req, res) {
    log.debug(req.user._id);
    Notification.find({ user: req.user._id }, function(err, notifications) {
        if (err) {
            res.send(err);
        }

        res.json(notifications);
    });
};

// DELETE
// remove all notifications
exports.removeNotifications = function(req, res) {
    Notification.remove({ user: req.user._id }, function(err, notifications) {
        if (err) {
            res.send(err);
        }

        res.json({ message: 'Success' });
    });
};
