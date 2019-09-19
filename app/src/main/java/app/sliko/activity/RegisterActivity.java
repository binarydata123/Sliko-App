package app.sliko.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularEditText;
import app.sliko.UI.SsRegularTextView;
import app.sliko.owner.activity.StadiumOwnerHomeActivity;
import app.sliko.user.UserDashboard;
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

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.etUserName)
    SsRegularEditText etUserName;
    @BindView(R.id.etPhone)
    SsRegularEditText etPhone;
    @BindView(R.id.etEmail)
    SsRegularEditText etEmail;
    @BindView(R.id.etPassword)
    SsRegularEditText etPassword;
    @BindView(R.id.etConfirmPassword)
    SsRegularEditText etConfirmPassword;
    @BindView(R.id.etFavouriteTeam)
    SsRegularEditText etFavouirteTeam;
    @BindView(R.id.etHeight)
    SsRegularEditText etHeight;
    @BindView(R.id.etWeight)
    SsRegularEditText etWeight;
    @BindView(R.id.leftFoot)
    RadioButton leftFoot;
    @BindView(R.id.rightFoot)
    RadioButton rightFoot;
    @BindView(R.id.playPositionSpinner)
    Spinner playPositionSpinner;
    @BindView(R.id.checkOwner)
    RadioButton checkOwner;
    @BindView(R.id.registerButton)
    SsRegularButton registerButton;
    @BindView(R.id.haveAccountClick)
    LinearLayout haveAccountClick;
    @BindView(R.id.registerToSilko)
    SsRegularTextView registerToSilko;
    Dialog dialog;
    String selectedPlayerPosition = "";

    String password = "", email = "", name = "", type = "";
    boolean forFb= false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(RegisterActivity.this);
        dialog = M.showDialog(RegisterActivity.this, "", false);
        if (getIntent().getStringExtra("for") != null &&
                getIntent().getStringExtra("for").equalsIgnoreCase("forFb")) {
            password = getIntent().getStringExtra("password");
            email = getIntent().getStringExtra("email");
            name = getIntent().getStringExtra("name");
            etUserName.setText(name);
            etEmail.setText(email);
            etConfirmPassword.setVisibility(View.GONE);
            etPassword.setVisibility(View.GONE);
            forFb = true;
            haveAccountClick.setVisibility(View.GONE);
            registerToSilko.setText(getString(R.string.please_complete_process));
        } else {
            forFb = false;
            haveAccountClick.setVisibility(View.VISIBLE);
            etConfirmPassword.setVisibility(View.VISIBLE);
            etPassword.setVisibility(View.VISIBLE);
        }
        setListeners();
        getPlaysPositions();
        playPositionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    selectedPlayerPosition = playPositionSpinner.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(forFb){
            if(LoginManager.getInstance()!=null){
                LoginManager.getInstance().logOut();
                Toast.makeText(this, getString(R.string.loggingOutFromFb), Toast.LENGTH_SHORT).show();
                finish();
            }
        }else{
            onBackPressed();
        }
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

            } else if(!forFb){
                if ((etPassword.length() == 0 || etPassword.getText().toString().trim().length() == 0)) {
                    Toast.makeText(this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();

                } else if ((etPassword.length() < 6)) {
                    Toast.makeText(this, getString(R.string.password_should_big), Toast.LENGTH_SHORT).show();

                } else if ((etConfirmPassword.length() == 0 || etConfirmPassword.getText().toString().trim().length() == 0)) {
                    Toast.makeText(this, getString(R.string.please_confirm_password), Toast.LENGTH_SHORT).show();

                } else if (!(etConfirmPassword.getText().toString().equalsIgnoreCase(etPassword.getText().toString()))) {
                    Toast.makeText(this, getString(R.string.password_do_not_match), Toast.LENGTH_SHORT).show();

                }
            } else {
                registerRequest();
            }
        });

        haveAccountClick.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    String role = "", fcmToken = "";

    /**Password contains unique id got from facebook using it as
     *social_id param and password for new user*/

    private void registerRequest() {
        if (checkOwner.isChecked()) {
            role = Api.OWNER;
        } else {
            role = Api.USER;
        }
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_register(etUserName.getText().toString(),
                etEmail.getText().toString(),
                etPhone.getText().toString(),
                forFb?password:etPassword.getText().toString(),
                role,
                forFb?password:"",
                fcmToken,
                etFavouirteTeam.getText().toString(),
                selectedPlayerPosition,
                etHeight.getText().toString(),
                etWeight.getText().toString(),
                rightFoot.isChecked() ? "Right Foot" : "Left Foot");
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
                        Log.e(">>data", "onResponse: " + jsonObject.toString());
                        if (status.equalsIgnoreCase("true")) {
                            Prefs.saveUserData(jsonObject.getJSONObject("data").toString(), RegisterActivity.this);
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            if(forFb){
                                if (jsonObject.getJSONObject("data").getString("role").equalsIgnoreCase("owner")) {
                                    startActivity(new Intent(RegisterActivity.this, StadiumOwnerHomeActivity.class));
                                } else {
                                    startActivity(new Intent(RegisterActivity.this, UserDashboard.class));
                                }
                            }else{
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            }
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
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

    ArrayList<String> playPositionsArray = new ArrayList<>();

    private void getPlaysPositions() {
        playPositionsArray.clear();
        playPositionsArray.add("Select player position");
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_playerPosition();
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
                        Prefs.savePlayerPositionData(jsonObject.toString(), RegisterActivity.this);
                        if (status.equalsIgnoreCase("true")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int l = 0; l < jsonArray.length(); l++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(l);
                                String positions_name = jsonObject1.getString("positions_name");
                                playPositionsArray.add(positions_name);
                            }
                            playPositionSpinner
                                    .setAdapter(M.makeSpinnerAdapterWhite(RegisterActivity.this,
                                            playPositionsArray, playPositionSpinner));
                        } else {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
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
