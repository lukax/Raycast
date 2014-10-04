/**
 * Created by lucas on 10/4/14.
 */
var Client = require('../models/Client');

// POST
// add a new Client
exports.addClient = function(req, res) {
    var client = new Client();

    client.name = req.body.name;
    client.id = req.body.id;
    client.secret = req.body.secret;
    client.userId = req.user._id;

    client.save(function(err) {
        if (err)
            res.send(err);

        res.json({ message: 'Client added to the locker!', data: client });
    });
};

// GET
// return list of Clients
exports.getClients = function(req, res) {
    Client.find({ userId: req.user._id }, function(err, clients) {
        if (err)
            res.send(err);

        res.json(clients);
    });
};
