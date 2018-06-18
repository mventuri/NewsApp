package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<FeedNews>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Adapter for the list of news feed
     */
    private FeedNewsAdapter mAdapter;

    private static final int FEED_NEWS_LOADER_ID = 1;

    /**
     * TextView that is displayed w/empty list
     */
    private TextView mEmptyStateTextView;

    /**
     * guardian data from guardian
     */
    private static final String GUARDIAN_URL =
            "https://content.guardianapis.com/search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView mainListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of news feed as input
        mAdapter = new FeedNewsAdapter(this, new ArrayList<FeedNews>());

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mainListView.setEmptyView(mEmptyStateTextView);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        mainListView.setAdapter(mAdapter);

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news feed.
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current feed news that was clicked on
                FeedNews currentFeedNews = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri feedNewsUri = Uri.parse(currentFeedNews.getUrl());

                // Create a new intent to view the feedNews URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, feedNewsUri);

                if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }

            }
        });
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(FEED_NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet);
        }
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_page_key)) ||
                key.equals(getString(R.string.settings_interest_key))){
            // Clear the ListView as a new query will be kicked off
            mAdapter.clear();

            // Hide the empty state text view as the loading indicator will be displayed
            mEmptyStateTextView.setVisibility(View.GONE);

            // Loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Restart the loader after query settings have been updated
            getLoaderManager().restartLoader(FEED_NEWS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<FeedNews>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String pageNumber = sharedPrefs.getString(
                getString(R.string.settings_page_key),
                getString(R.string.settings_page_default));

        String yourInterested = sharedPrefs.getString(
                getString(R.string.settings_interest_key),
                getString(R.string.settings_page_default));

        Uri baseUri = Uri.parse(GUARDIAN_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("show-fields", "byline");
        uriBuilder.appendQueryParameter("page-size", pageNumber);
        uriBuilder.appendQueryParameter("q", yourInterested);
        uriBuilder.appendQueryParameter("api-key", "6c0b6173-5034-4de3-a2c7-b8b784a529d7");
        return new FeedNewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<FeedNews>> loader, List<FeedNews> feedNews1) {
        // Hide loading indicator when data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display related message
        mEmptyStateTextView.setText(R.string.no_newsfeed);
        // Clear the adapter of previous news feed data
        mAdapter.clear();

        // If there is a valid list of {@link FeedNews}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (feedNews1 != null && !feedNews1.isEmpty()) {
            mAdapter.addAll(feedNews1);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<FeedNews>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}