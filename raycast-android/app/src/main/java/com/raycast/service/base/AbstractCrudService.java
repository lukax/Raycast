package com.raycast.service.base;

import android.net.Uri;

/**
 * Created by Lucas on 29/08/2014.
 */
public class AbstractCrudService {
    public interface ServiceListener<TResult>{
        void OnSuccess(TResult t);
        void OnFail();
    }

    protected final Uri rootUrl = Uri.parse("http://api.raycastapp.com/");
}
