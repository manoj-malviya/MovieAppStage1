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
public class FetchVideoTask extends AsyncTask<String, Void, ArrayList<Video>>
{
    AsyncTaskListner<ArrayList<Video>> mlistner;
    public FetchVideoTask(AsyncTaskListner<ArrayList<Video>> listner){
        this.mlistner = listner;
    }
    @Override
    protected void onPreExecute() {
        mlistner.onTaskStarted();
    }

    @Override
    protected ArrayList<Video> doInBackground(String... params){
        int movieId = Integer.parseInt(params[0]);
        //try{Thread.sleep(10000);}catch (Exception e) {}
        return TMDBService.getInstance().getVideos(movieId);
    }

    @Override
    protected void onPostExecute(ArrayList<Video> videos) {
        mlistner.onTaskFinished(videos);
    }
}