package com.raycast.service;

import android.os.AsyncTask;
import android.util.Log;

import com.raycast.domain.auth.Token;
import com.raycast.domain.svo.RaycastMessageSVO;
import com.raycast.service.auth.RaycastAuthStore;
import com.raycast.service.base.AbstractCrudService;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
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

    public boolean login(final String code){
        if(code == null){
            return false;
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            MultiValueMap<String, String> credendial = new LinkedMultiValueMap<String, String>();
            credendial.add("grant_type", "authorization_code");
            credendial.add("code", code);
            credendial.add("client_id", authStore.getClientId());
            credendial.add("client_secret", authStore.getClientSecret());
            ResponseEntity<Token> responseEntity = restTemplate.postForEntity(ROOT_URL + "/account/login", credendial, Token.class);
            Token token = responseEntity.getBody();
            if(token != null){
                authStore.setToken(token);
                return true;
            }
        } catch (Exception ex){
            Log.e(getClass().getName(), ex.getMessage(), ex);
        }
        return false;
    }

    public boolean login(final String username, final String password){
        if(username == null || password == null){
            return false;
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            MultiValueMap<String, String> credendial = new LinkedMultiValueMap<String, String>();
            credendial.add("grant_type", "password");
            credendial.add("username", username);
            credendial.add("password", password);
            credendial.add("client_id", authStore.getClientId());
            credendial.add("client_secret", authStore.getClientSecret());
            ResponseEntity<Token> responseEntity = restTemplate.postForEntity(ROOT_URL + "/oauth/token", credendial, Token.class);
            Token token = responseEntity.getBody();
            if(token != null){
                authStore.setToken(token);
            }
            return true;
        }
        catch (Exception ex){
            Log.e(getClass().getName(), ex.getMessage(), ex);
        }
        return false;
    }

    public boolean isLoggedIn(){
        if(authStore.getToken() == null) {
            return false;
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            headers.add("Authorization", authStore.getToken().getAuthorizationHeader());
            HttpEntity httpEntity = new HttpEntity(headers);
            ResponseEntity<RaycastMessageSVO> responseEntity = restTemplate.exchange(ROOT_URL, HttpMethod.GET, httpEntity, RaycastMessageSVO.class);
            return (responseEntity.getBody() != null);
        }
        catch(HttpClientErrorException ex){
            //Forbidden
            Log.d(getClass().getName(), "access forbidden", ex);
            Token newToken = exchangeRefreshToken();
            if(newToken != null){
                authStore.setToken(newToken);
                Log.d(getClass().getName(), "successfully exchanged the refresh token");
                return true;
            }
            return false;
        }
        catch(Exception ex) {
            Log.e(getClass().getName(), ex.getMessage(), ex);
            return false;
        }
    }

    private Token exchangeRefreshToken(){
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            MultiValueMap<String, String> credendial = new LinkedMultiValueMap<String, String>();
            credendial.add("grant_type", "refresh_token");
            credendial.add("refresh_token", authStore.getToken().getRefreshToken());
            credendial.add("client_id", authStore.getClientId());
            credendial.add("client_secret", authStore.getClientSecret());
            ResponseEntity<Token> responseEntity = restTemplate.postForEntity(ROOT_URL + "/oauth/token", credendial, Token.class);
            return responseEntity.getBody();
        }
        catch(Exception ex){
            Log.e(getClass().getName(), "error while trying to exchange refresh token", ex);
        }
        return null;
    }

}
