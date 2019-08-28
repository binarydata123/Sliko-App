package app.sliko.dialogs.models;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogConfirmation {
    private AlertDialog dialog_error;
    private TextView dialogConfirmationTitle;
    private TextView dialogConfirmationMessage;
    private Button okButton;
    private LinearLayout closeButton;

    public DialogConfirmation(AlertDialog dialog_error, TextView dialogConfirmationTitle,
                              TextView dialogConfirmationMessage, Button okButton, LinearLayout closeButton) {
        this.dialog_error = dialog_error;
        this.dialogConfirmationTitle = dialogConfirmationTitle;
        this.dialogConfirmationMessage = dialogConfirmationMessage;
        this.okButton = okButton;
        this.closeButton = closeButton;
    }

    public AlertDialog getDialog_error() {
        return dialog_error;
    }

    public TextView getDialogConfirmationTitle() {
        return dialogConfirmationTitle;
    }

    public TextView getDialogConfirmationMessage() {
        return dialogConfirmationMessage;
    }

    public Button getOkButton() {
        return okButton;
    }

    public LinearLayout getCloseButton() {
        return closeButton;
    }
}
