package com.girnarsoft.android.movieapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.girnarsoft.android.movieapp.DetailActivity;
import com.girnarsoft.android.movieapp.R;
import com.girnarsoft.android.tmdb.AsyncTaskListner;
import com.girnarsoft.android.tmdb.Constants;
import com.girnarsoft.android.tmdb.Movie;
import com.girnarsoft.android.tmdb.MovieAdapter;
import com.girnarsoft.android.tmdb.TMDBService;

import java.util.ArrayList;

public class MainActivityFragment extends Fragment implements AsyncTaskListner<ArrayList<Movie>> {

    private final String KEY = "key";

    private MovieAdapter adapter;
    private GridView movieGrid;
    private ArrayList<Movie> movies;
    private ProgressDialog dialog;
    private boolean isTaskRunning = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

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
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.setAction(Intent.ACTION_SEND);

                detailIntent.putExtra(Constants.MOVIE_ID, movie.id);
                detailIntent.putExtra(Constants.MOVIE_NAME, movie.name);
                detailIntent.putExtra(Constants.MOVIE_IMAGE, movie.image);
                detailIntent.putExtra(Constants.MOVIE_OVERVIEW, movie.overview);
                detailIntent.putExtra(Constants.MOVIE_RELEASE_DATE, movie.releaseDate);
                detailIntent.putExtra(Constants.MOVIE_RATING, movie.rating);

                if (detailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(detailIntent);
                }
            }
        });

        return rootView;
    }

    public void onTaskStarted(){
        if(dialog == null) {
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
        // If we are returning here from a screen orientation
        // and the AsyncTask is still working, re-create and display the
        // progress dialog.
        if (isTaskRunning) {
            if(dialog == null)
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

    private void retrieveMovies(){
        if(!isTaskRunning) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String sortOrder = preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

            new FetchMovieTask(this).execute(sortOrder);
        }
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>>
    {
        AsyncTaskListner<ArrayList<Movie>> mlistner;
        public FetchMovieTask(AsyncTaskListner<ArrayList<Movie>> listner){
            this.mlistner = listner;
        }
        @Override
        protected void onPreExecute() {
            mlistner.onTaskStarted();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params){
            String sortOrder = params[0];
            try{Thread.sleep(10000);}catch (Exception e) {}
            return TMDBService.getInstance().getMovies(sortOrder);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> newMovies) {
            mlistner.onTaskFinished(newMovies);
        }
    }
}