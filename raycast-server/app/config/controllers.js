/**
 * Created by lucas on 9/27/14.
 */

var log                 = require('../util/log')(module);
var authController      = require('../controllers/AuthController');
var oAuth2Controller    = require('../controllers/OAuth2Controller');
var messageController   = require('../controllers/MessageController');
var userController      = require('../controllers/UserController');
var commentController   = require('../controllers/CommentController');
var clientController    = require('../controllers/ClientController');
var accountController   = require('../controllers/AccountController');

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

    router.route('/user/id/:user_id')
        .get(userController.getUserById)
        .put(userController.updateUser)
        .delete(userController.removeUser);

    router.route('/user/:user_username')
        .get(userController.getUserByUsername);

    router.route('/user/email/:user_email')
        .get(userController.getUserByEmail);

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

    router.route('/message/:message_id')
        .get(messageController.getMessageById)
        .delete(messageController.removeMessage);

    router.route('/message/user/:user_username')
        .get(messageController.getAllMessageByUser);

    // --------- Comments ----------
    router.route('/comment')
        .post(commentController.addComment)
        .get(commentController.getAllComment);

    router.route('/comment/:comment_id')
        .get(commentController.getCommentById)
        .delete(commentController.removeComment);

    router.route('/comment/message/:message_id')
        .get(commentController.getAllCommentByMessage);


    return router;
};
