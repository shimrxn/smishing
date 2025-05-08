package com.example.smishingdetectionapp.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.news.Models.RSSFeedModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SavedNewsActivity extends AppCompatActivity implements SelectListener {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private BookmarkManager bookmarkManager;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_news);

        bookmarkManager = new BookmarkManager(this);

        ImageButton backButton = findViewById(R.id.news_bookmark_back);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_saved_news);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        emptyMessage = findViewById(R.id.text_no_saved_news);

        Set<String> savedLinks = bookmarkManager.getBookmarks();

        if (savedLinks.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            List<RSSFeedModel.Article> dummyArticles = new ArrayList<>();
            for (String link : savedLinks) {
                RSSFeedModel.Article article = new RSSFeedModel.Article();
                article.title = "Bookmarked Article";
                article.description = "Saved from your news feed.";
                article.link = link;
                article.pubDate = "";
                article.setBookmarked(true);
                dummyArticles.add(article);
            }

            adapter = new NewsAdapter(this, dummyArticles, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void OnNewsClicked(RSSFeedModel.Article article) {
        if (article != null && article.link != null && !article.link.isEmpty()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.link));
            startActivity(browserIntent);
        }
    }
}
