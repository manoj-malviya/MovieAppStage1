package com.girnarsoft.android.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.CancellationSignal;

/**
 * Created by User on 8/21/2015.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher = buildUriMatcher();

    private MovieDbHelper mDbHelper;

    private static final int MOVIES = 100;
    private static final int MOVIE_DETAIL = 101;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;

    static {
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_DETAIL);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        int match = mUriMatcher.match(uri);

        switch (match) {
            case MOVIES :
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_DETAIL:
                return MovieContract.MovieEntry.COTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Uri : " + uri);

        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int match = mUriMatcher.match(uri);

        Cursor cursor;
        switch (match) {
            case MOVIES :
                cursor = getMovies(projection, selection, selectionArgs, sortOrder);
                break;
            case MOVIE_DETAIL:
                cursor = getMovieById(uri, projection);
                break;
            default:
                throw new UnsupportedOperationException("Uri : " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

                if(id>0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(id);
                } else {
                    throw new SQLException("Unable to insert row " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid match " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);

        int count = 0;
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);

                        if(id>0) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                    getContext().getContentResolver().notifyChange(uri, null);
                } finally {
                    db.endTransaction();
                }

                break;
            default:
                count = super.bulkInsert(uri, values);
        }

        return count;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);

        int rows = 0;

        switch (match) {
            case MOVIES: {
                rows = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);

        int rows = 0;

        if(selection == null) {
            selection = "1";
        }

        switch (match) {
            case MOVIES: {
                rows = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    private Cursor getMovieById(Uri uri, String[] projection) {
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        //String[] selection = new String[] {MovieContract.MovieEntry.COLUMN_MOVIE_ID};
        String[] selectionArgs = new String[] {movieId};

        return sMovieQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                "movie_id = ?",
                selectionArgs,
                null,
                null,
                null);
    }

    private Cursor getMovies(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return sMovieQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


}
