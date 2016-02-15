// parsing it done in this class
package com.example.tarang.flickr_api;

import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by tarang on 11/2/16.
 */
public class Json_Parsing extends AsyncTask<String,String,List<Url_creation_>> {

    @Override
    protected List<Url_creation_> doInBackground(String... params) {
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
            List<Url_creation_> urlcreationList = new ArrayList<>();
            for(int i = 0 ; i < childArray.length();i++) {
                JSONObject finalObject = childArray.getJSONObject(i);
                Url_creation_ urlcreation = new Url_creation_(finalObject);
                urlcreationList.add(urlcreation); //adding elements in urlcreation list
            }
            return urlcreationList; //this list is passed to Post Execute function


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("THis is crap ", "Albatross");

        return null;
        // have done all the worker thread part
    }
    private OnTaskCompleted listener;

    public Json_Parsing(OnTaskCompleted listener){ // for setting callback in postexecute
        this.listener=listener;

    }

    @Override
    protected void onPostExecute(List<Url_creation_> urlcreations) {
        super.onPostExecute(urlcreations);
        if(urlcreations == null) {
            Log.d("Ohhooo this is wrong"," ");
        }
        listener.onTaskCompleted(urlcreations);


    }
}
