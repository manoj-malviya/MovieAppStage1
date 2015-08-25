package com.girnarsoft.android.tmdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.girnarsoft.android.data.MovieContract;

/**
 * Created by User on 8/25/2015.
 */
public class Utility {
    public static boolean addToFavourites(Context context, Movie movie) {
        if(Utility.ifInFavourites(context, movie)) {
            return true;
        } else {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.id);
            values.put(MovieContract.MovieEntry.COLUMN_NAME, movie.name);
            values.put(MovieContract.MovieEntry.COLUMN_IMAGE, movie.image);
            values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);
            values.put(MovieContract.MovieEntry.COLUMN_RATING, movie.rating);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASEDATE, movie.releaseDate);

            Uri uri = context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
        }

        return true;
    }

    public static boolean ifInFavourites(Context context, Movie movie) {
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

        String[] selectionArgs = {
                String.valueOf(movie.id)
        };

        String[] projection = {
                MovieContract.MovieEntry.COLUMN_MOVIE_ID
        };

        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        int count = cursor.getCount();

        cursor.close();

        if (count > 0){
            return true;
        }
        return false;
    }
}
