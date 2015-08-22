package com.girnarsoft.android.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.girnarsoft.android.data.MovieContract;
import com.girnarsoft.android.tmdb.AsyncTaskListner;
import com.girnarsoft.android.tmdb.Movie;
import com.girnarsoft.android.tmdb.TMDBService;

import java.util.ArrayList;
import java.util.Vector;

public class FetchMovieTask extends AsyncTask<String, Void, Void>
{
    AsyncTaskListner<ArrayList<Movie>> mlistner;
    Context mContext;

    public FetchMovieTask(Context context, AsyncTaskListner<ArrayList<Movie>> listner){
        this.mlistner = listner;
        this.mContext = context;
    }
    @Override
    protected void onPreExecute() {
        mlistner.onTaskStarted();
    }

    @Override
    protected Void doInBackground(String... params){
        String sortOrder = params[0];
        //try{Thread.sleep(10000);}catch (Exception e) {}
        ArrayList<Movie> movies = TMDBService.getInstance().getMovies(sortOrder);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(movies.size());

        for(Movie movie : movies) {
            ContentValues value = new ContentValues();

            value.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.id);
            value.put(MovieContract.MovieEntry.COLUMN_NAME, movie.name);
            value.put(MovieContract.MovieEntry.COLUMN_IMAGE, movie.image);
            value.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);
            value.put(MovieContract.MovieEntry.COLUMN_RATING, movie.rating);
            value.put(MovieContract.MovieEntry.COLUMN_RELEASEDATE, movie.releaseDate);

            cVVector.add(value);
        }

        if(cVVector.size()>0){
            mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,
                    cVVector.toArray(new ContentValues[]{}));
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mlistner.onTaskFinished(null);
    }
}