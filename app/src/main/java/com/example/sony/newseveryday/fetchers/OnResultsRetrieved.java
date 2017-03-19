package com.example.sony.newseveryday.fetchers;

import org.json.JSONObject;

import java.net.URL;

/**
 * Created by SONY on 03-03-2017.
 */

// Interface for handling the callbacks
public class OnResultsRetrieved {
    public void preRetrievinng() {
        //add progress bar
    }

    public void OnRetrieved(String[] jsonObject, URL[] urls) {
        // get the data
    }
}
