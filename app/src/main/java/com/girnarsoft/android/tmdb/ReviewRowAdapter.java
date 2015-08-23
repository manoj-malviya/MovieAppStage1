package com.girnarsoft.android.tmdb;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.girnarsoft.android.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewRowAdapter extends ArrayAdapter<Review> {
    private Context mContext;
    private int mReviewLayout;

    public ReviewRowAdapter(Context c, int itemLayout, List<Review> moviesList) {
        super(c, itemLayout, moviesList);
        mContext = c;
        mReviewLayout = itemLayout;
    }

//    public long getItemId(int position) {
//        return 0;
//    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mReviewLayout, parent, false);

            holder = new ViewHolder();
            holder.author = (TextView) row.findViewById(R.id.review_author);
            holder.content = (TextView) row.findViewById(R.id.review_content);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        row.setClickable(false);

        Review review = getItem(position);

        holder.author.setText(review.author);
        holder.content.setText(review.content);

        return row;
    }

    static class ViewHolder {
        TextView author;
        TextView content;
    }
}