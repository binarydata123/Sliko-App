package app.sliko.dialogs.models;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChangePasswordDialog {
    private AlertDialog dialog_error;
    private EditText etUserEmail;
    private EditText etPassword;
    private EditText etNewPassword;
    private Button cancelButton;
    private Button sendButton;

    public ChangePasswordDialog(AlertDialog dialog_error,
                                EditText etUserEmail, EditText etPassword, EditText etNewPassword,
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

    public EditText getEtUserEmail() {
        return etUserEmail;
    }

    public EditText getEtPassword() {
        return etPassword;
    }

    public EditText getEtNewPassword() {
        return etNewPassword;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getSendButton() {
        return sendButton;
    }
}