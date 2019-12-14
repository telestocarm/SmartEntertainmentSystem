package io.nandandesai.smartentertainmentsystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.SSLProtocolException;

import at.markushi.ui.CircleButton;
import io.nandandesai.smartentertainmentsystem.models.Movie;
import io.nandandesai.smartentertainmentsystem.utils.BlurImage;
import io.nandandesai.smartentertainmentsystem.utils.ErrorDialog;
import io.nandandesai.smartentertainmentsystem.utils.MovieDetailsFetcher;
import io.nandandesai.smartentertainmentsystem.utils.Settings;


public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView moviePoster;
    private TextView movieTitleText;
    private TextView movieSummaryText;
    private TextView releasedOnText;
    private ProgressBar progressBar;
    //this 'target' variable is related to Picasso. Used in blurring the background img of this activity
    private Target target;

    private String tmdbId;
    private Movie movie = null;
    private CircleButton playButton;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        moviePoster = (ImageView) findViewById(R.id.movieposter);
        movieTitleText = (TextView) findViewById(R.id.movietitle);
        movieSummaryText = (TextView) findViewById(R.id.moviesummary);
        releasedOnText = (TextView) findViewById(R.id.releasedon);
        progressBar = (ProgressBar) findViewById(R.id.movieprogressBar);
        playButton=findViewById(R.id.playButton);

        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                moviePoster.setImageBitmap(BlurImage.fastblur(bitmap, 1f, 70));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                moviePoster.setImageResource(R.mipmap.ic_launcher);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        tmdbId = getIntent().getStringExtra("tmdbId");
        settings = new Settings(this);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetailsActivity.this, MediaPlayerActivity.class);
                startActivity(intent);
            }
        });

        new FetchMovieDataTask().execute();

    }


    private String getProperDateFormat(String oldDate) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String newDate = null;
        try {
            date = inputFormat.parse(oldDate);
            newDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return oldDate;
        }
        return newDate;
    }

    private int getYear(String releasedOn) {
        try {
            String[] splittedDate = releasedOn.split("-");
            return Integer.parseInt(splittedDate[0]);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return 0;
        }
    }


    private class FetchMovieDataTask extends AsyncTask<Void, Void, Void> {
        private IOException ioException = null;
        private SSLProtocolException sslError = null;
        private SocketException socketException = null;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                movie=new MovieDetailsFetcher().getMovieInfo(tmdbId);
            } catch (IOException e) {
                ioException=e;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (ioException != null) {
                ErrorDialog.showDialog(MovieDetailsActivity.this, "Unexpected error occurred.");
                ioException.printStackTrace();
            } else if (sslError != null) {
                ErrorDialog.showDialog(MovieDetailsActivity.this, "There is a problem in establishing SSL connection. If you think the site is blocked, then try using Tor proxy.");
            } else if (socketException != null) {
                ErrorDialog.showDialog(MovieDetailsActivity.this, "Problem with connecting to Tor proxy. Make sure the proxy is running and you have set the host and port correctly in the settings.");
            }

            if (movie != null) {
                movieTitleText.setText(movie.getName());
                movieSummaryText.setText(movie.getOverview());
                releasedOnText.setText("Released on: " + getProperDateFormat(movie.getReleasedOn()));
                Picasso.get().load(movie.getPortraitPoster().toString()).into(target);
                playButton.setVisibility(View.VISIBLE);
                //listViewAdapter = new ListViewAdapter(MovieDetailsActivity.this, R.id.torrentlistMovie, torrents);

            } else {
                ErrorDialog.showDialog(MovieDetailsActivity.this, "Failed to fetch Movie details.");
            }
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.INVISIBLE);
        }
    }
}
