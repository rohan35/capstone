package com.example.sony.newseveryday.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by SONY on 03-03-2017.
 */

public class NewsContract {
    private NewsContract() {//Empty constructor
    }

    public static final String CONTENT_AUTHORITY = "com.example.sony.newseveryday";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH = "news_details";

    public static final class NewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH)
                .build();

        //Columns for table
        public static final String TABLE_NAME = "news_details";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COlUMN_TITLE = "title";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_URL_TO_IMAGE = "image_url";
        public static final String COlUMN_DESC = "description";
        public static final String COlUMN_DATE = "date";
        public static final String COLUMN_SOURCE_URL = "source_url";
        public static final String COLUMN_SAVED = "saved";


    }
}
