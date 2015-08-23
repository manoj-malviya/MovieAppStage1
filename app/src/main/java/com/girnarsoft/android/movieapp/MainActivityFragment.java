package com.girnarsoft.android.movieapp;

import android.support.v4.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.girnarsoft.android.data.MovieContract;
import com.girnarsoft.android.tmdb.AsyncTaskListner;
import com.girnarsoft.android.tmdb.Constants;
import com.girnarsoft.android.tmdb.Movie;
import com.girnarsoft.android.tmdb.MovieAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment implements AsyncTaskListner<ArrayList<Movie>>, SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final String KEY = "key";

    private static final int MOVIE_LOADER = 111;

    private MovieAdapter adapter;
    private GridView movieGrid;
    private ArrayList<Movie> movies;
    private ProgressDialog dialog;
    private boolean isTaskRunning = false;

    public static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_NAME,
            MovieContract.MovieEntry.COLUMN_IMAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASEDATE
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_NAME = 2;
    public static final int COL_IMAGE = 3;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_RATING = 5;
    public static final int COL_RELEASEDATE = 6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movies = new ArrayList<Movie>();
        if(savedInstanceState == null || !savedInstanceState.containsKey(KEY)) {
            retrieveMovies();
        } else {
            movies = savedInstanceState.getParcelableArrayList(KEY);
        }

        movieGrid = (GridView) rootView.findViewById(R.id.movie_grid);

        adapter = new MovieAdapter(getActivity(), R.layout.movie_grid_item, movies);
        movieGrid.setAdapter(adapter);

        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) movieGrid.getAdapter().getItem(position);

                MovieListItemClickListener listener = (MovieListItemClickListener) getActivity();

                listener.onItemSelected(movie);
            }
        });

        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);

        return rootView;
    }

    public void onTaskStarted() {
        if (dialog == null) {
            dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait a moment!");
        }
        isTaskRunning = true;
    }

    public void onTaskFinished(ArrayList<Movie> newMovies) {
        adapter.clear();
        adapter.addAll(newMovies);
        movies = newMovies;
        adapter.notifyDataSetChanged();
        if(dialog != null) {
            dialog.dismiss();
        }
        isTaskRunning = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(movies != null && movies.size()>0) {
            outState.putParcelableArrayList(KEY, movies);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getLoaderManager().initLoader(MOVIE_LOADER, savedInstanceState, this);
        // If we are returning here from a screen orientation
        // and the AsyncTask is still working, re-create and display the
        // progress dialog.
        if (isTaskRunning) {
            if (dialog == null)
                dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait a moment!");
            else
                dialog.show();
        }
    }

    @Override
    public void onDetach() {
        // All dialogs should be closed before leaving the activity in order to avoid
        // the: Activity has leaked window com.android.internal.policy... exception
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDetach();
    }

    private void retrieveMovies() {
        if (!isTaskRunning) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String sortOrder = preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

            if(sortOrder.equalsIgnoreCase(getString(R.string.pref_favourites))) {
                if(getLoaderManager().getLoader(MOVIE_LOADER) == null){
                    getLoaderManager().initLoader(MOVIE_LOADER, null, this).forceLoad();
                }else{
                    getLoaderManager().restartLoader(MOVIE_LOADER, null, this).forceLoad();
                }
            } else {
                new FetchMovieTask(this).execute(sortOrder);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        retrieveMovies();
    }

    //Loader callbacks

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;

        return new android.support.v4.content.CursorLoader(getActivity(), uri, MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            ArrayList<Movie> movies = new ArrayList<>();
            while(data.moveToNext()) {
                Movie movie = new Movie();
                movie.id = data.getInt(COL_MOVIE_ID);
                movie.name = data.getString(COL_NAME);
                movie.image = data.getString(COL_IMAGE);
                movie.overview = data.getString(COL_OVERVIEW);
                movie.rating = data.getString(COL_RATING);
                movie.releaseDate = data.getString(COL_RELEASEDATE);
                movies.add(movie);
            }
            onTaskFinished(movies);
        } else {
            Toast.makeText(getActivity(), "No Favourites available...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {}

}
