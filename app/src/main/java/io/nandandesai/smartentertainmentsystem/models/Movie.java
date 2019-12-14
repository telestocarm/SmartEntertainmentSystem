package io.nandandesai.smartentertainmentsystem.models;

import java.net.MalformedURLException;
import java.net.URL;

public class Movie {
    private String movieId;
    private String name;
    private URL portraitPoster;
    private String overview;
    private String releasedOn;
    private String rating;

    public Movie(String movieId, String name, String imageUrl, String overview, String releasedOn, String rating) throws MalformedURLException {
        this.movieId = movieId;
        this.name=name;
        if(imageUrl==null || imageUrl.equals("null")){
            this.portraitPoster=null;
        }else{
            this.portraitPoster=new URL("https://image.tmdb.org/t/p/w600_and_h900_bestv2"+imageUrl);
        }

        this.overview=overview;
        this.releasedOn=releasedOn;
        this.rating=rating;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getName() {
        return name;
    }

    public URL getPortraitPoster() {
        return portraitPoster;
    }

    public String getOverview(){
        return overview;
    }

    public String getReleasedOn(){
        return releasedOn;
    }

    public String getRating(){
        return rating;
    }
}
