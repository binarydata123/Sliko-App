package app.sliko.dialogs.models;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChangePasswordDialog {
    private AlertDialog dialog_error;
    private TextView etUserEmail;
    private TextView etPassword;
    private Button cancelButton;
    private Button sendButton;

    public ChangePasswordDialog(AlertDialog dialog_error,
                                TextView etUserEmail, TextView etPassword,
                                Button cancelButton, Button sendButton) {
        this.dialog_error = dialog_error;
        this.etUserEmail = etUserEmail;
        this.etPassword = etPassword;
        this.cancelButton = cancelButton;
        this.sendButton = sendButton;

    }

    public AlertDialog getDialog_error() {
        return dialog_error;
    }

    public TextView getEtUserEmail() {
        return etUserEmail;
    }

    public TextView getEtPassword() {
        return etPassword;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getSendButton() {
        return sendButton;
    }
}