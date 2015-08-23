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

public class VideoAdapter extends ArrayAdapter<Video> {
    private Context mContext;
    private int gridItemLayout;

    public VideoAdapter(Context c, int itemLayout, List<Video> videoList) {
        super(c, itemLayout, videoList);
        mContext = c;
        gridItemLayout = itemLayout;
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
            row = inflater.inflate(R.layout.video_list_item, parent, false);

            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.video_thumbnail);
            holder.title = (TextView) row.findViewById(R.id.video_title);
            holder.site = (TextView) row.findViewById(R.id.video_site);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        Video video = getItem(position);

        Picasso.with(mContext).load(video.thumbnail).into(holder.image);
        holder.title.setText(video.title);
        holder.site.setText(video.site);

        return row;
    }

    static class ViewHolder {
        TextView title;
        TextView site;
        ImageView image;
    }
}