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
public class DetailActivityFragment extends Fragment {

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

    private Movie mMovie;
    private ImageView mImageView;
    private TextView mNameView;
    private TextView mOverviewView;
    private TextView mReleaseDateView;
    private TextView mRatingView;

    public static DetailActivityFragment getInstance(Movie movie) {
        DetailActivityFragment detail = new DetailActivityFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", movie);

        detail.setArguments(bundle);

        return detail;
    }

    public Movie getMovieId() {
        return (Movie) getArguments().getParcelable("movie");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mMovie = getMovieId();

        mImageView = (ImageView) view.findViewById(R.id.detail_image);
        mNameView = (TextView) view.findViewById(R.id.detail_title);
        mOverviewView = (TextView) view.findViewById(R.id.detail_overview);
        mReleaseDateView = (TextView) view.findViewById(R.id.detail_release_date);
        mRatingView = (TextView) view.findViewById(R.id.detail_rating);

        getActivity().setTitle(mMovie.name);
        mNameView.setText(mMovie.name);
        mOverviewView.setText(mMovie.overview);
        mReleaseDateView.setText(getString(R.string.label_relase) + mMovie.releaseDate);
        mRatingView.setText(getString(R.string.label_rating) + mMovie.rating);
        Picasso.with(getActivity()).load(mMovie.image).into(mImageView);

        return view;
    }
}
