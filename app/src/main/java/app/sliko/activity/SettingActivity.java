package app.sliko.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularEditText;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.ChangePasswordDialog;
import app.sliko.dialogs.models.DialogConfirmation;
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

public class SettingActivity extends AppCompatActivity {
    @BindView(R.id.toolbarTitle)
    SsMediumTextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.enableDisableNotificationButton)
    Switch enableDisableNotificationButton;
    @BindView(R.id.etTime)
    SsRegularEditText etTime;
    @BindView(R.id.etTimeLayout)
    FrameLayout etTimeLayout;
    @BindView(R.id.saveButton)
    Button saveButton;
    @BindView(R.id.changePasswordLayout)
    FrameLayout changePasswordLayout;
    @BindView(R.id.deleteAccountLayout)
    FrameLayout deleteAccountLayout;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(SettingActivity.this);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.setting));
        dialog = M.showDialog(SettingActivity.this, "", false);
        fetchSettings();
        enableDisableNotificationButton.setOnClickListener(view -> enableDisableNotification(enableDisableNotificationButton.isChecked() ? "1" : "0"));
        saveButton.setOnClickListener(view -> {
            if (etTime.length() == 0 || etTime.getText().toString().trim().length() == 0) {
                Toast.makeText(SettingActivity.this, getString(R.string.please_enter_suitale_time), Toast.LENGTH_SHORT).show();
            } else {
                updateNotificationTime(etTime.getText().toString());
            }
        });

        changePasswordLayout.setOnClickListener(view -> {
            changePasswordDialog = DialogMethodCaller.openChangePasswordDialog(SettingActivity.this, R.layout.dialog_change_password, false);
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
                            , changePasswordDialog.getEtPassword().getText().toString(), changePasswordDialog.getEtNewPassword().getText().toString());
                }
            });
        });

        deleteAccountLayout.setOnClickListener(view -> {
            dialogConfirmation = DialogMethodCaller.openDialogConfirmation(SettingActivity.this, R.layout.dialog_confirmation, false);
            dialogConfirmation.getDialog_error().show();
            dialogConfirmation.getDialogConfirmationMessage().setText(getString(R.string.doYouWantToSignOut));
            dialogConfirmation.getDialogConfirmationTitle().setText(getString(R.string.signout));
            dialogConfirmation.getCloseButton().setOnClickListener(view12 -> dialogConfirmation.getDialog_error().dismiss());
            dialogConfirmation.getOkButton().setOnClickListener(view1 -> {
                dialogConfirmation.getDialog_error().dismiss();
                dialog.show();
                handler = new Handler();
                runnable = () -> {
                    dialog.cancel();
                    Prefs.clearUserData(SettingActivity.this);
                    startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                    finish();
                };
                handler.postDelayed(runnable, 500);
            });
        });
    }

    Handler handler;
    Runnable runnable;
    DialogConfirmation dialogConfirmation;

    private void deactivateApi() {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_deactivateAccount(M.fetchUserTrivialInfo(SettingActivity.this, "id"));
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
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
                            Prefs.clearUserData(SettingActivity.this);
                            Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SettingActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.i(">>error", "onResponse: " + e.getMessage());
                    Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(SettingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hitResetPassword(String email, String oldPassword, String newPassword) {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_resetPassword(M.fetchUserTrivialInfo(SettingActivity.this, "id"), email, oldPassword, newPassword);
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
                        Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(SettingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    ChangePasswordDialog changePasswordDialog;

    private void enableDisableNotification(String notificationCheck) {
        Log.i(">>error", "onResponse: " + M.fetchUserTrivialInfo(SettingActivity.this , "id"));
        Log.e(">>check", "enableDisableNotification: " + notificationCheck);
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_enableDisableNotification(M.fetchUserTrivialInfo(SettingActivity.this, "id"),
                Prefs.getStadiumId(SettingActivity.this), notificationCheck);
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
                        Log.e(">>error", "onResponse: " + jsonObject.toString());
                        if (status.equalsIgnoreCase("true")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (data.getString("check_notification").equalsIgnoreCase("0")) {
                                saveButton.setVisibility(View.GONE);
                                etTime.setEnabled(false);
                                etTimeLayout.setAlpha(.5f);
                            } else {
                                saveButton.setVisibility(View.VISIBLE);
                                etTime.setEnabled(true);
                                etTimeLayout.setAlpha(1.0f);
                            }
                        } else {
                            enableDisableNotificationButton.setChecked(enableDisableNotificationButton.isChecked());
                            Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SettingActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(SettingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNotificationTime(String time) {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_updateNotificationTime(M.fetchUserTrivialInfo(SettingActivity.this, "id"),
                Prefs.getStadiumId(SettingActivity.this), time);
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
                            if (data.getString("notification_time").equalsIgnoreCase("")
                                    || data.getString("notification_time").equalsIgnoreCase("null")) {
                                etTime.setText("");
                            } else {
                                etTime.setText(data.getString("notification_time"));
                                etTime.setSelection(etTime.length());
                                saveButton.setVisibility(View.VISIBLE);
                                etTimeLayout.setAlpha(1.0f);
                            }
                        } else {
                            saveButton.setVisibility(View.GONE);
                            etTime.setEnabled(false);
                            etTimeLayout.setAlpha(.5f);
                            Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SettingActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(SettingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSettings() {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_settingGet(M.fetchUserTrivialInfo(SettingActivity.this, "id"));
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
                            if (data.getString("notification_time").equalsIgnoreCase("")
                                    || data.getString("notification_time").equalsIgnoreCase("null")) {
                                etTime.setText("");
                            } else {
                                etTime.setText(data.getString("notification_time"));
                                etTime.setSelection(etTime.length());
                                saveButton.setVisibility(View.VISIBLE);
                                etTimeLayout.setAlpha(1.0f);
                            }
                            enableDisableNotificationButton.setChecked(data.getString("check_notification").equalsIgnoreCase("1"));
                            if (data.getString("check_notification").equalsIgnoreCase("0")) {
                                saveButton.setVisibility(View.GONE);
                                etTime.setEnabled(false);
                                etTimeLayout.setAlpha(.5f);
                            }
                        } else {
                            enableDisableNotificationButton.setChecked(false);
                            saveButton.setVisibility(View.GONE);
                            etTime.setEnabled(false);
                            etTimeLayout.setAlpha(.5f);
                        }
                    } else {
                        Toast.makeText(SettingActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(SettingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
