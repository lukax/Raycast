/**
 * Created by lucas on 10/4/14.
 */
var mongoose            = require('mongoose');
var faker               = require('faker');
var log                 = require('./app/util/log')(module);
var config              = require('./app/config/config');
var User                = require('./app/models/User');
var Client              = require('./app/models/Client');
var AccessToken         = require('./app/models/AccessToken');
var RefreshToken        = require('./app/models/RefreshToken');

mongoose.connect(config.mongoose.uri);

User.remove({}, function(err) {
    var user = new User({
        username: 'lucas',
        password: 'espindola',
        email: 'espdlucas@gmail.com'
    });
    user.save(function(err, user) {
        if(err) return log.error(err);
        else log.info('New user - %s:%s',user.username,user.password);
    });

    for(var i=0; i<4; i++) {
        user = new User({
            username: faker.internet.userName(),
            password: faker.internet.password(),
            email: faker.internet.email()
        });
        user.save(function(err, user) {
            if(err) return log.error(err);
            else log.info('New user - %s:%s',user.username,user.password);
        });
    }
});

Client.remove({}, function(err) {
    var client = new Client({ name: 'Android client', clientId: 'raycast', clientSecret:'android' });
    client.save(function(err, client) {
        if(err) return log.error(err);
        else log.info('New client - %s:%s',client.clientId,client.clientSecret);
    });
});
AccessToken.remove({}, function (err) {
    if (err) return log.error(err);
});
RefreshToken.remove({}, function (err) {
    if (err) return log.error(err);
});

setTimeout(function() {
    mongoose.disconnect();
}, 3000);
