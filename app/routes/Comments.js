module.exports = function(router){
	var commentsBear = require('../models/Comments');

    router.route('/comment')

    	//Add a new comment
		.post(function(req, res) {			
			var comments = new commentsBear();
			comments.to = req.body.to;
			comments.author = req.body.author;
			comments.comment = req.body.comment.substr(0, 160);
			comments.time = Date.now();
			comments.ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;

			comments.save(function(err) {
				if (err)
					res.send(err);

				res.json({ message: 'Success' });
			});
			
		})

		//Get all comments
		.get(function(req, res) {
			commentsBear.find(function(err, comments) {
				if (err)
					res.send(err);

				res.json(comments);
			});
		});


	router.route('/comment/:comment_id')

		//Get a comment by id
		.get(function(req, res) {
			commentsBear.findById(req.params.comment_id, function(err, commentsBear) {
				if (err)
					res.send(err);
				res.json(commentsBear);
			});
		})

		//Delete a comment by id
		.delete(function(req, res) {
			commentsBear.remove({
				_id: req.params.comment_id
			}, function(err, commentsBear) {
				if (err)
					res.send(err);

				res.json({ message: 'Success' });
			});
		});

	router.route('/comment/message/:message_id')

		//Get all comments from a message
		.get(function(req, res) {
			commentsBear.findByMessage(req.params.message_id, function(err, commentsBear) {
				if (err)
					res.send(err);
				res.json(commentsBear);
			});
		});
}