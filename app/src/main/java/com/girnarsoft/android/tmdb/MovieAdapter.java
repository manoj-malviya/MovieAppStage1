package com.girnarsoft.android.tmdb;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.girnarsoft.android.movieapp.MainActivityFragment;
import com.girnarsoft.android.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends CursorAdapter {

    public MovieAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_grid_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.image = (ImageView) view.findViewById(R.id.movie_grid_image);

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String image = cursor.getString(MainActivityFragment.COL_IMAGE);

        Picasso.with(context).load(image).into(holder.image);
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}