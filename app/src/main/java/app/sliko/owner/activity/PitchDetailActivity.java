package app.sliko.owner.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONObject;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.adapter.StadiumImagesAdapter;
import app.sliko.owner.adapter.ReviewsAdapter;
import app.sliko.owner.model.ReviewModel;
import app.sliko.utills.M;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class PitchDetailActivity extends AppCompatActivity {
    @BindView(R.id.stadiumImagesViewPager)
    ViewPager pitchImagesViewPager;
    @BindView(R.id.circleIndicator)
    CircleIndicator circleIndicator;
    @BindView(R.id.backButton)
    LinearLayout backButton;
    @BindView(R.id.pitchesReviewsRecyclerView)
    RecyclerView recyclerViewReviews;

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
    ArrayList<String> imgarray = new ArrayList<>();
    ReviewsAdapter reviewsAdapter;
    StadiumImagesAdapter stadiumImagesAdapter;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_detail);
        ButterKnife.bind(this);
        setAdapter();
        progressBarLoading.setVisibility(View.GONE);
        backButton.setOnClickListener(view -> finish());
        dialog = M.showDialog(this, "", false);
        getSinglePitchDetail();
        imgarray.clear();
        imgarray.add("https://images2.minutemediacdn.com/image/upload/c_fill,w_912,h_516,f_auto,q_auto,g_auto/shape/cover/sport/5c0fcde7d2f4cdf8c9000003.jpeg");
        imgarray.add("https://images2.minutemediacdn.com/image/upload/c_fill,w_912,h_516,f_auto,q_auto,g_auto/shape/cover/sport/5c0fcde7d2f4cdf8c9000003.jpeg");
        imgarray.add("https://www.insidesport.co/wp-content/uploads/2018/04/4-11-1.jpg");
        stadiumImagesAdapter = new StadiumImagesAdapter(PitchDetailActivity.this, imgarray);
        pitchImagesViewPager.setAdapter(stadiumImagesAdapter);
        circleIndicator.setViewPager(pitchImagesViewPager);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setAdapter() {
        reviewsAdapter = new ReviewsAdapter(PitchDetailActivity.this, reviewModelArrayList);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewsAdapter);
        reviewsAdapter.notifyDataSetChanged();
    }

    /* get Single Pitch Detail*/
    private void getSinglePitchDetail() {
//        dialog.show();
//        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
//        Call<ResponseBody> call = service.getSinglePitchDetail("", "");
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//                dialog.cancel();
//                if (response.isSuccessful()) {
//                    try {
//                        if (response.isSuccessful()) {
//                            String sResponse = response.body().toString();
//
//                            JSONObject jsonObject = new JSONObject(sResponse);
//                            String status = jsonObject.getString("status");
//                            String message = jsonObject.getString("message");
//
//
//                            if (status.equalsIgnoreCase("true")) {
//                                /*json object data */
//
//                                JSONObject jsondata = jsonObject.getJSONObject("data");
//                                AddPitchModel addPitchModel = new AddPitchModel();
//                                addPitchModel.setCreatedAt(jsondata.getString("created_at"));
//                                addPitchModel.setDescription(jsondata.getString("description"));
//                                addPitchModel.setId(jsondata.getInt("id"));
//                                addPitchModel.setPitchName(jsondata.getString("pitch_name"));
//                                addPitchModel.setPrice(jsondata.getString("price"));
//                                addPitchModel.setStadiumId(jsondata.getString("stadium_id"));
//                                addPitchModel.setUpdatedAt(jsondata.getString("updated_at"));
//                                addPitchModel.setUserId(jsondata.getString("user_id"));
//
//                                pitchAddress.setText(jsondata.getString("description"));
//                                pitchPrice.setText(jsondata.getString("price"));
//
//                                /* pitch_availability  */
//                                JSONObject jsonpitch_availability = jsondata.getJSONObject("pitch_availability");
//                                addPitchModel.setPitchAvailability(jsondata.getJSONObject("pitch_availability"));
//                                setAvailability(jsonpitch_availability);
//                                /*pitch Images*/
//                                imgarray = new ArrayList<>();
//                                JSONArray jsonimages = jsondata.getJSONArray("pitch_gallery");
//                                PitchGallery pitchGallery = new PitchGallery();
//                                for (int i = 0; i < jsonimages.length(); i++) {
//                                    JSONObject jsonIMG = jsonimages.getJSONObject(i);
//                                    pitchGallery.setCreatedAt(jsonIMG.getString("created_at"));
//                                    pitchGallery.setId(jsonIMG.getString("id"));
//                                    pitchGallery.setIsdeleted(jsonIMG.getString("isdeleted"));
//                                    pitchGallery.setPitchId(jsonIMG.getString("pitch_id"));
//                                    pitchGallery.setPitchImage(jsonIMG.getString("pitch_image"));
//                                    pitchGallery.setStatus(jsonIMG.getString("status"));
//                                    pitchGallery.setUpdatedAt(jsonIMG.getString("updated_at"));
//                                    arraylistpitchgallery.add(pitchGallery);
//                                    imgarray.add(jsonIMG.getString("pitch_image"));
//                                }
//                                addPitchModel.setPitchGallery(arraylistpitchgallery);
//                                stadiumImagesAdapter = new StadiumImagesAdapter(PitchDetailActivity.this, imgarray);
//                                pitchImagesViewPager.setAdapter(stadiumImagesAdapter);
//
//                            } else {
//                                Toast.makeText(PitchDetailActivity.this, message, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(PitchDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                        }
//
//
//                    } catch (Exception e) {
//                        Log.e(">>exception", "onResponse: " + e.getMessage() + "\n" + response.errorBody().toString());
//
//                        Toast.makeText(PitchDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(PitchDetailActivity.this, response.toString() + "\n" + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
//                    Log.e(">>response", "onResponse: " + response.toString() + "\n" + response.errorBody().toString());
//                }
//            }
//
//
//            @Override
//            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
//                dialog.cancel();
//                Toast.makeText(PitchDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    /*Pitch  Availability  */
    private void setAvailability(JSONObject jsonAvailb) {
        try {
            sunStartTime.setText(jsonAvailb.getString("sunday_start"));
            sunEndTime.setText(jsonAvailb.getString("sunday_end"));
            monStartTime.setText(jsonAvailb.getString("monday_start"));
            monEndTime.setText(jsonAvailb.getString("monday_end"));
            tueStartTime.setText(jsonAvailb.getString("tusday_start"));
            tueEndTime.setText(jsonAvailb.getString("tusday_end"));
            wedStartTime.setText(jsonAvailb.getString("wednesday_start"));
            wedEndTime.setText(jsonAvailb.getString("wednesday_end"));
            thursStartTime.setText(jsonAvailb.getString("thursday_start"));
            thursEndTime.setText(jsonAvailb.getString("thursday_end"));
            friStartTime.setText(jsonAvailb.getString("friday_start"));
            friEndTime.setText(jsonAvailb.getString("friday_end"));
            satStartTime.setText(jsonAvailb.getString("saturday_start"));//missing key
            satEndTime.setText(jsonAvailb.getString("saturday_end"));
            if (jsonAvailb.getString("day_monday").equals("1")) {
                mon_avail.setBackgroundResource(R.drawable.green_circle);

            } else {
                mon_avail.setBackgroundResource(R.drawable.green_circle);

            }
            if (jsonAvailb.getString("day_tusday").equals("1")) {
                tue_avail.setBackgroundResource(R.drawable.green_circle);
            } else {
                tue_avail.setBackgroundResource(R.drawable.red_circle);

            }
            if (jsonAvailb.getString("day_wednesday").equals("1")) {
                wed_avail.setBackgroundResource(R.drawable.green_circle);

            } else {
                wed_avail.setBackgroundResource(R.drawable.green_circle);

            }
            if (jsonAvailb.getString("day_thursday").equals("1")) {
                thurs_avail.setBackgroundResource(R.drawable.green_circle);

            } else {
                thurs_avail.setBackgroundResource(R.drawable.green_circle);

            }
            if (jsonAvailb.getString("day_friday").equals("1")) {
                fri_avail.setBackgroundResource(R.drawable.green_circle);

            } else {
                fri_avail.setBackgroundResource(R.drawable.green_circle);

            }
            if (jsonAvailb.getString("day_saturday").equals("1")) {
                sat_avail.setBackgroundResource(R.drawable.green_circle);

            } else {
                sat_avail.setBackgroundResource(R.drawable.green_circle);

            }
            if (jsonAvailb.getString("day_sunday").equals("1")) {
                sun_avail.setBackgroundResource(R.drawable.green_circle);

            } else {
                sun_avail.setBackgroundResource(R.drawable.green_circle);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
