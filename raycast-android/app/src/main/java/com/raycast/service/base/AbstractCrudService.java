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

    protected final Uri rootUrl = Uri.parse("http://api.raycastapp.com/");
    protected final Uri contextUrl;

    public AbstractCrudService(String contextPath){
        contextUrl = rootUrl.buildUpon().appendPath(contextPath).build();
    }
}
