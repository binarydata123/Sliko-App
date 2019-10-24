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

import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularEditText;
import app.sliko.UI.SsRegularTextView;
import app.sliko.dialogs.models.AfterConfirmPaymentModels;
import app.sliko.dialogs.models.BookPitchMauallyDialog;
import app.sliko.dialogs.models.ChangePasswordDialog;
import app.sliko.dialogs.models.DialogConfirmation;
import app.sliko.dialogs.models.DialogGiveFeedback;
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
        SsRegularTextView txtime = viewError.findViewById(R.id.txtime);
        SsRegularTextView txtdate = viewError.findViewById(R.id.txtdate);
        SsRegularButton bookButtonLayout = viewError.findViewById(R.id.bookButtonLayout);
        LinearLayout cancelButton = viewError.findViewById(R.id.cancelButton);
        return new BookPitchMauallyDialog(dialog_error, etName, etEmail,etPhone, bookButtonLayout,cancelButton,txtdate,txtime);
    }
    public static AfterConfirmPaymentModels afterConfirmPayment(final Context context, int layoutId, boolean isCancellable) {
        final View viewError = LayoutInflater.from(context).inflate(layoutId, null);
        AlertDialog dialog_error = makeDialog(context, viewError);
        dialog_error.setCancelable(isCancellable);

        SsRegularTextView stadiumName = viewError.findViewById(R.id.stadiumName);
        SsRegularTextView pitchName = viewError.findViewById(R.id.pitchName);
        SsRegularTextView pitchCost = viewError.findViewById(R.id.pitchCost);
        SsRegularTextView pitchTimeSlot = viewError.findViewById(R.id.pitchTimeSlot);
        SsRegularTextView pitchbookingDate = viewError.findViewById(R.id.pitchbookingDate);
        SsRegularButton closeButton = viewError.findViewById(R.id.closeButton);
        SsRegularTextView thankYouMessage = viewError.findViewById(R.id.thankYouMessage);
        return new AfterConfirmPaymentModels(dialog_error, stadiumName, pitchName,pitchCost, pitchTimeSlot,pitchbookingDate,closeButton,thankYouMessage);
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

    public static DialogGiveFeedback openDialogGiveFeedback(final Context context, int layoutId) {
        final View viewError = LayoutInflater.from(context).inflate(layoutId, null);
        AlertDialog dialog_error = makeDialog(context, viewError);
        dialog_error.setCancelable(false);
        ColorRatingBar F_userRating = viewError.findViewById(R.id.F_userRating);
        EditText F_description = viewError.findViewById(R.id.F_description);
        Button F_submitButton = viewError.findViewById(R.id.F_submitButton);
        LinearLayout F_CloseButton = viewError.findViewById(R.id.cancelButton);
        return new DialogGiveFeedback(dialog_error, F_userRating, F_description, F_submitButton, F_CloseButton);
    }
}
