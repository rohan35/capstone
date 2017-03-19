package com.example.sony.newseveryday.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SONY on 03-03-2017.
 */

public class NewsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "news.db";
    public static final int DATABASE_VERSION = 1;

    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_NEWS_TABLE =
                "CREATE TABLE " + NewsContract.NewsEntry.TABLE_NAME +
                        " ( " +
                        NewsContract.NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        NewsContract.NewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                        NewsContract.NewsEntry.COlUMN_TITLE + " TEXT NOT NULL, " +
                        NewsContract.NewsEntry.COLUMN_URL + " TEXT NOT NULL UNIQUE, " +
                        NewsContract.NewsEntry.COLUMN_URL_TO_IMAGE + " TEXT NOT NULL," +
                        NewsContract.NewsEntry.COlUMN_DESC + " TEXT NOT NULL," +
                        NewsContract.NewsEntry.COlUMN_DATE + " TEXT NOT NULL," +
                        NewsContract.NewsEntry.COLUMN_SOURCE_URL + " TEXT NOT NULL," +
                        NewsContract.NewsEntry.COLUMN_SAVED + " INTEGER DEFAULT 0" + " )";
        db.execSQL(SQL_CREATE_NEWS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NewsContract.NewsEntry.TABLE_NAME);
        onCreate(db);
    }
}
