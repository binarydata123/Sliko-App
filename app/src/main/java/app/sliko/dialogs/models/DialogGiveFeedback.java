package app.sliko.dialogs.models;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import hyogeun.github.com.colorratingbarlib.ColorRatingBar;

public class DialogGiveFeedback {
    private AlertDialog alertDialog;
    private ColorRatingBar F_userRating;
    private EditText F_description;
    private Button F_submitButton;
    private LinearLayout cancelButton;

    public DialogGiveFeedback(AlertDialog alertDialog, ColorRatingBar F_userRating, EditText F_description, Button F_submitButton, LinearLayout cancelButton) {
        this.alertDialog = alertDialog;
        this.F_userRating = F_userRating;
        this.F_description = F_description;
        this.F_submitButton = F_submitButton;
        this.cancelButton = cancelButton;
    }

    public LinearLayout getF_CloseButton() {
        return cancelButton;
    }

    public AlertDialog getAlertDialog() {
        return alertDialog;
    }

    public ColorRatingBar getF_userRating() {
        return F_userRating;
    }

    public EditText getF_description() {
        return F_description;
    }

    public Button getF_submitButton() {
        return F_submitButton;
    }
}