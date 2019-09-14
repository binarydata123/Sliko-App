package app.sliko.owner.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import app.sliko.EditProfileActivity;
import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.ChangePasswordDialog;
import app.sliko.events.ProfileUploadedSuccessEvent;
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

    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
    FloatingActionButton editProfileButton;
    @BindView(R.id.progressImage)
    ProgressBar progressImage;
    @BindView(R.id.etHeight)
    TextView etHeight;
    @BindView(R.id.etWeight)
    TextView etWeight;
    @BindView(R.id.etFootedness)
    TextView etFootedness;
    @BindView(R.id.changePasswordButton)
    Button changePasswordButton;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Dialog dialog;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ProfileUploadedSuccessEvent profileUploadedSuccessEvent) {
        if (profileUploadedSuccessEvent != null) {
            if (profileUploadedSuccessEvent.isStatus()) {
                    fetchProfileInfo();
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(ProfileFragment.this, view);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        dialog = M.showDialog(getActivity(), "", false);
        toolbar.setVisibility(View.GONE);
        progressImage.setVisibility(View.VISIBLE);
        fetchProfileInfo();
        setListener();
        return view;
    }

    ChangePasswordDialog changePasswordDialog;

    private void setListener() {
        editProfileButton.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), EditProfileActivity.class));

        });
        changePasswordButton.setOnClickListener(view -> {
            changePasswordDialog = DialogMethodCaller.openChangePasswordDialog(getActivity(), R.layout.dialog_change_password, false);
            changePasswordDialog.getDialog_error().show();
            changePasswordDialog.getCancelButton().setOnClickListener(view1 -> changePasswordDialog.getDialog_error().dismiss());
            changePasswordDialog.getSendButton().setOnClickListener(view12 -> {
                if (M.matchValidation(changePasswordDialog.getEtUserEmail())) {
                    Toast.makeText(getActivity(), getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show();
                } else if (!M.validateEmail(changePasswordDialog.getEtUserEmail().getText().toString())) {
                    Toast.makeText(getActivity(), getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show();
                } else if (M.matchValidation(changePasswordDialog.getEtPassword())) {
                    Toast.makeText(getActivity(), getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();

                } else if ((changePasswordDialog.getEtPassword().length() < 6)) {
                    Toast.makeText(getActivity(), getString(R.string.password_should_big), Toast.LENGTH_SHORT).show();

                } else if (M.matchValidation(changePasswordDialog.getEtNewPassword())) {
                    Toast.makeText(getActivity(), getString(R.string.please_enter_new_password), Toast.LENGTH_SHORT).show();

                } else if ((changePasswordDialog.getEtNewPassword().length() < 6)) {
                    Toast.makeText(getActivity(), getString(R.string.password_should_big), Toast.LENGTH_SHORT).show();

                } else {
                    changePasswordDialog.getDialog_error().dismiss();
                    hitResetPassword(changePasswordDialog.getEtUserEmail().getText().toString()
                            , changePasswordDialog.getEtPassword().getText().toString(), changePasswordDialog.getEtNewPassword().getText().toString());

                }
            });
        });
    }

    private void hitResetPassword(String email, String oldPassword, String newPassword) {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_resetPassword(M.fetchUserTrivialInfo(getActivity(), "id"), email, oldPassword, newPassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
                try {
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        Log.i(">>loginData", "onResponse: " + sResponse.toString());
                        JSONObject jsonObject = new JSONObject(sResponse);
                        String message = jsonObject.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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

    private void fetchProfileInfo() {
        dialog.show();
        String userID = M.fetchUserTrivialInfo(getActivity(), "id");
        Log.i(">>user_id", "fetchProfileInfo: " + userID);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_getUserProfile(M.fetchUserTrivialInfo(getActivity(), "id"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
                try {
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        String message = jsonObject.getString("message");
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("true")) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");

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
                            etName.setText(M.actAccordinglyWithJson(getActivity(), dataObject.getString("fullname")));
                            etEmail.setText(M.actAccordinglyWithJson(getActivity(), dataObject.getString("email")));
                            etPhone.setText(M.actAccordinglyWithJson(getActivity(), dataObject.getString("phone")));
                            etAddress.setText(M.actAccordinglyWithJson(getActivity(), dataObject.getString("address")));
                            etHeight.setText(M.actAccordinglyWithJson(getActivity(), dataObject.getString("height")));
                            etWeight.setText(M.actAccordinglyWithJson(getActivity(), dataObject.getString("weight")));
                            etFootedness.setText(M.actAccordinglyWithJson(getActivity(), dataObject.getString("footedness")));
                            etFavouriteTeam.setText(M.actAccordinglyWithJson(getActivity(), dataObject.getString("favourite_team")));
                            etPlayPosition.setText(M.actAccordinglyWithJson(getActivity(), dataObject.getString("play_position")));
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
}
