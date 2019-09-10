package app.sliko.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import app.sliko.EditProfileActivity;
import app.sliko.ForgotPasswordActivity;
import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.ChangePasswordDialog;
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

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.userImage)
    CircleImageView userImage;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
    @BindView(R.id.etHeight)
    TextView etHeight;
    @BindView(R.id.etWeight)
    TextView etWeight;
    @BindView(R.id.etFootedness)
    TextView etFootedness;
    @BindView(R.id.editProfileButton)
    Button editProfileButton; @BindView(R.id.changePasswordButton)
    Button changePasswordButton;
    @BindView(R.id.progressImage)
    ProgressBar progressImage;


    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        ButterKnife.bind(ProfileActivity.this);
        dialog = M.showDialog(ProfileActivity.this, "", false);

        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.profile));
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        fetchProfileInfo();
        setListener();
    }


    ChangePasswordDialog changePasswordDialog;
    private String profileResponse = "";

    private void setListener() {
        editProfileButton.setOnClickListener(view -> {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));

        });
        changePasswordButton.setOnClickListener(view -> {
            changePasswordDialog = DialogMethodCaller.openChangePasswordDialog(ProfileActivity.this, R.layout.dialog_change_password , false);
            changePasswordDialog.getDialog_error().show();
            changePasswordDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changePasswordDialog.getDialog_error().dismiss();
                }
            });
            changePasswordDialog.getSendButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changePasswordDialog.getDialog_error().dismiss();
                    hitResetPassword(changePasswordDialog.getEtUserEmail().getText().toString()
                            , changePasswordDialog.getEtPassword().getText().toString(),changePasswordDialog.getEtNewPassword().getText().toString());
                }
            });
        });
    }

    private void hitResetPassword(String email , String oldPassword  , String newPassword){
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_resetPassword(M.fetchUserTrivialInfo(ProfileActivity.this, "id"),email , oldPassword , newPassword);
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
                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean isProfileLoaded = false;

    private void fetchProfileInfo() {
        dialog.show();
        String userID = M.fetchUserTrivialInfo(ProfileActivity.this, "id");
        Log.i(">>user_id", "fetchProfileInfo: " + userID);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_getUserProfile(M.fetchUserTrivialInfo(ProfileActivity.this, "id"));
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
                            etName.setText(M.actAccordinglyWithJson(ProfileActivity.this, dataObject.getString("fullname")));
                            etEmail.setText(M.actAccordinglyWithJson(ProfileActivity.this, dataObject.getString("email")));
                            etPhone.setText(M.actAccordinglyWithJson(ProfileActivity.this, dataObject.getString("phone")));
                            etAddress.setText(M.actAccordinglyWithJson(ProfileActivity.this, dataObject.getString("address")));
                            etHeight.setText(M.actAccordinglyWithJson(ProfileActivity.this, dataObject.getString("height")));
                            etWeight.setText(M.actAccordinglyWithJson(ProfileActivity.this, dataObject.getString("weight")));
                            etFootedness.setText(M.actAccordinglyWithJson(ProfileActivity.this, dataObject.getString("footedness")));
                            etFavouriteTeam.setText(M.actAccordinglyWithJson(ProfileActivity.this, dataObject.getString("favourite_team")));
                            etPlayPosition.setText(M.actAccordinglyWithJson(ProfileActivity.this, dataObject.getString("play_position")));
                        } else {
                            Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
