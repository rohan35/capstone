package com.example.sony.newseveryday.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sony.newseveryday.R;
import com.example.sony.newseveryday.data.NewsContract;
import com.example.sony.newseveryday.data.NewsDbHelper;
import com.example.sony.newseveryday.models.NewsDetails;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SONY on 15-03-2017.
 */

public class SavedContentHandler extends AppCompatActivity {
    private NewsDetails newsDetails;
    private SQLiteDatabase db;
    @BindView(R.id.saved_title)
    TextView title;
    @BindView(R.id.saved_author)
    TextView author;
    @BindView(R.id.saved_date)
    TextView date;
    @BindView(R.id.saved_image)
    ImageView imageView;
    @BindView(R.id.saved_desc)
    TextView desc;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_layout);
        Intent i = getIntent();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NewsDbHelper newsDbHelper = new NewsDbHelper(this);
        db = newsDbHelper.getWritableDatabase();
        newsDetails = i.getExtras().getParcelable(getString(R.string.news_to_webview));
        ContextWrapper cw = new ContextWrapper(this);
        // path to /data/data/yourapp/app_data/imageFolder
        File directory = cw.getDir("ImageFolder", Context.MODE_PRIVATE);
        // Create imageFolder
        loadImageFromStorage(directory.getAbsolutePath());
        title.setText(newsDetails.getTitle());
        author.setText(newsDetails.getAuthor());
        date.setText(newsDetails.getPublishedAt());

        desc.setText(newsDetails.getmDescription());

    }

    private void loadImageFromStorage(String path) {

        try {
            File f = new File(path, newsDetails.getTitle());
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void unSave(View v) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NewsContract.NewsEntry.COLUMN_SAVED, 0);
        db.update(NewsContract.NewsEntry.TABLE_NAME, contentValues, NewsContract.NewsEntry.COLUMN_URL + "=?"
                , new String[]{String.valueOf(newsDetails.getUrl())});
        Snackbar.make(v, getString(R.string.removed_news), Snackbar.LENGTH_LONG).show();
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
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    // share method for sharing news
    public void share(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, newsDetails.getUrl());
        intent.setType("text/plain");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }
}
