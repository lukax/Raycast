package com.raycast.service;

import com.raycast.domain.Comment;
import com.raycast.event.MessageDetailFetchedEvent;
import com.raycast.event.MessagesFetchedEvent;
import com.raycast.event.base.RaycastEvent;
import com.raycast.repository.RaycastRESTClient;
import com.raycast.service.auth.RaycastRestErrorHandler;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.rest.RestService;

import static com.raycast.controller.RaycastApp.BUS;

/**
 * Created by lucas on 10/29/14.
 *
 * Utilizes the EventBus to emit the results
 * Handles errors
 */
@EBean(scope = EBean.Scope.Singleton)
public class RaycastService {
    public static final String NETWORK = "NETWORK";
    public static final String CACHE = "CACHE";
    
    @Bean Cache cache;
    @Bean RaycastRestErrorHandler errorHandler;
    @RestService RaycastRESTClient restClient;

    @AfterInject
    void afterInject(){
        restClient.setRestErrorHandler(errorHandler);
    }

    @Background(serial = CACHE)
    public void getMessages(double latitude, double longitude, double radius){
        postIfPresent(Cache.CacheKey.KEY_MESSAGES + "", MessagesFetchedEvent.class);
        getMessagesAsync(latitude, longitude, radius);
    }

    @Background(serial = CACHE)
    public void getMessageDetail(String messageId){
        postIfPresent(Cache.CacheKey.KEY_MESSAGEDETAIL + messageId, MessageDetailFetchedEvent.class);
        getMessageDetailAsync(messageId);
    }

    @Background(serial = NETWORK)
    public void addComment(String messageId, Comment comment){
        restClient.addComment(messageId, comment);
    }

    @Background(serial = NETWORK)
    void getMessagesAsync(double latitude, double longitude, double radius) {
        //TODO exception handling
        //TODO 401 status code handling
        cacheThenPost(Cache.CacheKey.KEY_MESSAGES + "", new MessagesFetchedEvent(restClient.getMessages(latitude, longitude, radius)));
    }

    @Background(serial = NETWORK)
    void getMessageDetailAsync(String messageId){
        //TODO exception handling
        //TODO 401 status code handling
        cacheThenPost(Cache.CacheKey.KEY_MESSAGEDETAIL + messageId, new MessageDetailFetchedEvent(restClient.getMessageById(messageId), restClient.getComments(messageId)));
    }

    //----------

    private <T extends RaycastEvent> void postIfPresent(String key, Class<T> eventClass){
        // Try to load the existing cache
        T cachedResult = cache.get(key, eventClass);
        // If there's something in cache, send the event
        if (cachedResult != null) BUS.post(cachedResult);
    }

    private void cacheThenPost(String key, RaycastEvent event){
        // Then load from server, asynchronously
        // Store the event in cache (replace existing if any)
        cache.put(key, event);
        // Post the event
        BUS.post(event);
    }
}
