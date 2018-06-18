package com.example.android.newsapp;

public class FeedNews {
    private String mTitle;
    private String mAuthor;
    private String mSectionName;
    private String mDate;
    private String mUrl;

    public FeedNews(String title, String sectionName, String author, String date, String url) {
        mTitle = title;
        mAuthor = author;
        mSectionName = sectionName;
        mDate = date;
        mUrl = url;
    }

    /**
     * Title of the news feed.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Author on news feed.
     */
    public String getAuthor() {return mAuthor;}

    /**
     * Section Name on news feed.
     */
    public String getSectionName() {
        return mSectionName;
    }


    /**
     * Date on news feed.
     */
    public String getDate() {
        return mDate;
    }

    /**
     * URL on news feed.
     */
    public String getUrl() {
        return mUrl;
    }
}
