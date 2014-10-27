'use strict';

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
