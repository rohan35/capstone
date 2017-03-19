package com.example.sony.newseveryday.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.example.sony.newseveryday.data.NewsContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import static android.R.attr.content;
import static android.R.attr.path;

/**
 * Created by SONY on 03-03-2017.
 */

public class JsonUtils {
    private static final String ARTICLES = "articles";
    private static final String ARTICLE_AUTHOR = "author";
    private static final String ARTICLE_TITLE = "title";
    private static final String ARTICLE_DESC = "description";
    private static final String ARTICLE_URL = "url";
    private static final String ARTICLE_URL_IMAGE = "urlToImage";
    private static final String ARTICLE_DATE = "publishedAt";
    private URL sourceUrl;
    private static Context context;
    private static String title;


    public static ContentValues[] getContentValuesFromJson(String json, URL srcUrl, Context c) throws JSONException {
        context = c;


        JSONObject newsJson = null;
        ContentValues[] newsContentValues = null;

        newsJson = new JSONObject(json);
        JSONArray articles_array = newsJson.getJSONArray(ARTICLES);


        newsContentValues = new ContentValues[articles_array.length()];
        for (int i = 0; i < articles_array.length(); i++) {
            String author, description, url, image_url, date;
            JSONObject article_detail = articles_array.getJSONObject(i);
            author = article_detail.getString(ARTICLE_AUTHOR);
            title = article_detail.getString(ARTICLE_TITLE);
            description = article_detail.getString(ARTICLE_DESC);
            url = article_detail.getString(ARTICLE_URL);
            image_url = article_detail.getString(ARTICLE_URL_IMAGE);
            date = article_detail.getString(ARTICLE_DATE);
            ContentValues newsValues = new ContentValues();
            Picasso.with(c)
                    .load(image_url)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            // loaded bitmap is here (bitmap)
                            saveToInternalStorage(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            // if loading of bitmap failed
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {


                        }
                    });
            newsValues.put(NewsContract.NewsEntry.COLUMN_AUTHOR, author);
            newsValues.put(NewsContract.NewsEntry.COlUMN_TITLE, title);
            newsValues.put(NewsContract.NewsEntry.COlUMN_DESC, description);
            newsValues.put(NewsContract.NewsEntry.COLUMN_URL, url);
            newsValues.put(NewsContract.NewsEntry.COLUMN_URL_TO_IMAGE, image_url);
            newsValues.put(NewsContract.NewsEntry.COlUMN_DATE, date);
            newsValues.put(NewsContract.NewsEntry.COLUMN_SOURCE_URL, srcUrl.toString());
            newsContentValues[i] = newsValues;


        }


        return newsContentValues;

    }

    private static String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageFolder
        File directory = cw.getDir("ImageFolder", Context.MODE_PRIVATE);
        // Create imageFolder
        File mypath = new File(directory, title);


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }
}
