package com.example.tarang.flickr_api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tarang on 4/2/16.
 */
public class ImageFlickr  {

    private String id;
    private String secret;
    private String server;
    private String farm;
    private String url;

    public ImageFlickr(JSONObject finalObject){
        try {
            this.id = (finalObject.getString("id"));
            this.farm = (finalObject.getString("farm"));
            this.secret = (finalObject.getString("secret"));
            this.server = (finalObject.getString("server"));
            generate_url();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String generate_url() {

        /* https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg */
        url = "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+".jpg";
        return url;
    }

    public String getUrl() {
        return url;
    }
}
