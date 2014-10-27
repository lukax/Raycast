'use strict';

var log                     = require('../util/log')(module);
var authController          = require('../controllers/AuthController');
var oAuth2Controller        = require('../controllers/OAuth2Controller');
var messageController       = require('../controllers/MessageController');
var userController          = require('../controllers/UserController');
var commentController       = require('../controllers/CommentController');
var clientController        = require('../controllers/ClientController');
var accountController       = require('../controllers/AccountController');
var notificationsController = require('../controllers/NotificationController');

module.exports = function(router){

    router.route('/oauth/token')
        .post(oAuth2Controller.token);

    router.route('/account/login')
        .post(accountController.token);

    // -----------------------------------------------------------------------
    // AUTHENTICATION REQUIRED REQUESTS
    // -----------------------------------------------------------------------
    router.use(authController.isAuthenticated, function(req, res, next) {
        log.info('New API request');
        next();
    });

    router.get('/', function(req, res) {
        res.json({ message: 'Raycast API' });
    });

    // --------- Users ----------
    router.route('/user')
        .post(userController.addUser)
        .get(userController.getUsers);

    router.route('/user/info')
        .get(userController.getUserInfo);

    // --------- Notifications ----------
    router.route('/user/notification')
        .get(notificationsController.getNotifications)
        .delete(notificationsController.removeNotifications);

    router.route('/user/notification/:notification_id')
        .delete(notificationsController.removeNotificationById);

    router.route('/user/id/:user_id')
        .get(userController.getUserById)
        .put(userController.updateUser)
        .delete(userController.removeUser);

    router.route('/user/email/:user_email')
        .get(userController.getUserByEmail);

    router.route('/user/:user_username')
        .get(userController.getUserByUsername);

    // --------- Clients ----------
    router.route('/client')
        .post(clientController.addClient)
        .get(clientController.getClients);

    // --------- Messages ----------
    router.route('/message')
        .post(messageController.addMessage)
        .get(messageController.getAllMessageByLocation);

    router.route('/message/filter')
        .post(messageController.getAllMessageByFilter);

    router.route('/message/all')
        .get(messageController.getAllMessage);

    router.route('/message/user/:user_username')
        .get(messageController.getAllMessageByUser);

    router.route('/message/:message_id')
        .get(messageController.getMessageById)
        .delete(messageController.removeMessage);

    // --------- Comments ----------
    router.route('/message/:message_id/comment')
        .post(commentController.addComment)
        .get(commentController.getAllCommentByMessage);

    router.route('/comment/:comment_id')
        .get(commentController.getCommentById)
        .delete(commentController.removeComment);


    return router;
};
