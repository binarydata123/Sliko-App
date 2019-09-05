package app.sliko.owner.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import app.sliko.R;
import app.sliko.location.SelectAddressLocation;
import app.sliko.models.StadiumImagesModel;
import app.sliko.owner.adapter.AddImagesAdapter;
import app.sliko.owner.adapter.StadiumOpeningAdapter;
import app.sliko.owner.events.SuccessFullyStadiumCreated;
import app.sliko.owner.model.AvailabilityModel;
import app.sliko.utills.M;
import app.sliko.web.Api;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStadiumActivity extends AppCompatActivity {

    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.stadiumName)
    EditText stadiumName;
    @BindView(R.id.stadiumDescription)
    EditText stadiumDescription;
    @BindView(R.id.stadiumAddress)
    TextView stadiumAddress;
    @BindView(R.id.pickImageLayout)
    LinearLayout pickImageLayout;
    @BindView(R.id.pitchImageRecyclerView)
    RecyclerView stadiumImagesRecyclerView;
    @BindView(R.id.availabilityRecyclerView)
    RecyclerView availabilityRecyclerView;
    private static final int PERMISSIONS_REQUEST_CODE = 0x1;
    Dialog dialog;
    private ArrayList<StadiumImagesModel> imagesEncodedList;
    String imageEncoded, address = "";
    AddImagesAdapter addPitchImageAdapter;
    String userId;
    String lat = "", lng = "";

    @BindView(R.id.chkboxsunday)
    CheckBox chkboxsunday;
    @BindView(R.id.chkboxmonday)
    CheckBox chkboxmonday;
    @BindView(R.id.chkboxtuesday)
    CheckBox chkboxtuesday;
    @BindView(R.id.chkboxwednsday)
    CheckBox chkboxwednsday;
    @BindView(R.id.chkboxthursday)
    CheckBox chkboxthursday;
    @BindView(R.id.chkboxfriday)
    CheckBox chkboxfriday;
    @BindView(R.id.chkboxsaturday)
    CheckBox chkboxsaturday;

    StadiumOpeningAdapter stadiumOpeningAdapter;
    private JSONArray timeSlots = null;

    String stadium_id = "";
    String stadiumType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stadium_activity);
        ButterKnife.bind(this);
        dialog = M.showDialog(this, "", false);
        userId = M.fetchUserTrivialInfo(AddStadiumActivity.this, "id");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        stadiumType = getIntent().getStringExtra("stadiumType");
        assert stadiumType != null;
        if (stadiumType.equalsIgnoreCase("add")) {
            toolbarTitle.setText(getString(R.string.addedStadium));
        } else {
            toolbarTitle.setText(getString(R.string.editStadium));
            if (getIntent().getStringExtra("stadium_id") != null) {
                stadium_id = getIntent().getStringExtra("stadium_id");
                fetchStadiumInfo();
            }
        }

        Log.e(">>", "onCreate: " + stadium_id);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        imagesEncodedList = new ArrayList<>();
        imagesEncodedList.clear();

        setStadiumImageAdapter();
        prepareStadiumData();
    }

    ArrayList<AvailabilityModel> availabilityModels = new ArrayList<>();

    private void setStadiumImageAdapter() {
        stadiumImagesRecyclerView.setLayoutManager(new LinearLayoutManager(AddStadiumActivity.this, LinearLayoutManager.HORIZONTAL, false));
        addPitchImageAdapter = new AddImagesAdapter(this, imagesEncodedList, "");
        stadiumImagesRecyclerView.setAdapter(addPitchImageAdapter);
    }

    private void prepareStadiumData() {
        availabilityModels.clear();
        for (int k = 0; k < Api.timingArray.length; k++) {
            AvailabilityModel availabilityModel = new AvailabilityModel();
            availabilityModel.setPicked(false);
            availabilityModel.setTime(Api.timingArray[k]);
            availabilityModels.add(availabilityModel);
        }
        stadiumOpeningAdapter = new StadiumOpeningAdapter(AddStadiumActivity.this, availabilityModels, "editView");
        availabilityRecyclerView.setLayoutManager(new GridLayoutManager(AddStadiumActivity.this, 3));
        availabilityRecyclerView.setAdapter(stadiumOpeningAdapter);
        stadiumOpeningAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.submitButton)
    void onSubmitButtonClick() {
        setlisteners();
    }

    @OnClick(R.id.stadiumAddress)
    void clickAddress() {
        startActivityForResult(new Intent(AddStadiumActivity.this, SelectAddressLocation.class), 200);
    }


    private void setlisteners() {

        if (stadiumName.length() == 0 || stadiumName.getText().toString().trim().length() == 0) {
            Toast.makeText(AddStadiumActivity.this, getResources().getString(R.string.plzEnterpitchname), Toast.LENGTH_SHORT).show();
        } else if (stadiumDescription.length() == 0 || stadiumDescription.getText().toString().trim().length() == 0) {
            Toast.makeText(AddStadiumActivity.this, getResources().getString(R.string.plzEnterdescption), Toast.LENGTH_SHORT).show();
        } else if (stadiumAddress.length() == 0 || stadiumAddress.getText().toString().trim().length() == 0) {
            Toast.makeText(AddStadiumActivity.this, getResources().getString(R.string.plzenteraddress), Toast.LENGTH_SHORT).show();
        } else if (stadiumOpeningAdapter.getSelectedArrayList().length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.please_select_atleast_one_time_slot), Toast.LENGTH_SHORT).show();
        } else if (imagesEncodedList.size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.please_insert_one_image_atleast), Toast.LENGTH_SHORT).show();
        } else {
            updateMyInfo();
        }

    }

    @OnClick(R.id.pickImageLayout)
    void clickPitchImage() {
        if ((ActivityCompat.checkSelfPermission(AddStadiumActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(AddStadiumActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(AddStadiumActivity.this, new String[]
                            {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
        } else {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), 102);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 102:
                if (data != null) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    if (data.getData() != null) {
                        Uri mImageUri = data.getData();
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor.getString(columnIndex);
                        StadiumImagesModel stadiumImagesModel = new StadiumImagesModel();
                        stadiumImagesModel.setImageId("");
                        stadiumImagesModel.setImageName(imageEncoded);
                        imagesEncodedList.add(stadiumImagesModel);
                        Log.i(">>idImage", "onActivityResult: " + imageEncoded + "\n" + mImageUri.getPath());
                        cursor.close();
                    } else {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mArrayUri.add(uri);
                                // Get the cursor
                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                imageEncoded = cursor.getString(columnIndex);
                                StadiumImagesModel stadiumImagesModel = new StadiumImagesModel();
                                stadiumImagesModel.setImageId("");
                                stadiumImagesModel.setImageName(imageEncoded);
                                imagesEncodedList.add(stadiumImagesModel);
                                Log.i(">>idImage", "onActivityResult: " + imageEncoded + "\n" + uri.getPath());
                                cursor.close();
                            }
                            Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        }
                    }
                    addPitchImageAdapter.notifyDataSetChanged();
                }

                break;
            case 200:
                lat = data.getStringExtra("lat");
                lng = data.getStringExtra("lng");
                Log.e(">>addressLat", "onActivityResult: " + lat + "\n" + lng);
                stadiumAddress.setText(data.getStringExtra("addressSelected"));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.appRequiresPermission), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    public void updateMyInfo() {
        ArrayList<StadiumImagesModel> files = new ArrayList<>(addPitchImageAdapter.getUpdatedList());
        if (lat.equalsIgnoreCase("") || lng.equalsIgnoreCase("")) {
            lat = Api.LAT + "";
            lng = Api.LNG + "";
        }
        try {
            dialog.show();
            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
            MultipartBody.Part[] multipart_body = null;
            if (files.size() != 0) {
                multipart_body = new MultipartBody.Part[files.size()];
                for (int k = 0; k < files.size(); k++) {
                    File file = new File(files.get(k).getImageName());
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                    multipart_body[k] = MultipartBody.Part.createFormData("stadium_image[]", file.getName(), reqFile);
                }
            }
            Call<ResponseBody> call = service.createStadium(
                    multipart_body,
                    RequestBody.create(MediaType.parse("multipart/form-data"), stadiumName.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), userId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), stadiumDescription.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), stadiumAddress.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), lat),
                    RequestBody.create(MediaType.parse("multipart/form-data"), lng),
                    RequestBody.create(MediaType.parse("multipart/form-data"), stadiumOpeningAdapter.getSelectedArrayList().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                    RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                    RequestBody.create(MediaType.parse("multipart/form-data"), chkboxmonday.isChecked() ? "1" : "0"),
                    RequestBody.create(MediaType.parse("multipart/form-data"), chkboxtuesday.isChecked() ? "1" : "0"),
                    RequestBody.create(MediaType.parse("multipart/form-data"), chkboxwednsday.isChecked() ? "1" : "0"),
                    RequestBody.create(MediaType.parse("multipart/form-data"), chkboxthursday.isChecked() ? "1" : "0"),
                    RequestBody.create(MediaType.parse("multipart/form-data"), chkboxfriday.isChecked() ? "1" : "0"),
                    RequestBody.create(MediaType.parse("multipart/form-data"), chkboxsaturday.isChecked() ? "1" : "0"),
                    RequestBody.create(MediaType.parse("multipart/form-data"), chkboxsunday.isChecked() ? "1" : "0")
            );

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
                            Toast.makeText(AddStadiumActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (status.equalsIgnoreCase("true")) {
                                //M.updateTrivialInfo(AddStadiumActivity.this, Api.IS_STADIUM, Api.STADIUM_ADDED);
                                EventBus.getDefault().postSticky(new SuccessFullyStadiumCreated(true));
                            } else {
                                EventBus.getDefault().postSticky(new SuccessFullyStadiumCreated(false));
                            }
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(AddStadiumActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(AddStadiumActivity.this, response.toString() + "\n" + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        Log.i(">>response", "onResponse: " + response.toString() + "\n" + response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(">>apiNotResponding", "update student failure : " + "api NotResponding Failure");

                    Toast.makeText(AddStadiumActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            call.enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(">>apiException", "update student failure : " + "api NotResponding Failure");
        }
    }

    private ArrayList<StadiumImagesModel> stadiumImagesDataArrayList;

    private void fetchStadiumInfo() {
        stadiumImagesDataArrayList = new ArrayList<>();
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_stadium_detail(M.fetchUserTrivialInfo(AddStadiumActivity.this, "id"));
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
                            String stadium_name = data.getString("stadium_name");
                            String description = data.getString("description");
                            String address = data.getString("address");
                            lat = data.getString("lat");
                            lng = data.getString("lng");

                            chkboxmonday.setChecked(data.getString("check_mon").equalsIgnoreCase("1"));
                            chkboxtuesday.setChecked(data.getString("check_mon").equalsIgnoreCase("1"));
                            chkboxwednsday.setChecked(data.getString("check_mon").equalsIgnoreCase("1"));
                            chkboxthursday.setChecked(data.getString("check_mon").equalsIgnoreCase("1"));
                            chkboxfriday.setChecked(data.getString("check_mon").equalsIgnoreCase("1"));
                            chkboxsaturday.setChecked(data.getString("check_mon").equalsIgnoreCase("1"));
                            chkboxsunday.setChecked(data.getString("check_mon").equalsIgnoreCase("1"));
                            stadiumName.setText(stadium_name);
                            stadiumDescription.setText(description);
                            stadiumAddress.setText(address);
                            JSONArray jsonArray = data.getJSONArray("stadium_gallery");
                            for (int k = 0; k < jsonArray.length(); k++) {
                                JSONObject stadiumImages = jsonArray.getJSONObject(k);
                                StadiumImagesModel stadiumImagesModel = new StadiumImagesModel();
                                String imageId = stadiumImages.getString("id");
                                String stadiumImage = stadiumImages.getString("stadium_image");
                                stadiumImagesModel.setImageId(imageId);
                                stadiumImagesModel.setImageName(stadiumImage);
                                stadiumImagesDataArrayList.add(stadiumImagesModel);
                            }
                            addPitchImageAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(AddStadiumActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddStadiumActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(AddStadiumActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(">>E", "onResponse: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(AddStadiumActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
