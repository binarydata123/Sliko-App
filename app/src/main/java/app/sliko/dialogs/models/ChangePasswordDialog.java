package app.sliko.dialogs.models;

import android.app.AlertDialog;
import android.widget.LinearLayout;

import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularEditText;

public class ChangePasswordDialog {
    private AlertDialog dialog_error;
    private SsRegularEditText etUserEmail;
    private SsRegularEditText etPassword;
    private SsRegularEditText etNewPassword;
    private LinearLayout cancelButton;
    private SsRegularButton sendButton;

    public ChangePasswordDialog(AlertDialog dialog_error,
                                SsRegularEditText etUserEmail, SsRegularEditText etPassword, SsRegularEditText etNewPassword,
                                LinearLayout cancelButton, SsRegularButton sendButton) {
        this.dialog_error = dialog_error;
        this.etUserEmail = etUserEmail;
        this.etPassword = etPassword;
        this.etNewPassword = etNewPassword;
        this.cancelButton = cancelButton;
        this.sendButton = sendButton;

    }

    public AlertDialog getDialog_error() {
        return dialog_error;
    }

    public SsRegularEditText getEtUserEmail() {
        return etUserEmail;
    }

    public SsRegularEditText getEtPassword() {
        return etPassword;
    }

    public SsRegularEditText getEtNewPassword() {
        return etNewPassword;
    }

    public LinearLayout getCancelButton() {
        return cancelButton;
    }

    public SsRegularButton getSendButton() {
        return sendButton;
    }
}