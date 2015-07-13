package com.girnarsoft.android.tmdb;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 7/8/2015.
 */
public class TMDBService {
    private static TMDBService instance;

    final String RESULTS = "results";
    final String POSTER_PATH = "poster_path";
    final String ID = "id";
    final String ORIGINAL_TITLE = "original_title";
    final String OVERVIEW = "overview";
    final String RELEASE_DATE = "release_date";
    final String USER_VOTE = "vote_average";

    private final String apiKey = "590e44278fc96621798bcff64fd36990";
    private final String baseUrl = "http://api.themoviedb.org/3";
    private final String ERROR_TAG = TMDBService.class.getSimpleName();
    private final String IMAGE_BASE_URL  = "http://image.tmdb.org/t/p/w185/";

    private TMDBService() {}

    public static TMDBService getInstance() {
        if(instance == null){
            instance = new TMDBService();
        }

        return instance;
    }

    public ArrayList<Movie> getMovies(String sort) {
        ArrayList<Movie> movies = new ArrayList<>();

        Uri endPoint = Uri.parse(baseUrl).buildUpon()
                .appendEncodedPath("discover/movie")
                .appendQueryParameter("api_key", apiKey)
                .appendQueryParameter("sort_by", sort)
                .build();

        String response = getResponse(endPoint.toString());

        try {
            movies = getMovieDataFromJson(response);
        } catch (JSONException jsonEx) {
            Log.e(ERROR_TAG, jsonEx.getMessage());
        }

        return movies;
    }

    public Movie getMovieDetail(String id){

        Uri endPoint = Uri.parse(baseUrl).buildUpon()
                .appendEncodedPath("movie")
                .appendQueryParameter("api_key", apiKey)
                .appendQueryParameter("sort_by", id)
                .build();

        String response = getResponse(endPoint.toString());

        try {
            Movie movie = getMovieFromJson(response);
            return movie;
        } catch (JSONException jsonEx) {
            Log.e(ERROR_TAG, jsonEx.getMessage());
        }

        return null;

    }

    public Movie getMovieFromJson(String movieJsonString) throws JSONException{
        JSONObject movieObject = new JSONObject(movieJsonString);

        Movie movie = new Movie();

        movie.image = IMAGE_BASE_URL + movieObject.getString(POSTER_PATH);
        movie.id = movieObject.getInt(ID);
        movie.name = movieObject.getString(ORIGINAL_TITLE);
        movie.overview = movieObject.getString(OVERVIEW);
        movie.releaseDate = movieObject.getString(RELEASE_DATE);
        movie.rating = movieObject.getString(USER_VOTE);

        return movie;
    }

    private ArrayList<Movie> getMovieDataFromJson(String movieDataString) throws JSONException {

        ArrayList<Movie> movies = new ArrayList<Movie>();

        JSONObject movieJson = new JSONObject(movieDataString);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        for(int i=0; i<movieArray.length(); i++) {
            JSONObject movieObject = movieArray.getJSONObject(i);

            Movie movie = new Movie();

            movie.image = IMAGE_BASE_URL + movieObject.getString(POSTER_PATH);
            movie.id = movieObject.getInt(ID);
            movie.name = movieObject.getString(ORIGINAL_TITLE);
            movie.overview = movieObject.getString(OVERVIEW);
            movie.releaseDate = movieObject.getString(RELEASE_DATE);
            movie.rating = movieObject.getString(USER_VOTE);

            movies.add(movie);
        }

        return movies;
    }

    private String getResponse(String endPoint) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(endPoint);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString();
        }catch (IOException e) {
            Log.e(ERROR_TAG, "Error ", e);
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(ERROR_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }


}
