package app.sliko.dialogs.models;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularEditText;
import app.sliko.UI.SsRegularTextView;

public class BookPitchMauallyDialog {

    private AlertDialog dialog_error;
    private SsRegularEditText etName;
    private SsRegularEditText etEmail;
    private SsRegularEditText etPhone;
    private SsRegularButton bookButtonLayout;
    private LinearLayout cancelButton;

    public SsRegularTextView getTxtdate() {
        return txtdate;
    }

    public SsRegularTextView getTxttime() {
        return txttime;
    }

    private SsRegularTextView txtdate;
private  SsRegularTextView txttime;
    public BookPitchMauallyDialog(AlertDialog dialog_error, SsRegularEditText etName,SsRegularEditText etEmail, SsRegularEditText etPhone,
                                  SsRegularButton bookButtonLayout,LinearLayout cancelButton,SsRegularTextView txtdate,SsRegularTextView txttime) {

        this.dialog_error = dialog_error;
        this.etName = etName;
        this.etEmail = etEmail;
        this.etPhone = etPhone;
        this.bookButtonLayout = bookButtonLayout;
        this.cancelButton = cancelButton;
        this.txtdate=txtdate;
        this.txttime= txttime;
    }

    public AlertDialog getDialog_error() {
        return dialog_error;
    }

    public SsRegularEditText getEtName() {
        return etName;
    }

    public SsRegularEditText getEtPhone() {
        return etPhone;
    }

    public SsRegularEditText getEtEmail() {
        return etEmail;
    }

    public SsRegularButton getBookButtonLayout() {
        return bookButtonLayout;
    }

    public LinearLayout getCancelButton() {
        return cancelButton;
    }
}