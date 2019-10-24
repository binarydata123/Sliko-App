package app.sliko.owner.fragment;//package app.sliko.owner.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.owner.adapter.ReviewsAdapter;
import app.sliko.owner.model.ReviewModel;
import app.sliko.utills.M;
import app.sliko.utills.Prefs;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllReviewsFragment extends Fragment {
    @BindView(R.id.allReviewsRecyclerView)
    RecyclerView allReviewsRecyclerView;

    @BindView(R.id.noDataLayout)
    LinearLayout noDataLayout;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.text)
    TextView text;

    private View view;
    private ReviewsAdapter reviewsAdapter;
    private ArrayList<ReviewModel> pitchModelArrayList;

    public static AllReviewsFragment newInstance() {
        AllReviewsFragment fragment = new AllReviewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Context context;

    Dialog dialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (context != null) {
            dialog = M.showDialog(context, "", false);
            getReviewsListing();
            setAdapter();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pitch_reviews, container, false);
        ButterKnife.bind(AllReviewsFragment.this, view);

        return view;
    }

    private void getReviewsListing() {
        pitchModelArrayList = new ArrayList<>();
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_reviewsAll(M.fetchUserTrivialInfo(context, "id"), Prefs.getStadiumId(context));
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
                                    pitchModelArrayList.add(reviewModel);
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
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(">>logError", "onResponse: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleNoRecord() {
        noDataLayout.setVisibility(View.VISIBLE);
        image.setBackgroundResource(R.drawable.ic_star);
        text.setText(getString(R.string.noRatingFound));
    }

    private void setAdapter() {
        reviewsAdapter = new ReviewsAdapter(context, pitchModelArrayList);
        allReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        allReviewsRecyclerView.setAdapter(reviewsAdapter);
        reviewsAdapter.notifyDataSetChanged();

    }
}
