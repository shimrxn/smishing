package com.example.smishingdetectionapp.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.smishingdetectionapp.R;

public class SmishingWidgetLarge extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences("SmishingPrefs", Context.MODE_PRIVATE);

        int detectionCount = prefs.getInt("detectionCount", 0);
        String profileType = prefs.getString("profileLabel", "New User");
        String trend = prefs.getString("weeklyTrend", "—");
        int confidence = prefs.getInt("confidence", 0);
        int safeDays = prefs.getInt("safeDays", 0);

        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_large);

            views.setTextViewText(R.id.widget_app_title, "Smishing Detection App");
            views.setTextViewText(R.id.widget_risk_score, "Detections: " + detectionCount);
            views.setTextViewText(R.id.widget_profile_type, "User Type: " + profileType);
            views.setTextViewText(R.id.widget_streak, "Safe Days: " + safeDays);

            manager.updateAppWidget(id, views);
        }
    }
}
