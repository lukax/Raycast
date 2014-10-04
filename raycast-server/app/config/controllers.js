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


    router.route('/clients')
        .post(authController.isAuthenticated, clientController.addClient)
        .get(authController.isAuthenticated, clientController.getClients);


    userController(router);

    return router;
};
