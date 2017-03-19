package com.example.sony.newseveryday.fetchers;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.sony.newseveryday.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SONY on 03-03-2017.
 */

public class NewsEverydaySyncTask extends AsyncTask<String[], Void, String[]> {
    private OnResultsRetrieved resultsRetrieved; // interface object for handling the calls
    private Context mContext;
    private URL url[] = null;

    public NewsEverydaySyncTask(Context c, OnResultsRetrieved resultsRetrieved1) {
        this.mContext = c;
        this.resultsRetrieved = resultsRetrieved1;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        resultsRetrieved.preRetrievinng();
    }

    @Override
    protected String[] doInBackground(String[]... params) {

// build the url
        url = NetworkUtils.buildUrl();
        String[] result = new String[url.length];
        for (int i = 0; i < url.length; i++) {
            //store the results returned by method by fetching data from newapi.org
            result[i] = multipleCalls(url[i]);


        }
        return result;
    }


    @Override
    protected void onPostExecute(String[] jsonObject) {
        super.onPostExecute(jsonObject);
        resultsRetrieved.OnRetrieved(jsonObject, url);
    }

    // created to fetch the results
    public String multipleCalls(URL url) {
        // used library to fetch the data  online
        OkHttpClient client = new OkHttpClient();
        Request request =
                new Request.Builder()
                        .url(url)
                        .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String jsonData = response.body().string();

                return jsonData;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Error Fetching data", response.body().toString());
        }

        return "Download failed";
    }
}
