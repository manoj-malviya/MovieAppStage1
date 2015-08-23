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

import com.girnarsoft.android.data.MovieContract;
import com.girnarsoft.android.tmdb.AsyncTaskListner;
import com.girnarsoft.android.tmdb.Constants;
import com.girnarsoft.android.tmdb.Movie;
import com.girnarsoft.android.tmdb.MovieAdapter;

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
            MovieContract.MovieEntry.COLUMN_IMAGE
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_IMAGE = 2;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        movies = new ArrayList<Movie>();
//        if (savedInstanceState == null || !savedInstanceState.containsKey(KEY)) {
//            retrieveMovies();
//        } else {
//            movies = savedInstanceState.getParcelableArrayList(KEY);
//        }

        movieGrid = (GridView) rootView.findViewById(R.id.movie_grid);

        adapter = new MovieAdapter(getActivity(), null, 0);
        movieGrid.setAdapter(adapter);

        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                long movieId = cursor.getLong(COL_MOVIE_ID);

                Uri uri = MovieContract.MovieEntry.buildMovieUri(movieId);

                MovieListItemClickListener listener = (MovieListItemClickListener) getActivity();

                listener.onItemSelected(uri);
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
        if (dialog != null) {
            dialog.dismiss();
        }
        isTaskRunning = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, savedInstanceState, this);
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

            new FetchMovieTask(this.getActivity(), this).execute(sortOrder);
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

        //return new Loader(getActivity(), uri, null, null, null, sortOrder);
        return new android.support.v4.content.CursorLoader(getActivity(), uri, MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}
