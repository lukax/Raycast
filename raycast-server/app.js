// BASE SETUP
// =============================================================================

var express    = require('express');
var app        = express();
var bodyParser = require('body-parser');
var passport   = require('passport');
var mongoose   = require('mongoose');
mongoose.connect((process.env.MONGOLAB_URI || 'mongodb://localhost/raycast'));

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(passport.initialize());

var port = process.env.PORT || 4000;

// ROUTES
// =============================================================================
var router = express.Router();

router.use(function(req, res, next) {
	console.log('New API request');
	next();
});

router.get('/', function(req, res) {
	res.json({ message: 'Raycast API' });
});

var authController = require('./app/controllers/AuthController');
var messageController = require('./app/controllers/MessageController');


router.route('/message')
    .post(authController.isAuthenticated, messageController.addMessage)
    .get(messageController.listMessageByLocation);

router.route('/message/filter')
    .post(messageController.listMessageByFilter);

router.route('/message/all')
    .get(messageController.listAllMessage);

router.route('/message/:message_id')
    .get(messageController.getMessageById)
    .delete(messageController.removeMessage);

router.route('/message/user/:user_username')
    .get(messageController.listMessageByUser);


require('./app/controllers/UserController')(router);
require('./app/controllers/CommentController')(router);


// REGISTER OUR ROUTES -------------------------------
app.use('/', router);

// START THE SERVER
// =============================================================================
app.listen(port);
console.log('Running on port ' + port);
