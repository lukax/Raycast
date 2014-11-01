package com.raycast.repository;

import com.raycast.domain.Comment;
import com.raycast.domain.Message;
import com.raycast.domain.Notification;
import com.raycast.service.auth.RaycastAuthHttpRequestInterceptor;
import com.raycast.service.base.AbstractCrudService;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.MediaType;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

/**
 * Created by Lucas on 08/10/2014.
 */
@Rest(rootUrl = AbstractCrudService.ROOT_URL,
      converters = {MappingJackson2HttpMessageConverter.class },
      interceptors = {RaycastAuthHttpRequestInterceptor.class })
@Accept(MediaType.APPLICATION_JSON)
public interface RaycastRESTClient extends RestClientErrorHandling {

    @Get("/message?latitude={latitude}&longitude={longitude}&radius={radius}")
    List<Message> getMessages(double latitude, double longitude, double radius);

    @Post("/message")
    void addMessage(Message message);

    @Get("/message/{messageId}")
    Message getMessageById(String messageId);

    @Get("/message/{messageId}/comment")
    List<Comment> getComments(String messageId);

    @Post("/message/{messageId}/comment")
    void addComment(String messageId, Comment comment);

    @Get("/user/notification")
    List<Notification> getUserNotifications();

    @Delete("/user/notification")
    void removeUserNotifications();

    @Delete("/user/notification/{notificationId}")
    void removeUserNotification(String notificationId);
}
