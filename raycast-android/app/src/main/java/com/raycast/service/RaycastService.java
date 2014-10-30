package com.raycast.service;

import com.raycast.controller.RaycastApp;
import com.raycast.domain.Message;
import com.raycast.event.MessagesFetchedEvent;
import com.raycast.service.base.RaycastRESTClient;
import com.raycast.service.util.Cache;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.rest.RestService;

import java.util.List;

import static com.raycast.controller.RaycastApp.BUS;

/**
 * Created by lucas on 10/29/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class RaycastService {
    public static final String NETWORK = "NETWORK";
    public static final String CACHE = "CACHE";

    @Bean Cache cache;
    @RestService RaycastRESTClient restClient;

    @Background(serial = CACHE)
    public void getMessages(double latitude, double longitude, double radius){
        // Try to load the existing cache
        MessagesFetchedEvent cachedResult =
                cache.get(Cache.CacheKey.KEY_MESSAGES, MessagesFetchedEvent.class);

        // If there's something in cache, send the event
        if (cachedResult != null) BUS.post(cachedResult);

        // Then load from server, asynchronously
        getMessagesAsync(latitude, longitude, radius);
    }

    @Background(serial = NETWORK)
    public void getMessagesAsync(double latitude, double longitude, double radius) {
        // Fetch the contacts (network access)
        //TODO exception handling
        //TODO 401 status code handling
        List<Message> messages = restClient.getMessages(latitude, longitude, radius);

        // Create the resulting event
        MessagesFetchedEvent event = new MessagesFetchedEvent(messages);

        // Store the event in cache (replace existing if any)
        cache.put(Cache.CacheKey.KEY_MESSAGES, event);

        // Post the event
        BUS.post(event);
    }
}
