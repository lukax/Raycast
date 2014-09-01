module.exports = function(router){
	var message = require('../models/Messages');
	var validator = require('validator');

	function validateMessage(req, res){
		var ok = true;

		if(req.body.author.trim() == ""){
			ok = false;
			res.send(412, { error: 'No author set' });
		}else{
			if(!validator.isAlphanumeric(req.body.author)){
				ok = false;
				res.send(412, { error: 'Not a valid author id' });
			}
		}

		if(req.body.message.trim() == ""){
			ok = false;
			res.send(412, { error: 'The message is empty' });
		}

		if(req.body.longitude.trim() == "" || req.body.latitude.trim() == ""){
			ok = false;
			res.send(412, { error: 'No coordinates set' });
		}else{
			if(!validator.isFloat(req.body.longitude) || !validator.isFloat(req.body.latitude)){
				ok = false;
				res.send(412, { error: 'Not a valid coordinate' });
			}
		}

		return ok;
	}


    router.route('/message')

    	//Add a new message
		.post(function(req, res) {
			if(validateMessage(req, res)){
				var messages = new message();
                /*messages.author = {
                    id: req.body.author_id,
                    name: req.body.author_name,
                    username: req.body.author_username,
                    image: req.body.author_image
                };*/
                messages.author = req.body.author;
				messages.message = req.body.message.substr(0, 160);
				messages.time = Date.now();
				messages.loc = {
                    type : "Point",
                    coordinates : [ req.body.longitude, req.body.latitude ]
                };
				messages.ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;

				messages.save(function(err) {
					if (err)
						res.send(err);

					res.json({ message: 'Success' });
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
                message.findByRadius(r, lat, lon, req.query.skip,
                            req.query.limit, function(err, message) {
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

				res.json({ message: 'Success' });
			});
		});

	router.route('/message/user/:user_username')

		//Get all messages from a user
		.get(function(req, res) {
			message.findByAuthor(req.params.user_username, function(err, message) {
				if (err)
					res.send(err);
				res.json(message);
			});
		});

}
