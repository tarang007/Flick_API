package com.example.tarang.flickr_api;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted {

    ViewPager viewPager;
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    public void onTaskCompleted(List<Url_creation_> urlcreations) { // callback method run after coming in post execute method for setting
                                                                        // UI in  post execute
        Image_View_Adapter adapter = new Image_View_Adapter(this, urlcreations,mMemoryCache);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        Log.d("Changes here ","see ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager)findViewById(R.id.view_pager);

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
                new Json_Parsing(this).execute("https://api.flickr.com/services/rest/?method=flickr.photos.search&" +
                        "api_key=2acde903bb60c61430689118b7a0cdad&tags=motivation&" +
                        "text=positive&format=json&nojsoncallback=1");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
