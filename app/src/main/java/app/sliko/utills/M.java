package app.sliko.utills;

import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.sliko.R;


public class M {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static String user_id = "user_id";

    public static Dialog showDialog(Context context, String message, boolean canCancel) {
        return CustomProgress.show(context, message, canCancel);
    }

    public static final String removeMultipleRegex = "";
    private static JSONObject userPreference;
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        String currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean matchValidation(EditText editText) {
        return (editText.getText().toString().trim().length() == 0 || editText.length() == 0);
    }


    //for data that is saved during login like user data
    public static String actAccordingly(Context context, String receivedString) {
        return (M.fetchUserTrivialInfo(context, receivedString).equalsIgnoreCase("") ||
                M.fetchUserTrivialInfo(context, receivedString).equalsIgnoreCase("null")
        ) ? "N.A"
                : M.fetchUserTrivialInfo(context, receivedString);
    }


    //for live data
    public static String actAccordinglyWithJson(Context context, String receivedString) {
        return (receivedString.equalsIgnoreCase("") ||
                receivedString.equalsIgnoreCase("null")
        ) ? "N.A"
                : receivedString;
    }

    //for Floating Point Data
    public static float actAccordinglyWithJson(String receivedString) {
        return (receivedString.equalsIgnoreCase("") ||
                receivedString.equalsIgnoreCase("null")
        ) ? 0
                : Float.parseFloat(receivedString);
    }

    public static String fetchUserTrivialInfo(Context context, String receivedVar) {
        try {
            userPreference = new JSONObject(Prefs.getUserData(context));
            return userPreference.getString(receivedVar);
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return "";
    }

    public static boolean hasTrivialInfo(Context context, String receivedVar) {
        try {
            userPreference = new JSONObject(Prefs.getUserData(context));
            return userPreference.has(receivedVar);
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return false;
    }

    public static String updateTrivialInfo(Context context, String receivedVar, String updatedValue) {
        try {
            userPreference = new JSONObject(Prefs.getUserData(context));
            userPreference.put(receivedVar, updatedValue);
            Prefs.saveUserData(userPreference.toString(), context);
            return userPreference.getString(receivedVar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String returnDateOnly(String inputString) {
        Log.i(">>inputDate", "changeDateFormat: " + inputString);
        String[] date1 = new String[1];
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(inputString);
            str = outputFormat.format(date);
            date1 = str.split(" ");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1[0];
    }

    public static String formateDateTimeBoth(String inputString) {
        Log.i(">>inputDate", "changeDateFormat: " + inputString);
        String[] date1 = new String[1];
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(inputString);
            str = outputFormat.format(date);
            date1 = str.split(" ");
            Log.i(">>method", "changeTimeFormat: " + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void hideUnhideErrorLables(EditText editText, final TextView errorLabel, final boolean hide) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorLabel.setVisibility(hide ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public static String upDateUserTrivialInfo(Context context, String key, String newVal) {
        try {
            userPreference = new JSONObject(Prefs.getUserData(context));
            userPreference.put(key, newVal);
            Prefs.saveUserData(userPreference.toString(), context);
            Log.i(">>val", "upDateUserTrivialInfo: " + userPreference.toString());
            return userPreference.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static ArrayAdapter<String> makeSpinnerAdapterWhite(Context context, ArrayList<String> spinnerArray, Spinner spinner) {
        ArrayAdapter<String> aa = new ArrayAdapter<String>(context, R.layout.text_view_white, spinnerArray);
        aa.setDropDownViewResource(R.layout.drop_down_list_item_white);
        spinner.setAdapter(aa);
        return aa;
    }

}
