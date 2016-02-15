// memory cache
package com.example.tarang.flickr_api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tarang on 11/2/16.
 */
public class MemoryCache {
    private String url;
    private ImageView mImageView;
    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCache(String url,ImageView mImageView,LruCache<String, Bitmap> mMemoryCache) {
        this.url = url;
        this.mImageView = mImageView;
        this.mMemoryCache = mMemoryCache;
    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key)
    {
        return mMemoryCache.get(key);
    }

    public void loadBitmap() {
        final String imageKey = String.valueOf(url);
        Log.e("In Load Bitmap ", imageKey);
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        } else {
            mImageView.setImageResource(R.drawable.image_placeholder);
            BitmapWorkerTask task = new BitmapWorkerTask();
            task.execute(url);
        }
    }

    class BitmapWorkerTask extends AsyncTask<String,Void,Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            HttpURLConnection connection = null;
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect(); // connected to Http connection
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
                inputStream.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return bitmap;
        }
    }


}
