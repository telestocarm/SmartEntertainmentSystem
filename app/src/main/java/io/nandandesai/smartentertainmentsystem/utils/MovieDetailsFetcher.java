package io.nandandesai.smartentertainmentsystem.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.ArrayList;

import io.nandandesai.smartentertainmentsystem.models.Movie;
import io.nandandesai.smartentertainmentsystem.models.Movie;


public class MovieDetailsFetcher {
    //7bed543d4ed59bd99d51ffa115965468
    //81266
    //1399
    //Potrait HD image: https://image.tmdb.org/t/p/w600_and_h900_bestv2/
    //Landscape image: https://image.tmdb.org/t/p/w500_and_h282_face/


    public MovieDetailsFetcher(){

    }

    public ArrayList<Movie> searchMovie(String yts, Proxy proxy, String query) throws IOException{
        String json=ContentFetcher.getContentFromUrl(yts+"/api/v2/list_movies.json?query_term=citizenfour"+URLEncoder.encode(query,"UTF-8"), null);
        if(json!=null){
            ArrayList<Movie> movieArrayList=new ArrayList<>();
            Movie movie;
            ObjectMapper objectMapper=new ObjectMapper();
            JsonNode jsonNode=objectMapper.readValue(json, JsonNode.class);
            JsonNode resultsArray=jsonNode.get("movies");
            for(int i=0; i<resultsArray.size(); i++){
                JsonNode resultsJson=resultsArray.get(i);
                String movieId=resultsJson.get("id").asText();
                String title=resultsJson.get("title").asText();
                String imageUrl=resultsJson.get("medium_cover_image").asText();
                String overview=resultsJson.get("summary").asText();
                String releasedOn=resultsJson.get("year").asText();
                String rating=resultsJson.get("rating").asText();
                movie =new Movie(movieId, title, imageUrl, overview, releasedOn,rating);
                movieArrayList.add(movie);
            }
            return movieArrayList;
        }
        return null;
    }


    public Movie getMovieInfo(String tmdbId) throws IOException{
        String json=ContentFetcher.getContentFromUrl("https://api.themoviedb.org/3/movie/"+tmdbId+"?api_key="+"7bed543d4ed59bd99d51ffa115965468"+"&language=en-US", null);
        if(json!=null) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode resultsJson = objectMapper.readValue(json, JsonNode.class);
            String tmdbMovieId = resultsJson.get("id").asText();
            String title = resultsJson.get("title").asText();
            String imageUrl = resultsJson.get("poster_path").asText();
            String overview = resultsJson.get("overview").asText();
            String releasedOn=resultsJson.get("release_date").asText();
            return new Movie(tmdbMovieId, title, imageUrl, overview, releasedOn,"");
        }else {
            return null;
        }
    }

    public ArrayList<Movie> getPopularMovies(String yts, Proxy proxy) throws IOException{
        String json=ContentFetcher.getContentFromUrl(yts+"/api/v2/list_movies.json?sort_by=year&limit=10", proxy);
        if(json!=null){
            ArrayList<Movie> movieArrayList=new ArrayList<>();
            Movie movie;
            ObjectMapper objectMapper=new ObjectMapper();
            System.out.println(json);
            JsonNode jsonNode=objectMapper.readValue(json, JsonNode.class);
            JsonNode dataNode=jsonNode.get("data");
            JsonNode resultsArray=dataNode.get("movies");
            for(int i=0; i<resultsArray.size(); i++){
                JsonNode resultsJson=resultsArray.get(i);
                String movieId=resultsJson.get("id").asText();
                String title=resultsJson.get("title").asText();
                String imageUrl=resultsJson.get("medium_cover_image").asText();
                String overview=resultsJson.get("summary").asText();
                String releasedOn=resultsJson.get("year").asText();
                String rating=resultsJson.get("rating").asText();
                movie =new Movie(movieId, title, imageUrl, overview, releasedOn,rating);
                movieArrayList.add(movie);
            }
            return movieArrayList;
        }
        return null;
    }

    public ArrayList<Movie> getPopularMovies() throws IOException{
        String json=ContentFetcher.getContentFromUrl("https://api.themoviedb.org/3/movie/popular?api_key="+"7bed543d4ed59bd99d51ffa115965468"+"&language=en-US&page=1", null);
        if(json!=null){
            ArrayList<Movie> movieArrayList=new ArrayList<>();
            Movie tmdbMovie;

            ObjectMapper objectMapper=new ObjectMapper();
            JsonNode jsonNode=objectMapper.readValue(json, JsonNode.class);
            JsonNode resultsArray=jsonNode.get("results");
            for(int i=0; i<resultsArray.size(); i++){
                JsonNode resultsJson=resultsArray.get(i);
                String tmdbMovieId=resultsJson.get("id").asText();
                String title=resultsJson.get("title").asText();
                String imageUrl=resultsJson.get("poster_path").asText();
                String overview=resultsJson.get("overview").asText();
                String releasedOn=resultsJson.get("release_date").asText();
                tmdbMovie=new Movie(tmdbMovieId, title, imageUrl, overview, releasedOn,"");
                movieArrayList.add(tmdbMovie);
            }
            return movieArrayList;
        }
        return null;
    }

}
