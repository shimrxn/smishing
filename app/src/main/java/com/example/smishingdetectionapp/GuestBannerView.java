package com.example.smishingdetectionapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.smishingdetectionapp.ui.login.LoginActivity;

public class GuestBannerView extends LinearLayout {

    public GuestBannerView(Context context) {
        super(context);
        init(context);
    }

    public GuestBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_guest_banner, this, true);
        setOrientation(VERTICAL);

        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isGuest = prefs.getBoolean("isGuest", false);

        if (isGuest) {
            setVisibility(VISIBLE);

            Button loginButton = findViewById(R.id.login_button);
            if (loginButton != null) {
                loginButton.setVisibility(VISIBLE);
                loginButton.setOnClickListener(v -> {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                });
            }
        } else {
            setVisibility(GONE);
        }
    }
}
