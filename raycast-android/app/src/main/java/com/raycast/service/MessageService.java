package com.raycast.service;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.raycast.domain.Message;
import com.raycast.service.base.AbstractCrudService;
import com.raycast.service.base.RaycastRESTClient;

import org.androidannotations.annotations.rest.RestService;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lucas on 29/08/2014.
 */
public class MessageService extends AbstractCrudService {

    public MessageService(){
        super("message");
    }

    /*
        LIST all messages from API being withing the specified radius of the coordinates
     */
    public void list(final Location location, final float radius, final ResponseListener<List<Message>> listener){
        try {
            new AsyncTask<Void, Void, List<Message>>() {
                @Override
                protected List<Message> doInBackground(Void... voids) {
                    final String url = contextUrl.buildUpon()
                            .appendQueryParameter("latitude", String.valueOf(location.getLatitude()))
                            .appendQueryParameter("longitude", String.valueOf(location.getLongitude()))
                            .appendQueryParameter("radius", String.valueOf(radius))
                            .build().toString();
                    Log.d(getClass().getName(), "attempting get request on: " + url);
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    ResponseEntity<Message[]> responseEntity = restTemplate.getForEntity(url, Message[].class);
                    Log.d(getClass().getName(), "result was: " + responseEntity.getBody().toString());
                    return Arrays.asList(responseEntity.getBody());
                }

                @Override
                protected void onPostExecute(List<Message> messages) {
                    if(messages != null) {
                        listener.onSuccess(messages);
                    }
                    else{
                        listener.onFail();
                    }
                }
            }.execute();
        } catch (RestClientException ex){
            Log.e("MessageService", ex.getMessage(), ex);
            listener.onFail();
        }
    }

    /*
        ADD a new Message to API
     */
    public void add(final Message msg, final ResponseListener<Message> listener){
        try {
            new AsyncTask<Void, Void, Message>() {
                @Override
                protected Message doInBackground(Void... voids) {
                    final String url = contextUrl.buildUpon()
                            .build().toString();
                    Log.d(getClass().getName(), "attempting post request on: " + url);
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    ResponseEntity<Message> responseEntity = restTemplate.postForEntity(url, msg, Message.class);
                    Log.d(getClass().getName(), "result was: " + responseEntity.getBody());
                    return responseEntity.getBody();
                }

                @Override
                protected void onPostExecute(Message message) {
                    if(message != null){
                        listener.onSuccess(message);
                    }else{
                        listener.onFail();
                    }
                }
            }.execute();
        } catch (RestClientException ex){
            Log.e(getClass().getName(), ex.getMessage(), ex);
            listener.onFail();
        }
    }

    /*
        GET a Message from API by it's ID
     */
    public void get(final String id, final ResponseListener<Message> listener){
        try{
            new AsyncTask<Void, Void, Message>() {
                @Override
                protected Message doInBackground(Void... voids) {
                    final String url = contextUrl.buildUpon()
                            .appendPath(id)
                            .build().toString();
                    Log.d(getClass().getName(), "attempting get request on: " + url);
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    ResponseEntity<Message> responseEntity = restTemplate.getForEntity(url, Message.class);
                    Log.d(getClass().getName(), "result was: " + responseEntity.getBody());
                    return responseEntity.getBody();
                }

                @Override
                protected void onPostExecute(Message message) {
                    if(message != null){
                        listener.onSuccess(message);
                    }else{
                        listener.onFail();
                    }
                }
            }.execute();

        } catch (RestClientException ex){
            Log.e(getClass().getName(), ex.getMessage(), ex);
            listener.onFail();
        }
    }
}
