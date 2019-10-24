package app.sliko.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import app.sliko.R;
import app.sliko.UI.SsRegularTextView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class AfterPayConfirmActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbarTitle)
    app.sliko.UI.SsMediumTextView toolbarTitle;
    @BindView(R.id.closeButton)
    app.sliko.UI.SsRegularButton closeButton;
    @BindView(R.id.thankYouMessage)
    app.sliko.UI.SsRegularTextView thankYouMessage;
    @BindView(R.id.successOrFailureImage)
    ImageView successOrFailureImage;
    Handler handler;
    Runnable r;


    @BindView(R.id.stadiumName)
    SsRegularTextView stadiumName;
    @BindView(R.id.pitchName)
    SsRegularTextView pitchName;
    @BindView(R.id.pitchCost)
    SsRegularTextView pitchCost;
    @BindView(R.id.pitchTimeSlot)
    SsRegularTextView pitchTimeSlot;
    @BindView(R.id.pitchbookingDate)
    SsRegularTextView pitchbookingDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);
        ButterKnife.bind(AfterPayConfirmActivity.this);
        toolbarTitle.setText(getString(R.string.confirmingPaymemt));
      //  closeButton.setOnClickListener(view -> onBackPressed());
        closeButton.setOnClickListener(view -> finish());

        final LinearLayout transitionsContainer = findViewById(R.id.transitionContainer);
        handler = new Handler();
        r = () -> {
            TransitionManager.beginDelayedTransition(transitionsContainer);
            thankYouMessage.setVisibility(View.VISIBLE);
            successOrFailureImage.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.VISIBLE);
            pitchName.setVisibility(View.VISIBLE);
            pitchCost.setVisibility(View.VISIBLE);
            pitchTimeSlot.setVisibility(View.VISIBLE);
            pitchbookingDate.setVisibility(View.VISIBLE);
            stadiumName.setVisibility(View.VISIBLE);

        };
        handler.postDelayed(r, 1500);
        pitchName.setText(getIntent().getStringExtra("pitchName"));
        pitchCost.setText(getString(R.string.paidAmount) + "" + getIntent().getStringExtra("cost"));
        pitchTimeSlot.setText(getIntent().getStringExtra("time"));
        pitchbookingDate.setText(getIntent().getStringExtra("bookingDate"));
        stadiumName.setText(getIntent().getStringExtra("stadiumName"));


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

               onBackPressed();
                //the current activity will get finished.
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(r);

    }
}
