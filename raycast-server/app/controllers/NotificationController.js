'use strict';

var log          = require('../util/log')(module);
var Notification = require('../models/Notification');

// GET
// return list of Notifications
exports.getNotifications = function(req, res) {
    Notification.find({ user: req.user._id }, function(err, notifications) {
        if (err) {
            res.send(err);
        }

        res.json(notifications);
    });
};

// DELETE
// remove a notification by id
exports.removeNotificationById = function(req, res) {
    Notification.remove({ _id: req.params.notification_id }, function(err, notifications) {
        if (err) {
            res.send(err);
        }

        res.json({ message: 'Success' });
    });
};

// DELETE
// remove all notifications
exports.removeAllNotifications = function(req, res) {
    Notification.remove({ user: req.user._id }, function(err, notifications) {
        if (err) {
            res.send(err);
        }

        res.json({ message: 'Success' });
    });
};
