package com.example.sony.newseveryday.models;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SONY on 03-03-2017.
 */

public class NewsDetails implements Parcelable {
    private String mAuthor;
    private String mTitle;
    private String mDescription;
    private String mUrl;
    private String mUrlToImage;
    private String mPublishedAt;
    private String mSourceUrl;

    public NewsDetails(String title, String url, String UrlToImage, String date, String sourceUrl, String author, String desc) {
        this.mTitle = title;
        this.mUrl = url;
        this.mUrlToImage = UrlToImage;
        this.mPublishedAt = date;
        this.mSourceUrl = sourceUrl;
        this.mAuthor = author;
        this.mDescription = desc;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getmSourceUrl() {
        return mSourceUrl;

    }

    public String getmDescription() {
        return mDescription;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getUrlToImage() {
        return mUrlToImage;
    }


    public String getPublishedAt() {
        return mPublishedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mUrl);
        dest.writeString(mUrlToImage);
        dest.writeString(mPublishedAt);
        dest.writeString(mAuthor);
        dest.writeString(mDescription);
        dest.writeString(mSourceUrl);

    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public NewsDetails createFromParcel(Parcel in) {
            return new NewsDetails(in);
        }

        public NewsDetails[] newArray(int size) {
            return new NewsDetails[size];
        }
    };

    // "De-parcel object
    public NewsDetails(Parcel in) {
        mTitle = in.readString();
        mUrl = in.readString();
        mUrlToImage = in.readString();
        mPublishedAt = in.readString();
        mAuthor = in.readString();
        mDescription = in.readString();
        mSourceUrl = in.readString();

    }
}
