module.exports = function(router){
	var comment = require('../models/Comment');
    var commentValidator = require('../validators/CommentValidator');
	var user = require('../models/User');


    router.route('/comment')

    	//Add a new comment
		.post(function(req, res) {
			if(commentValidator(req, res)){
				var comments = new comment();
				comments.to = req.body.to;
                comments.author = req.body.author,
				comments.comment = req.body.comment.substr(0, 160);
				comments.time = Date.now();
				comments.ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;

				comments.save(function(err, comments) {
					if (err)
						res.send(err);

                    var cmm = comments.toObject();

                    user.findById(comments.author, function(uerr, user) {
                        if(uerr)
                            res.send(uerr);

                        cmm.author = user.toObject();
                        res.json(cmm);
                    });
				});
			}
		})

		//Get all comments
		.get(function(req, res) {
			comment.find({}).populate('author').exec(function(err, comment) {
				if (err)
					res.send(err);

				res.json(comment);
			});
		});


	router.route('/comment/:comment_id')

		//Get a comment by id
		.get(function(req, res) {
			comment.findById(req.params.comment_id).populate('author').exec(function(err, comment) {
				if (err)
					res.send(err);

                if(comment == null)
                    res.json({error: 'No comment found'});

				res.json(comment);
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
};
