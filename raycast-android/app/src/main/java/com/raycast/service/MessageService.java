package com.raycast.service;

import android.net.Uri;
import android.util.Log;

import com.raycast.domain.Message;
import com.raycast.service.base.AbstractCrudService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lucas on 29/08/2014.
 */
public class MessageService extends AbstractCrudService {
    private final String contextPath = "message";
    private final Uri contextUrl;

    public MessageService(){
        contextUrl = rootUrl.buildUpon().appendPath(contextPath).build();
    }

    public List<Message> list(){
        try {
            final String url = contextUrl.buildUpon().appendPath("all").build().toString();
            Log.d("MessageService", "attempting request on: " + url);
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
}
