module.exports = function(router){
	var messagesBear = require('../models/Messages');
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
				var messages = new messagesBear();
				messages.author = req.body.author;
				messages.message = req.body.message.substr(0, 160);
				messages.time = Date.now();
				messages.loc = { type : "Point", coordinates : [ req.body.longitude, req.body.latitude ]};
				messages.ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;

				messages.save(function(err) {
					if (err)
						res.send(err);

					res.json({ message: 'Success' });
				});
			}			
		})

		//Get all messages
		.get(function(req, res) {
			messagesBear.find(function(err, messages) {
				if (err)
					res.send(err);

				res.json(messages);
			});
		});


	router.route('/message/:message_id')

		//Get a message by id
		.get(function(req, res) {
			messagesBear.findById(req.params.message_id, function(err, messagesBear) {
				if (err)
					res.send(err);
				res.json(messagesBear);
			});
		})

		//Delete a message by id
		.delete(function(req, res) {
			messagesBear.remove({
				_id: req.params.message_id
			}, function(err, messagesBear) {
				if (err)
					res.send(err);

				res.json({ message: 'Success' });
			});
		});

	router.route('/message/user/:user_username')

		//Get all messages from a user
		.get(function(req, res) {
			messagesBear.findByAuthor(req.params.user_username, function(err, messagesBear) {
				if (err)
					res.send(err);
				res.json(messagesBear);
			});
		});

	router.route('/message/:message_latitude/:message_longitude/:message_radius/:message_skip/:message_limit')

		//Get all messages from a location
		.get(function(req, res) {
			messagesBear.findByRadius(req.params.message_radius, req.params.message_latitude, 
						req.params.message_longitude, req.params.message_skip, 
						req.params.message_limit, function(err, messagesBear) {
				if (err)
					res.send(err);
				res.json(messagesBear);
			});
		});
}