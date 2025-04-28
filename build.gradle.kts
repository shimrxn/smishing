// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io") // ✅ Required for MPAndroidChart
    }
    dependencies {
        classpath("com.chaquo.python:gradle:15.0.1") // ✅ Chaquopy plugin
        // You can add other classpaths here
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}