package app.sliko.owner.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import app.sliko.R;
import app.sliko.models.StadiumImagesModel;
import app.sliko.owner.adapter.AddImagesAdapter;
import app.sliko.utills.M;
import app.sliko.utills.Prefs;
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

public class AddPitchActivity extends Activity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.etPitchName)
    EditText etPitchName;
    @BindView(R.id.etPitchDescription)
    EditText etPitchDescription;
    @BindView(R.id.etPitchPrice)
    EditText etPitchPrice;
    @BindView(R.id.pitchImageRecyclerView)
    RecyclerView pitchImageRecyclerView;

    private static final int PERMISSIONS_REQUEST_CODE = 0x1;
    Dialog dialog;
    private ArrayList<StadiumImagesModel> imagesEncodedList;
    AddImagesAdapter pitchImageAdapter;


    String pitchId = "";
    String userId = "";
    String stadiumId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pitch);
        ButterKnife.bind(this);
        stadiumId = Prefs.getStadiumId(AddPitchActivity.this);
        userId = M.fetchUserTrivialInfo(AddPitchActivity.this, "id");
        if (getIntent().getStringExtra("pitch_id") != null) {
            toolbarTitle.setText(getString(R.string.editPitch));

        } else {
            toolbarTitle.setText(getString(R.string.addPitch));
        }
        dialog = M.showDialog(this, "", false);
        imagesEncodedList = new ArrayList<>();
        imagesEncodedList.clear();
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        setAdapter();
    }


    private void setAdapter() {
        pitchImageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        pitchImageAdapter = new AddImagesAdapter(this, imagesEncodedList, "");
        pitchImageRecyclerView.setAdapter(pitchImageAdapter);
    }

    @OnClick(R.id.addPitchButton)
    void addPitchButtonClick() {
        if (etPitchName.length() == 0 || etPitchName.getText().toString().trim().length() == 0) {
            Toast.makeText(AddPitchActivity.this, getResources().getString(R.string.plzEnterpitchname), Toast.LENGTH_SHORT).show();
        } else if (etPitchDescription.length() == 0 || etPitchDescription.getText().toString().trim().length() == 0) {
            Toast.makeText(AddPitchActivity.this, getResources().getString(R.string.plzEnterdescption), Toast.LENGTH_SHORT).show();
        } else if (etPitchPrice.length() == 0 || etPitchPrice.getText().toString().trim().length() == 0) {
            Toast.makeText(AddPitchActivity.this, getResources().getString(R.string.enterprice), Toast.LENGTH_SHORT).show();
        } else if (imagesEncodedList.size() == 0) {
            Toast.makeText(this, "" + getResources().getString(R.string.please_insert_one_image_atleast), Toast.LENGTH_SHORT).show();
        } else {
            updateMyInfo();
        }
    }

    @OnClick(R.id.pickImageLayout)
    void selectMultipleImg() {
        if ((ActivityCompat.checkSelfPermission(AddPitchActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(AddPitchActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(AddPitchActivity.this, new String[]
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

    private void setListeners() {

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
                        StadiumImagesModel stadiumImagesModel = new StadiumImagesModel();
                        stadiumImagesModel.setImageId("");
                        stadiumImagesModel.setImageName(cursor.getString(columnIndex));
                        imagesEncodedList.add(stadiumImagesModel);
                        Log.e(">>idImage", "onActivityResult: " + mImageUri.getPath());
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
                                StadiumImagesModel stadiumImagesModel = new StadiumImagesModel();
                                stadiumImagesModel.setImageId("");
                                stadiumImagesModel.setImageName(cursor.getString(columnIndex));
                                imagesEncodedList.add(stadiumImagesModel);
                                Log.e(">>idImage", "onActivityResult: " + "\n" + uri.getPath());
                                cursor.close();
                            }
                            Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        }
                    }
                    pitchImageAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            }
        }
    }

    public void updateMyInfo() {
        ArrayList<StadiumImagesModel> files = new ArrayList<>(pitchImageAdapter.getUpdatedList());
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        MultipartBody.Part[] multipart_body = null;
        if (files.size() != 0) {
            multipart_body = new MultipartBody.Part[files.size()];
            for (int k = 0; k < files.size(); k++) {
                File file = new File(files.get(k).getImageName());
                RequestBody reqFile = RequestBody.create(MediaType.parse("*/*"), file);
                multipart_body[k] = MultipartBody.Part.createFormData("images[]", file.getName(), reqFile);
            }

        }
        Call<ResponseBody> call = service.createPitch(
                multipart_body,
                RequestBody.create(MediaType.parse("multipart/form-data"), M.fetchUserTrivialInfo(AddPitchActivity.this, "id")),
                RequestBody.create(MediaType.parse("multipart/form-data"), Prefs.getStadiumId(AddPitchActivity.this)),
                RequestBody.create(MediaType.parse("multipart/form-data"), etPitchName.getText().toString()),
                RequestBody.create(MediaType.parse("multipart/form-data"), etPitchDescription.getText().toString()),
                RequestBody.create(MediaType.parse("multipart/form-data"), etPitchPrice.getText().toString())
        );

        final Callback<ResponseBody> callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        if (response.isSuccessful()) {
                            String sResponse = response.body().toString();
                            JSONObject jsonObject = new JSONObject(sResponse);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            Toast.makeText(AddPitchActivity.this, message, Toast.LENGTH_SHORT).show();
                            Toast.makeText(AddPitchActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(AddPitchActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        Log.e(">>exception", "onResponse: " + e.getMessage() + "\n" + response.errorBody().toString());

                        Toast.makeText(AddPitchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddPitchActivity.this, response.toString() + "\n" + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    Log.e(">>response", "onResponse: " + response.toString() + "\n" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(">>apiNotResponding", "update student failure : " + "api NotResponding Failure");

                Toast.makeText(AddPitchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        call.enqueue(callback);
    }

//
//    private void getSinglePitchDetail() {
//        dialog.show();
//        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
//        Call<ResponseBody> call = service.getSinglePitchDetail("", "");
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//                dialog.cancel();
//                if (response.isSuccessful()) {
//                    try {
//                        if (response.isSuccessful()) {
//                            String sResponse = response.body().toString();
//
//                            JSONObject jsonObject = new JSONObject(sResponse);
//                            String status = jsonObject.getString("status");
//                            String message = jsonObject.getString("message");
//
//
//                            if (status.equalsIgnoreCase("true")) {
//                                /*json object data */
//
//                                JSONObject jsondata = jsonObject.getJSONObject("data");
//
//                                addPitchModel.setCreatedAt(jsondata.getString("created_at"));
//                                addPitchModel.setDescription(jsondata.getString("description"));
//                                addPitchModel.setId(jsondata.getInt("id"));
//                                addPitchModel.setPitchName();
//                                addPitchModel.setPrice(jsondata.getString("price"));
//                                addPitchModel.setStadiumId(jsondata.getString("stadium_id"));
//                                addPitchModel.setUpdatedAt(jsondata.getString("updated_at"));
//                                addPitchModel.setUserId(jsondata.getString("user_id"));
//                                etPitchName.setText(M.actAccordinglyWithJson(AddPitchActivity.this, jsondata.getString("pitch_name")));
//                                etPitchPrice.setText(M.actAccordinglyWithJson(AddPitchActivity.this, jsondata.getString("price")));
//                                etPitchDescription.setText(M.actAccordinglyWithJson(AddPitchActivity.this, jsondata.getString("description")));
//
//
//                                imgarray = new ArrayList<>();
//                                JSONArray jsonimages = jsondata.getJSONArray("pitch_gallery");
//                                PitchGallery pitchGallery = new PitchGallery();
//                                for (int i = 0; i < jsonimages.length(); i++) {
//                                    JSONObject jsonIMG = jsonimages.getJSONObject(i);
//                                    pitchGallery.setCreatedAt(jsonIMG.getString("created_at"));
//                                    pitchGallery.setId(jsonIMG.getString("id"));
//                                    pitchGallery.setIsdeleted(jsonIMG.getString("isdeleted"));
//                                    pitchGallery.setPitchId(jsonIMG.getString("pitch_id"));
//                                    pitchGallery.setPitchImage(jsonIMG.getString("pitch_image"));
//                                    pitchGallery.setStatus(jsonIMG.getString("status"));
//                                    pitchGallery.setUpdatedAt(jsonIMG.getString("updated_at"));
//                                    arraylistpitchgallery.add(pitchGallery);
//                                    imgarray.add(jsonIMG.getString("pitch_image"));
//                                }
//                                addPitchModel.setPitchGallery(arraylistpitchgallery);
//                                pitchImageAdapter = new AddImagesAdapter(EditPitchActivity.this, imgarray, "");
//                                recyclrVIPichImg.setAdapter(pitchImageAdapter);
//
//                            } else {
//                                Toast.makeText(EditPitchActivity.this, message, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(EditPitchActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                        }
//
//
//                    } catch (Exception e) {
//                        Log.e(">>exception", "onResponse: " + e.getMessage() + "\n" + response.errorBody().toString());
//
//                        Toast.makeText(EditPitchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(EditPitchActivity.this, response.toString() + "\n" + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
//                    Log.e(">>response", "onResponse: " + response.toString() + "\n" + response.errorBody().toString());
//                }
//            }
//
//
//            @Override
//            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
//                dialog.cancel();
//                Toast.makeText(EditPitchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


}
