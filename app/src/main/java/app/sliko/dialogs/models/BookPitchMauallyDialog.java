package app.sliko.dialogs.models;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BookPitchMauallyDialog {

    private AlertDialog dialog_error;
    private TextView etName;
    private TextView etPhone;
    private Button bookButtonLayout;
    private Button cancelButton;

    public BookPitchMauallyDialog(AlertDialog dialog_error, TextView etName, TextView etPhone, Button bookButtonLayout,Button cancelButton) {

        this.dialog_error = dialog_error;
        this.etName = etName;
        this.etPhone = etPhone;
        this.bookButtonLayout = bookButtonLayout;
        this.cancelButton = cancelButton;
    }

    public AlertDialog getDialog_error() {
        return dialog_error;
    }

    public TextView getEtName() {
        return etName;
    }

    public TextView getEtPhone() {
        return etPhone;
    }

    public Button getBookButtonLayout() {
        return bookButtonLayout;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}