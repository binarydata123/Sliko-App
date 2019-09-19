package app.sliko.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.sliko.R;
import app.sliko.UI.SsBoldTextView;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularEditText;
import app.sliko.UI.SsRegularTextView;
import app.sliko.dialogs.models.BookPitchMauallyDialog;
import app.sliko.dialogs.models.ChangePasswordDialog;
import app.sliko.dialogs.models.DialogConfirmation;
import app.sliko.dialogs.models.StadiumInfoDialog;
import hyogeun.github.com.colorratingbarlib.ColorRatingBar;

public class DialogMethodCaller {
    public static StadiumInfoDialog openStadiumInfoDialog(final Context context, int layoutId, boolean isCancellable) {
        final View viewError = LayoutInflater.from(context).inflate(layoutId, null);
        AlertDialog dialog_error = makeDialog(context, viewError);
        dialog_error.setCancelable(isCancellable);
        LinearLayout SD_stadiumSeeDetails = viewError.findViewById(R.id.SD_stadiumSeeDetails);
        ImageView SD_stadiumImage = viewError.findViewById(R.id.SD_stadiumImage);
        ProgressBar SD_stadiumProgressDialog = viewError.findViewById(R.id.SD_stadiumProgressDialog);
        SsMediumTextView SD_stadiumName = viewError.findViewById(R.id.SD_stadiumName);
        SsRegularTextView SD_stadiumPrice = viewError.findViewById(R.id.SD_stadiumPrice);
        ColorRatingBar SD_stadiumRating = viewError.findViewById(R.id.SD_stadiumRating);
        SsRegularTextView SD_stadiumReviews = viewError.findViewById(R.id.SD_stadiumReviews);
        SsRegularTextView SD_stadiumAddress = viewError.findViewById(R.id.SD_stadiumAddress);
        LinearLayout SD_stadiumCloseButton = viewError.findViewById(R.id.SD_stadiumCloseButton);
        return new StadiumInfoDialog(dialog_error, SD_stadiumSeeDetails, SD_stadiumImage, SD_stadiumProgressDialog, SD_stadiumName, SD_stadiumPrice,
                SD_stadiumRating, SD_stadiumReviews, SD_stadiumCloseButton,SD_stadiumAddress);
    }

    public static ChangePasswordDialog openChangePasswordDialog(final Context context, int layoutId, boolean isCancellable) {
        final View viewError = LayoutInflater.from(context).inflate(layoutId, null);
        AlertDialog dialog_error = makeDialog(context, viewError);
        dialog_error.setCancelable(isCancellable);
        SsRegularEditText etEmail = viewError.findViewById(R.id.etEmail);
        SsRegularEditText etOldPassword = viewError.findViewById(R.id.etOldPassword);
        SsRegularEditText etNewPassword = viewError.findViewById(R.id.etNewPassword);
        LinearLayout cancelButton = viewError.findViewById(R.id.cancelButton);
        SsRegularButton sendButton = viewError.findViewById(R.id.sendButton);
        return new ChangePasswordDialog(dialog_error, etEmail, etOldPassword, etNewPassword,cancelButton, sendButton);
    }

    public static BookPitchMauallyDialog openBookPitchMauallyDialog(final Context context, int layoutId, boolean isCancellable) {
        final View viewError = LayoutInflater.from(context).inflate(layoutId, null);
        AlertDialog dialog_error = makeDialog(context, viewError);
        dialog_error.setCancelable(isCancellable);
        SsRegularEditText etName = viewError.findViewById(R.id.etName);
        SsRegularEditText etEmail = viewError.findViewById(R.id.etEmail);
        SsRegularEditText etPhone = viewError.findViewById(R.id.etPhone);
        SsRegularButton bookButtonLayout = viewError.findViewById(R.id.bookButtonLayout);
        LinearLayout cancelButton = viewError.findViewById(R.id.cancelButton);
        return new BookPitchMauallyDialog(dialog_error, etName, etEmail,etPhone, bookButtonLayout,cancelButton);
    }

    public static DialogConfirmation openDialogConfirmation(final Context context, int layoutId, boolean isCancellable) {
        final View viewError = LayoutInflater.from(context).inflate(layoutId, null);
        AlertDialog dialog_error = makeDialog(context, viewError);
        dialog_error.setCancelable(isCancellable);
        SsMediumTextView dialogConfirmationTitle = viewError.findViewById(R.id.dialogConfirmationTitle);
        SsRegularTextView dialogConfirmationMessage = viewError.findViewById(R.id.dialogConfirmationMessage);
        SsRegularButton okButton = viewError.findViewById(R.id.okButton);
        LinearLayout closeButton = viewError.findViewById(R.id.closeButton);
        return new DialogConfirmation(dialog_error, dialogConfirmationTitle, dialogConfirmationMessage, okButton,closeButton);
    }

    private static AlertDialog makeDialog(Context context, View view) {
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.AlertDialogDanger).create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setView(view);
        return dialog;
    }
}
