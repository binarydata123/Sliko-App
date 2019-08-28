package app.sliko.owner.fragment;//package app.sliko.owner.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private View view;
    private ReviewsAdapter pitchAdapterOwner;
    private ArrayList<ReviewModel> pitchModelArrayList = new ArrayList<>();

    public static AllReviewsFragment newInstance() {
        AllReviewsFragment fragment = new AllReviewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pitch_reviews, container, false);
        ButterKnife.bind(AllReviewsFragment.this, view);
        dialog = M.showDialog(getActivity(), "", false);
        //getReviewsListing();
        setAdapter();
        return view;
    }

//    private void getReviewsListing() {
//        dialog.show();
//        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
//        Call<ResponseBody> call = service.ep_pitchList("1", "2");
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//                dialog.cancel();
//                try {
//                    if (response.isSuccessful()) {
//                        String sResponse = response.body().string();
//
//                        JSONObject jsonObject = new JSONObject(sResponse);
//                        String status = jsonObject.getString("status");
//                        String message = jsonObject.getString("message");
//                        if (status.equalsIgnoreCase("true")) {
//                            JSONArray reviewArray = jsonObject.getJSONArray("data");
//                            for (int k = 0; k < reviewArray.length(); k++) {
//                                JSONObject reviewObject = reviewArray.getJSONObject(k);
//                                ReviewModel reviewModel = new ReviewModel();
//                                reviewModel.setPitch_name(reviewObject.getJSONObject("pitch_detail").getString("xyz"));
//                                reviewModel.setFullname(reviewObject.getJSONObject("pitch_detail").getString("fullname"));
//                                reviewModel.setRating(reviewObject.getString("rating"));
//                                reviewModel.setMessage(reviewObject.getString("message"));
//                                reviewModel.setCreated_at(M.formateDateTimeBoth(reviewObject.getString("created_at")));
//                                reviewModel.setPitch_image(reviewObject.getJSONObject("pitch_detail").getString("pitch_image"));
//                            }
//                            pitchAdapterOwner.notifyDataSetChanged();
//                        } else {
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
//                dialog.cancel();
//                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//    }

    private void setAdapter() {
        pitchAdapterOwner = new ReviewsAdapter(getActivity(), pitchModelArrayList);
        allReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        allReviewsRecyclerView.setAdapter(pitchAdapterOwner);
        pitchAdapterOwner.notifyDataSetChanged();

    }
}
