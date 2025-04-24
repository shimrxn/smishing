package com.example.smishingdetectionapp;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RiskResultActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView percentageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_result);

        progressBar = findViewById(R.id.circularProgressBar);
        percentageText = findViewById(R.id.percentageText);

        int finalScore = getIntent().getIntExtra("score", 75);

        animateProgress(finalScore);

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

        ImageButton report_back = findViewById(R.id.RiskScannerResult_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, RiskScannerActivity.class));
            finish();
        });
    }

    private void animateProgress(int score) {
        progressBar.setMax(100);

        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, score);
        animator.setDuration(3000);
        animator.start();

        new Thread(() -> {
            for (int i = 0; i <= score; i++) {
                int finalI = i;
                runOnUiThread(() -> percentageText.setText(finalI + "%"));
                try {
                    Thread.sleep(15);  // Delay to match the animation speed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
