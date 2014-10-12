package com.raycast.service.base;

import android.net.Uri;

/**
 * Created by Lucas on 29/08/2014.
 */
public class AbstractCrudService {
    public interface ResponseListener<TResult>{
        void onSuccess(TResult t);
        void onFail();
    }

    public static final String ROOT_URL = "https://raycast.herokuapp.com";

    protected final Uri rootUrl = Uri.parse(ROOT_URL);
    protected final Uri contextUrl;

    public AbstractCrudService(String contextPath){
        contextUrl = rootUrl.buildUpon().appendPath(contextPath).build();
    }
}
