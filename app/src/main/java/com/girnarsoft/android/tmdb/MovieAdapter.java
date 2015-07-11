package com.girnarsoft.android.tmdb;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.girnarsoft.android.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private List<Movie> movies;
    private int gridItemLayout;

    public MovieAdapter(Context c, int itemLayout, List<Movie> moviesList) {
        mContext = c;
        gridItemLayout = itemLayout;
        movies = moviesList;
    }

    public int getCount() {
        return movies.size();
    }

    public Movie getItem(int position) {
        return movies.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(gridItemLayout, parent, false);

            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.movie_grid_image);
            //holder.imageTitle = (TextView) row.findViewById(R.id.movie_grid_text);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        Movie movie = getItem(position);

        //holder.imageTitle.setText(movie.name);

        Picasso.with(mContext).load(movie.image).into(holder.image);

        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}