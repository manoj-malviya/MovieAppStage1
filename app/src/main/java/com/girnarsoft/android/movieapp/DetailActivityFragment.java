package com.girnarsoft.android.movieapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.girnarsoft.android.data.MovieContract;
import com.girnarsoft.android.tmdb.Constants;
import com.girnarsoft.android.tmdb.Movie;
import com.girnarsoft.android.tmdb.MovieAdapter;
import com.girnarsoft.android.tmdb.TMDBService;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 112;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_NAME,
            MovieContract.MovieEntry.COLUMN_IMAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASEDATE
    };

    private static final int COL_NAME = 2;
    private static final int COL_IMAGE = 3;
    private static final int COL_OVERVIEW = 4;
    private static final int COL_RATING = 5;
    private static final int COL_RELEASEDATE = 6;

    private Uri mUri;
    private ImageView mImageView;
    private TextView mNameView;
    private TextView mOverviewView;
    private TextView mReleaseDateView;
    private TextView mRatingView;

    public static DetailActivityFragment getInstance(Uri uri) {
        DetailActivityFragment detail = new DetailActivityFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("uri", uri);

        detail.setArguments(bundle);

        return detail;
    }

    public Uri getMovieId() {
        return (Uri) getArguments().getParcelable("uri");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mUri = getMovieId();

        mImageView = (ImageView) view.findViewById(R.id.detail_image);
        mNameView = (TextView) view.findViewById(R.id.detail_title);
        mOverviewView = (TextView) view.findViewById(R.id.detail_overview);
        mReleaseDateView = (TextView) view.findViewById(R.id.detail_release_date);
        mRatingView = (TextView) view.findViewById(R.id.detail_rating);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(DETAIL_LOADER, savedInstanceState, this);
    }

    //Loader callbacks

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //return new Loader(getActivity(), uri, null, null, null, sortOrder);
        return new android.support.v4.content.CursorLoader(getActivity(), mUri, MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        if(data != null && data.moveToFirst()) {
            String name = data.getString(COL_NAME);
            String image = data.getString(COL_IMAGE);
            String overview = data.getString(COL_OVERVIEW);
            float rating = data.getFloat(COL_RATING);
            String releaseDate = data.getString(COL_RELEASEDATE);

            getActivity().setTitle(name);
            mNameView.setText(name);
            mOverviewView.setText(overview);
            mReleaseDateView.setText(getString(R.string.label_relase) + releaseDate);
            mRatingView.setText(getString(R.string.label_rating) + rating);
            Picasso.with(getActivity()).load(image).into(mImageView);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }


}
