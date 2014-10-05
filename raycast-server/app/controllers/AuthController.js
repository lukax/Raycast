'use strict';

var config                  = require('../config/config');
var User                    = require('../models/User');
var Client                  = require('../models/Client');
var AccessToken             = require('../models/AccessToken');
var RefreshToken            = require('../models/RefreshToken');
var passport                = require('passport');
var BasicStrategy           = require('passport-http').BasicStrategy;
var ClientPasswordStrategy  = require('passport-oauth2-client-password').Strategy;
var BearerStrategy          = require('passport-http-bearer').Strategy;

passport.use(new BasicStrategy(
    function(username, password, done) {
        User.findOne({ username: username }, function (err, user) {
            if (err) {
                return done(err);
            }

            if (!user) {
                return done(null, false);
            }

            if(!user.checkPassword(password)){
                // Password did not match
                return done(null, false);
            }

            return done(null, user);
        });
    }
));

passport.use(new ClientPasswordStrategy(
    function(clientId, clientSecret, done) {
        Client.findOne({ clientId: clientId }, function(err, client) {
            if (err) {
                return done(err);
            }

            if (!client) {
                return done(null, false);
            }

            if (client.clientSecret !== clientSecret) {
                return done(null, false);
            }

            return done(null, client);
        });
    }
));

passport.use(new BearerStrategy(
    function(accessToken, done) {
        AccessToken.findOne({ token: accessToken }, function(err, token) {
            if (err) {
                return done(err);
            }

            if (!token) {
                return done(null, false);
            }

            if( Math.round((Date.now()-token.created)/1000) > config.security.tokenLife ) {
                AccessToken.remove({ token: accessToken }, function (err) {
                    if (err) { return done(err); }
                });
                return done(null, false, { message: 'Token expired' });
            }

            User.findById(token.userId, function(err, user) {
                if (err) {
                    return done(err);
                }

                if (!user) {
                    return done(null, false, { message: 'Unknown user' });
                }

                var info = { scope: '*' };
                done(null, user, info);
            });
        });
    }
));

// basic auth endpoint
exports.isAuthenticated = passport.authenticate('bearer', { session : false });
