package com.girnarsoft.android.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 7/8/2015.
 */
public class Movie implements Parcelable{
    public int id;
    public String name;
    public String image;
    public String overview;
    public String rating;
    public String releaseDate;

    public Movie(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(overview);
        dest.writeString(rating);
        dest.writeString(releaseDate);
    }

    public static  final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image = in.readString();
        overview = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
    }
}
