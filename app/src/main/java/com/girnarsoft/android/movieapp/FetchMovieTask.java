package com.girnarsoft.android.movieapp;

import android.os.AsyncTask;

import com.girnarsoft.android.tmdb.AsyncTaskListner;
import com.girnarsoft.android.tmdb.Movie;
import com.girnarsoft.android.tmdb.TMDBService;

import java.util.ArrayList;

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
        //try{Thread.sleep(10000);}catch (Exception e) {}
        return TMDBService.getInstance().getMovies(sortOrder);
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> newMovies) {
        mlistner.onTaskFinished(newMovies);
    }
}