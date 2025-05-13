package com.example.smishingdetectionapp.Community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.ImageView;
import android.app.Activity;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;

import java.util.ArrayList;
import java.util.List;

public class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.PostViewHolder> {

    private final List<CommunityPost> originalPost;
    private final List<CommunityPost> filteredPost; //to work with the search bar


    public CommunityPostAdapter(List<CommunityPost> postList) {
        this.originalPost = postList;
        this.filteredPost = new ArrayList<>(postList);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community_post, parent, false);
        return new PostViewHolder(view);
    }

    // link to CommunityPost.java to display the post
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        CommunityPost post = filteredPost.get(position);
        holder.username.setText(post.getUsername());
        holder.date.setText(post.getDate());
        holder.posttitle.setText(post.getPosttitle());
        holder.postdescription.setText(post.getPostdescription());
        holder.likes.setText(String.valueOf(post.getLikes()));
        holder.comments.setText(String.valueOf(post.getComments()));

        // Open Post
        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, CommunityOpenPost.class);
            intent.putExtra("postId", post.getId());
            intent.putExtra("username", post.getUsername());
            intent.putExtra("date", post.getDate());
            intent.putExtra("posttitle", post.getPosttitle());
            intent.putExtra("postdescription", post.getPostdescription());
            intent.putExtra("likes", post.getLikes());
            intent.putExtra("comments", post.getComments());
            intent.putExtra("position", holder.getAdapterPosition());
            ((Activity) context).startActivityForResult(intent, 200);
        });

        // Delete Post
        holder.deleteIcon.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton("Yes", (dialogInterface, which) -> {
                        CommunityDatabaseAccess dbAccess = new CommunityDatabaseAccess(context);
                        dbAccess.open();
                        dbAccess.deletePost(post.getId());
                        dbAccess.close();

                        originalPost.remove(post);
                        filteredPost.remove(post);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .create();

            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });
    }

    @Override
    public int getItemCount() {
        return filteredPost.size();
    }

    // case insensitive general search and field-specific filter
    public void filter(String query, String field) {
        filteredPost.clear();

        if (query == null || query.trim().isEmpty() || field.equals("all")) {
            filteredPost.addAll(originalPost);
        } else {
            String lower = query.toLowerCase();
            for (CommunityPost post : originalPost) {
                switch (field) {
                    case "username":
                        if (post.getUsername().toLowerCase().contains(lower)) filteredPost.add(post);
                        break;
                    case "title":
                        if (post.getPosttitle().toLowerCase().contains(lower)) filteredPost.add(post);
                        break;
                    case "description":
                        if (post.getPostdescription().toLowerCase().contains(lower)) filteredPost.add(post);
                        break;
                    case "likes":
                        if (String.valueOf(post.getLikes()).contains(lower)) filteredPost.add(post);
                        break;
                    case "comments":
                        if (String.valueOf(post.getComments()).contains(lower)) filteredPost.add(post);
                        break;
                    case "date":
                        if (post.getDate() != null && post.getDate().toLowerCase().contains(lower)) filteredPost.add(post);
                        break;
                }
            }
        }

        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView username, date, posttitle, postdescription, likes, comments;
        ImageView userIcon, likeIcon, commentIcon, deleteIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.date);
            posttitle = itemView.findViewById(R.id.posttitle);
            postdescription = itemView.findViewById(R.id.postdescription);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            userIcon = itemView.findViewById(R.id.userIcon);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            commentIcon = itemView.findViewById(R.id.commentIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}