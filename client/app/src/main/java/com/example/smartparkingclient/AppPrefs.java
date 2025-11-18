package com.example.smartparkingclient;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {

    private static final String PREFS_NAME = "app_prefs";

    public static final String KEY_THEME = "theme";          // "light", "dark", "neutral"
    public static final String KEY_LANG = "lang";            // "ru", "en"
    public static final String KEY_NOTIFICATIONS = "notify"; // true / false

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ---------- тема ----------
    public static String getTheme(Context context) {
        return getPrefs(context).getString(KEY_THEME, "light");
    }

    public static void setTheme(Context context, String theme) {
        getPrefs(context).edit().putString(KEY_THEME, theme).apply();
    }

    // ---------- язык ----------
    public static String getLang(Context context) {
        return getPrefs(context).getString(KEY_LANG, "ru");
    }

    public static void setLang(Context context, String lang) {
        getPrefs(context).edit().putString(KEY_LANG, lang).apply();
    }

    // ---------- уведомления ----------
    public static boolean isNotificationsEnabled(Context context) {
        return getPrefs(context).getBoolean(KEY_NOTIFICATIONS, true);
    }

    public static void setNotificationsEnabled(Context context, boolean enabled) {
        getPrefs(context).edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }
}
