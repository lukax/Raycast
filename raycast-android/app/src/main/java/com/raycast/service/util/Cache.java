package com.raycast.service.util;

import android.content.Context;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by lucas on 10/29/14.
 */
@EBean
public class Cache {
    public static final String TAG = "Cache";
    private static final String DB_NAME = "raycastcache";
    @RootContext Context context;
    public static enum CacheKey {
        KEY_MESSAGES,
        KEY_MESSAGEDETAIL,
        KEY_COMMENTS
    }

    public <T> T get(String key, Class<T> returnType){
        T obj = null;
        try {
            DB db = DBFactory.open(context, DB_NAME);
            obj = db.getObject(key, returnType);
            db.close();
        } catch (SnappydbException e) {
            Log.d(TAG, "Error while getting obj from db");
        }
        return obj;
    }

    public <T> void put(String key, T value){
        try{
            DB db = DBFactory.open(context, DB_NAME);
            db.put(key, value);
            db.close();
        } catch (SnappydbException e) {
            Log.e(TAG, "Error while putting obj to db", e);
        }
    }
}
