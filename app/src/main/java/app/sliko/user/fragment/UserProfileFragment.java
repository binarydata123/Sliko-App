package app.sliko.user.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import app.sliko.EditProfileActivity;
import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularTextView;
import app.sliko.activity.LoginActivity;
import app.sliko.activity.SettingActivity;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.ChangePasswordDialog;
import app.sliko.dialogs.models.DialogConfirmation;
import app.sliko.events.ProfileUploadedSuccessEvent;
import app.sliko.utills.M;
import app.sliko.utills.Prefs;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileFragment extends Fragment {

    @BindView(R.id.userImage)
    CircleImageView userImage;
    @BindView(R.id.etName)
    SsMediumTextView etName;
    @BindView(R.id.etEmail)
    SsRegularTextView etEmail;
    @BindView(R.id.etPhone)
    SsRegularTextView etPhone;
    @BindView(R.id.etAddress)
    SsRegularTextView etAddress;
    @BindView(R.id.etFavouriteTeam)
    SsRegularTextView etFavouriteTeam;
    @BindView(R.id.etPlayPosition)
    SsRegularTextView etPlayPosition;
    @BindView(R.id.etHeight)
    SsRegularTextView etHeight;
    @BindView(R.id.etWeight)
    SsRegularTextView etWeight;
    @BindView(R.id.etFootedness)
    SsRegularTextView etFootedness;
    @BindView(R.id.editProfileButton)
    FloatingActionButton editProfileButton;
    @BindView(R.id.changePassword)
    LinearLayout changePassword;
    @BindView(R.id.progressImage)
    ProgressBar progressImage;
    @BindView(R.id.totalSpentText)
    TextView totalSpentText;
    @BindView(R.id.totalSpent)
    SsRegularTextView totalSpent;
    @BindView(R.id.totalBooking)
    TextView totalBooking;
    @BindView(R.id.settingLayout)
    LinearLayout settingLayout;
    @BindView(R.id.signOutLayout)
    LinearLayout signOutLayout;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Dialog dialog;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ProfileUploadedSuccessEvent profileUploadedSuccessEvent) {
        if (profileUploadedSuccessEvent != null) {
            if (profileUploadedSuccessEvent.isStatus()) {
                fetchProfileInfo();
            }
        }
    }

    View view;

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (context != null) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            totalSpentText.setText(getString(R.string.totalSpent));
            dialog = M.showDialog(context, "", false);
            fetchProfileInfo();
            setListener();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(UserProfileFragment.this, view);

        return view;
    }


    ChangePasswordDialog changePasswordDialog;
    private String profileResponse = "";

    private void setListener() {
        editProfileButton.setOnClickListener(view -> {

            startActivity(new Intent(context, EditProfileActivity.class));

        });
        changePassword.setOnClickListener(view -> {
            changePasswordDialog = DialogMethodCaller.openChangePasswordDialog(context, R.layout.dialog_change_password, false);
            changePasswordDialog.getDialog_error().show();
            changePasswordDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changePasswordDialog.getDialog_error().dismiss();
                }
            });
            changePasswordDialog.getEtUserEmail().setText(M.fetchUserTrivialInfo(getActivity(), "email"));
            changePasswordDialog.getEtUserEmail().setEnabled(false);
            changePasswordDialog.getSendButton().setOnClickListener(view1 -> {

                if (M.matchValidation(changePasswordDialog.getEtUserEmail())) {
                    Toast.makeText(context, getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show();
                } else if (!M.validateEmail(changePasswordDialog.getEtUserEmail().getText().toString())) {
                    Toast.makeText(context, getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show();
                } else if (M.matchValidation(changePasswordDialog.getEtPassword())) {
                    Toast.makeText(context, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();

                } else if ((changePasswordDialog.getEtPassword().length() < 6)) {
                    Toast.makeText(context, getString(R.string.password_should_big), Toast.LENGTH_SHORT).show();

                } else if (M.matchValidation(changePasswordDialog.getEtNewPassword())) {
                    Toast.makeText(context, getString(R.string.please_enter_new_password), Toast.LENGTH_SHORT).show();

                } else if ((changePasswordDialog.getEtNewPassword().length() < 6)) {
                    Toast.makeText(context, getString(R.string.password_should_big), Toast.LENGTH_SHORT).show();

                } else {
                    changePasswordDialog.getDialog_error().dismiss();
                    hitResetPassword(changePasswordDialog.getEtUserEmail().getText().toString()
                            , changePasswordDialog.getEtPassword().getText().toString(), changePasswordDialog.getEtNewPassword().getText().toString());
                }
            });
        });
        settingLayout.setVisibility(View.GONE);
        signOutLayout.setOnClickListener(view -> {
            dialogConfirmation = DialogMethodCaller.openDialogConfirmation(context, R.layout.dialog_confirmation, false);
            dialogConfirmation.getDialog_error().show();
            dialogConfirmation.getDialogConfirmationMessage().setText(getString(R.string.doYouWantToSignOut));
            dialogConfirmation.getDialogConfirmationTitle().setText(getString(R.string.signout));
            dialogConfirmation.getCloseButton().setOnClickListener(view12 -> dialogConfirmation.getDialog_error().dismiss());
            dialogConfirmation.getOkButton().setOnClickListener(view1 -> {
                dialogConfirmation.getDialog_error().dismiss();
                logoutApi();
            });
        });

        settingLayout.setOnClickListener(view -> startActivity(new Intent(context, SettingActivity.class)));

    }

    Handler handler;
    Runnable runnable;

    private void logoutApi() {
        dialog.show();
        handler = new Handler();
        runnable = () -> {
            dialog.cancel();
            Prefs.clearUserData(context);
            startActivity(new Intent(context, LoginActivity.class));
            ((Activity) context).finish();
        };
        handler.postDelayed(runnable, 500);
    }


    DialogConfirmation dialogConfirmation;

    private void hitResetPassword(String email, String oldPassword, String newPassword) {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_resetPassword(M.fetchUserTrivialInfo(context, "id"), email, oldPassword, newPassword);
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
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean isProfileLoaded = false;

    private void fetchProfileInfo() {
        dialog.show();
        String userID = M.fetchUserTrivialInfo(context, "id");
        Log.i(">>user_id", "fetchProfileInfo: " + userID);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_getUserProfile(M.fetchUserTrivialInfo(context, "id"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
                try {
                    if (response.isSuccessful()) {
                        isProfileLoaded = true;
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        profileResponse = jsonObject.toString();
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
                            totalSpent.setText(getActivity().getResources().getString(R.string.currencySymbol) + "0");
                            totalBooking.setText(M.actAccordinglyWithJson(getActivity(),dataObject.getString("total_count")) + "");
                            totalSpent.setText(getActivity().getString(R.string.currencySymbol) + "" + M.actAccordinglyWithJson(dataObject.getString("total_revenue")) + "");
                            etName.setText(M.actAccordinglyWithJson(context, dataObject.getString("fullname")));
                            etEmail.setText(M.actAccordinglyWithJson(context, dataObject.getString("email")));
                            etPhone.setText(M.actAccordinglyWithJson(context, dataObject.getString("phone")));
                            etAddress.setText(M.actAccordinglyWithJson(context, dataObject.getString("address")));
                            etHeight.setText(M.actAccordinglyWithJson(context, dataObject.getString("height")));
                            etWeight.setText(M.actAccordinglyWithJson(context, dataObject.getString("weight")));
                            etFootedness.setText(M.actAccordinglyWithJson(context, dataObject.getString("footedness")));
                            etFavouriteTeam.setText(M.actAccordinglyWithJson(context, dataObject.getString("favourite_team")));
                            etPlayPosition.setText(M.actAccordinglyWithJson(context, dataObject.getString("play_position")));
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
