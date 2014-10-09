// BASE SETUP
// =============================================================================

var express         = require('express');
var bodyParser      = require('body-parser');
var passport        = require('passport');
var mongoose        = require('mongoose');
var config          = require('./app/config/config');
var log             = require('./app/util/log')(module);
var controllers     = require('./app/config/controllers');

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

// Register api routes
app.use('/', controllers(router));

// Exception handler
app.use(function(req, res, next){
    res.status(404);
    log.debug('Not found URL: %s',req.url);
    res.send({ error: 'Not found' });
});

app.use(function(err, req, res, next){
    res.status(err.status || 500);
    log.error('Internal error(%d): %s',res.statusCode,err.message);
    res.send({ error: err.message });
});


// START THE SERVER
// =============================================================================
app.listen(port, function(){
    log.info('\nExpress server running \nPort: ' + port + ' \nDb: ' + dbUrl);
});
