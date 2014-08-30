module.exports = function(router){
	var user = require('../models/Users');
	var validator = require('validator');

	function validateUser(req, res){
		var ok = true;

		if(req.body.username.trim() == ""){
			ok = false;
			res.send(412, { error: 'No username set' });
		}else{
			if(!validator.isAlphanumeric(req.body.username)){
				ok = false;
				res.send(412, { error: 'Not a valid username' });
			}
		}

		if(req.body.name.trim() == ""){
			ok = false;
			res.send(412, { error: 'No name set' });
		}

		if(req.body.email.trim() == ""){
			ok = false;
			res.send(412, { error: 'No email set' });
		}else{
			if(!validator.isEmail(req.body.email)){
				ok = false;
				res.send(412, { error: 'Not a valid email' });
			}
		}

		return ok;
	}

    router.route('/user')

    	//Add a new user
		.post(function(req, res) {
			if(validateUser(req, res)){
				var users = new user();
				users.username = req.body.username;
				users.name = req.body.name;
				users.email = req.body.email;
				users.site = req.body.site;
				users.description = req.body.description;
				users.image = req.body.image;

				users.save(function(err) {
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

				if(validateUser(req, res)){
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
}
