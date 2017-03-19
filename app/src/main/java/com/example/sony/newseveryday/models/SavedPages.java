package com.example.sony.newseveryday.models;

/**
 * Created by SONY on 08-03-2017.
 */

public class SavedPages {
    public String mTitle;
    public String mUrl;
    public String mImageUrl;
    public String mDate;
    public String mEmail;
    public String mAuthor;
    public String mDesc;
    public String mSourceUrl;

    public SavedPages() {
    }

    public SavedPages(String email, String title, String url, String imageUrl, String date, String author, String desc, String sourceUrl) {
        this.mEmail = email;
        this.mTitle = title;
        this.mUrl = url;
        this.mImageUrl = imageUrl;
        this.mDate = date;
        this.mAuthor = author;
        this.mDesc = desc;
        this.mSourceUrl = sourceUrl;


    }
}
