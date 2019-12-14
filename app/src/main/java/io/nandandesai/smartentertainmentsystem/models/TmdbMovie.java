package io.nandandesai.smartentertainmentsystem.models;

import java.net.MalformedURLException;
import java.net.URL;

public class TmdbMovie {
    private String tmdbMovieId;
    private String name;
    private URL portraitPoster;
    private URL landscapePoster;
    private String overview;
    private String releasedOn;

    public TmdbMovie(String tmdbMovieId, String name, String imageUrl, String overview, String releasedOn) throws MalformedURLException {
        this.tmdbMovieId=tmdbMovieId;
        this.name=name;
        if(imageUrl==null || imageUrl=="null"){
            this.portraitPoster=null;
            this.landscapePoster=null;
        }else{
            this.portraitPoster=new URL("https://image.tmdb.org/t/p/w600_and_h900_bestv2"+imageUrl);
            this.landscapePoster=new URL("https://image.tmdb.org/t/p/w500_and_h282_face"+imageUrl);
        }

        this.overview=overview;
        this.releasedOn=releasedOn;
    }

    public String getTmdbMovieid() {
        return tmdbMovieId;
    }

    public String getName() {
        return name;
    }

    public URL getPortraitPoster() {
        return portraitPoster;
    }

    public URL getLandscapePoster() {
        return landscapePoster;
    }

    public String getOverview(){
        return overview;
    }

    public String getReleasedOn(){
        return releasedOn;
    }
}
