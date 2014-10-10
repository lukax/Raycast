/**
 * Created by lucas on 10/4/14.
 */
'use strict';
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
var oauth2Client = new google.auth.OAuth2(config.security.clientId, config.security.clientSecret);

// create OAuth 2.0 server
var server = oauth2orize.createServer();

// Exchange g+ token for access token
server.exchange(oauth2orize.exchange.code(function(client, code, redirectURI, done) {

    oauth2Client.getToken(code, function(err, tokens) {
        // Now tokens contains an access_token and an optional refresh_token. Save them.
        if(err) {
            log.error('AccountController oauth2Client.getToken', err);
            return done(null, false);
        }
        oauth2Client.setCredentials(tokens);
        requestUserInfo();
    });

    var foundEmail = null;

    function requestUserInfo(){
        plus.people.get({ userId: 'me', auth: oauth2Client }, function(err, response) {
            if(err) {
                log.error('AccountController requestUserInfo', err);
                return done(err);
            }

            for(var i = 0; i < response.emails.length; i++){
                if(response.emails[i].type === 'account'){
                    foundEmail = response.emails[i].value;
                    break;
                }
            }
            if(!foundEmail){
                log.error('AccountController', 'could not find account email of user');
                return done({ message: 'No account email found' });
            }

            User.findOne({ email: foundEmail }, function(err, user) {
                if(err || !user){
                    forNewUser(user, response);
                }
                else{
                    forExistantUser(user);
                }
            });
        });
    }

    function forNewUser(user, plusPeopleMe){
        log.info('AccountController creating new user', foundEmail);
        user = new User();
        user.username = foundEmail.split('@')[0];
        user.password = crypto.randomBytes(32).toString('base64');
        user.email = foundEmail;
        user.name = plusPeopleMe.displayName;
        user.description = 'Novo usuário Raycast';
        user.image = plusPeopleMe.image.url;
        user.save(function(err){
            if(err) { return done(err); }
            generateAuthToken(user);
        });
    }

    function forExistantUser(user){
        log.info('AccountController authenticating user', foundEmail);
        generateAuthToken(user);
    }

    function generateAuthToken(user){
        RefreshToken.remove({ userId: user.userId, clientId: client.clientId }, function (err) {
            if (err) {
                return done(err);
            }
        });
        AccessToken.remove({ userId: user.userId, clientId: client.clientId }, function (err) {
            if (err) {
                return done(err);
            }
        });

        var tokenValue = crypto.randomBytes(32).toString('base64');
        var refreshTokenValue = crypto.randomBytes(32).toString('base64');
        var token = new AccessToken({ token: tokenValue, clientId: client.clientId, userId: user.userId });
        var refreshToken = new RefreshToken({ token: refreshTokenValue, clientId: client.clientId, userId: user.userId });
        refreshToken.save(function (err) {
            if (err) { return done(err); }
        });
        var info = { scope: '*' };
        token.save(function (err, token) {
            if (err) { return done(err); }
            done(null, tokenValue, refreshTokenValue, { 'expires_in': config.security.tokenLife });
        });
    }

}));

// token endpoint
exports.token = [
    passport.authenticate(['basic', 'oauth2-client-password'], { session: false }),
    server.token(),
    server.errorHandler()
];
