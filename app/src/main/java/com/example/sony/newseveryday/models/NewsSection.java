package com.example.sony.newseveryday.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SONY on 05-03-2017.
 */

public class NewsSection {
    public String mSection;

    public NewsSection(String section) {
        this.mSection = section;
    }

    public String getmSection() {
        return mSection;
    }
}