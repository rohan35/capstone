package com.example.sony.newseveryday.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sony.newseveryday.MainActivity;
import com.example.sony.newseveryday.R;
import com.example.sony.newseveryday.data.NewsContract;
import com.example.sony.newseveryday.data.NewsDbHelper;
import com.example.sony.newseveryday.models.NewsDetails;
import com.example.sony.newseveryday.models.SavedPages;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.delight.android.webview.AdvancedWebView;

/**
 * Created by SONY on 06-03-2017.
 */

public class WebViewActivity extends AppCompatActivity implements AdvancedWebView.Listener {
    private AdvancedWebView mWebView;
    private Cursor c;
    @BindView(R.id.progressBar)
    ProgressBar pb;
    private String newsUrl;
    private SQLiteDatabase db;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private NewsDetails newsDetails;
    private static final String SELECT_SQL = "SELECT * FROM " + NewsContract.NewsEntry.TABLE_NAME + " WHERE " +
            NewsContract.NewsEntry.COLUMN_SAVED + "=1";
    Intent i;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NewsDbHelper newsDbHelper = new NewsDbHelper(this);
        db = newsDbHelper.getWritableDatabase();
        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.setListener(this, this);
        i = getIntent();
        newsDetails = i.getExtras().getParcelable(getString(R.string.news_to_webview));
        newsUrl = newsDetails.getUrl();
        db = newsDbHelper.getReadableDatabase();
        c = db.rawQuery(SELECT_SQL, null);
        if (c != null) {
            while (c.moveToNext()) {
                if (c.getString(MainActivity.INDEX_NEWS_URL).equals(newsUrl)) {
                    save.setText(getString(R.string.saved));
                    save.setEnabled(false);
                }
            }

        }
        mWebView.loadUrl(newsUrl);
        final int totalProgressTime = 100;
        final Thread t = new Thread() {
            @Override
            public void run() {
                int jumpTime = 0;

                while (jumpTime < totalProgressTime) {
                    try {
                        sleep(200);
                        jumpTime += 5;
                        pb.setProgress(jumpTime);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();


    }

    public void save(View v) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("savedPages");
        String userId = mDatabase.push().getKey();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String email = sharedPref.getString(getString(R.string.email), "");
        System.out.println(email);
        if (email != null || email != "") {


            SavedPages savedPages = new SavedPages(email, newsDetails.getTitle(), newsDetails.getUrl(), newsDetails.getUrlToImage()
                    , newsDetails.getPublishedAt(), newsDetails.getAuthor(), newsDetails.getmDescription(), newsDetails.getmSourceUrl());
            mDatabase.child(userId).setValue(savedPages);
        }
        ContentValues values = new ContentValues();
        values.put("saved", 1);
        getContentResolver().update(NewsContract.NewsEntry.CONTENT_URI, values, NewsContract.NewsEntry.COLUMN_URL + "=?",
                new String[]{String.valueOf(newsDetails.getUrl())});
        save.setText(getString(R.string.saved));
        save.setEnabled(false);
        Snackbar.make(v, getString(R.string.article_saved), Snackbar.LENGTH_LONG)
                .show();


    }

    public void share(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, newsDetails.getUrl());
        intent.setType("text/plain");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (!mWebView.onBackPressed()) {
            return;
        }
        // ...

        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(String url) {
        pb.setVisibility(View.GONE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }

}
