// setting of pageADAPTER
package com.example.tarang.flickr_api;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by tarang on 11/2/16.
 */
public class Image_View_Adapter extends PagerAdapter {
    private List<Url_creation_> urlcreationList;
    private ImageView imageView;
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    public Image_View_Adapter(Context mContext,List<Url_creation_> urlcreationList,LruCache<String, Bitmap> mMemoryCache) {
        this.urlcreationList = urlcreationList;
        this.mContext = mContext;
        this.mMemoryCache = mMemoryCache;
    }



    @Override
    public int getCount() {
        return urlcreationList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (View)object);
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View v = inflater.inflate(R.layout.swipe, container, false);
        imageView = (ImageView)v.findViewById(R.id.imageView);
        MemoryCache memoryCache = new MemoryCache(urlcreationList.get(position).getUrl(),imageView,mMemoryCache);
        memoryCache.loadBitmap();
        ((ViewPager) container).addView(v);
        return v;
    }
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {

        Log.d("Is we are under Item", "Lets see  " + position);
        ((ViewPager) collection).removeView((View) view);
    }
}
