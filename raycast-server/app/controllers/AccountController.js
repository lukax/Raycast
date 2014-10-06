/**
 * Created by lucas on 10/4/14.
 */
var log                 = require('../util/log')(module);
var oauth2orize         = require('oauth2orize');
var passport            = require('passport');
var crypto              = require('crypto');
var config              = require('../config/config.json');
var User                = require('../models/User');
var Client              = require('../models/Client');
var AccessToken         = require('../models/AccessToken');
var RefreshToken        = require('../models/RefreshToken');

var google = require('googleapis');
var plus = google.plus('v1');
var goauth2 = google.oauth2('v1');
//var oauth2Client = new google.auth.OAuth2(config.security.clientId, config.security.clientSecret, '');

// create OAuth 2.0 server
var server = oauth2orize.createServer();

// Exchange g+ token for access token
server.exchange(oauth2orize.exchange.password(function(client, username, password, scope, done) {

    goauth2.tokeninfo({ id_token: username }, function(err, response){
        if(!err) { return done(err); }
        return done('ok');
    });

    // Retrieve tokens via token exchange explained above or set them:
    // oauth2Client.setCredentials({
    //   access_token: username
    // });

    // plus.people.get({ userId: 'me', auth: oauth2Client }, function(err, response) {
    //     if(err) { return done(err); }
    //     return response;
    // });

    // User.findOne({ username: username }, function(err, user) {
    //     if (err) { return done(err); }
    //     if (!user) { return done(null, false); }
    //     if (!user.checkPassword(password)) { return done(null, false); }

    //     RefreshToken.remove({ userId: user.userId, clientId: client.clientId }, function (err) {
    //         if (err) return done(err);
    //     });
    //     AccessToken.remove({ userId: user.userId, clientId: client.clientId }, function (err) {
    //         if (err) return done(err);
    //     });

    //     var tokenValue = crypto.randomBytes(32).toString('base64');
    //     var refreshTokenValue = crypto.randomBytes(32).toString('base64');
    //     var token = new AccessToken({ token: tokenValue, clientId: client.clientId, userId: user.userId });
    //     var refreshToken = new RefreshToken({ token: refreshTokenValue, clientId: client.clientId, userId: user.userId });
    //     refreshToken.save(function (err) {
    //         if (err) { return done(err); }
    //     });
    //     var info = { scope: '*' };
    //     token.save(function (err, token) {
    //         if (err) { return done(err); }
    //         done(null, tokenValue, refreshTokenValue, { 'expires_in': config.security.tokenLife });
    //     });
    // });
}));

// token endpoint
exports.token = [
    passport.authenticate(['basic', 'oauth2-client-password'], { session: false }),
    server.token(),
    server.errorHandler()
];
