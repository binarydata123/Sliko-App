package app.navizinhanca.utils.alerts.models;

import android.app.AlertDialog;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import hyogeun.github.com.colorratingbarlib.ColorRatingBar;

public class StadiumInfoDialog {
    private AlertDialog alertDialog;
    private LinearLayout sd_stadiumSeeDetails;
    private ImageView sd_stadiumImage;
    private ProgressBar sd_stadiumProgressDialog;
    private TextView sd_stadiumName;
    private TextView sd_stadiumPrice;
    private ColorRatingBar sd_stadiumRating;
    private TextView sd_stadiumReviews;
    private TextView SD_stadiumAddress;
    private LinearLayout sd_stadiumCloseButton;

    public StadiumInfoDialog(AlertDialog alertDialog, LinearLayout sd_stadiumSeeDetails,
                             ImageView sd_stadiumImage, ProgressBar sd_stadiumProgressDialog,
                             TextView sd_stadiumName, TextView sd_stadiumPrice, ColorRatingBar sd_stadiumRating,
                             TextView sd_stadiumReviews, LinearLayout sd_stadiumCloseButton,
                             TextView SD_stadiumAddress) {
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

    public TextView getSD_stadiumAddress() {
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

    public TextView getSd_stadiumName() {
        return sd_stadiumName;
    }

    public TextView getSd_stadiumPrice() {
        return sd_stadiumPrice;
    }

    public ColorRatingBar getSd_stadiumRating() {
        return sd_stadiumRating;
    }

    public TextView getSd_stadiumReviews() {
        return sd_stadiumReviews;
    }

    public LinearLayout getSd_stadiumCloseButton() {
        return sd_stadiumCloseButton;
    }
}