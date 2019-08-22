package app.sliko.owner.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.owner.adapter.ReviewsAdapter;
import app.sliko.owner.model.ReviewModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PitchDetailActivity extends AppCompatActivity {

    @BindView(R.id.pitchesReviewsRecyclerView)
    RecyclerView recyclerViewReviews;
    @BindView(R.id.backButton)
    LinearLayout backButton;

    @BindView(R.id.sun_avail)
    ImageView sun_avail;
    @BindView(R.id.sunStartTime)
    TextView sunStartTime;
    @BindView(R.id.sunEndTime)
    TextView sunEndTime;

    @BindView(R.id.mon_avail)
    ImageView mon_avail;
    @BindView(R.id.monStartTime)
    TextView monStartTime;
    @BindView(R.id.monEndTime)
    TextView monEndTime;

    @BindView(R.id.tue_avail)
    ImageView tue_avail;
    @BindView(R.id.tueStartTime)
    TextView tueStartTime;
    @BindView(R.id.tueEndTime)
    TextView tueEndTime;

    @BindView(R.id.wed_avail)
    ImageView wed_avail;
    @BindView(R.id.wedStartTime)
    TextView wedStartTime;
    @BindView(R.id.wedEndTime)
    TextView wedEndTime;


    @BindView(R.id.thurs_avail)
    ImageView thurs_avail;
    @BindView(R.id.thursStartTime)
    TextView thursStartTime;
    @BindView(R.id.thursEndTime)
    TextView thursEndTime;

    @BindView(R.id.fri_avail)
    ImageView fri_avail;
    @BindView(R.id.friStartTime)
    TextView friStartTime;
    @BindView(R.id.friEndTime)
    TextView friEndTime;

    @BindView(R.id.sat_avail)
    ImageView sat_avail;
    @BindView(R.id.satStartTime)
    TextView satStartTime;
    @BindView(R.id.satEndTime)
    TextView satEndTime;
    @BindView(R.id.progressBarLoading)
    ProgressBar progressBarLoading;
    @BindView(R.id.pitchName)
    TextView pitchName;
    @BindView(R.id.pitchAddress)
    TextView pitchAddress;
    @BindView(R.id.pitchPrice)
    TextView pitchPrice;
    ArrayList<ReviewModel> reviewModelArrayList = new ArrayList<>();
    ReviewsAdapter reviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_detail);
        ButterKnife.bind(this);
        setAdapter();
        progressBarLoading.setVisibility(View.GONE);
        backButton.setOnClickListener(view -> finish());
    }

    private void setAdapter() {
        reviewsAdapter = new ReviewsAdapter(PitchDetailActivity.this, reviewModelArrayList);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewsAdapter);
        reviewsAdapter.notifyDataSetChanged();
    }
}
