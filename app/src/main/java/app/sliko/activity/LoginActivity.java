package app.sliko.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;

import app.sliko.ForgotPasswordActivity;
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

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.checkOwner)
    RadioButton checkOwner;
    @BindView(R.id.checkUser)
    RadioButton checkUser;
    @BindView(R.id.etUserEmail)
    SsRegularEditText etUserEmail;
    @BindView(R.id.etPassword)
    SsRegularEditText etPassword;
    @BindView(R.id.loginbtn)
    SsRegularButton loginbtn;
    @BindView(R.id.noAccountClick)
    LinearLayout noAccountClick;
    @BindView(R.id.forgotPassword)
    SsRegularTextView forgotPassword;
    @BindView(R.id.fbLoginButton)
    LinearLayout fbLoginButton;
    Dialog dialog;
    CallbackManager callbackManager;

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e(">>key", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (Exception e) {
            Log.e(">>key", "printHashKey()", e);
        }
    }


    String password , name , email;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        //printHashKey();
        ButterKnife.bind(LoginActivity.this);
        dialog = M.showDialog(LoginActivity.this, "", false);
        setListeners();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                (object, response) -> {
                                    try {
                                        /**id as password**/
                                        Log.e(">>id", "onSuccess: "+object.toString());
                                        password = object.getString("id");
                                        name = object.getString("name");
                                        Log.e(">>id", "onSuccess: " + password + "\n" + name);
                                        if (!object.has("email")) {
                                            LoginManager.getInstance().logOut();
                                            Toast.makeText(LoginActivity.this, getString(R.string.sorry_could_not_retrive_your_email), Toast.LENGTH_SHORT).show();
                                        } else {
                                            email = object.getString("name");
                                            loginPassword(object.getString("email"), password);
                                        }
                                    } catch (JSONException e) {
                                        LoginManager.getInstance().logOut();
                                        Log.e(">>error", "onSuccess: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.e(">>error", "onError: " + "Login attempt cancelled.");
                        Toast.makeText(LoginActivity.this, "Login attempt cancelled.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                        Log.e(">>error", "onError: " + exception.getMessage());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
                loginPassword(etUserEmail.getText().toString(), etPassword.getText().toString());
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

        fbLoginButton.setOnClickListener(view ->
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile", "user_friends")));
    }

    String role = Api.USER, fcmToken = Api.TOKEN;

    private void loginPassword(String email, String password) {
        if (checkOwner.isChecked()) {
            role = Api.OWNER;
        } else {
            role = Api.USER;
        }
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_login(etUserEmail.getText().toString(), etPassword.getText().toString(),
                fcmToken);
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
                            Log.e(">>loginData", "onResponse: " + jsonObject.toString());
                            Prefs.saveUserData(jsonObject.getJSONObject("data").toString(), LoginActivity.this);
                            if (jsonObject.getJSONObject("data").getString("role").equalsIgnoreCase("owner")) {
                                startActivity(new Intent(LoginActivity.this, StadiumOwnerHomeActivity.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, UserDashboard.class));
                            }
                        } else {
                            if (jsonObject.getString("error").equalsIgnoreCase("1")) {
                                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)
                                        .putExtra("password" , password)
                                        .putExtra("email" , email)
                                        .putExtra("for" , "forFb")
                                        .putExtra("name" , name)
                                );
                            }else{
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                        }
                        finish();
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

