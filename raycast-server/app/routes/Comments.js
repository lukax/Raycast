module.exports = function(router){
	var comment = require('../models/Comments');
	var validator = require('validator');

	function validateComment(req, res){
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

		if(req.body.to.trim() == ""){
			ok = false;
			res.send(412, { error: 'No message set' });
		}else{
			if(!validator.isAlphanumeric(req.body.to)){
				ok = false;
				res.send(412, { error: 'Not a valid message id' });
			}
		}

		if(req.body.comment.trim() == ""){
			ok = false;
			res.send(412, { error: 'The comment is empty' });
		}

		return ok;
	}

    router.route('/comment')

    	//Add a new comment
		.post(function(req, res) {
			if(validateComment(req, res)){
				var comments = new comment();
				comments.to = req.body.to;
                comments.author = req.body.author,
				comments.comment = req.body.comment.substr(0, 160);
				comments.time = Date.now();
				comments.ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;

				comments.save(function(err) {
					if (err)
						res.send(err);

					res.json({ message: 'Success' });
				});
			}
		})

		//Get all comments
		.get(function(req, res) {
			comment.find({}).populate('author').exec(function(err, message) {
				if (err)
					res.send(err);

				res.json(message);
			});
		});


	router.route('/comment/:comment_id')

		//Get a comment by id
		.get(function(req, res) {
			comment.findById(req.params.comment_id).populate('author').exec(function(err, message) {
				if (err)
					res.send(err);
				res.json(message);
			});
		})

		//Delete a comment by id
		.delete(function(req, res) {
			comment.remove({
				_id: req.params.comment_id
			}, function(err, comment) {
				if (err)
					res.send(err);

				res.json({ message: 'Success' });
			});
		});

	router.route('/comment/message/:message_id')

		//Get all comments from a message
		.get(function(req, res) {
			comment.findByMessage(req.params.message_id, function(err, message) {
				if (err)
					res.send(err);
				res.json(message);
			});
		});
}
