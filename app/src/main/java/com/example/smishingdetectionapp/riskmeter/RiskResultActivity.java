package com.example.smishingdetectionapp.riskmeter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RiskResultActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView percentageText;
    TextView riskLevelText;

    // Light traffic indicators
    View lightAgeGroup, lightSmsApp, lightSecurityApp, lightSpamFilter, lightDeviceLock, lightUnknownSources, lightSmsBehaviour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_result);

        // Initialize views
        progressBar = findViewById(R.id.circularProgressBar);
        percentageText = findViewById(R.id.percentageText);
        riskLevelText = findViewById(R.id.riskLevelText);

        lightAgeGroup = findViewById(R.id.light_age_group);
        lightSmsApp = findViewById(R.id.light_sms_app);
        lightSecurityApp = findViewById(R.id.light_security_app);
        lightSpamFilter = findViewById(R.id.light_spam_filter);
        lightDeviceLock = findViewById(R.id.light_device_lock);
        lightUnknownSources = findViewById(R.id.light_unknown_sources);
        lightSmsBehaviour = findViewById(R.id.light_sms_behaviour);


        RiskScannerLogic.scanHabits(this, progressBar, riskLevelText, lightAgeGroup, lightSmsApp, lightSecurityApp, lightSpamFilter, lightDeviceLock, lightUnknownSources, lightSmsBehaviour);

        //  navigation bar
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_news);

        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });

        // back button
        ImageButton report_back = findViewById(R.id.RiskScannerResult_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    // changing texts and progress bar
    public void animateProgress(ProgressBar progressBar, TextView percentageText, int score) {
        progressBar.setMax(100);

        // Animate progress bar
        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, score);
        animator.setDuration(1500);
        animator.start();

        // Animate percentage text
        ValueAnimator textAnimator = ValueAnimator.ofInt(0, score);
        textAnimator.setDuration(1500);
        textAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            percentageText.setText(animatedValue + "%");
        });
        textAnimator.start();
    }
}

