package com.example.smishingdetectionapp.Community;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.Community.CommunityDatabaseAccess;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommunityPostActivity extends AppCompatActivity {

    private RecyclerView postsRecyclerView;
    private CommunityPostAdapter adapter;
    private List<CommunityPost> postList;
    private EditText searchInput;
    private String selectedField = "all";
    private CommunityDatabaseAccess dbAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communityposts);

        dbAccess = new CommunityDatabaseAccess(this);
        dbAccess.open();

        if (dbAccess.isEmpty()) {
            int id1 = (int) dbAccess.insertPost(new CommunityPost(-1, "User1", "2025-05-11",
                    "Is this legit: 0280067670?",
                    "This number keeps calling me...", 15, 1));

            int id2 = (int) dbAccess.insertPost(new CommunityPost(-1, "User3", "2025-05-10",
                    "Scammer named Albert",
                    "I got scammed by someone called Albert.", 8, 0));

            dbAccess.insertComment(id1, "User22", "2025-05-11",
                    "I have just received a call from this number today! We should report on the app!");
        }

        postList = dbAccess.getAllPosts();


        // Setup UI
        searchInput = findViewById(R.id.searchInput);
        ImageView filterBtn = findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> {
            String[] fields = {"All", "Username", "Date", "Title", "Description", "Likes", "Comments"};
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Filter by")
                    .setItems(fields, (dialog, which) -> {
                        selectedField = fields[which].toLowerCase();
                        adapter.filter(searchInput.getText().toString(), selectedField);
                    })
                    .show();
        });

        ImageView clearSearch = findViewById(R.id.clearSearch);
        clearSearch.setOnClickListener(v -> searchInput.setText(""));

        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommunityPostAdapter(postList);
        postsRecyclerView.setAdapter(adapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString(), selectedField);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        FloatingActionButton addPostButton = findViewById(R.id.addPostButton);
        addPostButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommunityPostActivity.this, CommunityNewPost.class);
            startActivityForResult(intent, 100);
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Report"));
        tabLayout.getTabAt(1).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    startActivity(new Intent(CommunityPostActivity.this, CommunityHomeActivity.class));
                    finish();
                } else if (tab.getPosition() == 2) {
                    Toast.makeText(CommunityPostActivity.this, "Report page coming soon :)", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        ImageButton communityBack = findViewById(R.id.community_back);
        communityBack.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            } else {
                return false;
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 100) {
                String username = data.getStringExtra("username");
                String title = data.getStringExtra("posttitle");
                String description = data.getStringExtra("postdescription");
                int likes = data.getIntExtra("likes", 0);
                int comments = data.getIntExtra("comments", 0);
                String timestamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                CommunityPost newPost = new CommunityPost(-1, username, timestamp, title, description, likes, comments);
                dbAccess.insertPost(newPost);
                postList.add(0, newPost);
                searchInput.setText("");
                adapter.filter("", "all");
                adapter.notifyDataSetChanged();
                postsRecyclerView.scrollToPosition(0);
                Toast.makeText(this, "New post added!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == 200) {
                int position = data.getIntExtra("position", -1);
                int updatedComments = data.getIntExtra("updatedComments", -1);
                if (position != -1 && updatedComments != -1 && position < postList.size()) {
                    CommunityPost post = postList.get(position);
                    post.comments = updatedComments;
                    dbAccess.updatePostComments(post.getId(), updatedComments);
                    adapter.notifyItemChanged(position);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        dbAccess.close();
        super.onDestroy();
    }
}