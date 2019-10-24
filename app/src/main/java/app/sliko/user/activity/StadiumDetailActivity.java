package app.sliko.user.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import app.sliko.activity.BookingActivity;
import app.sliko.adapter.PitchAdapterUser;
import app.sliko.adapter.StadiumDetailOpeningAdapter;
import app.sliko.adapter.StadiumImagesAdapter;
import app.sliko.adapter.StadiumOffDaysAdapter;
import app.sliko.owner.model.AvailabilityModel;
import app.sliko.utills.M;
import app.sliko.utills.Prefs;
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
    @BindView(R.id.stadiumOffDaysRecyclerView)
    RecyclerView stadiumOffDaysRecyclerView;
    @BindView(R.id.stadiumOffDaysLayout)
    LinearLayout stadiumOffDaysLayout;
    @BindView(R.id.noImagesLayout)
    LinearLayout noImagesLayout;
    @BindView(R.id.stadiumPhone)
    SsRegularTextView stadiumPhone;
    @BindView(R.id.timeslotset)
    SsRegularTextView timeslotset;
@BindView(R.id.timingText)
    TextView timingText;
@BindView(R.id.cardid)
    CardView cardid;
/*@BindView(R.id.bookButton)
    SsRegularButton bookButton;*/

    PitchAdapterUser pitchAdapterUser;
    ArrayList<app.sliko.owner.model.PitchModel> pitchModelArrayList = new ArrayList<>();

    String stadium_id, stadium_name;
    String user_id;
    String lowestPrice;
    private ArrayList<String> reviewsModelArrayList = new ArrayList<>();
    private ArrayList<String> pitchGalleryStringArrayList = new ArrayList<>();
    StadiumImagesAdapter stadiumImagesAdapter;
    String chksunday,chkmonday,chktuesday,chkwednesday,chkthursday,chkfriday,chksaturday;

    Dialog dialog;
    String firsttime="",lasttime="";


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
       /* bookButton.setOnClickListener(view -> startActivity(new Intent(StadiumDetailActivity.this, BookingActivity.class)
                .putExtra("pitch_id", pitch_id)
                .putExtra("user_id", user_id)
                .putExtra("stadium_id", stadium_id)));*/
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    ArrayList<AvailabilityModel> availabilityModelArrayList = new ArrayList<>();
    StadiumDetailOpeningAdapter stadiumOpeningAdapter;
//nitiitin01n@mailinator.com
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

                            String check_mon = data.getString("check_mon");
                            String phone_number = data.getString("phone_number");
                            stadiumPhone.setText(M.actAccordinglyWithJson(StadiumDetailActivity.this, phone_number));
                            String check_tue = data.getString("check_tue");
                            String check_wed = data.getString("check_wed");
                            String check_thu = data.getString("check_thu");
                            String check_fri = data.getString("check_fri");
                            String check_sat = data.getString("check_sat");
                            String check_sun = data.getString("check_sun");
                            ArrayList<String> offDayCheck = new ArrayList<>();
                            offDayCheck.add(check_mon);
                            offDayCheck.add(check_tue);
                            offDayCheck.add(check_wed);
                            offDayCheck.add(check_thu);
                            offDayCheck.add(check_fri);
                            offDayCheck.add(check_sat);
                            offDayCheck.add(check_sun);

                            Log.e("values check days","==="+offDayCheck);
                            ArrayList<String> offDays = new ArrayList<String>() {
                                {
                                    add("Mon");
                                    add("Tues");
                                    add("Wed");
                                    add("Thurs");
                                    add("Fri");
                                    add("Sat");
                                    add("Sun");
                                }
                            };

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
                                    pitchModel.setPitch_type(pitchObject.getString("pitch_type"));
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
                                 firsttime = availabilityModelArrayList.get(0).getTime();
                                String[] separated = firsttime.split("-");
                                String s1=     separated[0].trim();
                                String s2=    separated[1].trim();
                            //    Log.e("s1s2","=="+s1+"<><>"+s2);

                                 lasttime =availabilityModelArrayList.get(availabilityModelArrayList.size()-1).getTime();
                                String[] separated2 = lasttime.split("-");
                                String s11=     separated2[0].trim();
                                String s12=    separated2[1].trim();
                               Log.e("s1s2","=="+s1+"<><>"+s12);

                                timingText.setText(s1+" - "+s12);
                             //   Log.e("first","===="+firsttime+'\n'+"===="+lasttime);
                              //  timingText.setText(s1);




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


                            stadiumOffDayAdapter = new StadiumOffDaysAdapter(StadiumDetailActivity.this, offDayCheck, offDays, firsttime, lasttime, new StadiumOffDaysAdapter.TimeSlotListnerCustom() {
                                @Override
                                public void timeSetMethod(int position, String str,boolean chkrecyvisibilityslot) {
                                    if (chkrecyvisibilityslot==false){
                                        Log.e("chk slot","<><>"+str);
                                        stadiumOffDaysRecyclerView.setVisibility(View.GONE);

                                        cardid.setVisibility(View.VISIBLE);
                                        timeslotset.setText(str);
                                    }else {
                                        Log.e("chk slot else","<><>");

                                        stadiumOffDaysRecyclerView.setVisibility(View.VISIBLE);
                                        cardid.setVisibility(View.GONE);
                                    }

                                }

                                @Override
                                public void chkDaysNotBooked(String sun, String mon, String tues, String wed, String thurs, String fri, String sat) {
                                    chksunday= sun;
                                    chkmonday= mon;
                                    chktuesday = tues;
                                    chkwednesday = wed;
                                    chkthursday = thurs;
                                    chkfriday = fri;
                                    chksaturday = sat;
                                 //   Log.e("ALL DAYS ","<><>"+sun+'\n'+mon+'\n'+tues+'\n'+wed+'\n'+thurs+'\n'+fri+'\n'+sat);

                                }
                            });
                            stadiumOffDaysRecyclerView.setLayoutManager(new GridLayoutManager(StadiumDetailActivity.this, 3));
                            stadiumOffDaysRecyclerView.setAdapter(stadiumOffDayAdapter);
                            Prefs.saveDays(check_sun,check_mon,check_tue,check_wed,check_thu,check_fri,check_sat,StadiumDetailActivity.this);
                            Log.e("getDa1ys","=="+ Prefs.getSun(StadiumDetailActivity.this));
                            Log.e("ge2tDays","=="+ Prefs.getMon(StadiumDetailActivity.this));
                            Log.e("ge3tDays","=="+ Prefs.getTues(StadiumDetailActivity.this));
                            Log.e("ge4tDays","=="+ Prefs.getWed(StadiumDetailActivity.this));
                            Log.e("get5Days","=="+ Prefs.getThurs(StadiumDetailActivity.this));
                            Log.e("getD6ays","=="+ Prefs.getFri(StadiumDetailActivity.this));
                            Log.e("getD7ays","=="+ Prefs.getSat(StadiumDetailActivity.this));
                            stadiumOffDayAdapter.notifyDataSetChanged();
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

    StadiumOffDaysAdapter stadiumOffDayAdapter;

    private void setAdapter() {
        pitchAdapterUser = new PitchAdapterUser(StadiumDetailActivity.this, pitchModelArrayList);
        pitchesRecyclerView.setLayoutManager(new LinearLayoutManager(StadiumDetailActivity.this));
        pitchesRecyclerView.setAdapter(pitchAdapterUser);
        pitchAdapterUser.notifyDataSetChanged();
    }
}
