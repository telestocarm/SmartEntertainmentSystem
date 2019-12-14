package io.nandandesai.smartentertainmentsystem.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class ImageItem {
    private URL imageUrl;
    private String tmdbId;

    public ImageItem(String imageUrl,String tmdbId) {
        try{
            this.imageUrl = new URL(imageUrl);
            this.tmdbId=tmdbId;
        }catch(MalformedURLException e){
            this.imageUrl=null;
            this.tmdbId=null;
        }
    }

    public URL getImageURL() {
        return imageUrl;
    }

    public String getTmdbId() {
        return tmdbId;
    }
}
