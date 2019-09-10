package app.sliko.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import app.sliko.ForgotPasswordActivity;
import app.sliko.R;
import app.sliko.owner.activity.StadiumOwnerHomeActivity;
import app.sliko.utills.M;
import app.sliko.utills.Prefs;
import app.sliko.web.Api;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.checkOwner)
    RadioButton checkOwner;
    @BindView(R.id.checkUser)
    RadioButton checkUser;
    @BindView(R.id.etUserEmail)
    EditText etUserEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.loginbtn)
    Button loginbtn;
    @BindView(R.id.noAccountClick)
    LinearLayout noAccountClick;
    @BindView(R.id.forgotPassword)
    TextView forgotPassword;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(LoginActivity.this);
        dialog = M.showDialog(LoginActivity.this, "", false);
        setListeners();
    }

    private void setListeners() {
        loginbtn.setOnClickListener(view -> {
            if ((etUserEmail.length() == 0 || etUserEmail.getText().toString().trim().length() == 0)) {
                Toast.makeText(LoginActivity.this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();

            } else if (!M.validateEmail(etUserEmail.getText().toString())) {
                Toast.makeText(LoginActivity.this, getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show();

            } else if ((etPassword.length() == 0 || etPassword.getText().toString().trim().length() == 0)) {
                Toast.makeText(LoginActivity.this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();

            } else {
                loginPassword();
            }
        });

        noAccountClick.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        forgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            finish();
        });
    }

    String role = Api.USER, fcmToken = Api.TOKEN;

    private void loginPassword() {
        if (checkOwner.isChecked()) {
            role = Api.OWNER;
        } else {
            role = Api.USER;
        }
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_login(etUserEmail.getText().toString(), etPassword.getText().toString(),
                fcmToken, role);
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
                            Log.i(">>loginData", "onResponse: " + jsonObject.toString());
                            Prefs.saveUserData(jsonObject.getJSONObject("data").toString(), LoginActivity.this);
                            if (jsonObject.getJSONObject("data").has("is_stadium")) {
                                startActivity(new Intent(LoginActivity.this, StadiumOwnerHomeActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

