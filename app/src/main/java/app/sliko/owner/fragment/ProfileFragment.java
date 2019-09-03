package app.sliko.owner.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import app.sliko.R;
import app.sliko.utills.M;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private View view;


    @BindView(R.id.userImage)
    CircleImageView userImage;
    @BindView(R.id.etName)
    TextView etName;
    @BindView(R.id.etEmail)
    TextView etEmail;
    @BindView(R.id.etPhone)
    TextView etPhone;
    @BindView(R.id.etAddress)
    TextView etAddress;
    @BindView(R.id.etFavouriteTeam)
    TextView etFavouriteTeam;
    @BindView(R.id.etPlayPosition)
    TextView etPlayPosition;
    @BindView(R.id.editProfileButton)
    Button editProfileButton;   @BindView(R.id.progressImage)
    ProgressBar progressImage;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(ProfileFragment.this, view);
        dialog = M.showDialog(getActivity(), "", false);

        fetchProfileInfo();
        return view;
    }

    private void fetchProfileInfo() {
        dialog.show();
        String userID =M.fetchUserTrivialInfo(getActivity(), "id");
        Log.i(">>user_id", "fetchProfileInfo: "  + userID);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_getUserProfile(M.fetchUserTrivialInfo(getActivity(), "id"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
                try {
                    if(response.isSuccessful()){
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        String message = jsonObject.getString("message");
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("true")) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            progressImage.setVisibility(View.VISIBLE);
                            Picasso.get().load(dataObject.getString("profilepic")).error(R.drawable.user).into(userImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    progressImage.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    progressImage.setVisibility(View.GONE);
                                }
                            });
                            etName.setText(M.actAccordinglyWithJson(getActivity() , dataObject.getString("fullname")));
                            etEmail.setText(M.actAccordinglyWithJson(getActivity() , dataObject.getString("email")));
                            etPhone.setText(M.actAccordinglyWithJson(getActivity() , dataObject.getString("phone")));
                            etAddress.setText(M.actAccordinglyWithJson(getActivity() , dataObject.getString("address")));
                            etFavouriteTeam.setText(M.actAccordinglyWithJson(getActivity() , dataObject.getString("favourite_team")));
                            etPlayPosition.setText(M.actAccordinglyWithJson(getActivity() , dataObject.getString("play_position")));
                        }else{
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    }else{
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


}
