package app.sliko.owner.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

import app.sliko.R;
import app.sliko.models.StadiumImagesModel;
import app.sliko.owner.adapter.AddImagesAdapter;
import app.sliko.utills.M;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPitchActivity extends Activity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.etfname)
    EditText etfname;
    @BindView(R.id.etdescriptn)
    EditText etdescriptn;
    @BindView(R.id.etprice)
    EditText etprice;
    @BindView(R.id.ll_addmultiImg)
    LinearLayout llAddmultiImg;
    @BindView(R.id.recyclrVIPichImg)
    RecyclerView recyclrVIPichImg;
    @BindView(R.id.chkboxsunday)
    CheckBox chkboxsunday;
    @BindView(R.id.sundayST)
    TextView sundayST;
    @BindView(R.id.sundayET)
    TextView sundayET;
    @BindView(R.id.chkboxmonday)
    CheckBox chkboxmonday;
    @BindView(R.id.mondayST)
    TextView mondayST;
    @BindView(R.id.mondayET)
    TextView mondayET;
    @BindView(R.id.chkboxtuesday)
    CheckBox chkboxtuesday;
    @BindView(R.id.tuesdayST)
    TextView tuesdayST;
    @BindView(R.id.tuesdayET)
    TextView tuesdayET;
    @BindView(R.id.chkboxwednsday)
    CheckBox chkboxwednsday;
    @BindView(R.id.wednsdayST)
    TextView wednsdayST;
    @BindView(R.id.wednsdyET)
    TextView wednsdyET;
    @BindView(R.id.chkboxthursday)
    CheckBox chkboxthursday;
    @BindView(R.id.thursdayST)
    TextView thursdayST;
    @BindView(R.id.thursdayET)
    TextView thursdayET;
    @BindView(R.id.chkboxfriday)
    CheckBox chkboxfriday;
    @BindView(R.id.fridayST)
    TextView fridayST;
    @BindView(R.id.fridayEt)
    TextView fridayEt;
    @BindView(R.id.chkboxsaturday)
    CheckBox chkboxsaturday;
    @BindView(R.id.saturdayST)
    TextView saturdayST;
    @BindView(R.id.saturdayET)
    TextView saturdayET;
    private static final int PERMISSIONS_REQUEST_CODE = 0x1;
    Dialog dialog;
    private ArrayList<StadiumImagesModel> imagesEncodedList;
    String chkbxsunday = "0", chkbxmonday = "0", chkbxtuesday = "0", chkbxwednsday = "0", chkbxthursday = "0",
            chkbxfriday = "0", chkbxsaturday = "0";
    AddImagesAdapter pitchImageAdapter;
    LinearLayoutManager lm;
    private int mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpitche);
        ButterKnife.bind(this);
        dialog = M.showDialog(this, "", false);
        imagesEncodedList = new ArrayList<>();
        imagesEncodedList.clear();

        listenerCheckbox();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.addPitch));
        lm = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclrVIPichImg.setLayoutManager(lm);
        pitchImageAdapter = new AddImagesAdapter(this, imagesEncodedList, "");
        recyclrVIPichImg.setAdapter(pitchImageAdapter);
    }

    public void listenerCheckbox() {
        chkboxsunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkbxsunday = "1";
                    sundayST.setEnabled(true);
                    sundayET.setEnabled(true);
                } else {
                    chkbxsunday = "0";
                    sundayST.setEnabled(false);
                    sundayET.setEnabled(false);
                }
            }
        });

        chkboxmonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkbxmonday = "1";
                    mondayST.setEnabled(true);
                    mondayET.setEnabled(true);
                } else {
                    chkbxmonday = "0";
                    mondayST.setEnabled(false);
                    mondayET.setEnabled(false);
                }
            }
        });


        chkboxtuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkbxtuesday = "1";
                    tuesdayST.setEnabled(true);
                    tuesdayET.setEnabled(true);
                } else {
                    chkbxtuesday = "0";
                    tuesdayST.setEnabled(false);
                    tuesdayET.setEnabled(false);
                }
            }
        });


        chkboxwednsday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkbxwednsday = "1";
                    wednsdayST.setEnabled(true);
                    wednsdyET.setEnabled(true);
                } else {
                    chkbxwednsday = "0";
                    wednsdayST.setEnabled(false);
                    wednsdyET.setEnabled(false);
                }
            }
        });


        chkboxthursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkbxthursday = "1";
                    thursdayST.setEnabled(true);
                    thursdayET.setEnabled(true);
                } else {
                    chkbxthursday = "0";
                    thursdayST.setEnabled(false);
                    thursdayET.setEnabled(false);
                }
            }
        });


        chkboxfriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkbxfriday = "1";
                    fridayST.setEnabled(true);
                    fridayEt.setEnabled(true);
                } else {
                    chkbxfriday = "0";
                    fridayST.setEnabled(false);
                    fridayEt.setEnabled(false);
                }
            }
        });

        chkboxsaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkbxsaturday = "1";
                    saturdayST.setEnabled(true);
                    saturdayET.setEnabled(true);
                } else {
                    chkbxsaturday = "0";
                    saturdayST.setEnabled(false);
                    saturdayET.setEnabled(false);
                }
            }
        });
    }

    @OnClick(R.id.addpitchbtn)
    void onAddpitchbtnClick() {
        setlisteners();
    }

    @OnClick(R.id.ll_addmultiImg)
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

    private void setlisteners() {
        if (etfname.length() == 0 || etfname.getText().toString().trim().length() == 0) {
            Toast.makeText(AddPitchActivity.this, getResources().getString(R.string.plzEnterpitchname), Toast.LENGTH_SHORT).show();
        } else if (etdescriptn.length() == 0 || etdescriptn.getText().toString().trim().length() == 0) {
            Toast.makeText(AddPitchActivity.this, getResources().getString(R.string.plzEnterdescption), Toast.LENGTH_SHORT).show();
        } else if (etprice.length() == 0 || etprice.getText().toString().trim().length() == 0) {
            Toast.makeText(AddPitchActivity.this, getResources().getString(R.string.enterprice), Toast.LENGTH_SHORT).show();
        } else if (imagesEncodedList.size() == 0) {
            Toast.makeText(this, "" + getResources().getString(R.string.please_insert_one_image_atleast), Toast.LENGTH_SHORT).show();
        } else {
            updateMyInfo();
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

    /* Time select */
    @OnClick(R.id.sundayST)
    void setSundayST() {
        timePickerDialog(sundayST);

    }

    @OnClick(R.id.sundayET)
    void setsundayET() {
        timePickerDialog(sundayET);


    }

    @OnClick(R.id.mondayST)
    void setmondayST() {
        timePickerDialog(mondayST);

    }

    @OnClick(R.id.mondayET)
    void setmondayET() {
        timePickerDialog(mondayET);

    }

    @OnClick(R.id.tuesdayST)
    void settuesdayST() {
        timePickerDialog(tuesdayST);

    }

    @OnClick(R.id.tuesdayET)
    void settuesdayET() {
        timePickerDialog(tuesdayET);

    }

    @OnClick(R.id.wednsdayST)
    void setwednsdayST() {
        timePickerDialog(wednsdayST);

    }

    @OnClick(R.id.wednsdyET)
    void setwednsdyET() {
        timePickerDialog(wednsdyET);

    }

    @OnClick(R.id.thursdayST)
    void setthursdayST() {
        timePickerDialog(thursdayST);

    }

    @OnClick(R.id.thursdayET)
    void setthursdayET() {
        timePickerDialog(thursdayET);

    }

    @OnClick(R.id.fridayST)
    void setfridayST() {
        timePickerDialog(fridayST);

    }

    @OnClick(R.id.fridayEt)
    void setfridayEt() {
        timePickerDialog(fridayEt);

    }

    @OnClick(R.id.saturdayST)
    void setsaturdayST() {
        timePickerDialog(saturdayST);

    }

    @OnClick(R.id.saturdayET)
    void setsaturdayET() {
        timePickerDialog(saturdayET);

    }

    public void timePickerDialog(TextView txtTime) {
// Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        txtTime.setText(hourOfDay + ":" + minute);

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    /*Api*/
    public void updateMyInfo() {
//        ArrayList<StadiumImagesModel> files = new ArrayList<>(pitchImageAdapter.getUpdatedList());
//
//        try {
//            Log.e("size", "==" + files.size() + "==" + files.toString());
//            dialog.show();
//            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
//            MultipartBody.Part[] multipart_body = null;
//            if (files.size() != 0) {
//                multipart_body = new MultipartBody.Part[files.size()];
//                for (int k = 0; k < files.size(); k++) {
//                    File file = new File(files.get(k).getImageName());
//                    RequestBody reqFile = RequestBody.create(MediaType.parse("*/*"), file);
//                    multipart_body[k] = MultipartBody.Part.createFormData("images[]", file.getName(), reqFile);
//                }
//
//            }
//            Log.e("name", "==" + etfname.getText().toString() + chkbxsunday + sundayST.getText().toString() +
//                    sundayET.getText().toString() + mondayST.getText().toString());
//
//            Call<ResponseBody> call = service.createPitch(
//                    multipart_body,
//                    RequestBody.create(MediaType.parse("multipart/form-data"), etfname.getText().toString()),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), "1"),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), "2"),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), etdescriptn.getText().toString()),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), etprice.getText().toString()),
//
//                    RequestBody.create(MediaType.parse("multipart/form-data"), chkbxsunday),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), sundayST.getText().toString()),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), sundayET.getText().toString()),
//
//                    RequestBody.create(MediaType.parse("multipart/form-data"), chkbxmonday),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), mondayST.getText().toString()),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), mondayET.getText().toString()),
//
//
//                    RequestBody.create(MediaType.parse("multipart/form-data"), chkbxtuesday),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), tuesdayST.getText().toString()),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), tuesdayET.getText().toString()),
//
//
//                    RequestBody.create(MediaType.parse("multipart/form-data"), chkbxwednsday),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), wednsdayST.getText().toString()),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), wednsdyET.getText().toString()),
//
//
//                    RequestBody.create(MediaType.parse("multipart/form-data"), chkbxthursday),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), thursdayST.getText().toString()),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), thursdayET.getText().toString()),
//
//
//                    RequestBody.create(MediaType.parse("multipart/form-data"), chkbxfriday),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), fridayST.getText().toString()),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), fridayEt.getText().toString()),
//
//                    RequestBody.create(MediaType.parse("multipart/form-data"), chkbxsaturday),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), saturdayST.getText().toString()),
//                    RequestBody.create(MediaType.parse("multipart/form-data"), saturdayET.getText().toString())
//
//            );
//
//            final Callback<ResponseBody> callback = new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    dialog.dismiss();
//                    if (response.isSuccessful()) {
//                        try {
//
//                            if (response.isSuccessful()) {
//                                String sResponse = response.body().toString();
//
//                                JSONObject jsonObject = new JSONObject(sResponse);
//                                String status = jsonObject.getString("status");
//                                String message = jsonObject.getString("message");
//
//
//                                Toast.makeText(AddPitchActivity.this, message, Toast.LENGTH_SHORT).show();
//                                if (status.equalsIgnoreCase("true")) {
//                                    EventBus.getDefault().postSticky(new SuccessFullyStadiumCreated(true));
//                                } else {
//                                    EventBus.getDefault().postSticky(false);
//                                }
//                                finish();
//                            } else {
//                                Toast.makeText(AddPitchActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                            }
//
//
//                        } catch (Exception e) {
//                            Log.e(">>exception", "onResponse: " + e.getMessage() + "\n" + response.errorBody().toString());
//
//                            Toast.makeText(AddPitchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(AddPitchActivity.this, response.toString() + "\n" + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
//                        Log.e(">>response", "onResponse: " + response.toString() + "\n" + response.errorBody().toString());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Log.e(">>apiNotResponding", "update student failure : " + "api NotResponding Failure");
//
//                    Toast.makeText(AddPitchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            };
//            call.enqueue(callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d(">>apiException", "update student failure : " + "api NotResponding Failure");
//        }
//    }
    }
}
