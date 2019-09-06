package app.sliko.owner.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.BookPitchMauallyDialog;
import app.sliko.owner.adapter.O_PitchBookingAdapter;
import app.sliko.owner.model.BookingModel;
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

public class OwnerPitchBookingActivity extends AppCompatActivity {
    @BindView(R.id.addBookingButton)
    FloatingActionButton addBookingButton;
    @BindView(R.id.pickStartDate)
    LinearLayout pickStartDate;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pickEndDate)
    LinearLayout pickEndDate;
    @BindView(R.id.searchButton)
    LinearLayout searchButton;
    @BindView(R.id.pitchBookingRecyclerView)
    RecyclerView pitchBookingRecyclerView;
    O_PitchBookingAdapter o_pitchBookingAdapter;
    ArrayList<BookingModel> bookingModelArrayList = new ArrayList<>();
    @BindView(R.id.startDateText)
    TextView startDateText;
    @BindView(R.id.endDateText)
    TextView endDateText;
    Dialog dialog;
    @BindView(R.id.noDataLayout)
    LinearLayout noDataLayout;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.text)
    TextView text;

    String pitch_id = "";
    String stadium_id = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_pitch_booking);
        ButterKnife.bind(OwnerPitchBookingActivity.this);
        dialog = M.showDialog(OwnerPitchBookingActivity.this, "", false);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.bookingDetail));
        toolbar.setNavigationOnClickListener(view -> finish());

        pitch_id = getIntent().getStringExtra("pitch_id");
        stadium_id = getIntent().getStringExtra("stadium_id");
        setListeners();
        getBookingDataForPitch();
    }

    BookPitchMauallyDialog bookPitchMauallyDialog;

    private String start_date = "", end_date = "";

    private void getDate(TextView et, int whichDate) {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(OwnerPitchBookingActivity.this, R.style.DialogTheme, (datepicker, selectedyear, selectedmonth, selectedday) -> {
            selectedmonth = selectedmonth + 1;
            et.setText("" + selectedyear + "-" + selectedmonth + "-" + selectedday);
            if (whichDate == 0) {
                start_date = et.getText().toString();
            } else {
                end_date = et.getText().toString();
            }
        }, mYear, mMonth, mDay);
        mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        mDatePicker.setTitle(whichDate == 0 ? "Pick Start Date" : "Pick End Date");
        mDatePicker.show();
    }

    private void setListeners() {
        searchButton.setOnClickListener(view -> {
            if (startDateText.getText().toString().length() == 0) {
                Toast.makeText(OwnerPitchBookingActivity.this, getString(R.string.pleaseSelectStartDate), Toast.LENGTH_SHORT).show();
            } else if (endDateText.getText().toString().length() == 0) {
                Toast.makeText(OwnerPitchBookingActivity.this, getString(R.string.pleaseSelectEndDate), Toast.LENGTH_SHORT).show();
            } else {
                getBookingDataForPitch();
            }
        });
        pickEndDate.setOnClickListener(view ->
                getDate(startDateText, 1)
        );
        pickStartDate.setOnClickListener(view ->
                getDate(endDateText, 0)
        );
        addBookingButton.setOnClickListener(view -> {
            bookPitchMauallyDialog = DialogMethodCaller.openBookPitchMauallyDialog(OwnerPitchBookingActivity.this, R.layout.dialog_add_booking_manually, false);
            bookPitchMauallyDialog.getDialog_error().show();
            bookPitchMauallyDialog.getCancelButton().setOnClickListener(view1 -> bookPitchMauallyDialog.getDialog_error().cancel());
        });
    }

    private void getBookingDataForPitch() {
        dialog.show();
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_bookingList(M.fetchUserTrivialInfo(OwnerPitchBookingActivity.this, "id"),
                stadium_id, pitch_id, start_date, end_date);
        Log.e(">>stadiumId", "getBookingForUser: " + Prefs.getStadiumId(OwnerPitchBookingActivity.this));
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
                            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("data");
                            if (jsonArray.length() > 0) {
                                for (int k = 0; k < jsonArray.length(); k++) {
                                    BookingModel bookingModel = new BookingModel();
                                    JSONObject dataObject = jsonArray.getJSONObject(k);
                                    bookingModel.setFullname(dataObject.getString("fullname"));
                                    bookingModel.setPhone(dataObject.getString("phone"));
                                    bookingModel.setStadium_name(dataObject.getString("stadium_name"));
                                    bookingModel.setStadium_address(dataObject.getString("stadium_address"));
                                    bookingModel.setPitch_name(dataObject.getString("pitch_name"));
                                    bookingModel.setPrice(dataObject.getString("price"));
                                    bookingModel.setCost(dataObject.getString("cost"));
                                    bookingModel.setPitch_review_avg(dataObject.getString("pitch_review_avg"));
                                    bookingModel.setId(dataObject.getString("id"));
                                    bookingModel.setStadium_id(dataObject.getString("stadium_id"));
                                    bookingModel.setPitch_id(dataObject.getString("pitch_id"));
                                    bookingModel.setStart_date(dataObject.getString("start_date"));
                                    bookingModel.setEnd_date(dataObject.getString("end_date"));
                                    bookingModel.setTime(dataObject.getString("time"));
                                    bookingModel.setUser_id(dataObject.getString("user_id"));
                                }
                                setAdapter();
                                o_pitchBookingAdapter.notifyDataSetChanged();
                                noDataLayout.setVisibility(View.GONE);
                                text.setText(getString(R.string.noBookingAvailableUSer));
                            } else {
                                noDataLayout.setVisibility(View.VISIBLE);
                                image.setBackgroundResource(R.drawable.ic_booking);
                                text.setText(getString(R.string.noBookingAvailableUSer));
                            }
                        } else {
                            noDataLayout.setVisibility(View.GONE);
                            image.setBackgroundResource(R.drawable.ic_booking);
                            text.setText(getString(R.string.noBookingAvailableUSer));
                        }
                    } else {
                        Toast.makeText(OwnerPitchBookingActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(OwnerPitchBookingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(OwnerPitchBookingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void setAdapter() {
        o_pitchBookingAdapter = new O_PitchBookingAdapter(OwnerPitchBookingActivity.this, bookingModelArrayList, "owner");
        pitchBookingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pitchBookingRecyclerView.setAdapter(o_pitchBookingAdapter);
        o_pitchBookingAdapter.notifyDataSetChanged();
    }
}
