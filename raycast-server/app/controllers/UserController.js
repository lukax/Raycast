'use strict';

module.exports = function(router){
	var user = require('../models/User');
    var userValidator = require('../validators/UserValidator');


    router.route('/user')

    	//Add a new user
		.post(function(req, res) {
			if(userValidator(req, res)){
				var usr = new user();
				usr.username = req.body.username;
                usr.password = req.body.password;
				usr.name = req.body.name;
				usr.email = req.body.email;
				usr.site = req.body.site;
				usr.description = req.body.description;
				usr.image = req.body.image;

				usr.save(function(err) {
					if (err)
						res.send(err);

					res.json({ message: 'Success' });
				});
			}

		})

		//Get all users
		.get(function(req, res) {
			user.find(function(err, users) {
				if (err)
					res.send(err);

				res.json(users);
			});
		});


	router.route('/user/id/:user_id')

		//Get a user by id
		.get(function(req, res) {
			user.findById(req.params.user_id, function(err, user) {
				if (err)
					res.send(err);
				res.json(user);
			});
		})

		//Update a user by id
		.put(function(req, res) {
			user.findById(req.params.user_id, function(err, user) {
				if (err)
					res.send(err);

				if(userValidator(req, res)){
					user.username = req.body.username;
					user.name = req.body.name;
					user.email = req.body.email;
					user.site = req.body.site;
					user.description = req.body.description;
					user.image = req.body.image;

					user.save(function(err) {
						if (err)
							res.send(err);

						res.json({ message: 'Success' });
					});
				}
			});
		})

		//Delete a user by id
		.delete(function(req, res) {
			user.remove({
				_id: req.params.user_id
			}, function(err, user) {
				if (err)
					res.send(err);

				res.json({ message: 'Success' });
			});
		});

	router.route('/user/:user_username')

		//Get a user by username
		.get(function(req, res) {
			user.findByUsername(req.params.user_username, function(err, user) {
				if (err)
					res.send(err);
				res.json(user);
			});
		});

	router.route('/user/email/:user_email')

		//Get a user by username
		.get(function(req, res) {
			user.findByEmail(req.params.user_email, function(err, user) {
				if (err)
					res.send(err);
				res.json(user);
			});
		});
};
