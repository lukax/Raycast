// BASE SETUP
// =============================================================================

var express    = require('express');
var app        = express();
var bodyParser = require('body-parser');
var mongoose   = require('mongoose');
mongoose.connect((process.env.MONGOLAB_URI || 'mongodb://localhost/raycast'));

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

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

require('./app/routes/UserController')(router);
require('./app/routes/MessageController')(router);
require('./app/routes/CommentController')(router);

// REGISTER OUR ROUTES -------------------------------
app.use('/', router);

// START THE SERVER
// =============================================================================
app.listen(port);
console.log('Running on port ' + port);
