module.exports = function(router){
	var usersBear = require('../models/Users');

    router.route('/user')

    	//Add a new user
		.post(function(req, res) {			
			var users = new usersBear();
			users.login = req.body.login;
			users.password = req.body.password;
			users.name = req.body.name;
			users.email = req.body.email;
			users.site = req.body.site;
			users.description = req.body.description;
			users.photo = req.body.photo;

			users.save(function(err) {
				if (err)
					res.send(err);

				res.json({ message: 'Success' });
			});
			
		})

		//Get all users
		.get(function(req, res) {
			usersBear.find(function(err, users) {
				if (err)
					res.send(err);

				res.json(users);
			});
		});


	router.route('/user/id/:user_id')

		//Get a user by id
		.get(function(req, res) {
			usersBear.findById(req.params.user_id, function(err, usersBear) {
				if (err)
					res.send(err);
				res.json(usersBear);
			});
		})

		//Update a user by id
		.put(function(req, res) {
			usersBear.findById(req.params.user_id, function(err, usersBear) {
				if (err)
					res.send(err);

				usersBear.login = req.body.login;
				usersBear.password = req.body.password;
				usersBear.name = req.body.name;
				usersBear.email = req.body.email;
				usersBear.site = req.body.site;
				usersBear.description = req.body.description;
				usersBear.photo = req.body.photo;

				usersBear.save(function(err) {
					if (err)
						res.send(err);

					res.json({ message: 'Success' });
				});

			});
		})

		//Delete a user by id
		.delete(function(req, res) {
			usersBear.remove({
				_id: req.params.user_id
			}, function(err, usersBear) {
				if (err)
					res.send(err);

				res.json({ message: 'Success' });
			});
		});

	router.route('/user/:user_username')

		//Get a user by username
		.get(function(req, res) {
			usersBear.findByUsername(req.params.user_username, function(err, usersBear) {
				if (err)
					res.send(err);
				res.json(usersBear);
			});
		});
}