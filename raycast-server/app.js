// BASE SETUP
// =============================================================================

var express    = require('express');
var bodyParser = require('body-parser');
var passport   = require('passport');
var mongoose   = require('mongoose');
var config     = require('./app/config/config');

mongoose.connect((process.env.MONGOLAB_URI || config.mongoose.uri));

var app = express();
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(passport.initialize());


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

// REGISTER OUR ROUTES -------------------------------
app.use('/', require('./app/config/controllers')(router));


// START THE SERVER
// =============================================================================
app.listen(process.env.PORT || config.port);
