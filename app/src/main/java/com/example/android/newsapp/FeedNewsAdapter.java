package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FeedNewsAdapter extends ArrayAdapter<FeedNews> {
    public FeedNewsAdapter(Context context, List<FeedNews> feedNews1) {
        super(context, 0, feedNews1);
    }
    // ViewHolder class for efficient memory usage
    static class ViewHolder {
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private TextView mDataTextView;
        private TextView mSectionTextView;
    }

    /**
     * List item view to display information about the news feed
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.feed_news_list_item, parent, false);
            holder = new ViewHolder();
            holder.mTitleTextView = convertView.findViewById(R.id.title);
            holder.mAuthorTextView = convertView.findViewById(R.id.author);
            holder.mDataTextView = convertView.findViewById(R.id.date);
            holder.mSectionTextView = convertView.findViewById(R.id.section);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Find the news feed at the given position in the list of news feed
        FeedNews currentFeedNews = getItem(position);

        // Display the info of the current news feed in that all TextView
        holder.mTitleTextView.setText(currentFeedNews.getTitle());
        holder.mAuthorTextView.setText(currentFeedNews.getAuthor());
        holder.mDataTextView.setText(currentFeedNews.getDate());
        holder.mSectionTextView.setText(currentFeedNews.getSectionName());

        // Return the list item view that is now showing the right data
        return convertView;
    }
}
