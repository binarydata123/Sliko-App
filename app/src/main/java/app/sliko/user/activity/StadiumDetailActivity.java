package app.sliko.user.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularTextView;
import app.sliko.adapter.PitchAdapterUser;
import app.sliko.adapter.StadiumDetailOpeningAdapter;
import app.sliko.adapter.StadiumImagesAdapter;
import app.sliko.owner.model.AvailabilityModel;
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

public class StadiumDetailActivity extends AppCompatActivity {
    @BindView(R.id.pitchesRecyclerView)
    RecyclerView pitchesRecyclerView;
    @BindView(R.id.stadiumImagesViewPager)
    ViewPager viewPager;
    @BindView(R.id.circleIndicator)
    CircleIndicator circleIndicator;


    @BindView(R.id.toolbarTitle)
    SsMediumTextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.stadiumName)
    SsMediumTextView stadiumName;
    @BindView(R.id.stadiumDescription)
    SsRegularTextView stadiumDescription;
    @BindView(R.id.stadiumAddress)
    SsRegularTextView stadiumAddress;
    @BindView(R.id.stadiumPrice)
    SsRegularTextView stadiumPrice;
    @BindView(R.id.stadiumRating)
    ColorRatingBar stadiumRating;
    @BindView(R.id.stadiumRatingCount)
    SsRegularTextView stadiumRatingCount;
    @BindView(R.id.stadiumCreatedDate)
    SsRegularTextView stadiumCreatedDate;
    @BindView(R.id.noPitchLayout)
    LinearLayout noPitchLayout;
    @BindView(R.id.stadiumAvailabilityLayout)
    LinearLayout stadiumAvailabilityLayout;
    @BindView(R.id.stadiumAvailabilityRecyclerView)
    RecyclerView stadiumAvailabilityRecyclerView;
    @BindView(R.id.noImagesLayout)
    LinearLayout noImagesLayout;

    PitchAdapterUser pitchAdapterUser;
    ArrayList<app.sliko.owner.model.PitchModel> pitchModelArrayList = new ArrayList<>();

    String stadium_id, stadium_name;
    String user_id;
    String lowestPrice;
    private ArrayList<String> reviewsModelArrayList = new ArrayList<>();
    private ArrayList<String> pitchGalleryStringArrayList = new ArrayList<>();
    StadiumImagesAdapter stadiumImagesAdapter;

    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_staidium_detail);
        ButterKnife.bind(StadiumDetailActivity.this);
        stadiumAvailabilityLayout.setVisibility(View.VISIBLE);
        dialog = M.showDialog(StadiumDetailActivity.this, "", false);
        stadium_id = getIntent().getStringExtra("stadium_id");
        stadium_name = getIntent().getStringExtra("stadium_name");
        user_id = getIntent().getStringExtra("user_id");
        lowestPrice = getIntent().getStringExtra("lowestPrice");
        setAdapter();
        reviewsModelArrayList.clear();

        fetchStadiumInfo(user_id);
        Log.e(">>idInStadiumDetails", "onCreate: " + user_id + "\n" + stadium_id
        );
        toolbarTitle.setText(stadium_name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    ArrayList<AvailabilityModel> availabilityModelArrayList = new ArrayList<>();
    StadiumDetailOpeningAdapter stadiumOpeningAdapter;

    private void fetchStadiumInfo(String user_id) {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_stadium_detail(user_id);
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
                            JSONObject data = jsonObject.getJSONObject("data");
                            String stadium_name = data.getString("stadium_name");
                            String id = data.getString("id");
                            String description = data.getString("description");
                            String address = data.getString("address");
                            String pitch_review_avg = data.getString("review_avg");

                            if (pitch_review_avg.equalsIgnoreCase("null")) {
                                stadiumRatingCount.setText(getString(R.string.noReviews));
                            } else {
                                String count = pitch_review_avg.equalsIgnoreCase("null") ?
                                        "0"
                                        : pitch_review_avg;
                                stadiumRatingCount.setText(count + " " + getString(R.string.reviews));
                            }
                            stadiumRating.setRating(pitch_review_avg.equalsIgnoreCase("null") ?
                                    Float.parseFloat("0")
                                    : Float.parseFloat(pitch_review_avg));
                            String created_at = data.getString("created_at");
                            stadiumPrice.setText(M.actAccordinglyWithJson(StadiumDetailActivity.this, lowestPrice));
                            stadiumName.setText(stadium_name);
                            stadiumDescription.setText("Description: " + description);
                            stadiumAddress.setText(getString(R.string.adddress) + address);
                            stadiumCreatedDate.setText(M.returnDateOnly(created_at));
                            Object stadium_gallery = data.get("stadium_gallery");
                            if (stadium_gallery instanceof JSONObject) {
                                noImagesLayout.setVisibility(View.VISIBLE);
                            } else {
                                noImagesLayout.setVisibility(View.GONE);
                                JSONArray stadium_galleryArray = data.getJSONArray("stadium_gallery");
                                for (int k = 0; k < stadium_galleryArray.length(); k++) {
                                    JSONObject stadiumImages = stadium_galleryArray.getJSONObject(k);
                                    Log.i(">>ataImage", "onResponse: " + stadiumImages.getString("stadium_image"));
                                    reviewsModelArrayList.add(stadiumImages.getString("stadium_image"));
                                }
                            }
                            JSONArray pitchArray = data.getJSONArray("pitch_listing");
                            if (pitchArray.length() > 0) {
                                for (int k = 0; k < pitchArray.length(); k++) {
                                    JSONObject pitchObject = pitchArray.getJSONObject(k);
                                    app.sliko.owner.model.PitchModel pitchModel = new app.sliko.owner.model.PitchModel();
                                    pitchModel.setPitch_name(pitchObject.getString("pitch_name"));
                                    pitchModel.setProcess_booking(pitchObject.getString("process_booking"));
                                    pitchModel.setComplete_booking(pitchObject.getString("complete_booking"));
                                    pitchModel.setPitch_review_avg(pitchObject.getString("pitch_review_avg"));
                                    pitchModel.setId(pitchObject.getString("id"));
                                    pitchModel.setPrice(pitchObject.getString("price"));
                                    pitchModel.setStadium_id(pitchObject.getString("stadium_id"));
                                    pitchModel.setUser_id(pitchObject.getString("user_id"));
                                    pitchGalleryStringArrayList = new ArrayList<>();
                                    if (pitchObject.get("pitch_gallery") instanceof JSONObject) {
                                        pitchGalleryStringArrayList.add(pitchObject.getJSONObject("pitch_gallery").getString("pitch_image"));
                                        pitchModel.setPitch_gallery(pitchGalleryStringArrayList);
                                        pitchModelArrayList.add(pitchModel);
                                    } else {
                                        JSONArray pitchGallery = pitchObject.getJSONArray("pitch_gallery");
                                        JSONObject pitchImage = pitchGallery.getJSONObject(0);
                                        pitchGalleryStringArrayList.add(pitchImage.getString("pitch_image"));
                                        pitchModel.setPitch_gallery(pitchGalleryStringArrayList);
                                        pitchModelArrayList.add(pitchModel);
                                    }

                                }
                                setAdapter();
                                noPitchLayout.setVisibility(View.GONE);
                            } else {
                                pitchesRecyclerView.setVisibility(View.GONE);
                                noPitchLayout.setVisibility(View.VISIBLE);
                            }
                            String slotInterval = data.getString("slot_intervel");
                            List<String> slotIntervalList = Arrays.asList(slotInterval.split(","));
                            for (int k = 0; k < slotIntervalList.size(); k++) {
                                AvailabilityModel availabilityModel = new AvailabilityModel();
                                availabilityModel.setTime(slotIntervalList.get(k));
                                availabilityModelArrayList.add(availabilityModel);
                            }
                            if (availabilityModelArrayList.size() > 0) {
                                stadiumOpeningAdapter = new StadiumDetailOpeningAdapter(StadiumDetailActivity.this, availabilityModelArrayList, "detailView");
                                stadiumAvailabilityRecyclerView.setLayoutManager(new GridLayoutManager(StadiumDetailActivity.this, 3));
                                stadiumAvailabilityRecyclerView.setAdapter(stadiumOpeningAdapter);
                                stadiumOpeningAdapter.notifyDataSetChanged();
                            } else {
                                stadiumAvailabilityLayout.setVisibility(View.GONE);
                            }
                            stadiumImagesAdapter = new StadiumImagesAdapter(StadiumDetailActivity.this, reviewsModelArrayList);
                            viewPager.setAdapter(stadiumImagesAdapter);
                            circleIndicator.setViewPager(viewPager);
                        } else {
                            Toast.makeText(StadiumDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(StadiumDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(StadiumDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(StadiumDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter() {
        pitchAdapterUser = new PitchAdapterUser(StadiumDetailActivity.this, pitchModelArrayList);
        pitchesRecyclerView.setLayoutManager(new LinearLayoutManager(StadiumDetailActivity.this));
        pitchesRecyclerView.setAdapter(pitchAdapterUser);
        pitchAdapterUser.notifyDataSetChanged();
    }
}
