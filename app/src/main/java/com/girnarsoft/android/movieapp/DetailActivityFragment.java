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
import com.girnarsoft.android.tmdb.Review;
import com.girnarsoft.android.tmdb.ReviewRowAdapter;
import com.girnarsoft.android.tmdb.Video;
import com.girnarsoft.android.tmdb.VideoAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements AsyncTaskListner<DetailActivityFragment.MovieVideoReviews> {

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

    private boolean isTaskRunning = false;

    private Movie mMovie;
    private ImageView mImageView;
    private TextView mNameView;
    private TextView mOverviewView;
    private TextView mReleaseDateView;
    private TextView mRatingView;
    private TextView mTrailersView;
    private ListView mVideoListView;
    private TextView mReviewView;
    private ListView mReviewListView;

    private VideoAdapter mVideoAdapter;
    private ReviewRowAdapter mReviewAdapter;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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

        mTrailersView = (TextView) view.findViewById(R.id.detail_trailers);
        mVideoListView = (ListView) view.findViewById(R.id.video_list_view);

        mReviewView = (TextView) view.findViewById(R.id.detail_reviews);
        mReviewListView = (ListView) view.findViewById(R.id.review_list_view);

        getActivity().setTitle(mMovie.name);
        mNameView.setText(mMovie.name);
        mOverviewView.setText(mMovie.overview);
        mReleaseDateView.setText(getString(R.string.label_relase) + mMovie.releaseDate);
        mRatingView.setText(getString(R.string.label_rating) + mMovie.rating);
        Picasso.with(getActivity()).load(mMovie.image).into(mImageView);

        mReviewAdapter = new ReviewRowAdapter(getActivity(), R.layout.review_list_item, new ArrayList<Review>());
        mReviewListView.setAdapter(mReviewAdapter);

        mVideoAdapter = new VideoAdapter(getActivity(), R.layout.video_list_item, new ArrayList<Video>());
        mVideoListView.setAdapter(mVideoAdapter);

        mVideoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = mVideoAdapter.getItem(position);
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.key));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + video.key));
                    startActivity(intent);
                }
            }
        });

        if(!isTaskRunning)
            new FetchVideoTask(this).execute(String.valueOf(mMovie.id));

        return view;
    }

    //video callbacks


    @Override
    public void onTaskStarted() {
        isTaskRunning = true;
    }

    @Override
    public void onTaskFinished(MovieVideoReviews data) {
        mVideoAdapter.clear();
        mVideoAdapter.addAll(data.videos);
        mVideoAdapter.notifyDataSetChanged();
        if(data.videos.size()>0) {
            mTrailersView.setVisibility(View.VISIBLE);
        } else {
            mTrailersView.setVisibility(View.GONE);
        }

        mReviewAdapter.clear();
        mReviewAdapter.addAll(data.reviews);
        mReviewAdapter.notifyDataSetChanged();
        if(data.reviews.size()>0) {
            mReviewView.setVisibility(View.VISIBLE);
        } else {
            mReviewView.setVisibility(View.GONE);
        }

        isTaskRunning = false;
    }

    public static class MovieVideoReviews {
        public ArrayList<Video> videos = new ArrayList<>();
        public ArrayList<Review> reviews = new ArrayList<>();
    }
}
