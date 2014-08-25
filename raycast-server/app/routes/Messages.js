module.exports = function(router){
	var messagesBear = require('../models/Messages');

    router.route('/message')

    	//Add a new message
		.post(function(req, res) {			
			var messages = new messagesBear();
			messages.author = req.body.author;
			messages.message = req.body.message.substr(0, 160);
			messages.time = Date.now();
			messages.location = '{ type: "Point", coordinates: [ '+req.body.longitude+', '+req.body.latitude+' ]';
			//messages.latitude = req.body.latitude;
			//messages.longitude = req.body.longitude;
			messages.ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;

			messages.save(function(err) {
				if (err)
					res.send(err);

				res.json({ message: 'Success' });
			});
			
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
}