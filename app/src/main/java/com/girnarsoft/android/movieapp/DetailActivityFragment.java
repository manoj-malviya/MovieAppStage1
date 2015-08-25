package com.girnarsoft.android.movieapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.girnarsoft.android.tmdb.AsyncTaskListner;
import com.girnarsoft.android.tmdb.Movie;
import com.girnarsoft.android.tmdb.Review;
import com.girnarsoft.android.tmdb.ReviewRowAdapter;
import com.girnarsoft.android.tmdb.Utility;
import com.girnarsoft.android.tmdb.Video;
import com.girnarsoft.android.tmdb.VideoAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements AsyncTaskListner<DetailActivityFragment.MovieVideoReviews> {

    private static final int DETAIL_LOADER = 112;

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final String SHARE_HASH_TAG = " #MovieApp";

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
    private ImageButton mFavourite;

    private VideoAdapter mVideoAdapter;
    private ReviewRowAdapter mReviewAdapter;

    private MenuItem mShareMenu;

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
        setHasOptionsMenu(true); //show share menu
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
        mFavourite = (ImageButton) view.findViewById(R.id.favourite_button);

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

        mFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavourites(v);
            }
        });

        if(!isTaskRunning && mMovie.id > 0)
            new FetchVideoTask(this).execute(String.valueOf(mMovie.id));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean status = Utility.ifInFavourites(getActivity(), mMovie);
        if(status){
            mFavourite.setImageResource(R.drawable.ic_star_black_24dp);
            mFavourite.setClickable(false);
        }
    }

    //favroute button click handler
    public void addToFavourites(View view) {
        boolean status = Utility.addToFavourites(getActivity(), mMovie);
        if(status) {
            mFavourite.setImageResource(R.drawable.ic_star_black_24dp);
            mFavourite.setClickable(false);
        }
    }

    //sharing
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        mShareMenu = menu.findItem(R.id.action_share);

        mShareMenu.setVisible(false);
    }

    private Intent getShareIntent(String videoKey) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String url = "http://www.youtube.com/watch?v=" + videoKey;
        intent.putExtra(Intent.EXTRA_TEXT, url+SHARE_HASH_TAG);

        return intent;
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

            ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(mShareMenu);
            if(provider != null) {
                provider.setShareIntent(getShareIntent(data.videos.get(0).key));
                mShareMenu.setVisible(true);
            } else {
                Log.e(LOG_TAG, "Shareprovider is empty?");
            }

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
