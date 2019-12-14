package io.nandandesai.smartentertainmentsystem.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;

import io.nandandesai.smartentertainmentsystem.MediaPlayerActivity;
import io.nandandesai.smartentertainmentsystem.MovieDetailsActivity;
import io.nandandesai.smartentertainmentsystem.R;
import io.nandandesai.smartentertainmentsystem.models.Movie;
import io.nandandesai.smartentertainmentsystem.utils.ErrorDialog;
import io.nandandesai.smartentertainmentsystem.utils.ImageItem;
import io.nandandesai.smartentertainmentsystem.utils.MovieDetailsFetcher;
import io.nandandesai.smartentertainmentsystem.utils.Settings;
import io.nandandesai.smartentertainmentsystem.viewadapters.GridViewAdapter;


public class MovieFragment extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ProgressBar mProgressBar;
    private TextView headingText;

    private Settings settings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movies,container,false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBarMovie);
        gridView = (GridView) view.findViewById(R.id.gridViewMovie);
        headingText=(TextView) view.findViewById(R.id.headingTextMovie);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("tmdbId", item.getTmdbId());
                startActivity(intent);
            }
        });

        settings=new Settings(getContext());

        new FetchImagesTask(null).execute();

        return view;
    }


    private ArrayList<ImageItem> getData(String searchQuery) throws IOException {
        ArrayList<ImageItem> arrayList=new ArrayList<>();
        ImageItem imageItem=null;
        MovieDetailsFetcher fetcher=new MovieDetailsFetcher();
        ArrayList<Movie> movies = null;
        if(searchQuery!=null){
            if(settings.getUseProxyForYTS()) {
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(settings.getTorProxyAddress(), settings.getTorProxyPort()));
                movies = fetcher.searchMovie(settings.getYTSSiteLink(), proxy, searchQuery);
            }else{
//                movies = fetcher.searchMovie(settings.getYTSSiteLink(), null, searchQuery);
            }
        }else {
            if(settings.getUseProxyForYTS()) {
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(settings.getTorProxyAddress(), settings.getTorProxyPort()));
                movies = fetcher.getPopularMovies(settings.getYTSSiteLink(), proxy);
            }else{
                movies = fetcher.getPopularMovies();
            }
        }

        if(movies==null){
            return null;
        }

        for(Movie movie: movies){
            if(movie.getPortraitPoster()!=null) {
                imageItem = new ImageItem(movie.getPortraitPoster().toString(), movie.getMovieId());
                arrayList.add(imageItem);
            }
        }
        return arrayList;
    }

    public class FetchImagesTask extends AsyncTask<String, Void, Void> {
        private ArrayList<ImageItem> imageItems=new ArrayList<>();
        private String searchQuery=null;
        private IOException ioException=null;
        public FetchImagesTask(String searchQuery){
            this.searchQuery=searchQuery;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                imageItems = getData(searchQuery);
                if(imageItems==null){
                    throw new IOException("There might be some problem with Tmdb API");
                }
            }catch (IOException io){
                io.printStackTrace();
                ioException=io;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if(ioException!=null){
                if(imageItems==null){
                    ErrorDialog.showDialog(getActivity(), "Tmdb API error.");
                }else {
                    ErrorDialog.showDialog(getActivity(), "There was an unexpected error. Maybe it is due to network problem or a YTS API error. You may want to use a proxy or a VPN if you think the YTS site is blocked.");
                }
                ioException.printStackTrace();
            }else{
                gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, imageItems);
                gridView.setAdapter(gridAdapter);
                gridView.setVisibility(View.VISIBLE);
                if(imageItems.size()==0){
                    Toast.makeText(getContext(), "No Movies found",Toast.LENGTH_SHORT).show();
                }
            }
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            if(searchQuery==null){
                headingText.setText("Popular movies");
            }else{
                headingText.setText("Search results");
            }
            gridView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

}