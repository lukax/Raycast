package com.raycast.service;

import android.os.AsyncTask;
import android.util.Log;

import com.raycast.domain.auth.CodeAuthCredential;
import com.raycast.domain.auth.PasswordAuthCredential;
import com.raycast.domain.auth.Token;
import com.raycast.service.auth.RaycastAuthStore;
import com.raycast.service.base.AbstractCrudService;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Lucas on 08/10/2014.
 */
@EBean(scope = EBean.Scope.Singleton)
public class AccountService extends AbstractCrudService {

    @Bean
    RaycastAuthStore authStore;

    public AccountService(){
        super("account");
    }

    public void login(final String code, final ResponseListener<Token> listener){
        try {
            new AsyncTask<Void, Void, Token>() {
                @Override
                protected Token doInBackground(Void... voids) {
                    final String url = contextUrl.buildUpon()
                            .appendPath("login")
                            .build().toString();
                    Log.d(getClass().getName(), "attempting get request on: " + url);
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    CodeAuthCredential credential = new CodeAuthCredential(code, authStore.getClientId(), authStore.getClientSecret());
                    ResponseEntity<Token> responseEntity = restTemplate.postForEntity(url, credential, Token.class);
                    Log.d(getClass().getName(), "result was: " + responseEntity.getBody().toString());
                    return responseEntity.getBody();
                }

                @Override
                protected void onPostExecute(Token token) {
                    authStore.setToken(token);
                    if(token != null) {
                        listener.onSuccess(token);
                    }
                    else{
                        listener.onFail();
                    }
                }
            }.execute();
        } catch (RestClientException ex){
            Log.e(getClass().getName(), ex.getMessage(), ex);
            listener.onFail();
        }
    }

    public void login(final String username, final String password, final ResponseListener<Token> listener){
        try {
            new AsyncTask<Void, Void, Token>() {
                @Override
                protected Token doInBackground(Void... voids) {
                    final String url = contextUrl.buildUpon()
                            .appendPath("login")
                            .build().toString();
                    Log.d(getClass().getName(), "attempting get request on: " + url);
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    PasswordAuthCredential credential = new PasswordAuthCredential(username, password, authStore.getClientId(), authStore.getClientSecret());
                    ResponseEntity<Token> responseEntity = restTemplate.postForEntity(url, credential, Token.class);
                    Log.d(getClass().getName(), "result was: " + responseEntity.getBody().toString());
                    return responseEntity.getBody();
                }

                @Override
                protected void onPostExecute(Token token) {
                    authStore.setToken(token);
                    if(token != null) {
                        listener.onSuccess(token);
                    }
                    else{
                        listener.onFail();
                    }
                }
            }.execute();
        } catch (RestClientException ex){
            Log.e(getClass().getName(), ex.getMessage(), ex);
            listener.onFail();
        }
    }

    public boolean isLoggedIn(){
        return authStore.getToken() != null;
    }
}
