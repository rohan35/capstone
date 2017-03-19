package com.example.sony.newseveryday.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.example.sony.newseveryday.MainActivity;
import com.example.sony.newseveryday.R;
import com.example.sony.newseveryday.models.NewsDetails;
import com.example.sony.newseveryday.models.NewsSection;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SONY on 05-03-2017.
 */

public class VerticalRecyclerViewAdapter extends RecyclerView.Adapter<VerticalRecyclerViewAdapter.Holder> {
    private Context context;
    public List<NewsSection> mSections;
    private static ArrayList<NewsDetails> newsDetails = new ArrayList<>();
    ArrayList<NewsDetails> technology = new ArrayList<>();
    ArrayList<NewsDetails> world = new ArrayList<>();
    ArrayList<NewsDetails> sports = new ArrayList<>();
    ArrayList<NewsDetails> entertainment = new ArrayList<>();
    ArrayList<NewsDetails> bussiness = new ArrayList<>();

    private Cursor mCursor;
    private HeadLinesAdapter adapter;
    private final static int FADE_DURATION = 1000;
    Intent intent;


    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;


    }

    public VerticalRecyclerViewAdapter(Context c, List<NewsSection> sections, Cursor newCursor) {
        this.context = c;
        this.mSections = sections;
        this.mCursor = newCursor;
        if (newCursor != null && newCursor.getCount() > 0) {
            while (newCursor.moveToNext()) {
                // add all data to newsdetails model
                newsDetails.add(new NewsDetails(newCursor.getString(MainActivity.INDEX_NEWS_TITLE)
                        , newCursor.getString(MainActivity.INDEX_NEWS_URL)
                        , newCursor.getString(MainActivity.INDEX_NEWS_IMAGE_URL), newCursor.getString(MainActivity.INDEX_NEWS_DATE),
                        newCursor.getString(MainActivity.INDEX_SRC_URL), newCursor.getString(MainActivity.INDEX_NEWS_AUTHOR),
                        newCursor.getString(MainActivity.INDEX_NEWS_DESC)));
            }
            // call method to  divide the data into different category
            sortNews(newsDetails);

        }


    }

    @Override
    public VerticalRecyclerViewAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_vertical_rv, null, false);

        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(VerticalRecyclerViewAdapter.Holder holder, final int position) {
        intent = new Intent(context, LoadMoreActivity.class);
        holder.sectionTitle.setText(mSections.get(position).getmSection());
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsDetails.size() > 0) {
                    if (position == 0) {
                        intent.putExtra(context.getString(R.string.load_more_intent), world.get(position).getmSourceUrl());

                    } else if (position == 1) {
                        intent.putExtra(context.getString(R.string.load_more_intent), technology.get(0).getmSourceUrl());
                    } else if (position == 2) {
                        intent.putExtra(context.getString(R.string.load_more_intent), sports.get(0).getmSourceUrl());
                    } else if (position == 3) {
                        intent.putExtra(context.getString(R.string.load_more_intent), bussiness.get(0).getmSourceUrl());

                    } else if (position == 4) {
                        intent.putExtra(context.getString(R.string.load_more_intent), entertainment.get(0).getmSourceUrl());

                    }
                    Bundle bndlanimation =
                            null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        bndlanimation = ActivityOptions.makeCustomAnimation(context, R.animator.web_view_animation_enter, R.animator.web_view_animation_exit).toBundle();

                        context.startActivity(intent, bndlanimation);
                    } else {
                        context.startActivity(intent);
                    }

                }
            }
        });
        if (position == 0) {


            adapter = new HeadLinesAdapter(context, position, world);
            if (world.size() > 0) {
            }


        } else if (position == 1) {


            adapter = new HeadLinesAdapter(context, position, technology);
            //

        } else if (position == 2) {


            adapter = new HeadLinesAdapter(context, position, sports);
            //  ;


        } else if (position == 3) {


            adapter = new HeadLinesAdapter(context, position, bussiness);
//

        } else if (position == 4) {


            adapter = new HeadLinesAdapter(context, position, entertainment);
        }


        LinearLayoutManager layoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.headlinesRecyclerView.setLayoutManager(layoutManager);
        holder.headlinesRecyclerView.setHasFixedSize(true);
        holder.headlinesRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        setScaleAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {

        return mSections.size();
    }

    public void sortNews(ArrayList<NewsDetails> details) {
        for (int i = 0; i < details.size(); i++) {
            if (details.get(i).getmSourceUrl().contains(context.getString(R.string.tech_container_link_part))) {
                technology.add(new NewsDetails(details.get(i).getTitle()
                        , details.get(i).getUrl()
                        , details.get(i).getUrlToImage(), details.get(i).getPublishedAt(),
                        details.get(i).getmSourceUrl(), details.get(i).getAuthor(), details.get(i).getmDescription()));
            } else if (details.get(i).getmSourceUrl().contains(context.getString(R.string.world_container_link_part))) {

                world.add(new NewsDetails(details.get(i).getTitle()
                        , details.get(i).getUrl()
                        , details.get(i).getUrlToImage(), details.get(i).getPublishedAt(),
                        details.get(i).getmSourceUrl(), details.get(i).getAuthor(), details.get(i).getmDescription()));
            } else if (details.get(i).getmSourceUrl().contains(context.getString(R.string.buss_container_link_part))) {

                bussiness.add(new NewsDetails(details.get(i).getTitle()
                        , details.get(i).getUrl()
                        , details.get(i).getUrlToImage(), details.get(i).getPublishedAt(),
                        details.get(i).getmSourceUrl(), details.get(i).getAuthor(), details.get(i).getmDescription()));
            } else if (details.get(i).getmSourceUrl().contains(context.getString(R.string.sports_container_link_part))) {

                sports.add(new NewsDetails(details.get(i).getTitle()
                        , details.get(i).getUrl()
                        , details.get(i).getUrlToImage(), details.get(i).getPublishedAt(),
                        details.get(i).getmSourceUrl(), details.get(i).getAuthor(), details.get(i).getmDescription()));
            } else if (details.get(i).getmSourceUrl().contains(context.getString(R.string.ent_container_link_part))) {

                entertainment.add(new NewsDetails(details.get(i).getTitle()
                        , details.get(i).getUrl()
                        , details.get(i).getUrlToImage(), details.get(i).getPublishedAt(),
                        details.get(i).getmSourceUrl(), details.get(i).getAuthor(), details.get(i).getmDescription()));
            }


        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.section_title)
        TextView sectionTitle;
        @BindView(R.id.btn_more)
        Button more;
        @BindView(R.id.headlines_rv)
        RecyclerView headlinesRecyclerView;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
}