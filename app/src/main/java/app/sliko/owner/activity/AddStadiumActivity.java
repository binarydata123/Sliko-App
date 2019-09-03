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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
    AutoCompleteTextView stadiumAddress;
    @BindView(R.id.ll_selectmultiImg)
    LinearLayout llSelectmultiImg;
    @BindView(R.id.recyclrVIPichImg)
    RecyclerView recyclrVIPichImg;
    @BindView(R.id.availabilityRecyclerView)
    RecyclerView availabilityRecyclerView;
    private static final int PERMISSIONS_REQUEST_CODE = 0x1;
    Dialog dialog;
    private ArrayList<StadiumImagesModel> imagesEncodedList;
    String imageEncoded, address = "";
    AddImagesAdapter addPitchImageAdapter;
    LinearLayoutManager lm;
    String userId;
    String lat = "", lng = "";

    StadiumOpeningAdapter stadiumOpeningAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stadium_activity);
        ButterKnife.bind(this);
        userId = M.fetchUserTrivialInfo(AddStadiumActivity.this, "id");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.addedStadium));
        dialog = M.showDialog(this, "", false);
        imagesEncodedList = new ArrayList<>();
        imagesEncodedList.clear();
        lm = new LinearLayoutManager(AddStadiumActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclrVIPichImg.setLayoutManager(lm);
        addPitchImageAdapter = new AddImagesAdapter(this, imagesEncodedList, "");
        recyclrVIPichImg.setAdapter(addPitchImageAdapter);


        prepareStadiumData();
    }

    ArrayList<AvailabilityModel> availabilityModels = new ArrayList<>();

    private void prepareStadiumData() {
        availabilityModels.clear();
        for (int k = 0; k < Api.timingArray.length; k++) {
            AvailabilityModel availabilityModel = new AvailabilityModel();
            availabilityModel.setPicked(false);
            availabilityModel.setTime(Api.timingArray[k]);
            availabilityModels.add(availabilityModel);
        }
        stadiumOpeningAdapter = new StadiumOpeningAdapter(AddStadiumActivity.this, availabilityModels);
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
        stadiumAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    makeAddressSuggestion(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        stadiumAddress.setOnItemClickListener((parent, view, position, id) -> {
            address = arrayListOfAddresses.get(position);
            getLatLong(address);
        });
    }

    private void getLatLong(String address) {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_getLatLng(address);
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
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            lat = dataObject.getString("lat");
                            lng = dataObject.getString("lng");
                        } else {
                            Toast.makeText(AddStadiumActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddStadiumActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(AddStadiumActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(AddStadiumActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<String> arrayListOfAddresses;
    ArrayAdapter<String> adapter_ArrayListOfAddress;

    private void makeAddressSuggestion(String query) {
        arrayListOfAddresses = new ArrayList<>();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_addressSuggestions(query);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        if (dataArray.length() > 0) {
                            for (int k = 0; k < dataArray.length(); k++) {
                                String getAddressString = dataArray.getString(k);
                                arrayListOfAddresses.add(getAddressString);
                                Log.i(">>index", "onSuccess: " + getAddressString);
                            }
                            Log.i(">>sie", "onSuccess: " + arrayListOfAddresses.size() + "");
                            adapter_ArrayListOfAddress = new ArrayAdapter<String>(AddStadiumActivity.this, R.layout.auto_complete_text, R.id.text, arrayListOfAddresses);
                            stadiumAddress.setThreshold(1);
                            stadiumAddress.setAdapter(adapter_ArrayListOfAddress);
                            adapter_ArrayListOfAddress.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(AddStadiumActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(AddStadiumActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AddStadiumActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setlisteners() {

        if (stadiumName.length() == 0 || stadiumName.getText().toString().trim().length() == 0) {
            Toast.makeText(AddStadiumActivity.this, getResources().getString(R.string.plzEnterpitchname), Toast.LENGTH_SHORT).show();
        } else if (stadiumDescription.length() == 0 || stadiumDescription.getText().toString().trim().length() == 0) {
            Toast.makeText(AddStadiumActivity.this, getResources().getString(R.string.plzEnterdescption), Toast.LENGTH_SHORT).show();
        } else if (stadiumAddress.length() == 0 || stadiumAddress.getText().toString().trim().length() == 0) {
            Toast.makeText(AddStadiumActivity.this, getResources().getString(R.string.plzenteraddress), Toast.LENGTH_SHORT).show();
        } else if (imagesEncodedList.size() == 0) {
            Toast.makeText(this, "" + getResources().getString(R.string.please_insert_one_image_atleast), Toast.LENGTH_SHORT).show();
        } else {
            updateMyInfo();
        }

    }

    @OnClick(R.id.ll_selectmultiImg)
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
            Log.e("size", "==" + files.size() + "==" + files.toString());
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
                    RequestBody.create(MediaType.parse("multipart/form-data"), lng));

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
                                M.updateTrivialInfo(AddStadiumActivity.this, Api.IS_STADIUM, Api.STADIUM_ADDED);
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


}
