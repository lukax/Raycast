module.exports = function(router){
	var message = require('../models/Messages');
	var messageValidator = require('../validation/MessageValidator');
    var user = require('../models/Users');


    router.route('/message')

    	//Add a new message
		.post(function(req, res) {
            if(messageValidator(req, res)){
				var messages = new message();
                messages.author = typeof req.body.author == 'string' ? req.body.author : req.body.author._id;
				messages.message = req.body.message.substr(0, 160);
				messages.time = Date.now();
				messages.loc = {
                    type : "Point",
                    coordinates : req.body.loc.coordinates
                };
				messages.ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;

				messages.save(function(err, messages) {
					if (err)
						res.send(err);

                    var msg = messages.toObject();

                    user.findById(messages.author, function(uerr, user) {
                        if(uerr)
                            res.send(uerr);

                        msg.author = user.toObject();
                        res.json(msg);
                    });
				});
			}
		})

        //Get all messages from a location
        .get(function(req, res) {
            var lat = Number(req.query.latitude) || null;
            var lon = Number(req.query.longitude) || null;
            var r = Number(req.query.radius) || null;

            if((lat == null) || (lon == null) || (r == null)){
                res.send(400, { error: 'Insufficient arguments' });
            }else{
                message.findByRadius(r, lat, lon, null, null, null, function(err, message) {
                    if (err)
                        res.send(err);
                    res.json(message);
                });
            }
        });

    router.route('/message/filter')
        //Get all messages from a location with filters
        .post(function(req, res) {
            var lat = Number(req.query.latitude) || null;
            var lon = Number(req.query.longitude) || null;
            var r = Number(req.query.radius) || null;

            if((lat == null) || (lon == null) || (r == null)){
                res.send(400, { error: 'Insufficient arguments' });
            }else{
                message.findByRadius(r, lat, lon, req.body.skip,
                            req.body.limit, req.body.time, function(err, message) {
                    if (err)
                        res.send(err);
                    res.json(message);
                });
            }
        });


    router.route('/message/all')

		//Get all messages
		.get(function(req, res) {
            message.find({}).populate('author').exec(function(err, message) {
                if (err)
                    res.send(err);
                res.json(message);
            });
		});


	router.route('/message/:message_id')

		//Get a message by id
		.get(function(req, res) {
			message.findById(req.params.message_id).populate('author').exec(function(err, message) {
				if (err)
					res.send(err);

                if(message == null)
                    res.json({error: 'No message found'});

				res.json(message);
			});
		})

		//Delete a message by id
		.delete(function(req, res) {
			message.remove({
				_id: req.params.message_id
			}, function(err, message) {
				if (err)
					res.send(err);

                if(message == 0){
                    res.json({error: 'No message found'});
                }else{
                    res.json({ message: 'Success' });
                }
			});
		});

	router.route('/message/user/:user_username')

		//Get all messages from a user
		.get(function(req, res) {
			message.findByAuthor(req.params.user_username, function(err, message) {
				if (err)
					res.send(err);

                if(message == null)
                    res.json({error: 'No message found'});

				res.json(message);
			});
		});

}
