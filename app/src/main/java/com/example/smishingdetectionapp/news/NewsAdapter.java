package com.example.smishingdetectionapp.news;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.news.Models.RSSFeedModel;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    private final List<RSSFeedModel.Article> articles;
    private final SelectListener listener;
    private final BookmarkManager bookmarkManager;
    private final Context context;
    private String formattedDescription;

    public NewsAdapter(Context context, List<RSSFeedModel.Article> articles, SelectListener listener) {
        this.context = context;
        this.articles = articles;
        this.listener = listener;
        this.bookmarkManager = new BookmarkManager(context);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        RSSFeedModel.Article article = articles.get(position);

        holder.text_title.setText(article.title);
        holder.text_description.setText(article.description);
        Log.d("DebugTag1", "Value " + article.description);

        // Format and clean description with safety check
        formattedDescription = article.description.replaceAll("\\<.*?\\>", "");
        if (formattedDescription.length() > 98) {
            formattedDescription = formattedDescription.substring(84, formattedDescription.length() - 14);
        }
        holder.text_description.setText(formattedDescription);

        holder.text_pubDate.setText(article.getFormattedDate());

        // --- BOOKMARK LOGIC ---
        ImageButton bookmarkButton = holder.bookmarkButton;

        boolean isBookmarked = bookmarkManager.isBookmarked(article.link);
        article.setBookmarked(isBookmarked);
        bookmarkButton.setImageResource(isBookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border);

        bookmarkButton.setOnClickListener(v -> {
            boolean newStatus = !article.isBookmarked();
            article.setBookmarked(newStatus);

            if (newStatus) {
                bookmarkManager.saveBookmark(article.link);
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled);
                Toast.makeText(context, "Bookmarked", Toast.LENGTH_SHORT).show();
                Log.d("Bookmark", "Bookmarked: " + article.title);
            } else {
                bookmarkManager.removeBookmark(article.link);
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_border);
                Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                Log.d("Bookmark", "Unbookmarked: " + article.title);
            }
        });

        holder.cardView.setOnClickListener(v -> listener.OnNewsClicked(article));
    }

    @Override
    public int getItemCount() {
        return Math.min(articles.size(), 9);
    }
}
