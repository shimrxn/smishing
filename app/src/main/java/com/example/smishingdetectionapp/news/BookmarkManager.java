package com.example.smishingdetectionapp.news;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class BookmarkManager {
    private static final String PREFS_NAME = "BookmarksPrefs";
    private static final String KEY_BOOKMARKS = "bookmarked_links";

    private final SharedPreferences sharedPreferences;

    public BookmarkManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveBookmark(String link) {
        Set<String> bookmarks = new HashSet<>(getBookmarks());
        bookmarks.add(link);
        sharedPreferences.edit().putStringSet(KEY_BOOKMARKS, bookmarks).apply();
    }

    public void removeBookmark(String link) {
        Set<String> bookmarks = new HashSet<>(getBookmarks());
        bookmarks.remove(link);
        sharedPreferences.edit().putStringSet(KEY_BOOKMARKS, bookmarks).apply();
    }

    public boolean isBookmarked(String link) {
        return getBookmarks().contains(link);
    }

    public Set<String> getBookmarks() {
        return new HashSet<>(sharedPreferences.getStringSet(KEY_BOOKMARKS, new HashSet<>()));
    }
}
