package com.example.sony.newseveryday;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sony.newseveryday.data.NewsContract;
import com.example.sony.newseveryday.fetchers.NewsEverydaySyncTask;
import com.example.sony.newseveryday.fetchers.OnResultsRetrieved;
import com.example.sony.newseveryday.models.NewsSection;
import com.example.sony.newseveryday.utils.HeadLinesAdapter;
import com.example.sony.newseveryday.utils.JsonUtils;
import com.example.sony.newseveryday.utils.LoadMoreActivity;
import com.example.sony.newseveryday.utils.LoginActivity;
import com.example.sony.newseveryday.utils.NetworkUtils;
import com.example.sony.newseveryday.utils.SavedNews;
import com.example.sony.newseveryday.utils.Settings;
import com.example.sony.newseveryday.utils.VerticalRecyclerViewAdapter;
import com.example.sony.newseveryday.widget.WidgetProvider;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] MAIN_NEWS_PROJECTION = {
            NewsContract.NewsEntry._ID,
            NewsContract.NewsEntry.COLUMN_AUTHOR,
            NewsContract.NewsEntry.COlUMN_TITLE,
            NewsContract.NewsEntry.COLUMN_URL,
            NewsContract.NewsEntry.COLUMN_URL_TO_IMAGE,
            NewsContract.NewsEntry.COlUMN_DESC,
            NewsContract.NewsEntry.COlUMN_DATE,
            NewsContract.NewsEntry.COLUMN_SOURCE_URL
    };
    private List<NewsSection> sList = new ArrayList<>();
    private VerticalRecyclerViewAdapter mAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    TextView emailTv, nameTv;
    ImageView imageView;
    private String email;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    public static final int INDEX_NEWS_ID = 0;
    public static final int INDEX_NEWS_AUTHOR = 1;
    public static final int INDEX_NEWS_TITLE = 2;
    public static final int INDEX_NEWS_URL = 3;
    public static final int INDEX_NEWS_IMAGE_URL = 4;
    public static final int INDEX_NEWS_DESC = 5;
    public static final int INDEX_NEWS_DATE = 6;
    public static final int INDEX_SRC_URL = 7;
    View.OnClickListener mOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        NewsSection newsSection = new NewsSection(getString(R.string.section_world));
        sList.add(newsSection);
        newsSection = new NewsSection(getString(R.string.section_tech));
        sList.add(newsSection);
        newsSection = new NewsSection(getString(R.string.section_sports));
        sList.add(newsSection);
        newsSection = new NewsSection(getString(R.string.section_bussiness));
        sList.add(newsSection);
        newsSection = new NewsSection(getString(R.string.section_entertainment));
        sList.add(newsSection);


        mRecyclerView = (RecyclerView) findViewById(R.id.vertical_rv_list);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);

        nameTv = (TextView) v.findViewById(R.id.name);
        emailTv = (TextView) v.findViewById(R.id.email);
        imageView = (ImageView) v.findViewById(R.id.imageView);
        mSwipeRefreshLayout.setRefreshing(true);
        getSupportLoaderManager().initLoader(0, null, this);


        if (tryAgain()) {
            getNews().execute();

        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Snackbar snackbar = Snackbar
                    .make(drawer, getString(R.string.no_connectivity), Snackbar.LENGTH_LONG);

            snackbar.show();

        }


        if (getIntent().getExtras() != null) {

            Intent i = getIntent();
            email = i.getExtras().getString(getString(R.string.email));
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.email), email);
            editor.commit();
            String name = i.getExtras().getString(getString(R.string.user_name));

            emailTv.setText(email);
            nameTv.setText(name);

        }
        if (email == null || email.equals("")) {

            menu.findItem(R.id.login).setVisible(true);
        } else {

            menu.findItem(R.id.signout).setVisible(true);

        }

    }

    public boolean tryAgain() {
        if (!NetworkUtils.isNetworkConnected(this)) {
            return false;
        } else {

            return true;
        }
    }

    public NewsEverydaySyncTask getNews() {
        return new NewsEverydaySyncTask(this, new OnResultsRetrieved() {

            @Override
            public void preRetrievinng() {
                super.preRetrievinng();
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void OnRetrieved(String[] jsonObject, URL[] urls) {
                super.OnRetrieved(jsonObject, urls);

                ContentValues[] contentValues = new ContentValues[0];
                try {

                    ContentResolver newsContentResolver = getContentResolver();

                /* Delete old  data because we don't need to keep duplicate data */
                    newsContentResolver.delete(
                            NewsContract.NewsEntry.CONTENT_URI,
                            NewsContract.NewsEntry.COLUMN_SAVED + "=?",
                            new String[]{String.valueOf(0)});
                    for (int i = 0; i < jsonObject.length; i++) {
                        contentValues = JsonUtils.getContentValuesFromJson(jsonObject[i], urls[i], getApplicationContext());

                        if (contentValues != null && contentValues.length != 0) {
                /* Get a handle on the ContentResolver to delete and insert data */


                /* Insert our new data into ContentProvider */
                            newsContentResolver.bulkInsert(
                                    NewsContract.NewsEntry.CONTENT_URI,
                                    contentValues);
                            mSwipeRefreshLayout.setRefreshing(false);
                            mRecyclerView.setVisibility(View.VISIBLE);


                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, Settings.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        URL[] url;
        url = NetworkUtils.buildUrl();
        Intent intent = new Intent(this, LoadMoreActivity.class);

        if (id == R.id.nav_world) {

            intent.putExtra(getString(R.string.load_more_intent), url[0].toString());
            startActivity(intent);

        } else if (id == R.id.nav_technology) {
            intent.putExtra(getString(R.string.load_more_intent), url[1].toString());
            startActivity(intent);

        } else if (id == R.id.nav_sports) {
            intent.putExtra(getString(R.string.load_more_intent), url[2].toString());
            startActivity(intent);

        } else if (id == R.id.nav_bussiness) {
            intent.putExtra(getString(R.string.load_more_intent), url[3].toString());
            startActivity(intent);

        } else if (id == R.id.nav_enterntainment) {
            intent.putExtra(getString(R.string.load_more_intent), url[4].toString());
            startActivity(intent);
        } else if (id == R.id.nav_saved) {
            Intent i = new Intent(MainActivity.this, SavedNews.class);
            startActivity(i);


        } else if (id == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);


        } else if (id == R.id.login) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri newsQueryUri = NewsContract.NewsEntry.CONTENT_URI;
        CursorLoader cl = new CursorLoader(this,
                newsQueryUri,
                MAIN_NEWS_PROJECTION,
                null,
                null,
                null);
        return cl;


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new VerticalRecyclerViewAdapter(this, sList, data);
        mRecyclerView.setAdapter(mAdapter);

        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        ComponentName name = new ComponentName(this, WidgetProvider.class);
        int[] ids = AppWidgetManager.getInstance(this).getAppWidgetIds(name);

        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
        if (data.getCount() == 0) mRecyclerView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onRefresh() {
        if (tryAgain()) {
            getNews().execute();


        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
        }
    }
}
