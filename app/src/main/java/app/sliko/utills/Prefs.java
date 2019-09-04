package app.sliko.utills;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private Prefs() {

    }

    public static final int VAR = 446;

    private static String userData = "userData";
    private static String lat = "lat";
    private static String lng = "lng";
    private static String stadiumID = "stadiumID";
    private static String APP_KEY = "APP_KEY";

    public static void saveUserData(String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(userData, value);
        editor.apply();
    }

    public static void clearUserData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();
    }

    public static String getUserData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString(userData, "");
    }

    public static void saveuserId(String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(lat, value);
        editor.apply();
    }

    public static String getuserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString(lat, "");
    }

    public static void saveLng(String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(lng, value);
        editor.apply();
    }

    public static String getLng(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString(lng, "");
    }

    public static void saveStadiumId(String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(stadiumID, value);
        editor.apply();
    }

    public static String getStadiumId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString(stadiumID, "");
    }

}
