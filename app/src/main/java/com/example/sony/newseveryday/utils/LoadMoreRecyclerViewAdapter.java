package com.example.sony.newseveryday.utils;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.sony.newseveryday.MainActivity;
import com.example.sony.newseveryday.R;
import com.example.sony.newseveryday.models.NewsDetails;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SONY on 07-03-2017.
 */

public class LoadMoreRecyclerViewAdapter extends RecyclerView.Adapter<LoadMoreRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private int mType = 0;
    private final static int FADE_DURATION = 1000;

    public LoadMoreRecyclerViewAdapter(Context c, Cursor cursor, int type) {
        this.mContext = c;
        this.mCursor = cursor;
        this.mType = type;

    }

    @Override
    public LoadMoreRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.load_more_layout, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(myViewHolder.getAdapterPosition());

                String author = mCursor.getString(MainActivity.INDEX_NEWS_AUTHOR);
                String newsUrl = mCursor.getString(MainActivity.INDEX_NEWS_URL);
                String title = mCursor.getString(MainActivity.INDEX_NEWS_TITLE);
                String imageUrl = mCursor.getString(MainActivity.INDEX_NEWS_IMAGE_URL);
                String date = mCursor.getString(MainActivity.INDEX_NEWS_DATE);
                String sourceUrl = mCursor.getString(MainActivity.INDEX_SRC_URL);
                String desc = mCursor.getString(MainActivity.INDEX_NEWS_DESC);
                final NewsDetails details = new NewsDetails(title, newsUrl, imageUrl, date, sourceUrl, author, desc);
                switch (mType) {
                    case 0: {
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra(mContext.getString(R.string.news_to_webview), details);
                        Bundle bndlanimation =
                                null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            bndlanimation = ActivityOptions.makeCustomAnimation(mContext, R.animator.web_view_animation_enter, R.animator.web_view_animation_exit).toBundle();

                            mContext.startActivity(intent, bndlanimation);
                        } else {
                            mContext.startActivity(intent);
                        }


                        break;

                    }
                    case 1: {
                        Intent intent = new Intent(mContext, SavedContentHandler.class);
                        intent.putExtra(mContext.getString(R.string.news_to_webview), details);
                        Bundle bndlanimation = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            bndlanimation = ActivityOptions.makeCustomAnimation(mContext, R.animator.web_view_animation_enter, R.animator.web_view_animation_exit).toBundle();

                            mContext.startActivity(intent, bndlanimation);
                        } else {
                            mContext.startActivity(intent);
                        }


                        break;
                    }
                }


            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(LoadMoreRecyclerViewAdapter.MyViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String title = mCursor.getString(2);
        String image_url = mCursor.getString(4);
        String author = mCursor.getString(1);

        holder.loadMoreTitle.setText(title);

        Picasso.with(mContext).load(image_url).into(holder.loadMoreImage);
        for (int i = 0; i < getItemCount(); i++) {


            setFadeAnimation(holder.itemView);

        }
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.load_more_image)
        ImageView loadMoreImage;
        @BindView(R.id.load_more_title)
        TextView loadMoreTitle;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

}
