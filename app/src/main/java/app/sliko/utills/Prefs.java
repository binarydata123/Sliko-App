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
    private static String playerPosition = "playerPosition";
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
    public static void savePlayerPositionData(String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(playerPosition, value);
        editor.apply();
    }
    public static String getPlayerPositionData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString(playerPosition, "");
    }


    public static void saveDays(String sun, String mon,String tues,String wed,String thurs,String fri,String sat,Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sun", sun);
        editor.putString("mon", mon);
        editor.putString("tues", tues);
        editor.putString("wed", wed);
        editor.putString("thurs", thurs);
        editor.putString("fri", fri);
        editor.putString("sat", sat);

        editor.apply();
    }

    public static String getSun(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("sun", "");
    }
    public static String getMon(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("mon", "");
    }
    public static String getTues(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("tues", "");
    }
    public static String getWed(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("wed", "");
    }
    public static String getThurs(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("thurs", "");
    }
    public static String getFri(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("fri", "");
    }
    public static String getSat(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("sat", "");
    }
/* owner side*/
public static void saveDaysowner(String sun, String mon,String tues,String wed,String thurs,String fri,String sat,Context context) {
    SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString("sun", sun);
    editor.putString("mon", mon);
    editor.putString("tues", tues);
    editor.putString("wed", wed);
    editor.putString("thurs", thurs);
    editor.putString("fri", fri);
    editor.putString("sat", sat);

    editor.apply();
}

    public static String getSuno(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("sun", "");
    }
    public static String getMono(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("mon", "");
    }
    public static String getTueso(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("tues", "");
    }
    public static String getWedo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("wed", "");
    }
    public static String getThurso(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("thurs", "");
    }
    public static String getFrio(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("fri", "");
    }
    public static String getSato(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_KEY, Activity.MODE_PRIVATE);
        return preferences.getString("sat", "");
    }
}
