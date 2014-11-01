package com.raycast.service.auth;

import android.util.Log;

import com.raycast.controller.RaycastApp;
import com.raycast.event.RaycastErrorEvent;
import com.raycast.service.AccountService;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Created by Lucas on 01/11/2014.
 */
@EBean
public class RaycastRestErrorHandler implements RestErrorHandler {
    public static final String TAG = "RaycastRestErrorHandler";

    @Bean AccountService accountService;

    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException ex) {
        Log.e(TAG, "exception handled", ex);
        if(ex.contains(HttpClientErrorException.class)){
            switch(((HttpClientErrorException)ex).getStatusCode()){
                case UNAUTHORIZED:
                    if(!accountService.isLoggedIn()){
                        RaycastErrorEvent event = new RaycastErrorEvent("Você precisa estar logado para poder usar o APP!");
                        RaycastApp.BUS.post(event);
                    }
                    break;
                }
        }
        else{
            RaycastErrorEvent event = new RaycastErrorEvent("Erro ao comunicar-se com o servidor :(");
            RaycastApp.BUS.post(event);
        }
    }
}
