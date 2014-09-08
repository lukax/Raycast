package com.raycast.service;

import android.net.Uri;
import android.util.Log;

import com.raycast.domain.Message;
import com.raycast.domain.util.Coordinates;
import com.raycast.service.base.AbstractCrudService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
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

    // For TESTING purposes only
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
        } catch (Exception e) {
            Log.e("MessageService", e.getMessage(), e);
        }
        return null;
    }

    public List<Message> list(Coordinates coordinates, double radius){
        try {
            final String url = contextUrl.buildUpon()
                    .appendQueryParameter("latitude", String.valueOf(coordinates.getLatitude()))
                    .appendQueryParameter("longitude", String.valueOf(coordinates.getLongitude()))
                    .appendQueryParameter("radius", String.valueOf(radius))
                    .build().toString();
            Log.d("MessageService", "attempting get request on: " + url);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<Message[]> responseEntity = restTemplate.getForEntity(url, Message[].class);
            Log.d("MessageService", "result was: " + responseEntity.getBody());
            return Arrays.asList(responseEntity.getBody());
        } catch (Exception e){
            Log.e("MessageService", e.getMessage(), e);
        }
        return null;
    }

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
        } catch (Exception e){
            Log.e("MessageService", e.getMessage(), e);
        }
        return null;
    }
}
