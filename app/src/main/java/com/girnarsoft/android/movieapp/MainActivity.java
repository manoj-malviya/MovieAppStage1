package com.girnarsoft.android.movieapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.girnarsoft.android.tmdb.Constants;
import com.girnarsoft.android.tmdb.Movie;
import com.girnarsoft.android.tmdb.MovieAdapter;
import com.girnarsoft.android.tmdb.TMDBService;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public static class MainActivityFragment extends Fragment {

        private MovieAdapter adapter;
        private GridView movieGrid;

        public MainActivityFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

            movieGrid = (GridView) rootView.findViewById(R.id.movie_grid);

            movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie movie = (Movie)movieGrid.getAdapter().getItem(position);
                    Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                    detailIntent.setAction(Intent.ACTION_SEND);

                    detailIntent.putExtra(Constants.MOVIE_ID, movie.id);
                    detailIntent.putExtra(Constants.MOVIE_NAME, movie.name);
                    detailIntent.putExtra(Constants.MOVIE_IMAGE, movie.image);
                    detailIntent.putExtra(Constants.MOVIE_OVERVIEW, movie.overview);
                    detailIntent.putExtra(Constants.MOVIE_RELEASE_DATE, movie.releaseDate);
                    detailIntent.putExtra(Constants.MOVIE_RATING, movie.rating);

                    if(detailIntent.resolveActivity(getActivity().getPackageManager()) != null){
                        startActivity(detailIntent);
                    }
                }
            });

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String sortOrder = preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

            new FetchMovieTask().execute(sortOrder);
        }

        public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>>
        {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(getActivity(), null, "Loading...");
            }

            @Override
            protected List<Movie> doInBackground(String... params) {
                String sortOrder = params[0];
                return TMDBService.getInstance().getMovies(sortOrder);
            }

            @Override
            protected void onPostExecute(List<Movie> movies) {

                adapter = new MovieAdapter(getActivity(), R.layout.movie_grid_item, movies);

                movieGrid.setAdapter(adapter);

                dialog.dismiss();
            }
        }
    }
}
