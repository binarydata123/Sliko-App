package app.sliko.dialogs.models;

import android.app.AlertDialog;
import android.widget.LinearLayout;

import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularTextView;

public class DialogConfirmation {
    private AlertDialog dialog_error;
    private SsMediumTextView dialogConfirmationTitle;
    private SsRegularTextView dialogConfirmationMessage;
    private SsRegularButton okButton;
    private LinearLayout closeButton;

    public DialogConfirmation(AlertDialog dialog_error, SsMediumTextView dialogConfirmationTitle,
                              SsRegularTextView dialogConfirmationMessage, SsRegularButton okButton, LinearLayout closeButton) {
        this.dialog_error = dialog_error;
        this.dialogConfirmationTitle = dialogConfirmationTitle;
        this.dialogConfirmationMessage = dialogConfirmationMessage;
        this.okButton = okButton;
        this.closeButton = closeButton;
    }

    public AlertDialog getDialog_error() {
        return dialog_error;
    }

    public SsMediumTextView getDialogConfirmationTitle() {
        return dialogConfirmationTitle;
    }

    public SsRegularTextView getDialogConfirmationMessage() {
        return dialogConfirmationMessage;
    }

    public SsRegularButton getOkButton() {
        return okButton;
    }

    public LinearLayout getCloseButton() {
        return closeButton;
    }
}
