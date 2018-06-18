package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String WEBTITLE = "webTitle";
    private static final String SECTIONNAME = "sectionName";
    private static final String WEBPUBLICATIONDATE = "webPublicationDate";
    private static final String WEBURL ="webUrl";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the guardian data set and return a list of {@link FeedNews} objects.
     */
    public static List<FeedNews> fetchFeeedNewsData(String requestUrl) {

        // URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link FeedNews}s
        List<FeedNews> feedNews = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link FeedNews}s
        return feedNews;
    }

    /**
     * New URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {

                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link FeedNews} objects that has been built up from
     * parsing the given JSON response.
     */


    private static List<FeedNews> extractFeatureFromJson(String feedNewsJSON) {
        // JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(feedNewsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding newsFeed to
        List<FeedNews> feedNews = new ArrayList<>();

        try {

            // JSONObject from the JSON response string


            JSONObject baseJsonResponse = new JSONObject(feedNewsJSON);

            JSONObject baseJsonResults = baseJsonResponse.getJSONObject(RESPONSE);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or feedNews1).
            JSONArray feedNewsArray = baseJsonResults.getJSONArray(RESULTS);

            // For each feed news in the feedNewsArray, create an {@link FeedNews} object
            for (int i = 0; i < feedNewsArray.length(); i++) {
                JSONObject currentFeedNews = feedNewsArray.getJSONObject(i);
                String title = currentFeedNews.getString(WEBTITLE);
                String author = "(unknown author)";
                if (currentFeedNews.has("fields")) {
                    JSONObject fieldsObject = currentFeedNews.getJSONObject("fields");

                    if (fieldsObject.has("byline")) {
                        author = fieldsObject.getString("byline");
                    }
                }
                String sectionName = currentFeedNews.getString(SECTIONNAME);
                String date = currentFeedNews.getString(WEBPUBLICATIONDATE);
                String url = currentFeedNews.getString(WEBURL);
                FeedNews feed = new FeedNews(title, sectionName, author, date, url);
                feedNews.add(feed);
            }

        } catch (JSONException e) {

            Log.e("QueryUtils", "Problem parsing JSON results", e);
        }

        // Return the list of feed news
        return feedNews;
    }
}