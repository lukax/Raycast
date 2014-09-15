package com.raycast.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Thiago on 30/08/2014.
 */
public class LoadImage extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView imageArea;

    public LoadImage(String url, ImageView imageArea) {
        this.url = url;
        this.imageArea = imageArea;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            URL path = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) path.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Bitmap image = BitmapFactory.decodeStream(inputStream);

            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        imageArea.setImageBitmap(bitmap);
    }
}
