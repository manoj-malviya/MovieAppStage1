package com.girnarsoft.android.movieapp;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.girnarsoft.android.tmdb.AsyncTaskListner;
import com.girnarsoft.android.tmdb.Constants;
import com.girnarsoft.android.tmdb.Movie;
import com.girnarsoft.android.tmdb.MovieAdapter;
import com.girnarsoft.android.tmdb.TMDBService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements MovieListItemClickListener {

    private boolean mTwoPane = false;
    private static final String DETAILFRAGEMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.movie_detail_container) != null) {

            mTwoPane = true;

            if(savedInstanceState == null) {
                DetailActivityFragment detail = DetailActivityFragment.getInstance(new Movie());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, detail, DETAILFRAGEMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
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
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);

            if(settingIntent.resolveActivity(getPackageManager()) != null){
                startActivity(settingIntent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {
        if(mTwoPane) {

            DetailActivityFragment detail = DetailActivityFragment.getInstance(movie);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detail)
                    .commit();

        } else {

            Intent detailIntent = new Intent(this, DetailActivity.class);
//            detailIntent.putExtra(Constants.MOVIE_ID, movie.id);
//            detailIntent.putExtra(Constants.MOVIE_NAME, movie.name);
//            detailIntent.putExtra(Constants.MOVIE_IMAGE, movie.image);
//            detailIntent.putExtra(Constants.MOVIE_OVERVIEW, movie.overview);
//            detailIntent.putExtra(Constants.MOVIE_RELEASE_DATE, movie.releaseDate);
//            detailIntent.putExtra(Constants.MOVIE_RATING, movie.rating);
            detailIntent.putExtra("movie", movie);

            startActivity(detailIntent);
        }
    }
}
