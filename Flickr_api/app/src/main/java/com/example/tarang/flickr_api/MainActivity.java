package com.example.tarang.flickr_api;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    private LruCache<String, Bitmap> mMemoryCache;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create global configuration and initialize ImageLoader with this config
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.d("Max memory"," "+maxMemory);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        Log.d("Cache Size"," "+cacheSize);
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };


//         DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//        .cacheInMemory(true)
//        .cacheOnDisk(true)
//        .build();
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
//        .defaultDisplayImageOptions(defaultOptions)
//        .build();
//        ImageLoader.getInstance().init(config); // Do it on Application start
        viewPager = (ViewPager)findViewById(R.id.view_pager);

    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    public void loadBitmap(String url, ImageView mImageView) {
        final String imageKey = String.valueOf(url);
        Log.e("In Load Bitmap ",imageKey);
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

    public class JSonParsing extends AsyncTask<String,String,List<ImageFlickr>> {
        @Override
        protected List<ImageFlickr> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String line;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect(); // connected to Http connection

                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String final_JSon = buffer.toString();

                //Log.e("Hello World",final_JSon);
                JSONObject parentObject = new JSONObject(final_JSon);
                JSONObject childObject = parentObject.getJSONObject("photos");
                JSONArray childArray = childObject.getJSONArray("photo");
/*                for(int i =0;i<childArray.length();i++){

                }*/
                List<ImageFlickr> imageFlickrList = new ArrayList<>();
                for(int i = 0 ; i < childArray.length();i++) {
                    JSONObject finalObject = childArray.getJSONObject(i);
                    ImageFlickr imageFlickr = new ImageFlickr(finalObject);
                    imageFlickrList.add(imageFlickr); //adding elements in imageFlickr list
                }
                return imageFlickrList; //this list is passed to Post Execute function


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("THis is crap ","Albatross");

            return null;
            // have done all the worker thread part
        }

        @Override
        protected void onPostExecute(List<ImageFlickr> imageFlickrs) {
            super.onPostExecute(imageFlickrs);
            if(imageFlickrs == null) {
                Log.d("Ohhooo this is wrong"," ");
            }
            // here we need to set adapter
            ImageViewerAdapter adapter = new ImageViewerAdapter(MainActivity.this,imageFlickrs);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(0);

        }
    }



    public class ImageViewerAdapter extends PagerAdapter {


        /*private List<MovieModel> movieModelList;
        private int resource;
        private LayoutInflater inflater;*/

        private List<ImageFlickr> imageFlickrList;
//        private int resource;
//        private LayoutInflater inflater;
        ImageView imageView;
        Context mContext;


        public ImageViewerAdapter(Context context, List<ImageFlickr> imageFlickrList) {
            mContext = context;
            this.imageFlickrList = imageFlickrList;
//            this.resource = resource;
//            this.inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {

            int l = imageFlickrList.size();
            Log.d("Message", " "+l);
           // Log.v("MainActivity","tarang");
            return imageFlickrList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
           // Log.e("Again here", "2");
            return view == ((View)object);
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
//convertView = inflater.inflate(resource,null);
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            View v = inflater.inflate(R.layout.swipe,container, false);
            imageView = (ImageView)v.findViewById(R.id.imageView);
            Log.e("Hello WOrld", "In the code" + position);

            loadBitmap(imageFlickrList.get(position).getUrl(),imageView);
            //Log.d("URL", imageFlickrList.get(position).getUrl());
           // ImageLoader.getInstance().displayImage(imageFlickrList.get(position).getUrl(), imageView); //when we want to
            //download images
            ((ViewPager) container).addView(v);

            //transformPage(v, position);
            return v;

        }


        //private static final float MIN_SCALE = 0.75f;

//        public void transformPage(View view, float position) {
//            int pageWidth = view.getWidth();
//            Log.d("transform", " " + position);
//            if (position < -1) { // [-Infinity,-1)
//                // This page is way off-screen to the left.
//                view.setAlpha(0);
//                Log.d("Goku","2");
//
//            } else if (position <= 0) { // [-1,0]
//                // Use the default slide transition when moving to the left page
//                view.setAlpha(0);
//                view.setTranslationX(0);
//                view.setScaleX(1);
//                view.setScaleY(1);
//                Log.d("Goku,","3");
//
//            } else if (position <= 1) { // (0,1]
//                // Fade the page out.
//                //view.setAlpha(1 - position);
//                view.setAlpha(0);
//                // Counteract the default slide transition
//                view.setTranslationX(pageWidth * -position);
//
//                // Scale the page down (between MIN_SCALE and 1)
//                float scaleFactor = MIN_SCALE
//                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
//                view.setScaleX(scaleFactor);
//                view.setScaleY(scaleFactor);
//                Log.d("Goku","4");
//
//            } else { // (1,+Infinity]
//                // This page is way off-screen to the right.
//                Log.d("Goku","5");
//                view.setAlpha(1);
//            }
//
//        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {

            Log.d("Is we are under Item","Lets see  "+position);
                    ((ViewPager) collection).removeView((View) view);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
                new JSonParsing().execute("https://api.flickr.com/services/rest/?method=flickr.photos.search&" +
                        "api_key=2acde903bb60c61430689118b7a0cdad&tags=motivation&" +
                        "text=positive&format=json&nojsoncallback=1");
            //https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=4f1f97505cac6c8e43932b9c2db835ad&tags=motivation
            // &text=positive&format=json&nojsoncallback=1&
            // auth_token=72157664280475472-4abb50613ab5a7fe&api_sig=2419fc92d3afdf73a2beab127f28529b
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
