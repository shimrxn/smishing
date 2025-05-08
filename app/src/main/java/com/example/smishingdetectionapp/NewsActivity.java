package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.news.Models.RSSFeedModel;
import com.example.smishingdetectionapp.news.NewsAdapter;
import com.example.smishingdetectionapp.news.NewsRequestManager;
import com.example.smishingdetectionapp.news.OnFetchDataListener;
import com.example.smishingdetectionapp.news.SavedNewsActivity;
import com.example.smishingdetectionapp.news.SelectListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class NewsActivity extends SharedActivity implements SelectListener {
    RecyclerView recyclerView;
    NewsAdapter adapter;
    NewsRequestManager manager;
    ProgressBar progressBar;
    TextView errorMessage;
    Button refreshButton, savedNewsButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        errorMessage = findViewById(R.id.errorTextView);
        recyclerView = findViewById(R.id.news_recycler_view);
        refreshButton = findViewById(R.id.refreshButton);
        savedNewsButton = findViewById(R.id.btn_saved_news); // new

        // Saved News button click → open SavedNewsActivity
        savedNewsButton.setOnClickListener(v -> {
            Intent intent = new Intent(NewsActivity.this, SavedNewsActivity.class);
            startActivity(intent);
        });

        // Bottom navigation setup
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_news);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        // Fetch articles
        manager = new NewsRequestManager(this);
        manager.fetchRSSFeed(new OnFetchDataListener<RSSFeedModel.Feed>() {
            @Override
            public void onFetchData(List<RSSFeedModel.Article> list, String message) {
                showNews(list);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                errorMessage.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            private void showNews(List<RSSFeedModel.Article> list) {
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(NewsActivity.this, 1));
                adapter = new NewsAdapter(NewsActivity.this, list, NewsActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });

        // Refresh button click
        refreshButton.setOnClickListener(v -> {
            if (isNetworkConnected()) {
                loadData();
            } else {
                Toast.makeText(this, "You Have Lost Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            );
        }
        return false;
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        manager = new NewsRequestManager(this);
        manager.fetchRSSFeed(new OnFetchDataListener<RSSFeedModel.Feed>() {
            @Override
            public void onFetchData(List<RSSFeedModel.Article> list, String message) {
                showNews(list);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                errorMessage.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            private void showNews(List<RSSFeedModel.Article> list) {
                adapter = new NewsAdapter(NewsActivity.this, list, NewsActivity.this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(NewsActivity.this, 1));
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void OnNewsClicked(RSSFeedModel.Article article) {
        if (article != null && article.link != null && !article.link.isEmpty()) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.link));
                startActivity(browserIntent);
            } catch (Exception e) {
                Log.e("NewsActivity", "Error opening URL", e);
                Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No URL available", Toast.LENGTH_SHORT).show();
        }
    }
}
