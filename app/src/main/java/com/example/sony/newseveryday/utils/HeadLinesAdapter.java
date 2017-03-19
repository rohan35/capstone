package com.example.sony.newseveryday.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sony.newseveryday.MainActivity;
import com.example.sony.newseveryday.R;
import com.example.sony.newseveryday.models.NewsDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SONY on 02-03-2017.
 */

public class HeadLinesAdapter extends RecyclerView.Adapter<HeadLinesAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<NewsDetails> newsDetails = new ArrayList<>();
    private static int[] mPos = new int[6];

    public HeadLinesAdapter(Context c, int pos, ArrayList<NewsDetails> newsDetailsArrayList) {
        this.mContext = c;
        mPos[pos] = pos;
        this.newsDetails = newsDetailsArrayList;

    }

    @Override
    public HeadLinesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Intent i = ((Activity) mContext).getIntent();
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.main_headlines_rv, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = myViewHolder.getAdapterPosition();

                Intent intent = new Intent(mContext, WebViewActivity.class);
                String author = newsDetails.get(position).getAuthor();
                String title = newsDetails.get(position).getTitle();
                String newsUrl = newsDetails.get(position).getUrl();
                String imageUrl = newsDetails.get(position).getUrlToImage();
                String date = newsDetails.get(position).getPublishedAt();
                String sourceUrl = newsDetails.get(position).getmSourceUrl();
                String desc = newsDetails.get(position).getmDescription();

                NewsDetails newsDetails = new NewsDetails(title, newsUrl, imageUrl, date, sourceUrl, author, desc);
                intent.putExtra(mContext.getString(R.string.news_to_webview), newsDetails);

                Bundle bndlanimation =
                        null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    bndlanimation = ActivityOptions.makeCustomAnimation(mContext, R.animator.web_view_animation_enter, R.animator.web_view_animation_exit).toBundle();

                    mContext.startActivity(intent, bndlanimation);
                } else {
                    mContext.startActivity(intent);
                }
            }
        });


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(HeadLinesAdapter.MyViewHolder holder, int position) {

        if (newsDetails.size() > 0) {
            String title = newsDetails.get(position).getTitle();
            String image_url = newsDetails.get(position).getUrlToImage();


            holder.headline_title.setText(title);
            Picasso.with(mContext).load(image_url).into(holder.headlinne_image);
        }


    }

    @Override
    public int getItemCount() {

        if (null == newsDetails) return 0;
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.headline_image)
        ImageView headlinne_image;
        @BindView(R.id.headline_title)
        TextView headline_title;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
