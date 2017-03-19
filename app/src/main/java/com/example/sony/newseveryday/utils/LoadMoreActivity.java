package com.example.sony.newseveryday.utils;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.sony.newseveryday.R;
import com.example.sony.newseveryday.data.NewsContract;
import com.example.sony.newseveryday.data.NewsDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SONY on 07-03-2017.
 */

public class LoadMoreActivity extends AppCompatActivity {
    @BindView(R.id.load_more_rv)
    RecyclerView mRecyclerView;
    private LoadMoreRecyclerViewAdapter mAdapter;
    private SQLiteDatabase db;
    private Intent intent;
    private String sourceUrl = "";
    private String SELECT_SQL;
    private Cursor c;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_more_rv);
        ButterKnife.bind(this);
        NewsDbHelper newsDbHelper = new NewsDbHelper(this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        intent = getIntent();
        sourceUrl = intent.getExtras().getString(getString(R.string.load_more_intent));
        //Query to get specific category results
        SELECT_SQL = "SELECT * FROM " + NewsContract.NewsEntry.TABLE_NAME + " where " + NewsContract.NewsEntry.COLUMN_SOURCE_URL
                + "= " + "\"" + sourceUrl + "\"";

        db = newsDbHelper.getReadableDatabase();
        c = db.rawQuery(SELECT_SQL, null);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new LoadMoreRecyclerViewAdapter(this, c, 0);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
