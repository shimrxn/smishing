package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.smishingdetectionapp.PulseInjectorKt.injectPulsing;

public class RiskScannerActivity extends AppCompatActivity {

    private TextView scanningText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riskscanner);

        ComposeView pulseView = findViewById(R.id.pulseComposeView);
        injectPulsing(pulseView);

        scanningText = findViewById(R.id.scanningText);

        // Initial visibility
        scanningText.setVisibility(View.VISIBLE);
        pulseView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(RiskScannerActivity.this, RiskResultActivity.class);
            startActivity(intent);
            finish();
        }, 9000);

        // Bottom navigation logic
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

        // Back button
        ImageButton report_back = findViewById(R.id.RiskScanner_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, RiskScannerTCActivity.class));
            finish();
        });
    }
}
