package app.sliko.owner.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import app.sliko.R;
import app.sliko.adapter.StadiumImagesAdapter;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.DialogConfirmation;
import app.sliko.owner.activity.AddPitchActivity;
import app.sliko.owner.activity.AddStadiumActivity;
import app.sliko.owner.activity.EditStadiumActivity;
import app.sliko.owner.adapter.PitchAdapterOwner;
import app.sliko.owner.events.SuccessFullyStadiumCreated;
import app.sliko.owner.model.PitchModel;
import app.sliko.utills.M;
import app.sliko.web.Api;
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
    @BindView(R.id.pitchesLayout)
    TextView pitchesLayout;
    @BindView(R.id.progressBarLoading)
    ProgressBar progressBarLoading;
    @BindView(R.id.stadiumName)
    TextView stadiumName;
    @BindView(R.id.stadiumLocation)
    TextView stadiumLocation;
    @BindView(R.id.stadiumDescription)
    TextView stadiumDescription;
    @BindView(R.id.stadiumRating)
    ColorRatingBar stadiumRating;
    @BindView(R.id.createdDate)
    TextView createdDate;
    @BindView(R.id.SD_stadiumReviews)
    TextView SD_stadiumReviews;
    @BindView(R.id.editStadiumLayout)
    LinearLayout editStadiumLayout;
    @BindView(R.id.deleteStadiumLayout)
    LinearLayout deleteSadiumLayout;
    @BindView(R.id.stadiumChangeLayout)
    LinearLayout changeStadiumLayout;
    @BindView(R.id.noPitchLayout)
    LinearLayout noPitchLayout;
    private ArrayList<String> reviewsModelArrayList;
    private StadiumImagesAdapter stadiumImagesAdapter;
    private View view;
    private String is_stadium = "";
    private WeakReference<StadiumDetailsFragment> weakReference;
    private Dialog dialog;

    public static StadiumDetailsFragment newInstance() {
        Bundle args = new Bundle();
        StadiumDetailsFragment fragment = new StadiumDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null)
            updateLayout();
        setListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.owner_fragment_stadium_detail, container, false);
        ButterKnife.bind(StadiumDetailsFragment.this, view);
        dialog = M.showDialog(getActivity(), "", false);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        weakReference = new WeakReference<>(StadiumDetailsFragment.this);
        changeStadiumLayout.setVisibility(View.VISIBLE);
        return view;
    }

    private void updateLayout() {
        is_stadium = M.fetchUserTrivialInfo(getActivity(), Api.IS_STADIUM);
        Log.i(">>value", "updateLayout: " + is_stadium);
        if (is_stadium.equalsIgnoreCase("0")) {
            noStadiumLayout.setVisibility(View.VISIBLE);
            stadiumLayout.setVisibility(View.GONE);
        } else {
            noStadiumLayout.setVisibility(View.GONE);
            stadiumLayout.setVisibility(View.VISIBLE);
            fetchStadiumInfo();
            setAdapter();
        }
    }

    private ArrayList<String> pitchGalleryStringArrayList;
    String id;

    private void fetchStadiumInfo() {
        pitchModelArrayList = new ArrayList<>();
        reviewsModelArrayList = new ArrayList<>();
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_stadium_detail(M.fetchUserTrivialInfo(getActivity(), "id"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
                try {
                    reviewsModelArrayList.clear();
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("true")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String stadium_name = data.getString("stadium_name");
                            id = data.getString("id");
                            String description = data.getString("description");
                            String address = data.getString("address");
                            String pitch_review_avg = data.getString("review_avg");


                            if (pitch_review_avg.equalsIgnoreCase("null")) {
                                SD_stadiumReviews.setText(getString(R.string.noReviews));
                                stadiumRating.setVisibility(View.GONE);
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
                            stadiumLocation.setText(getString(R.string.adddress) + address);

                            createdDate.setText(getString(R.string.stadiumAddedOn) + M.formateDateTimeBoth(created_at));
                            Object stadium_gallery = data.get("stadium_gallery");
                            if (stadium_gallery instanceof JSONObject) {
                                JSONObject stadium_galleryObject = data.getJSONObject("stadium_gallery");
                                reviewsModelArrayList.add(stadium_galleryObject.getString("stadium_image"));
                            } else {
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
                                    pitchModel.setStadium_id(pitchObject.getString("stadium_id"));
                                    pitchModel.setUser_id(pitchObject.getString("user_id"));
                                    pitchGalleryStringArrayList = new ArrayList<>();
                                    JSONArray pitchGallery = pitchObject.getJSONArray("pitch_gallery");
                                    JSONObject pitchImage = pitchGallery.getJSONObject(0);
                                    pitchGalleryStringArrayList.add(pitchImage.getString("pitch_image"));
                                    pitchModel.setPitch_gallery(pitchGalleryStringArrayList);
                                    pitchModelArrayList.add(pitchModel);
                                }
                                noPitchLayout.setVisibility(View.GONE);
                            } else {
                                pitchesRecyclerView.setVisibility(View.GONE);
                                noPitchLayout.setVisibility(View.VISIBLE);
                                noPitchLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(getActivity(), AddPitchActivity.class));
                                    }
                                });
                            }


                            stadiumImagesAdapter = new StadiumImagesAdapter(getActivity(), reviewsModelArrayList);
                            viewPager.setAdapter(stadiumImagesAdapter);
                            circleIndicator.setViewPager(viewPager);
                            pitchAdapterOwner.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private PitchAdapterOwner pitchAdapterOwner;

    private void setAdapter() {
        pitchAdapterOwner = new PitchAdapterOwner(getActivity(), pitchModelArrayList);
        pitchesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pitchesRecyclerView.setAdapter(pitchAdapterOwner);

    }

    private DialogConfirmation dialogConfirmation;

    private void setListeners() {
        addStadiumButton.setOnClickListener(view -> startActivity(new Intent(getActivity(), AddStadiumActivity.class)));
        editStadiumLayout.setOnClickListener(view -> startActivity(new Intent(getActivity(), EditStadiumActivity.class).putExtra("stadium_id", id)));
        deleteSadiumLayout.setOnClickListener(view -> {
                    dialogConfirmation = DialogMethodCaller.openDialogConfirmation(getActivity(), R.layout.dialog_confirmation, false);
                    dialogConfirmation.getDialog_error().show();
                    dialogConfirmation.getDialogConfirmationTitle().setText(getString(R.string.deleteStadium));
                    dialogConfirmation.getDialogConfirmationMessage().setText(getString(R.string.sureDeleteThisStadium));
                    dialogConfirmation.getOkButton().setOnClickListener(view12 -> {
                        dialogConfirmation.getDialog_error().cancel();
                        M.updateTrivialInfo(getActivity(), Api.IS_STADIUM, Api.STADIUM_REMOVE);
                        EventBus.getDefault().postSticky(new SuccessFullyStadiumCreated(true));
                    });
                    dialogConfirmation.getCloseButton().setOnClickListener(view1 -> dialogConfirmation.getDialog_error().cancel());
                }
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(SuccessFullyStadiumCreated successFullyStadiumCreated) {
        if (successFullyStadiumCreated != null) {
            if (successFullyStadiumCreated.isStatus()) {
                updateLayout();
            }
        }
    }
}
