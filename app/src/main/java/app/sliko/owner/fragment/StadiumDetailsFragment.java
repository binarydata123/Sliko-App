package app.sliko.owner.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularTextView;
import app.sliko.adapter.StadiumImagesAdapter;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.DialogConfirmation;
import app.sliko.events.ProfileUploadedSuccessEvent;
import app.sliko.events.RefreshEvent;
import app.sliko.owner.activity.AddPitchActivity;
import app.sliko.owner.activity.AddStadiumActivity;
import app.sliko.owner.adapter.PitchAdapterOwner;
import app.sliko.owner.events.StadiumExistEventOrNot;
import app.sliko.owner.events.SuccessFullyStadiumCreated;
import app.sliko.owner.model.PitchModel;
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

public class StadiumDetailsFragment extends Fragment {
    @BindView(R.id.pitchesRecyclerView)
    RecyclerView pitchesRecyclerView;
    @BindView(R.id.stadiumImagesViewPager)
    ViewPager viewPager;
    @BindView(R.id.circleIndicator)
    CircleIndicator circleIndicator;
    private ArrayList<PitchModel> pitchModelArrayList;
    @BindView(R.id.stadiumLayout)
    CoordinatorLayout stadiumLayout;
    @BindView(R.id.noStadiumLayout)
    LinearLayout noStadiumLayout;
    @BindView(R.id.addStadiumButton)
    Button addStadiumButton;
    @BindView(R.id.progressBarLoading)
    ProgressBar progressBarLoading;
    @BindView(R.id.stadiumName)
    SsRegularTextView stadiumName;
    @BindView(R.id.bookingCount)
    SsMediumTextView bookingCount;
    @BindView(R.id.amountCount)
    SsMediumTextView amountCount;
    @BindView(R.id.stadiumLocation)
    SsRegularTextView stadiumLocation;
    @BindView(R.id.stadiumDescription)
    SsRegularTextView stadiumDescription;
    @BindView(R.id.stadiumRating)
    ColorRatingBar stadiumRating;
    @BindView(R.id.createdDate)
    SsRegularTextView createdDate;
    @BindView(R.id.SD_stadiumReviews)
    SsRegularTextView SD_stadiumReviews;
    @BindView(R.id.editStadiumLayout)
    FloatingActionButton editStadiumLayout;
    @BindView(R.id.deleteStadiumLayout)
    FloatingActionButton deleteSadiumLayout;
    @BindView(R.id.stadiumChangeLayout)
    LinearLayout changeStadiumLayout;
    @BindView(R.id.noPitchLayout)
    LinearLayout noPitchLayout;
    @BindView(R.id.sortSpinner)
    Spinner sortByDays;
    @BindView(R.id.sortByPitch)
    Spinner sortByPitch;
    @BindView(R.id.sortByModeOfPayment)
    Spinner sortByModeOfPayment;
    @BindView(R.id.pitchSpinnerLayout)
    LinearLayout pitchSpinnerLayout;
    @BindView(R.id.noImagesLayout)
    LinearLayout noImagesLayout;
    @BindView(R.id.addPitch)
    SsMediumTextView addPitch;
    private ArrayList<String> reviewsModelArrayList;
    private StadiumImagesAdapter stadiumImagesAdapter;
    private View view;
    private String is_stadium = "";
    private WeakReference<StadiumDetailsFragment> weakReference;
    private Dialog dialog;

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static StadiumDetailsFragment newInstance() {
        Bundle args = new Bundle();
        StadiumDetailsFragment fragment = new StadiumDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (context != null) {
            dialog = M.showDialog(context, "", false);
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            weakReference = new WeakReference<>(StadiumDetailsFragment.this);
            changeStadiumLayout.setVisibility(View.VISIBLE);
            sortArrayList = new ArrayList<String>() {
                {
                    add("Tất cả");
                    add("Hôm qua");
                    add("7 ngày qua");
                    add("30 ngày qua");
                }
            };
            sortArrayListActualList = new ArrayList<String>() {
                {
                    add("All");
                    add("1");
                    add("7");
                    add("30");
                }
            };

            modeOfPaymentDecidingiList = new ArrayList<String>() {
                {
                    add("Tất cả");
                    add("Ngoại tuyến");
                    add("ZaloPay");
                    add("Trả tiền cho Momo");
                }
            };

            modeOfPayment = new ArrayList<String>() {
                {
                    add("All");
                    add("Offline");
                    add("ZaloPay");
                    add("Momo");
                }
            };

            sortByDays.setAdapter(M.makeSpinnerAdapterWhite(context, sortArrayList, sortByDays));

            sortByModeOfPayment.setAdapter(M.makeSpinnerAdapterWhite(context, modeOfPaymentDecidingiList, sortByModeOfPayment));

            sortByDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (days) {

                        if (sortByDays.getSelectedItemPosition() == 0) {
                            filter_days = "";
                        } else {
                            filter_days = sortArrayListActualList.get(i);

                        }
                        getCurrentStadiumData();

                    } else {
                        days = true;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            sortByModeOfPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (payment) {
                        if (sortByModeOfPayment.getSelectedItemPosition() == 0) {
                            filter_payment_type = "";
                        } else {
                            filter_payment_type = modeOfPayment.get(i);

                        }
                        getCurrentStadiumData();

                    } else {
                        payment = true;
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            sortByPitch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (pitch) {
                        if (sortByPitch.getSelectedItemPosition() != 0) {
                            pitch_id = pitchModelArrayList.get(sortByPitch.getSelectedItemPosition() - 1).getId();

                        } else {
                            pitch_id = "";
                        }
                        getCurrentStadiumData();

                    } else {
                        pitch = true;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            addPitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context, AddPitchActivity.class));
                }
            });

            setListeners();
            Log.e(">>user_id", "onViewCreated: " + M.fetchUserTrivialInfo(context, "id"));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private ArrayList<String> modeOfPayment;
    private ArrayList<String> modeOfPaymentDecidingiList;
    private ArrayList<String> sortArrayList;
    private ArrayList<String> sortArrayListActualList;

    private boolean days = false;
    private boolean pitch = false;
    private boolean payment = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.owner_fragment_stadium_detail, container, false);
        ButterKnife.bind(StadiumDetailsFragment.this, view);

        return view;
    }

    private void getCurrentStadiumData() {
        Log.e(">>data", "getCurrentStadiumData: " + M.fetchUserTrivialInfo(context, "id") + "\n" +

                Prefs.getStadiumId(context) + "\n" + pitch_id + "\n" + filter_days + "\n" + filter_payment_type);
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_getStadiumRevenueDetails(
                M.fetchUserTrivialInfo(context, "id"),
                Prefs.getStadiumId(context), pitch_id,
                filter_days,
                filter_payment_type);
        Log.e(">>detailsFrag", "getCurrentStadiumData: " + M.fetchUserTrivialInfo(context, "id")
                + "\n" +
                Prefs.getStadiumId(context) + "\n" + pitch_id + "\n" +
                filter_days + "\n" +
                filter_payment_type);
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
                            JSONObject data = jsonObject.getJSONObject("data");
                            String total_count = data.getString("total_count");
                            String total_amount = data.getString("total_amount");
                            bookingCount.setText(total_count);
                            amountCount.setText(getString(R.string.currencySymbol) + Float.parseFloat(total_amount));
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(">>code", "onResponse: " + response.code());
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(">>logError", "onResponse: " + e.getMessage());

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Log.e(">>code", "onResponse: " + t.getMessage());
            }
        });
    }

    private String filter_days = "", pitch_id = "", filter_payment_type = "";

    ArrayList<String> pitchListing = new ArrayList<>();

    private ArrayList<String> pitchGalleryStringArrayList;
    String id;

    private void fetchStadiumInfo(String response) {
        pitchListing.add("Tất cả");
        pitchModelArrayList = new ArrayList<>();
        reviewsModelArrayList = new ArrayList<>();
        try {
            reviewsModelArrayList.clear();
            JSONObject jsonObject = new JSONObject(response);
            Log.e(">>e", "fetchStadiumInfo: " + jsonObject.toString());
            String status = jsonObject.getString("status");
            String message = jsonObject.getString("message");
            if (status.equalsIgnoreCase("true")) {
                JSONObject data = jsonObject.getJSONObject("data");
                String stadium_name = data.getString("stadium_name");
                id = data.getString("id");
                Prefs.saveStadiumId(id, context);
                String description = data.getString("description");
                String address = data.getString("address");
                String pitch_review_avg = data.getString("review_avg");
                if (pitch_review_avg.equalsIgnoreCase("null")) {
                    SD_stadiumReviews.setText(getString(R.string.noReviews));
                } else {
                    String count = pitch_review_avg.equalsIgnoreCase("null") ?
                            "0"
                            : pitch_review_avg;
                    SD_stadiumReviews.setText(count + " " + getString(R.string.reviews));
                    stadiumRating.setVisibility(View.VISIBLE);
                }
                stadiumRating.setRating(pitch_review_avg.equalsIgnoreCase("null") ?
                        Float.parseFloat("0")
                        : Float.parseFloat(pitch_review_avg));
                String created_at = data.getString("created_at");
                stadiumName.setText(stadium_name);
                stadiumDescription.setText(description);
                stadiumLocation.setText(address);
                createdDate.setText(getString(R.string.stadiumAddedOn) + M.formateDateTimeBoth(created_at));
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
                        PitchModel pitchModel = new PitchModel();
                        pitchModel.setPitch_name(pitchObject.getString("pitch_name"));
                        pitchModel.setProcess_booking(pitchObject.getString("process_booking"));
                        pitchModel.setComplete_booking(pitchObject.getString("complete_booking"));
                        pitchModel.setPitch_review_avg(pitchObject.getString("pitch_review_avg"));
                        pitchModel.setId(pitchObject.getString("id"));
                        pitchModel.setPitch_type(pitchObject.getString("pitch_type"));
                        Log.e(">>pitchID", "fetchStadiumInfo: " + pitchObject.getString("id"));
                        pitchModel.setPrice(pitchObject.getString("price"));
                        pitchModel.setStadium_id(pitchObject.getString("stadium_id"));
                        pitchModel.setUser_id(pitchObject.getString("user_id"));
                        pitchGalleryStringArrayList = new ArrayList<>();
                        JSONArray pitchGallery = pitchObject.getJSONArray("pitch_gallery");
                        JSONObject pitchImage = pitchGallery.getJSONObject(0);
                        pitchGalleryStringArrayList.add(pitchImage.getString("pitch_image"));
                        pitchModel.setPitch_gallery(pitchGalleryStringArrayList);
                        pitchModelArrayList.add(pitchModel);
                        pitchListing.add(pitchObject.getString("pitch_name"));
                    }
                    sortByPitch.setAdapter(M.makeSpinnerAdapterWhite(context, pitchListing, sortByPitch));
                    setAdapter();
                    pitchAdapterOwner.notifyDataSetChanged();
                    noPitchLayout.setVisibility(View.GONE);
                    pitchSpinnerLayout.setVisibility(View.VISIBLE);
                } else {
                    pitchSpinnerLayout.setVisibility(View.GONE);
                    pitchesRecyclerView.setVisibility(View.GONE);
                    noPitchLayout.setVisibility(View.VISIBLE);
                    noPitchLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(context, AddPitchActivity.class));
                        }
                    });
                }
                stadiumImagesAdapter = new StadiumImagesAdapter(context, reviewsModelArrayList);
                viewPager.setAdapter(stadiumImagesAdapter);
                circleIndicator.setViewPager(viewPager);
                getCurrentStadiumData();
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(">>logError", "onResponse: " + e.getMessage());
        }
    }

    private PitchAdapterOwner pitchAdapterOwner;

    private void setAdapter() {

        pitchAdapterOwner = new PitchAdapterOwner(context, pitchModelArrayList);
        pitchesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        pitchesRecyclerView.setAdapter(pitchAdapterOwner);
    }

    private DialogConfirmation dialogConfirmation;

    private void setListeners() {
        addStadiumButton.setOnClickListener(view -> startActivity(new Intent(context, AddStadiumActivity.class)
                .putExtra("stadiumType", "add")));
        editStadiumLayout.setOnClickListener(view -> startActivity(new Intent(context, AddStadiumActivity.class)
                .putExtra("stadiumType", "edit")
                .putExtra("stadium_id", id)));
        deleteSadiumLayout.setOnClickListener(view -> {
                    dialogConfirmation = DialogMethodCaller.openDialogConfirmation(context, R.layout.dialog_confirmation, false);
                    dialogConfirmation.getDialog_error().show();
                    dialogConfirmation.getDialogConfirmationTitle().setText(getString(R.string.deleteStadium));
                    dialogConfirmation.getDialogConfirmationMessage().setText(getString(R.string.sureDeleteThisStadium));
                    dialogConfirmation.getOkButton().setText(getString(R.string.yes));
                    dialogConfirmation.getOkButton().setOnClickListener(view12 -> {
                        dialogConfirmation.getDialog_error().cancel();
                        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
                        Call<ResponseBody> call = service.ep_deleteStadium(id, M.fetchUserTrivialInfo(context, "id"));
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        String sResponse = response.body().string();
                                        JSONObject jsonObject = new JSONObject(sResponse);
                                        String status = jsonObject.getString("status");
                                        String message = jsonObject.getString("message");
                                        Log.e(">>data", "onResponse: " + jsonObject.toString());
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                        if (status.equalsIgnoreCase("true")) {
                                            Prefs.saveStadiumId("", context);
                                            EventBus.getDefault().postSticky(new StadiumExistEventOrNot(false, ""));
                                        }
                                    } else {
                                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    });
                    dialogConfirmation.getCloseButton().setOnClickListener(view1 -> dialogConfirmation.getDialog_error().cancel());
                }
        );
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onResponseSuccess(StadiumExistEventOrNot stadiumExistEventOrNot) {
        if (stadiumExistEventOrNot != null) {
            if (stadiumExistEventOrNot.isResponseSuccess()) {
                noStadiumLayout.setVisibility(View.GONE);
                stadiumLayout.setVisibility(View.VISIBLE);

                fetchStadiumInfo(stadiumExistEventOrNot.getReponse());
            } else {
                noStadiumLayout.setVisibility(View.VISIBLE);
                stadiumLayout.setVisibility(View.GONE);
                EventBus.getDefault().postSticky(new SuccessFullyStadiumCreated(false));
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(RefreshEvent refreshEvent) {
        if (refreshEvent != null) {
           if (refreshEvent.status==true){
               setAdapter();
           }
        }
    }


}
