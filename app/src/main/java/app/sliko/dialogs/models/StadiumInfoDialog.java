package app.sliko.dialogs.models;

import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import app.sliko.UI.SsBoldTextView;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularTextView;
import hyogeun.github.com.colorratingbarlib.ColorRatingBar;

public class StadiumInfoDialog {
    private AlertDialog alertDialog;
    private LinearLayout sd_stadiumSeeDetails;
    private ImageView sd_stadiumImage;
    private ProgressBar sd_stadiumProgressDialog;
    private SsMediumTextView sd_stadiumName;
    private SsRegularTextView sd_stadiumPrice;
    private ColorRatingBar sd_stadiumRating;
    private SsRegularTextView sd_stadiumReviews;
    private SsRegularTextView SD_stadiumAddress;
    private LinearLayout sd_stadiumCloseButton;

    public StadiumInfoDialog(AlertDialog alertDialog, LinearLayout sd_stadiumSeeDetails,
                             ImageView sd_stadiumImage, ProgressBar sd_stadiumProgressDialog,
                             SsMediumTextView sd_stadiumName, SsRegularTextView sd_stadiumPrice, ColorRatingBar sd_stadiumRating,
                             SsRegularTextView sd_stadiumReviews, LinearLayout sd_stadiumCloseButton,
                             SsRegularTextView SD_stadiumAddress) {
        this.alertDialog = alertDialog;
        this.sd_stadiumSeeDetails = sd_stadiumSeeDetails;
        this.sd_stadiumImage = sd_stadiumImage;
        this.sd_stadiumProgressDialog = sd_stadiumProgressDialog;
        this.sd_stadiumName = sd_stadiumName;
        this.sd_stadiumPrice = sd_stadiumPrice;
        this.sd_stadiumRating = sd_stadiumRating;
        this.sd_stadiumReviews = sd_stadiumReviews;
        this.sd_stadiumCloseButton = sd_stadiumCloseButton;
        this.SD_stadiumAddress = SD_stadiumAddress;
    }

    public SsRegularTextView getSD_stadiumAddress() {
        return SD_stadiumAddress;
    }

    public AlertDialog getAlertDialog() {
        return alertDialog;
    }

    public LinearLayout getSd_stadiumSeeDetails() {
        return sd_stadiumSeeDetails;
    }

    public ImageView getSd_stadiumImage() {
        return sd_stadiumImage;
    }

    public ProgressBar getSd_stadiumProgressDialog() {
        return sd_stadiumProgressDialog;
    }

    public SsMediumTextView getSd_stadiumName() {
        return sd_stadiumName;
    }

    public SsRegularTextView getSd_stadiumPrice() {
        return sd_stadiumPrice;
    }

    public ColorRatingBar getSd_stadiumRating() {
        return sd_stadiumRating;
    }

    public SsRegularTextView getSd_stadiumReviews() {
        return sd_stadiumReviews;
    }

    public LinearLayout getSd_stadiumCloseButton() {
        return sd_stadiumCloseButton;
    }
}
