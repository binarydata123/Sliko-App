package app.sliko.owner.activity;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import app.sliko.R;
import app.sliko.adapter.StadiumImagesAdapter;
import app.sliko.owner.adapter.ReviewsAdapter;
import app.sliko.owner.model.ReviewModel;
import app.sliko.utills.M;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import hyogeun.github.com.colorratingbarlib.ColorRatingBar;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PitchDetailActivity extends AppCompatActivity {
    @BindView(R.id.stadiumImagesViewPager)
    ViewPager pitchImagesViewPager;
    @BindView(R.id.circleIndicator)
    CircleIndicator circleIndicator;
    @BindView(R.id.backButton)
    LinearLayout backButton;
    @BindView(R.id.totalBookingLayout)
    LinearLayout totalBookingLayout;

    @BindView(R.id.pitchName)
    TextView pitchName;
    @BindView(R.id.pitchPrice)
    TextView pitchPrice;
    @BindView(R.id.stadiumName)
    TextView stadiumName;

    @BindView(R.id.stadiumDescription)
    TextView stadiumDescription;
    @BindView(R.id.stadiumAddress)
    TextView stadiumAddress;
    @BindView(R.id.upcomingBookings)
    TextView upcomingBookings;

    @BindView(R.id.completeBookings)
    TextView completeBookings;
    @BindView(R.id.pitchRatingCount)
    TextView pitchRatingCount;
    @BindView(R.id.pitchDescription)
    TextView pitchDescription;
    @BindView(R.id.pitchRating)
    ColorRatingBar pitchRating;
    @BindView(R.id.pitchesReviewsRecyclerView)
    RecyclerView pitchesReviewsRecyclerView;
    @BindView(R.id.progressBarLoading)
    ProgressBar progressBarLoading;
    @BindView(R.id.noDataLayout)
    LinearLayout noDataLayout;
    @BindView(R.id.bookButton)
    Button bookButton;
    ReviewsAdapter reviewsAdapter;
    Dialog dialog;

    String pitch_id = "";
    String stadium_id = "";
    String user_id = "";
    StadiumImagesAdapter stadiumImagesAdapter;
    ArrayList<String> pitchImagesArrayList = new ArrayList<>();
    ArrayList<ReviewModel> reviewModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_detail);
        ButterKnife.bind(this);
        weakReference = new WeakReference<>(PitchDetailActivity.this);

        totalBookingLayout.setVisibility(View.GONE);
        dialog = M.showDialog(this, "", false);
        pitch_id = getIntent().getStringExtra("pitch_id");
        stadium_id = getIntent().getStringExtra("stadium_id");
        user_id = getIntent().getStringExtra("user_id");
        Log.e(">>pitchDetailId", "onCreate: " + pitch_id + "\n" + stadium_id);
        backButton.setOnClickListener(view -> finish());
        getSinglePitchDetail();
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        new LoadReviews(weakReference).execute();
    }

    private void setAdapter() {
        reviewsAdapter = new ReviewsAdapter(PitchDetailActivity.this, reviewModelArrayList);
        pitchesReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pitchesReviewsRecyclerView.setAdapter(reviewsAdapter);
    }

    private void getSinglePitchDetail() {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_pitchDetail(pitch_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
                try {
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Log.e(">>stadiumDetails", "onResponse: " + jsonObject.toString());
                        if (status.equalsIgnoreCase("true")) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            pitchName.setText(M.actAccordinglyWithJson(PitchDetailActivity.this, dataObject.getString("pitch_name")));
                            pitchDescription.setText(getString(R.string.description) + M.actAccordinglyWithJson(PitchDetailActivity.this, dataObject.getString("description")));
                            pitchPrice.setText(getString(R.string.price) + M.actAccordinglyWithJson(PitchDetailActivity.this, dataObject.getString("price")));
                            stadiumName.setText(M.actAccordinglyWithJson(PitchDetailActivity.this, dataObject.getJSONObject("stadium").getString("stadium_name")));
                            stadiumDescription.setText(M.actAccordinglyWithJson(PitchDetailActivity.this, dataObject.getJSONObject("stadium").getString("description")));
                            stadiumAddress.setText(M.actAccordinglyWithJson(PitchDetailActivity.this, dataObject.getJSONObject("stadium").getString("address")));

                            if (dataObject.getString("pitch_review_avg").equalsIgnoreCase("null")) {
                                pitchRatingCount.setText(getString(R.string.noReviews));
                            } else {
                                String count = dataObject.getString("pitch_review_avg").equalsIgnoreCase("null") ?
                                        "0"
                                        : dataObject.getString("pitch_review_avg");
                                pitchRatingCount.setText(count + " " + getString(R.string.reviews));
                            }
                            pitchRating.setRating(dataObject.getString("pitch_review_avg").equalsIgnoreCase("null") ?
                                    Float.parseFloat("0")
                                    : Float.parseFloat(dataObject.getString("pitch_review_avg")));
                            Object stadium_gallery = dataObject.get("pitch_gallery");
                            if (stadium_gallery instanceof JSONObject) {
                                JSONObject stadiumImages = dataObject.getJSONObject("pitch_gallery");
                                pitchImagesArrayList.add(stadiumImages.getString("pitch_image"));
                            } else {
                                JSONArray stadium_galleryArray = dataObject.getJSONArray("pitch_gallery");
                                for (int k = 0; k < stadium_galleryArray.length(); k++) {
                                    JSONObject stadiumImages = stadium_galleryArray.getJSONObject(k);
                                    Log.i(">>ataImage", "onResponse: " + stadiumImages.getString("pitch_image"));
                                    pitchImagesArrayList.add(stadiumImages.getString("pitch_image"));
                                }
                            }
                            stadiumImagesAdapter = new StadiumImagesAdapter(PitchDetailActivity.this, pitchImagesArrayList);
                            pitchImagesViewPager.setAdapter(stadiumImagesAdapter);
                            circleIndicator.setViewPager(pitchImagesViewPager);
                        } else {
                            Toast.makeText(PitchDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PitchDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(PitchDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(PitchDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    WeakReference<PitchDetailActivity> weakReference;

    private static class LoadReviews extends AsyncTask<Void, Void, Void> {
        WeakReference<PitchDetailActivity> weakReference;

        private LoadReviews(WeakReference<PitchDetailActivity> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakReference.get().progressBarLoading.setVisibility(View.VISIBLE);
            weakReference.get().setAdapter();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            weakReference.get().reviewsAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            weakReference.get().getReviewsListing();
            return null;
        }
    }

    private void getReviewsListing() {
        reviewModelArrayList = new ArrayList<>();
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_reviewsForPitch(user_id, stadium_id, pitch_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
                try {
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("true")) {
                            JSONArray reviewArray = jsonObject.getJSONArray("data");
                            if (reviewArray.length() > 0) {
                                for (int k = 0; k < reviewArray.length(); k++) {
                                    JSONObject reviewObject = reviewArray.getJSONObject(k);
                                    ReviewModel reviewModel = new ReviewModel();
                                    reviewModel.setPitch_name(reviewObject.getJSONObject("pitch_detail").getString("xyz"));
                                    reviewModel.setFullname(reviewObject.getJSONObject("pitch_detail").getString("fullname"));
                                    reviewModel.setRating(reviewObject.getString("rating"));
                                    reviewModel.setMessage(reviewObject.getString("message"));
                                    reviewModel.setCreated_at(M.formateDateTimeBoth(reviewObject.getString("created_at")));
                                    reviewModel.setPitch_image(reviewObject.getJSONObject("pitch_detail").getString("pitch_image"));
                                    reviewModelArrayList.add(reviewModel);
                                }
                                reviewsAdapter.notifyDataSetChanged();
                                noDataLayout.setVisibility(View.GONE);
                            } else {
                                handleNoRecord();
                            }
                        } else {
                            handleNoRecord();
                        }
                    } else {
                        Toast.makeText(PitchDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(PitchDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(PitchDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleNoRecord() {
        noDataLayout.setVisibility(View.VISIBLE);
    }
}
