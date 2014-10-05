// BASE SETUP
// =============================================================================

var express    = require('express');
var bodyParser = require('body-parser');
var passport   = require('passport');
var mongoose   = require('mongoose');
var config     = require('./app/config/config');
var log        = require('./app/util/log')(module);
var port = process.env.PORT || config.port;
var dbUrl = process.env.MONGOLAB_URI || config.mongoose.uri;

mongoose.connect(dbUrl);

var app = express();
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(passport.initialize());


// ROUTES
// =============================================================================
var router = express.Router();

router.use(function(req, res, next) {
	log.info('New API request');
	next();
});

router.get('/', function(req, res) {
	res.json({ message: 'Raycast API' });
});

// REGISTER OUR ROUTES -------------------------------
app.use('/', require('./app/config/controllers')(router));


// START THE SERVER
// =============================================================================
app.listen(port, function(){
    log.info('Express server listening on port: ' + port + ' with the db: ' + dbUrl);
});
