package com.example.sony.newseveryday.widget;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.sony.newseveryday.R;
import com.example.sony.newseveryday.data.NewsContract;
import com.example.sony.newseveryday.models.NewsDetails;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by SONY on 11-03-2017.
 */

public class WidgetRemoteViewService extends RemoteViewsService {
    private NewsDetails newsDetails;
    private static final String[] NEWS_COLUMNS = {
            NewsContract.NewsEntry._ID,
            NewsContract.NewsEntry.COLUMN_AUTHOR,
            NewsContract.NewsEntry.COlUMN_TITLE,
            NewsContract.NewsEntry.COLUMN_URL,
            NewsContract.NewsEntry.COLUMN_URL_TO_IMAGE,
            NewsContract.NewsEntry.COlUMN_DESC,
            NewsContract.NewsEntry.COlUMN_DATE,
            NewsContract.NewsEntry.COLUMN_SOURCE_URL
    };

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;


            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        NewsContract.NewsEntry.CONTENT_URI,
                        NEWS_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_item_layout);
                try {
                    Bitmap bit =
                            Picasso.with(WidgetRemoteViewService.this)
                                    .load(data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_URL_TO_IMAGE)))
                                    .get();
                    views.setImageViewBitmap(R.id.widget_load_more_image, bit);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                views.setTextViewText(R.id.widget_load_more_title, data.getString(data.getColumnIndex
                        (NewsContract.NewsEntry.COlUMN_TITLE)));
                //
                // views.setTextViewText(R.id.load_more_author, data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_AUTHOR)));


                final Intent fillInIntent = new Intent();
                data.moveToPosition(position);
                newsDetails = new NewsDetails(data.getString(data.getColumnIndex(NewsContract.NewsEntry.COlUMN_TITLE))
                        , data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_URL))
                        , data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_URL_TO_IMAGE))
                        , data.getString(data.getColumnIndex(NewsContract.NewsEntry.COlUMN_DATE)),
                        data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_SOURCE_URL)),
                        data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_AUTHOR)),
                        data.getString(data.getColumnIndex(NewsContract.NewsEntry.COlUMN_DESC)));
                fillInIntent.putExtra(getString(R.string.news_to_webview), newsDetails);
                views.setOnClickFillInIntent(R.id.widget_list_item
                        , fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null; // use the default loading view
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                // Get the row ID for the view at the specified position
                if (data != null && data.moveToPosition(position)) {
                    final int QUOTES_ID_COL = 0;
                    return data.getLong(QUOTES_ID_COL);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

        };
    }
}