package app.sliko.dialogs.models;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BookPitchMauallyDialog {

    private AlertDialog dialog_error;
    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private Button bookButtonLayout;
    private Button cancelButton;

    public BookPitchMauallyDialog(AlertDialog dialog_error, EditText etName,EditText etEmail, EditText etPhone, Button bookButtonLayout,Button cancelButton) {

        this.dialog_error = dialog_error;
        this.etName = etName;
        this.etEmail = etEmail;
        this.etPhone = etPhone;
        this.bookButtonLayout = bookButtonLayout;
        this.cancelButton = cancelButton;
    }

    public AlertDialog getDialog_error() {
        return dialog_error;
    }

    public EditText getEtName() {
        return etName;
    }

    public EditText getEtPhone() {
        return etPhone;
    }

    public EditText getEtEmail() {
        return etEmail;
    }

    public Button getBookButtonLayout() {
        return bookButtonLayout;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}