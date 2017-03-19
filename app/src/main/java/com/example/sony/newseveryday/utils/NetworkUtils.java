package com.example.sony.newseveryday.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SONY on 03-03-2017.
 */

public class NetworkUtils {
    private static final String API_KEY = "e359f39014224851b27264d5f7de8ccf";
    private static final String URL_WORLD = "https://newsapi.org/v1/articles?source=google-news&sortBy=top";
    private static final String URL_TECH = " https://newsapi.org/v1/articles?source=ars-technica&sortBy=top";
    private static final String URL_BUSSINESS = "https://newsapi.org/v1/articles?source=bloomberg&sortBy=top";
    private static final String URL_SPORTS = "https://newsapi.org/v1/articles?source=bbc-sport&sortBy=top";
    private static final String URL_ENTERTAINMENT = " https://newsapi.org/v1/articles?source=buzzfeed&sortBy=top";
    private static final String API_KEY_NAME = "apiKey";


    public static URL[] buildUrl() {
        Uri techUri, bussinessUri, sportsUri, entertainmentUri, worldUri;
        worldUri = Uri.parse(URL_WORLD).buildUpon()
                .appendQueryParameter(API_KEY_NAME, API_KEY)
                .build();

        techUri = Uri.parse(URL_TECH).buildUpon()
                .appendQueryParameter(API_KEY_NAME, API_KEY)
                .build();
        bussinessUri = Uri.parse(URL_BUSSINESS).buildUpon()
                .appendQueryParameter(API_KEY_NAME, API_KEY)
                .build();
        sportsUri = Uri.parse(URL_SPORTS).buildUpon()
                .appendQueryParameter(API_KEY_NAME, API_KEY)
                .build();
        entertainmentUri = Uri.parse(URL_ENTERTAINMENT).buildUpon()
                .appendQueryParameter(API_KEY_NAME, API_KEY)
                .build();

        // buildUri = Uri.parse(SOURCE_URL).buildUpon().build();

        URL url_world = null;
        URL url_tech = null;
        URL url_buss = null;
        URL url_sport = null;
        URL url_entertainment = null;

        URL url[] = new URL[5];


        try {
            url_world = new URL(worldUri.toString());
            url_tech = new URL(techUri.toString());
            url_buss = new URL(bussinessUri.toString());
            url_sport = new URL(sportsUri.toString());
            url_entertainment = new URL(entertainmentUri.toString());
            url[0] = url_world;
            url[1] = url_tech;
            url[2] = url_sport;
            url[3] = url_buss;
            url[4] = url_entertainment;


        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return url;

    }

    public static boolean isNetworkConnected(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
