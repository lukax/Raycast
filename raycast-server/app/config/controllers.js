/**
 * Created by lucas on 9/27/14.
 */

module.exports = function(router){
    var authController = require('../controllers/AuthController');
    var messageController = require('../controllers/MessageController');
    var userController = require('../controllers/UserController');
    var commentController = require('../controllers/CommentController');

    router.route('/message')
        .post(authController.isAuthenticated, messageController.addMessage)
        .get(messageController.listMessageByLocation);

    router.route('/message/filter')
        .post(messageController.listMessageByFilter);

    router.route('/message/all')
        .get(messageController.listAllMessage);

    router.route('/message/:message_id')
        .get(messageController.getMessageById)
        .delete(messageController.removeMessage);

    router.route('/message/user/:user_username')
        .get(messageController.listMessageByUser);

    userController(router);

    commentController(router);

    return router;
};
