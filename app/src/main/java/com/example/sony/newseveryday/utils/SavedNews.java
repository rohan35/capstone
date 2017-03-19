package com.example.sony.newseveryday.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.sony.newseveryday.R;
import com.example.sony.newseveryday.data.NewsContract;
import com.example.sony.newseveryday.data.NewsDbHelper;
import com.example.sony.newseveryday.models.NewsDetails;
import com.example.sony.newseveryday.models.SavedPages;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SONY on 11-03-2017.
 */

public class SavedNews extends AppCompatActivity {
    @BindView(R.id.load_more_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.no_news)
    TextView noNews;
    LoadMoreRecyclerViewAdapter mAdapter;
    private SQLiteDatabase db;
    ArrayList<String> details = new ArrayList<>();


    private Cursor c;
    private static final String SELECT_SQL = "SELECT * FROM " + NewsContract.NewsEntry.TABLE_NAME + " WHERE " +
            NewsContract.NewsEntry.COLUMN_SAVED + "=1";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_more_rv);
        ButterKnife.bind(this);
        NewsDbHelper newsDbHelper = new NewsDbHelper(this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        db = newsDbHelper.getReadableDatabase();
        c = db.rawQuery(SELECT_SQL, null);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String email = sharedPref.getString(getString(R.string.email), "");

        if (c != null && c.getCount() > 0) {
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new LoadMoreRecyclerViewAdapter(this, c, 1);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else if (!(email.equals(""))) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("savedPages");
            Query recentPostsQuery = mDatabase.orderByChild("mEmail").equalTo(email);
            recentPostsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    SavedPages user = dataSnapshot.getValue(SavedPages.class);
                    if (user != null) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            SavedPages savedPages = ds.getValue(SavedPages.class);
                            ContentValues values = new ContentValues();
                            values.put(NewsContract.NewsEntry.COLUMN_AUTHOR, savedPages.mAuthor);
                            values.put(NewsContract.NewsEntry.COlUMN_TITLE, savedPages.mTitle);
                            values.put(NewsContract.NewsEntry.COLUMN_URL, savedPages.mUrl);
                            values.put(NewsContract.NewsEntry.COLUMN_URL_TO_IMAGE, savedPages.mImageUrl);
                            values.put(NewsContract.NewsEntry.COlUMN_DESC, savedPages.mDesc);
                            values.put(NewsContract.NewsEntry.COlUMN_DATE, savedPages.mDate);
                            values.put(NewsContract.NewsEntry.COLUMN_SOURCE_URL, savedPages.mSourceUrl);
                            values.put(NewsContract.NewsEntry.COLUMN_SAVED, 1);
                            db.insert(NewsContract.NewsEntry.TABLE_NAME, null, values);


                        }
                        c = db.rawQuery(SELECT_SQL, null);
                        if (c != null && c.getCount() > 0) {
                            mRecyclerView.setHasFixedSize(true);
                            mAdapter = new LoadMoreRecyclerViewAdapter(SavedNews.this, c, 1);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }


                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value

                }
            });
        } else {
            noNews.setVisibility(View.VISIBLE);
        }
    }

}
