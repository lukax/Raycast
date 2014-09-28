/**
 * Created by lucas on 9/27/14.
 */

var config                  = require('../config/config');
var User                    = require('../models/User');
var Client                  = require('../models/oauth/Client');
var AccessToken             = require('../models/oauth/AccessToken');
var RefreshToken            = require('../models/oauth/RefreshToken');
var passport                = require('passport');
var BasicStrategy           = require('passport-http').BasicStrategy;
var ClientPasswordStrategy  = require('passport-oauth2-client-password').Strategy;
var BearerStrategy          = require('passport-http-bearer').Strategy;

passport.use(new BasicStrategy(
    function(username, password, callback) {
        User.findOne({ username: username }, function (err, user) {
            if (err) { return callback(err); }

            // No user found with that username
            if (!user) { return callback(null, false); }

            // Make sure the password is correct
            user.verifyPassword(password, function(err, isMatch) {
                if (err) { return callback(err); }

                // Password did not match
                if (!isMatch) { return callback(null, false); }

                // Success
                return callback(null, user);
            });
        });
    }
));

passport.use(new ClientPasswordStrategy(
    function(clientId, clientSecret, done) {
        Client.findOne({ clientId: clientId }, function(err, client) {
            if (err) { return done(err); }
            if (!client) { return done(null, false); }
            if (client.clientSecret != clientSecret) { return done(null, false); }

            return done(null, client);
        });
    }
));

passport.use(new BearerStrategy(
    function(accessToken, done) {
        AccessToken.findOne({ token: accessToken }, function(err, token) {
            if (err) { return done(err); }
            if (!token) { return done(null, false); }

            if( Math.round((Date.now()-token.created)/1000) > config.get('security:tokenLife') ) {
                AccessToken.remove({ token: accessToken }, function (err) {
                    if (err) return done(err);
                });
                return done(null, false, { message: 'Token expired' });
            }

            User.findById(token.userId, function(err, user) {
                if (err) { return done(err); }
                if (!user) { return done(null, false, { message: 'Unknown user' }); }

                var info = { scope: '*' }
                done(null, user, info);
            });
        });
    }
));

exports.isAuthenticated = passport.authenticate('basic', { session : false });
