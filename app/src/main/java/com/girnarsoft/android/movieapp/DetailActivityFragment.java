package com.girnarsoft.android.movieapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.girnarsoft.android.tmdb.AsyncTaskListner;
import com.girnarsoft.android.tmdb.Movie;
import com.girnarsoft.android.tmdb.Video;
import com.girnarsoft.android.tmdb.VideoAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements AsyncTaskListner<ArrayList<Video>> {

    private static final int DETAIL_LOADER = 112;

//    private static final String[] MOVIE_COLUMNS = {
//            MovieContract.MovieEntry._ID,
//            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
//            MovieContract.MovieEntry.COLUMN_NAME,
//            MovieContract.MovieEntry.COLUMN_IMAGE,
//            MovieContract.MovieEntry.COLUMN_OVERVIEW,
//            MovieContract.MovieEntry.COLUMN_RATING,
//            MovieContract.MovieEntry.COLUMN_RELEASEDATE
//    };
//
//    private static final int COL_NAME = 2;
//    private static final int COL_IMAGE = 3;
//    private static final int COL_OVERVIEW = 4;
//    private static final int COL_RATING = 5;
//    private static final int COL_RELEASEDATE = 6;

    private Movie mMovie;
    private ImageView mImageView;
    private TextView mNameView;
    private TextView mOverviewView;
    private TextView mReleaseDateView;
    private TextView mRatingView;
    private ListView mVideoListView;

    private VideoAdapter mVideoAdapter;

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

        mVideoListView = (ListView) view.findViewById(R.id.video_list_view);

        getActivity().setTitle(mMovie.name);
        mNameView.setText(mMovie.name);
        mOverviewView.setText(mMovie.overview);
        mReleaseDateView.setText(getString(R.string.label_relase) + mMovie.releaseDate);
        mRatingView.setText(getString(R.string.label_rating) + mMovie.rating);
        Picasso.with(getActivity()).load(mMovie.image).into(mImageView);

        mVideoAdapter = new VideoAdapter(getActivity(), R.layout.video_list_item, new ArrayList<Video>());
        mVideoListView.setAdapter(mVideoAdapter);

        mVideoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = mVideoAdapter.getItem(position);
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.key));
                    startActivity(intent);
                }catch (ActivityNotFoundException ex){
                    Intent intent=new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v="+video.key));
                    startActivity(intent);
                }
            }
        });

        new FetchVideoTask(this).execute(String.valueOf(mMovie.id));

        return view;
    }

    //video callbacks


    @Override
    public void onTaskStarted() {}

    @Override
    public void onTaskFinished(ArrayList<Video> data) {
        mVideoAdapter.clear();
        mVideoAdapter.addAll(data);
        mVideoAdapter.notifyDataSetChanged();
    }
}
