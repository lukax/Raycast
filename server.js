// BASE SETUP
// =============================================================================

var express    = require('express');
var app        = express();
var bodyParser = require('body-parser');
var mongoose   = require('mongoose');
mongoose.connect('mongodb://mestre:eunaosei@ds063919.mongolab.com:63919/raycast');

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var port = process.env.PORT || 4000; 		// port

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

require('./app/routes/Users')(router);
require('./app/routes/Messages')(router);
require('./app/routes/Comments')(router);

// REGISTER OUR ROUTES -------------------------------
app.use('/', router);

// START THE SERVER
// =============================================================================
app.listen(port);
console.log('Running on port ' + port);