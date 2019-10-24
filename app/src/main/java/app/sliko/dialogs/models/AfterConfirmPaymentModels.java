package app.sliko.dialogs.models;

import android.app.AlertDialog;

import app.sliko.R;
import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularTextView;
import butterknife.BindView;

public class AfterConfirmPaymentModels {
    public AlertDialog getDialog_error() {
        return dialog_error;
    }

    private AlertDialog dialog_error;
    public AfterConfirmPaymentModels(AlertDialog dialog_error,SsRegularTextView stadiumName, SsRegularTextView pitchName, SsRegularTextView pitchCost, SsRegularTextView pitchTimeSlot, SsRegularTextView pitchbookingDate, SsRegularButton closeButton, SsRegularTextView thankYouMessage) {
        this.dialog_error = dialog_error;
        this.stadiumName = stadiumName;
        this.pitchName = pitchName;
        this.pitchCost = pitchCost;
        this.pitchTimeSlot = pitchTimeSlot;
        this.pitchbookingDate = pitchbookingDate;
        this.closeButton = closeButton;
        this.thankYouMessage = thankYouMessage;
    }

    SsRegularTextView stadiumName;
    SsRegularTextView pitchName;

    public SsRegularTextView getStadiumName() {
        return stadiumName;
    }

    public SsRegularTextView getPitchName() {
        return pitchName;
    }

    public SsRegularTextView getPitchCost() {
        return pitchCost;
    }

    public SsRegularTextView getPitchTimeSlot() {
        return pitchTimeSlot;
    }

    public SsRegularTextView getPitchbookingDate() {
        return pitchbookingDate;
    }

    public SsRegularButton getCloseButton() {
        return closeButton;
    }

    public SsRegularTextView getThankYouMessage() {
        return thankYouMessage;
    }

    SsRegularTextView pitchCost;
    SsRegularTextView pitchTimeSlot;
    SsRegularTextView pitchbookingDate;
    SsRegularButton closeButton;
    SsRegularTextView thankYouMessage;
}
