package com.example.sony.newseveryday.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by SONY on 04-03-2017.
 */

public class NewsProvider extends ContentProvider {
    public static final int CODE_NEWS = 1;
    public static final int CODE_NEWS_WITH_TITLE = 2;
    private NewsDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mOpenHelper = new NewsDbHelper(getContext());
        return true;

    }

    public static UriMatcher buildUriMatcher() {
        String content = NewsContract.CONTENT_AUTHORITY;
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(content, NewsContract.PATH, CODE_NEWS);
        matcher.addURI(content, NewsContract.PATH + "/#", CODE_NEWS_WITH_TITLE);
        return matcher;

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_NEWS_WITH_TITLE:
                String normalizedUtcDateString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{normalizedUtcDateString};
                cursor = mOpenHelper.getReadableDatabase().query(NewsContract.NewsEntry.TABLE_NAME,
                        projection,
                        NewsContract.NewsEntry.COlUMN_TITLE + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        null);
                break;

            case CODE_NEWS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        NewsContract.NewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_NEWS: {
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        String newsTitle =
                                value.getAsString(NewsContract.NewsEntry.COlUMN_TITLE);


                        long _id = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;

            }


            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        throw new SQLException("Failed to add a record into " + uri);


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted;
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_NEWS:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        NewsContract.NewsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;


    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int numRowsUpdated;
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_NEWS:
                numRowsUpdated = mOpenHelper.getWritableDatabase().update(
                        NewsContract.NewsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsUpdated;

    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
