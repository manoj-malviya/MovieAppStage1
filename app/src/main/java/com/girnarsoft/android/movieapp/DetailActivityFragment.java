package com.girnarsoft.android.movieapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Movie movie = new Movie();
        Intent intent = getActivity().getIntent();

        movie.id = intent.getIntExtra(Constants.MOVIE_ID, 0);
        movie.name = intent.getStringExtra(Constants.MOVIE_NAME);
        movie.overview = intent.getStringExtra(Constants.MOVIE_OVERVIEW);
        movie.image = intent.getStringExtra(Constants.MOVIE_IMAGE);
        movie.releaseDate = intent.getStringExtra(Constants.MOVIE_RELEASE_DATE);
        movie.rating = intent.getStringExtra(Constants.MOVIE_RATING);

        getActivity().setTitle(movie.name);
        ImageView imageView = (ImageView) view.findViewById(R.id.detail_image);
        TextView nameView = (TextView) view.findViewById(R.id.detail_title);
        TextView overviewView = (TextView) view.findViewById(R.id.detail_overview);
        TextView releaseDateView = (TextView) view.findViewById(R.id.detail_release_date);
        TextView ratingView = (TextView) view.findViewById(R.id.detail_rating);

        nameView.setText(movie.name);
        overviewView.setText(movie.overview);
        releaseDateView.setText(getString(R.string.label_relase) + movie.releaseDate);
        ratingView.setText(getString(R.string.label_rating) + movie.rating);
        Picasso.with(getActivity()).load(movie.image).into(imageView);


        return view;
    }
}
