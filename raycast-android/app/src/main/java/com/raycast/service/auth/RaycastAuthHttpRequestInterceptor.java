package com.raycast.service.auth;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Created by Lucas on 09/10/2014.
 */
@EBean(scope = EBean.Scope.Singleton)
public class RaycastAuthHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Bean
    RaycastAuthStore authStore;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        headers.setAuthorization(new HttpAuthentication() {
            @Override
            public String getHeaderValue() {
                return "Bearer " + authStore.getToken().getAccessToken();
            }
        });
        return execution.execute(request, body);
    }
}
