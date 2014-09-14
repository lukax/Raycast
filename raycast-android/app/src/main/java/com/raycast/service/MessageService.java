package com.raycast.service;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.raycast.domain.Message;
import com.raycast.domain.util.Coordinates;
import com.raycast.service.base.AbstractCrudService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lucas on 29/08/2014.
 */
public class MessageService extends AbstractCrudService {
    private final String contextPath = "message";
    private final Uri contextUrl;

    public MessageService(){
        contextUrl = rootUrl.buildUpon().appendPath(contextPath).build();
    }

    /*
        LIST all messages with no filtering from API
     */
    public List<Message> list(){
        try {
            final String url = contextUrl.buildUpon().appendPath("all")
                    .build().toString();
            Log.d("MessageService", "attempting get request on: " + url);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<Message[]> responseEntity = restTemplate.getForEntity(url, Message[].class);
            Log.d("MessageService", "result was: " + responseEntity.getBody());
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException ex) {
            Log.e("MessageService", ex.getMessage(), ex);
        }
        return null;
    }

    /*
        LIST all messages from API being withing the specified radius of the coordinates
     */
    public List<Message> list(Location location, float radius){
        try {
            final String url = contextUrl.buildUpon()
                    .appendQueryParameter("latitude", String.valueOf(location.getLatitude()))
                    .appendQueryParameter("longitude", String.valueOf(location.getLongitude()))
                    .appendQueryParameter("radius", String.valueOf(radius))
                    .build().toString();
            Log.d("MessageService", "attempting get request on: " + url);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<Message[]> responseEntity = restTemplate.getForEntity(url, Message[].class);
            Log.d("MessageService", "result was: " + responseEntity.getBody());
            return Arrays.asList(responseEntity.getBody());
        } catch (RestClientException ex){
            Log.e("MessageService", ex.getMessage(), ex);
        }
        return null;
    }

    /*
        ADD a new Message to API
     */
    public Message add(Message msg){
        try {
            final String url = contextUrl.buildUpon()
                    .build().toString();
            Log.d("MessageService", "attempting post request on: " + url);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<Message> responseEntity = restTemplate.postForEntity(url, msg, Message.class);
            Log.d("MessageService", "result was: " + responseEntity.getBody());
            return responseEntity.getBody();
        } catch (RestClientException ex){
            Log.e("MessageService", ex.getMessage(), ex);
        }
        return null;
    }

    /*
        GET a Message from API by it's ID
     */
    public Message get(String id){
        try{
            final String url = contextUrl.buildUpon()
                    .appendPath(id)
                    .build().toString();
            Log.d("MessageService", "attempting get request on: " + url);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<Message> responseEntity = restTemplate.getForEntity(url, Message.class);
            Log.d("MessageService", "result was: " + responseEntity.getBody());
            return responseEntity.getBody();
        } catch (RestClientException ex){
            Log.e("MessageService", ex.getMessage(), ex);
        }
        return null;
    }
}
