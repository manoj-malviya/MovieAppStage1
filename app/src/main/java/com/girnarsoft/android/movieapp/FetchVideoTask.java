package com.girnarsoft.android.movieapp;

import android.os.AsyncTask;

import com.girnarsoft.android.tmdb.AsyncTaskListner;
import com.girnarsoft.android.tmdb.Movie;
import com.girnarsoft.android.tmdb.TMDBService;
import com.girnarsoft.android.tmdb.Video;

import java.util.ArrayList;

/**
 * Created by User on 8/23/2015.
 */
public class FetchVideoTask extends AsyncTask<String, Void, DetailActivityFragment.MovieVideoReviews>
{
    AsyncTaskListner<DetailActivityFragment.MovieVideoReviews> mlistner;
    public FetchVideoTask(AsyncTaskListner<DetailActivityFragment.MovieVideoReviews> listner){
        this.mlistner = listner;
    }
    @Override
    protected void onPreExecute() {
        mlistner.onTaskStarted();
    }

    @Override
    protected DetailActivityFragment.MovieVideoReviews doInBackground(String... params){
        int movieId = Integer.parseInt(params[0]);
        //try{Thread.sleep(10000);}catch (Exception e) {}
        DetailActivityFragment.MovieVideoReviews data = new DetailActivityFragment.MovieVideoReviews();
        data.videos = TMDBService.getInstance().getVideos(movieId);
        data.reviews = TMDBService.getInstance().getReviews(movieId);
        return data;
    }

    @Override
    protected void onPostExecute(DetailActivityFragment.MovieVideoReviews data) {
        mlistner.onTaskFinished(data);
    }
}