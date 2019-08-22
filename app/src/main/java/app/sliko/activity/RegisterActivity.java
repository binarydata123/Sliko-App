package app.sliko.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import app.sliko.R;
import app.sliko.utills.M;
import app.sliko.web.Api;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.etUserName)
    EditText etUserName;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etConfirmPassword)
    EditText etConfirmPassword;
    @BindView(R.id.checkOwner)
    RadioButton checkOwner;
    @BindView(R.id.registerButton)
    Button registerButton;
    @BindView(R.id.haveAccountClick)
    LinearLayout haveAccountClick;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(RegisterActivity.this);
        dialog = M.showDialog(RegisterActivity.this, "", false);
        setListeners();

    }

    private void setListeners() {
        registerButton.setOnClickListener(view -> {
            if (etUserName.length() == 0 || etUserName.getText().toString().trim().length() == 0) {
                Toast.makeText(this, getString(R.string.please_enter_username), Toast.LENGTH_SHORT).show();
            } else if ((etPhone.length() == 0 || etPhone.getText().toString().trim().length() == 0)) {
                Toast.makeText(this, getString(R.string.please_enter_phone), Toast.LENGTH_SHORT).show();

            } else if ((etPhone.length() > 15)) {
                Toast.makeText(this, getString(R.string.please_enter_valid_phone), Toast.LENGTH_SHORT).show();

            } else if ((etEmail.length() == 0 || etEmail.getText().toString().trim().length() == 0)) {
                Toast.makeText(this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();

            } else if (!M.validateEmail(etEmail.getText().toString())) {
                Toast.makeText(this, getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show();

            } else if ((etPassword.length() == 0 || etPassword.getText().toString().trim().length() == 0)) {
                Toast.makeText(this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();

            } else if ((etConfirmPassword.length() == 0 || etConfirmPassword.getText().toString().trim().length() == 0)) {
                Toast.makeText(this, getString(R.string.please_confirm_password), Toast.LENGTH_SHORT).show();

            } else if (!(etConfirmPassword.getText().toString().equalsIgnoreCase(etEmail.getText().toString()))) {
                Toast.makeText(this, getString(R.string.password_do_not_match), Toast.LENGTH_SHORT).show();

            } else {
                registerRequest();
            }
        });
        haveAccountClick.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    String role = "", social_id = "", fcmToken = "";

    private void registerRequest() {
        if (checkOwner.isChecked()) {
            role = Api.OWNER;
        } else {
            role = Api.USER;
        }
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_register(etUserName.getText().toString(),
                etEmail.getText().toString(), etPhone.getText().toString(), etPassword.getText().toString(), role, social_id, fcmToken);
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
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegisterActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
