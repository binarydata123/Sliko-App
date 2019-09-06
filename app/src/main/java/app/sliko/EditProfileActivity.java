package app.sliko;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import app.sliko.location.SelectAddressLocation;
import app.sliko.owner.events.SuccessFullyStadiumCreated;
import app.sliko.utills.FilePath;
import app.sliko.utills.M;
import app.sliko.utills.Prefs;
import app.sliko.web.Api;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.progressImage)
    ProgressBar progressImage;
    @BindView(R.id.userImage)
    CircleImageView userImage;
    String profileResponse;
    String type;
    Dialog dialog;
    @BindView(R.id.etUserName)
    EditText etUserName;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etFavouriteTeam)
    EditText etFavouriteTeam;
    @BindView(R.id.etHeight)
    EditText etHeight;
    @BindView(R.id.etWeight)
    EditText etWeight;
    @BindView(R.id.etAddress)
    TextView etAddress;
    @BindView(R.id.rightFoot)
    RadioButton rightFoot;
    @BindView(R.id.leftFoot)
    RadioButton leftFoot;
    @BindView(R.id.saveButton)
    Button saveButton;
    @BindView(R.id.playPositionSpinner)
    Spinner playPositionSpinner;
    String selectedPlayerPosition = "";

    @OnClick(R.id.saveButton)
    public void saveButton() {
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

        } else if ((etFavouriteTeam.length() == 0 || etFavouriteTeam.getText().toString().trim().length() == 0)) {
            Toast.makeText(this, getString(R.string.please_enter_team), Toast.LENGTH_SHORT).show();

        } else if (playPositionSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, getString(R.string.please_select_play_position), Toast.LENGTH_SHORT).show();

        } else if ((etHeight.length() == 0 || etHeight.getText().toString().trim().length() == 0)) {
            Toast.makeText(this, getString(R.string.please_enter_height), Toast.LENGTH_SHORT).show();

        } else if ((etWeight.length() == 0 || etWeight.getText().toString().trim().length() == 0)) {
            Toast.makeText(this, getString(R.string.please_enter_weight), Toast.LENGTH_SHORT).show();

        } else {
            updateProfileData(photoFile);
        }
    }

    Call<ResponseBody> call;

    private void updateProfileData(File fileName) {
        dialog.show();
        if (lat.equalsIgnoreCase("") || lng.equalsIgnoreCase("")) {
            lat = Api.LAT + "";
            lng = Api.LNG + "";
        }
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        MultipartBody.Part multipart_body = null;

        if (photoFile != null) {
            File file = new File(fileName.getName());
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            multipart_body = MultipartBody.Part.createFormData("profilepic", file.getName(), reqFile);
            call = service.editProfile(
                    multipart_body,
                    RequestBody.create(MediaType.parse("multipart/form-data"), M.fetchUserTrivialInfo(EditProfileActivity.this, "id")),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etUserName.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etAddress.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), lat),
                    RequestBody.create(MediaType.parse("multipart/form-data"), lng),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etPhone.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etHeight.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etWeight.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etFavouriteTeam.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), playPositionSpinner.getSelectedItem().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), rightFoot.isChecked() ? "Right Foot" : "Left Foot"));

        } else {
            call = service.editProfileWithoutImage(
                    RequestBody.create(MediaType.parse("multipart/form-data"), M.fetchUserTrivialInfo(EditProfileActivity.this, "id")),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etUserName.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etAddress.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), lat),
                    RequestBody.create(MediaType.parse("multipart/form-data"), lng),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etPhone.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etHeight.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etWeight.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), etFavouriteTeam.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), playPositionSpinner.getSelectedItem().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), rightFoot.isChecked() ? "Right Foot" : "Left Foot"));

        }


        final Callback<ResponseBody> callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (status.equalsIgnoreCase("true")) {
                            //M.updateTrivialInfo(AddStadiumActivity.this, Api.IS_STADIUM, Api.STADIUM_ADDED);
                            EventBus.getDefault().postSticky(new SuccessFullyStadiumCreated(true));
                        } else {
                            EventBus.getDefault().postSticky(new SuccessFullyStadiumCreated(false));
                        }
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(EditProfileActivity.this, response.toString() + "\n" + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    Log.i(">>response", "onResponse: " + response.toString() + "\n" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(">>apiNotResponding", "update student failure : " + "api NotResponding Failure");

                Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        call.enqueue(callback);

    }

    private static final int PERMISSIONS_REQUEST_CODE = 0x1;

    @OnClick(R.id.userImage)
    public void userImageLayout() {
        if ((ActivityCompat.checkSelfPermission(EditProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(EditProfileActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]
                            {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
        } else {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), 101);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.enablePermission), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    File photoFile = null;
    String imageFilePath = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 200:
                lat = data.getStringExtra("lat");
                lng = data.getStringExtra("lng");
                Log.e(">>addressLat", "onActivityResult: " + lat + "\n" + lng);
                etAddress.setText(data.getStringExtra("addressSelected"));
                break;
            case 101:
                if (resultCode != Activity.RESULT_CANCELED) {
                    Uri uri = null;
                    if (data != null) {
                        uri = data.getData();
                        try {
                            photoFile = M.createImageFile(EditProfileActivity.this);
                            imageFilePath = FilePath.getPath(this, uri);
                            photoFile = new File(imageFilePath);
                            Picasso.get().load(photoFile).into(userImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        ButterKnife.bind(EditProfileActivity.this);
        dialog = M.showDialog(EditProfileActivity.this, "", false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.editProfile));
        type = getIntent().getStringExtra("type");
        profileResponse = getIntent().getStringExtra("profileResponse");
        Log.e(">>", "onCreate: " + profileResponse);

        fetchProfileInfo();
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

    @OnClick(R.id.addressLayout)
    public void etAddressClick() {
        startActivityForResult(new Intent(EditProfileActivity.this, SelectAddressLocation.class), 200);
    }


    String lat = "", lng = "";
    ArrayList<String> playPositionsArray = new ArrayList<>();
    int positionToSet;

    private void getPlayerPositionData(String selectedPlayerPosition) {
        playPositionsArray.clear();
        playPositionsArray.add("Select player position");
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_playerPosition();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Prefs.savePlayerPositionData(jsonObject.toString(), EditProfileActivity.this);
                        if (status.equalsIgnoreCase("true")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int l = 0; l < jsonArray.length(); l++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(l);
                                String positions_name = jsonObject1.getString("positions_name");
                                playPositionsArray.add(positions_name);
                                if (!selectedPlayerPosition.equalsIgnoreCase(""))
                                    if (positions_name.equalsIgnoreCase(selectedPlayerPosition)) {
                                        positionToSet = l;
                                    }
                            }
                            playPositionSpinner
                                    .setAdapter(M.makeSpinnerAdapterWhite(EditProfileActivity.this,
                                            playPositionsArray, playPositionSpinner));
                            if (!selectedPlayerPosition.equalsIgnoreCase(""))
                                playPositionSpinner.setSelection((positionToSet + 1));
                        } else {
                            Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProfileInfo() {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_getUserProfile(M.fetchUserTrivialInfo(EditProfileActivity.this, "id"));
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
                            etUserName.setText(M.actAccordinglyWithJson(EditProfileActivity.this, dataObject.getString("fullname")));
                            etEmail.setText(M.actAccordinglyWithJson(EditProfileActivity.this, dataObject.getString("email")));
                            etPhone.setText(M.actAccordinglyWithJson(EditProfileActivity.this, dataObject.getString("phone")));
                            etAddress.setText(M.actAccordinglyWithJson(EditProfileActivity.this, dataObject.getString("address")));
                            etHeight.setText(M.actAccordinglyWithJson(EditProfileActivity.this, dataObject.getString("height")));
                            etWeight.setText(M.actAccordinglyWithJson(EditProfileActivity.this, dataObject.getString("weight")));
                            if (dataObject.getString("footedness").trim().equalsIgnoreCase("LeftFoot")) {
                                leftFoot.setChecked(true);
                            } else {
                                rightFoot.setChecked(true);
                            }
                            etFavouriteTeam.setText(M.actAccordinglyWithJson(EditProfileActivity.this, dataObject.getString("favourite_team")));
                            selectedPlayerPosition = dataObject.getString("play_position");

                            getPlayerPositionData(selectedPlayerPosition);
                        } else {
                            Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
