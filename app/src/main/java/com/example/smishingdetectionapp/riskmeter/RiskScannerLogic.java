package com.example.smishingdetectionapp.riskmeter;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.content.pm.PackageManager;
import android.app.KeyguardManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.example.smishingdetectionapp.R;

public class RiskScannerLogic {

    private static int calculatedScore = 0;

    public static void scanHabits(RiskResultActivity activity, ProgressBar progressBar, TextView riskLevelText,
                                  View lightAgeGroup, View lightSmsApp, View lightSecurityApp, View lightSpamFilter,
                                  View lightDeviceLock, View lightUnknownSources, View lightSmsBehaviour) {

        int totalScore = 0;

        // age group (this is hardcoded for now as safe)
        int userAge = 45;
        int ageGroupRisk = calculateAgeGroupRisk(userAge);
        totalScore += ageGroupRisk;
        setLightColor(lightAgeGroup, getColorForRisk(ageGroupRisk));

        // does user receive suspicious text? Yes - 15% risk and No - 0%
        boolean smsBehaviorRisk = checkSmsBehavior(activity);
        totalScore += smsBehaviorRisk ? 15 : 0;
        setLightColor(lightSmsBehaviour, smsBehaviorRisk ? "#EF5350" : "#66BB6A");

        // does user have security apps installed? Yes - 0% risk and No - 14% risk
        boolean securityAppInstalled = checkSecurityApps(activity);
        totalScore += securityAppInstalled ? 0 : 14;
        setLightColor(lightSecurityApp, securityAppInstalled ? "#66BB6A" : "#EF5350");

        // does user have spam filter app installed? Yes - 0% risk and No - 14% risk
        boolean spamFilterInstalled = checkSpamFilterApp(activity);
        totalScore += spamFilterInstalled ? 0 : 14;
        setLightColor(lightSpamFilter, spamFilterInstalled ? "#66BB6A" : "#EF5350");

        // does the phone have a password/pin? Yes - 0% risk and No - 14% risk
        boolean deviceSecured = checkDeviceLock(activity);
        totalScore += deviceSecured ? 0 : 14;
        setLightColor(lightDeviceLock, deviceSecured ? "#66BB6A" : "#EF5350");

        // does user have the option to install apps from unknown sources? Yes - 14% risk and No - 0% risk
        boolean unknownSourcesEnabled = checkUnknownSources(activity);
        totalScore += unknownSourcesEnabled ? 14 : 0;
        setLightColor(lightUnknownSources, unknownSourcesEnabled ? "#EF5350" : "#66BB6A");

        // does user have a trusted app used for messaging? Yes - 0% risk and No - 14% risk
        boolean trustedSmsApp = checkTrustedSmsApp(activity);
        totalScore += trustedSmsApp ? 0 : 14;
        setLightColor(lightSmsApp, trustedSmsApp ? "#66BB6A" : "#EF5350");

        // capping at 100%
        if (totalScore > 100) totalScore = 100;


        calculatedScore = totalScore;
        updateRiskLevel(riskLevelText, totalScore);
        updateProgressBarColor(progressBar, totalScore);

        activity.animateProgress(progressBar, activity.percentageText, totalScore);
    }

    // update the progress bar color and text
    private static void updateProgressBarColor(ProgressBar progressBar, int score) {
        if (score <= 30) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(progressBar.getContext(), R.color.green));
        } else if (score <= 60) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(progressBar.getContext(), R.color.orange));
        } else {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(progressBar.getContext(), R.color.redd));
        }
    }

    // update the risk level text
    private static void updateRiskLevel(TextView riskLevelText, int totalScore) {
        String riskLevel;
        if (totalScore <= 30) {
            riskLevel = "Low Risk";
        } else if (totalScore <= 60) {
            riskLevel = "Moderate Risk";
        } else {
            riskLevel = "High Risk";
        }
        riskLevelText.setText(riskLevel);
    }


    private static String getColorForRisk(int risk) {
        if (risk <= 30) return "#66BB6A";
        if (risk <= 60) return "#FFEB3B";
        return "#EF5350";
    }

    // age group scoring
    private static int calculateAgeGroupRisk(int age) {
        if (age >= 18 && age <= 24) return 15;
        if (age >= 25 && age <= 34) return 10;
        return 0;
    }


    private static boolean checkSmsBehavior(Context context) {
        // aaliyan
        return false;  //
    }

    private static boolean checkSecurityApps(Context context) {
        PackageManager pm = context.getPackageManager();
        String[] knownSecurityApps = {"com.norton.mobilesecurity", "com.mcafee.android", "com.bitdefender.antivirus"};
        for (String app : knownSecurityApps) {
            try {
                pm.getPackageInfo(app, 0);  //  heck if security apps are  installed
                return true;
            } catch (PackageManager.NameNotFoundException ignored) { }
        }
        return false;  // no security apps
    }

    private static boolean checkSpamFilterApp(Context context) {
        PackageManager pm = context.getPackageManager();
        String[] spamFilters = {"com.google.android.apps.messaging", "com.mrnumber.blocker", "com.truecaller"};
        for (String app : spamFilters) {
            try {
                pm.getPackageInfo(app, 0);  // check if spam filter app are installed
                return true;
            } catch (PackageManager.NameNotFoundException ignored) { }
        }
        return false;  // no
    }

    // Checking if the phone has a password/pin
    private static boolean checkDeviceLock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km != null && km.isDeviceSecure();  // does phone secured with PIN or password
    }


    private static boolean checkUnknownSources(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // checking if apps have the permission to request installations from unknown sources
            return context.getPackageManager().canRequestPackageInstalls();
        } else {
            // older versions
            try {
                return android.provider.Settings.Secure.getInt(context.getContentResolver(),
                        android.provider.Settings.Secure.INSTALL_NON_MARKET_APPS) == 1;
            } catch (Settings.SettingNotFoundException e) {
                return false;
            }
        }
    }

    // is the default messaging app safe
    private static boolean checkTrustedSmsApp(Context context) {
        String defaultSmsApp = Settings.Secure.getString(context.getContentResolver(), "sms_default_application");
        return defaultSmsApp != null && (
                defaultSmsApp.contains("messages") || defaultSmsApp.contains("samsung")  // Google Messages and Samsung Messages are trusted
        );
    }

    public static int getCalculatedScore() {
        return calculatedScore;
    }

    private static void setLightColor(View view, String color) {
        view.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(color)));
    }
}
