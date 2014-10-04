/**
 * Created by lucas on 9/27/14.
 */

module.exports = function(router){
    var authController = require('../controllers/AuthController');
    var messageController = require('../controllers/MessageController');
    var userController = require('../controllers/UserController');
    var commentController = require('../controllers/CommentController');
    var clientController = require('../controllers/ClientController');


    router.route('/message')
        .post(authController.isAuthenticated, messageController.addMessage)
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


    router.route('/comment')
        .post(authController.isAuthenticated, commentController.addComment)
        .get(commentController.getAllComment);

    router.route('/comment/:comment_id')
        .get(commentController.getCommentById)
        .delete(commentController.removeComment);

    router.route('/comment/message/:message_id')
        .get(commentController.getAllCommentByMessage);


    router.route('/user')
        .post(userController.addUser)
        .get(userController.getUsers);

    router.route('/user/id/:user_id')
        .get(userController.getUserById)
        .put(userController.updateUser)
        .delete(userController.removeUser);

    router.route('/user/:user_username')
        .get(userController.getUserByUsername);

    router.route('/user/email/:user_email')
        .get(userController.getUserByEmail);


    router.route('/clients')
        .post(authController.isAuthenticated, clientController.addClient)
        .get(authController.isAuthenticated, clientController.getClients);


    return router;
};
