package com.example.smishingdetectionapp;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class RadarActivity extends AppCompatActivity {

    private TextView radarStatus, tipBanner, lastUpdated;
    private Handler handler = new Handler();
    private Map<String, Integer> regionCounts = new LinkedHashMap<>();
    private int index = 0;
    private String[] regionOrder;
    private BarChart barChart;
    private PieChart pieChart;
    private Spinner regionFilter;
    private String[] tips = {
            "🚫 Never click on unknown links.",
            "🔒 Enable spam filters in your messaging app.",
            "📵 Ignore suspicious SMS from unknown senders.",
            "🧠 Be cautious of messages asking for personal info."
    };
    private int tipIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        radarStatus = findViewById(R.id.radarStatus);
        tipBanner = findViewById(R.id.tipBanner);
        lastUpdated = findViewById(R.id.lastUpdated);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        regionFilter = findViewById(R.id.region_filter);

        ImageButton backButton = findViewById(R.id.radar_back);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(RadarActivity.this, MainActivity.class));
            finish();
        });

        setupRegionFilter();
        rotateTipBanner();
        fetchDetectionsPeriodically();
    }

    private void setupRegionFilter() {
        List<String> filters = new ArrayList<>();
        filters.add("All Regions");
        filters.add("Sydney");
        filters.add("Melbourne");
        filters.add("Brisbane");
        filters.add("Perth");
        filters.add("Adelaide");
        filters.add("Canberra");
        filters.add("Hobart");
        filters.add("Darwin");
        filters.add("Regional NSW");
        filters.add("Western Australia");
        filters.add("Victoria");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters);
        regionFilter.setAdapter(adapter);
    }

    private void fetchDetectionsPeriodically() {
        handler.postDelayed(() -> {
            loadDetections();
            runRegionCycle();
            generateCharts();
            fetchDetectionsPeriodically();
        }, 10000); // refresh every 10 seconds
    }

    private void loadDetections() {
        regionCounts.clear();

        DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
        db.open();
        Cursor cursor = db.getDetections();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("Phone_Number"));
                String region = mapPhoneToRegion(phone);
                if (!region.equals("Unknown Region")) {
                    regionCounts.put(region, regionCounts.getOrDefault(region, 0) + 1);
                }
            }
            cursor.close();
        }

        db.close();
        regionOrder = regionCounts.keySet().toArray(new String[0]);
    }

    private void runRegionCycle() {
        if (regionCounts.isEmpty()) {
            radarStatus.setText("No smishing activity detected.");
            return;
        }

        String selectedRegion = regionFilter.getSelectedItem().toString();
        String region = "All Regions".equals(selectedRegion) ? regionOrder[index] : selectedRegion;

        int count = regionCounts.getOrDefault(region, 0);
        String alertLevel = count >= 4 ? "🔴 High" : count >= 2 ? "⚠️ Alert" : "🟢 Low";
        radarStatus.setText(alertLevel + " activity in: " + region + " (" + count + " detections)");

        if (count >= 2) {
            animatePulse(radarStatus);  // Add pulse for Alert or High
        }

        String timestamp = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        lastUpdated.setText("Last updated: " + timestamp);

        index = (index + 1) % regionOrder.length;
    }

    private void animatePulse(View view) {
        AlphaAnimation pulse = new AlphaAnimation(0.5f, 1.0f);
        pulse.setDuration(600);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setRepeatCount(5);
        view.startAnimation(pulse);
    }

    private void generateCharts() {
        List<BarEntry> barEntries = new ArrayList<>();
        List<PieEntry> pieEntries = new ArrayList<>();
        int i = 0;
        String selected = regionFilter.getSelectedItem().toString();

        for (Map.Entry<String, Integer> entry : regionCounts.entrySet()) {
            if (!selected.equals("All Regions") && !entry.getKey().equals(selected)) continue;
            barEntries.add(new BarEntry(i++, entry.getValue()));
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Detections by Region");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        Description barDesc = new Description();
        barDesc.setText("");
        barChart.setDescription(barDesc);
        barChart.invalidate();

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(48f);
        pieChart.setEntryLabelTextSize(10f);
        pieChart.setEntryLabelColor(getResources().getColor(android.R.color.white));
        Description pieDesc = new Description();
        pieDesc.setText("");
        pieChart.setDescription(pieDesc);
        pieChart.invalidate();

        pieChart.setOnChartValueSelectedListener(new com.github.mikephil.charting.listener.OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(com.github.mikephil.charting.data.Entry e, com.github.mikephil.charting.highlight.Highlight h) {
                if (e instanceof PieEntry) {
                    showTooltipDialog(((PieEntry) e).getLabel(), (int) ((PieEntry) e).getValue());
                }
            }

            @Override
            public void onNothingSelected() { }
        });
    }

    private void rotateTipBanner() {
        tipBanner.setText(tips[tipIndex]);
        tipIndex = (tipIndex + 1) % tips.length;

        // Fade in-out animation
        AlphaAnimation fade = new AlphaAnimation(0.0f, 1.0f);
        fade.setDuration(500);
        tipBanner.startAnimation(fade);

        handler.postDelayed(this::rotateTipBanner, 3000);
    }

    private void showTooltipDialog(String region, int count) {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_region_info, null);
        ((TextView) view.findViewById(R.id.region_name)).setText("📍 " + region);
        ((TextView) view.findViewById(R.id.detection_count)).setText("🔢 Detections: " + count);
        ((TextView) view.findViewById(R.id.recommendation)).setText("💡 Avoid suspicious SMS & links.");
        dialog.setContentView(view);
        dialog.show();
    }

    private String mapPhoneToRegion(String phone) {
        if (phone == null || phone.length() < 2) return "Unknown Region";
        if (phone.startsWith("41")) return "Sydney";
        if (phone.startsWith("42")) return "Melbourne";
        if (phone.startsWith("43")) return "Brisbane";
        if (phone.startsWith("44")) return "Perth";
        if (phone.startsWith("61")) return "Adelaide";
        if (phone.startsWith("39")) return "Canberra";
        if (phone.startsWith("32")) return "Hobart";
        if (phone.startsWith("90")) return "Darwin";
        if (phone.startsWith("15") || phone.startsWith("93")) return "Regional NSW";
        if (phone.startsWith("95")) return "Western Australia";
        if (phone.startsWith("30") || phone.startsWith("31")) return "Victoria";
        return "Unknown Region";
    }
}
