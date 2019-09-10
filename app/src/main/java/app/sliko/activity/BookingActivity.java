package app.sliko.activity;

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

import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import app.sliko.R;
import app.sliko.booking.VerticalPitchModel;
import app.sliko.booking.model.UserBookingModel;
import app.sliko.events.PayingForPitchEvent;
import app.sliko.fragment.MyDialogFragment;
import app.sliko.owner.adapter.reports.HeaderTimingAdapter;
import app.sliko.owner.adapter.reports.VerticalPitchAdapter;
import app.sliko.owner.adapter.reports.VerticalTimingAdapter;
import app.sliko.owner.model.AvailabilityModel;
import app.sliko.utills.M;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {
    private View view;

    @BindView(R.id.timingVerticalRecyclerView)
    RecyclerView timingVerticalRecyclerView;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pitchVerticalRecyclerView)
    RecyclerView pitchVerticalRecyclerView;
    @BindView(R.id.originalTimingForStadium)
    RecyclerView headerRecyclerViewForTimeSlot;
    @BindView(R.id.calendarView)
    CollapsibleCalendar calendarView;
    @BindView(R.id.noDataLayout)
    LinearLayout noDataLayout;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.text)
    TextView text;


    String pitch_id;
    String stadium_id;
    String user_id;
    String bookingDate;

    int SELECTED_DAY;
    int SELECTED_MONTH;
    int SELECTED_YEAR;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking);
        ButterKnife.bind(BookingActivity.this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.bookPitch));
        pitch_id = getIntent().getStringExtra("pitch_id");
        stadium_id = getIntent().getStringExtra("stadium_id");
        user_id = getIntent().getStringExtra("user_id");
        dialog = M.showDialog(BookingActivity.this, "", false);

        headerRecyclerViewForTimeSlot.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                timingVerticalRecyclerView.scrollBy(dx, dy);
            }
        });

        calendarView.setTodayItemBackgroundDrawable(getResources().getDrawable(R.drawable.gree_circle));
        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.gree_circle));
        calendarView.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {

            }


            @Override
            public void onItemClick(View v) {
                prepareDataForResponse();
            }

            @Override
            public void onDataUpdate() {

            }

            @Override
            public void onMonthChange() {

            }

            @Override
            public void onWeekChange(int position) {

            }
        });

        prepareDataForResponse();

    }


    private void prepareDataForResponse() {
        SELECTED_MONTH = calendarView.getMonth();
        SELECTED_YEAR = calendarView.getYear();
        SELECTED_DAY = calendarView.getSelectedDay().getDay();
        Log.e(">>date", "onCreate: " + calendarView.getMonth() + "\n" +
                calendarView.getYear() + "\n" +
                calendarView.getSelectedDay().getDay());
        bookingDate = SELECTED_DAY + "-" + (SELECTED_MONTH < 10 ? "0" + SELECTED_MONTH : SELECTED_MONTH) + "-" + SELECTED_YEAR;
        Log.e(">>params", "prepareDataForResponse: " + user_id + "\n" + stadium_id + "\n" + bookingDate);
        fetchBookingData(bookingDate);
    }

    Dialog dialog;
    private VerticalPitchAdapter verticalPitchAdapter;
    private ArrayList<VerticalPitchModel> verticalPitchArrayList;
    private VerticalTimingAdapter verticalTimingAdapter;

    ArrayList<AvailabilityModel> availabilityModels = new ArrayList<>();
    ArrayList<UserBookingModel> timeListInside;
    HashMap<Integer, ArrayList<UserBookingModel>> timingData;
    HeaderTimingAdapter headerTimingAdapter;

    String stadiumName;
    String stadium_description;
    String stadium_address;

    private void fetchBookingData(String bookingDate) {
        verticalPitchArrayList = new ArrayList<>();

        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_userPitchBooking("5", "20", "21-08-2019");
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
                        Log.e(">>bookingResponse", "onResponse: " + jsonObject.toString());
                        if (status.equalsIgnoreCase("true")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            stadiumName = data.getString("stadium_name");
                            stadium_description = data.getString("stadium_description");
                            stadium_address = data.getString("stadium_address");
                            String stadiumSlot = data.getString("stadium_slot");
                            JSONArray stadiumSlotArray = new JSONArray(stadiumSlot);
                            for (int l = 0; l < stadiumSlotArray.length(); l++) {
                                AvailabilityModel availabilityModel = new AvailabilityModel();
                                availabilityModel.setTime(stadiumSlotArray.getString(l));
                                availabilityModel.setPicked(false);
                                availabilityModels.add(availabilityModel);
                            }
                            timingData = new HashMap<>();
                            JSONArray pitchArray = data.getJSONArray("pitch_listing");
                            for (int k = 0; k < pitchArray.length(); k++) {
                                timeListInside = new ArrayList<>();
                                JSONObject pitchObject = pitchArray.getJSONObject(k);
                                VerticalPitchModel verticalPitchModel = new VerticalPitchModel();
                                verticalPitchModel.setPitchName(pitchObject.getString("pitch_name"));
                                verticalPitchModel.setPitchId(pitchObject.getString("id"));
                                verticalPitchModel.setPitchPrice(pitchObject.getString("price"));
                                verticalPitchModel.setStadiumId(pitchObject.getString("stadium_id"));
                                verticalPitchModel.setUserId(pitchObject.getString("user_id"));
                                verticalPitchArrayList.add(verticalPitchModel);
                                JSONArray bookedDataArray = pitchObject.getJSONArray("booked_data");
                                for (int m = 0; m < bookedDataArray.length(); m++) {
                                    JSONObject object = bookedDataArray.getJSONObject(m);
                                    UserBookingModel userBookingModel = new UserBookingModel();
                                    userBookingModel.setShow_slot(object.getString("show_slot"));
                                    userBookingModel.setBooked_status(object.getString("booked_status"));
                                    userBookingModel.setTime(object.getString("time"));
                                    timeListInside.add(userBookingModel);
                                }
                                timingData.put(k, timeListInside);
                            }
                            verticalPitchAdapter = new VerticalPitchAdapter(BookingActivity.this, verticalPitchArrayList);
                            pitchVerticalRecyclerView.setLayoutManager(new LinearLayoutManager(BookingActivity.this));
                            pitchVerticalRecyclerView.setAdapter(verticalPitchAdapter);
                            verticalPitchAdapter.notifyDataSetChanged();

                            headerTimingAdapter = new HeaderTimingAdapter(BookingActivity.this, availabilityModels);
                            headerRecyclerViewForTimeSlot.setLayoutManager(new LinearLayoutManager(BookingActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            headerRecyclerViewForTimeSlot.setAdapter(headerTimingAdapter);
                            headerTimingAdapter.notifyDataSetChanged();

                            verticalTimingAdapter = new VerticalTimingAdapter(BookingActivity.this, timingData, "userView");
                            timingVerticalRecyclerView.setLayoutManager(new LinearLayoutManager(BookingActivity.this));
                            timingVerticalRecyclerView.setAdapter(verticalTimingAdapter);
                            verticalTimingAdapter.notifyDataSetChanged();
                        } else {
                            handleBooking(message);
                        }
                    } else {
                        handleBooking(response.message());
                    }
                } catch (Exception e) {
                    handleBooking(e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(BookingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleBooking(String messs) {
        noDataLayout.setVisibility(View.VISIBLE);
        text.setText(messs.equalsIgnoreCase("") ? getString(R.string.noBookingAvailable) : messs);
        image.setBackgroundResource(R.drawable.ic_booking);
    }

    SimpleDateFormat df, sdf;
    Date getSelectedDate, getCurrentDate;
    String cost, time, pitchIdReceivedForBooking, stadiumIdReceivedForBooking, userIdToBeSent, pitchName;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(PayingForPitchEvent payingForPitchEvent) {
        if (payingForPitchEvent != null) {
            if (payingForPitchEvent.isStatus()) {
//                try {
//                    sdf = new SimpleDateFormat("dd-MM-yyyy");
//                    getSelectedDate = sdf.parse(bookingDate);
//                    Date c = Calendar.getInstance().getTime();
//                    df = new SimpleDateFormat("dd-MM-yyyy");
//                    String formattedDate = df.format(c);
//                    getCurrentDate = sdf.parse(formattedDate);
//                    assert getCurrentDate != null;
//                    if (!(getCurrentDate.after(getSelectedDate))) {
                time = payingForPitchEvent.getTime();
                cost = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getPitchPrice();
                pitchIdReceivedForBooking = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getPitchId();
                stadiumIdReceivedForBooking = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getStadiumId();
                pitchName = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getPitchName();
                userIdToBeSent = M.fetchUserTrivialInfo(BookingActivity.this, "id");
                Log.e(">>bookingDetails", "onEvent: " + time + "\n" +
                        cost + "\n" + pitchIdReceivedForBooking + "\n" + stadiumIdReceivedForBooking + "\n" + userIdToBeSent);
                HashMap<String, String> deleverables = new HashMap<>();
                deleverables.put("time", time);
                deleverables.put("cost", cost);
                deleverables.put("pitchIdReceivedForBooking", pitchIdReceivedForBooking);
                deleverables.put("stadiumIdReceivedForBooking", stadiumIdReceivedForBooking);
                deleverables.put("userIdToBeSent", userIdToBeSent);
                deleverables.put("pitchName", pitchName);
                deleverables.put("stadiumName", stadiumName);
                deleverables.put("stadium_description", stadium_description);
                deleverables.put("stadium_address", stadium_address);
                deleverables.put("bookingDate", bookingDate);
                MyDialogFragment bottomSheetFragment = new MyDialogFragment(deleverables);
                bottomSheetFragment.show(getSupportFragmentManager(), "pitchBookingDialogFragment");
            } else {
                Toast.makeText(this, getString(R.string.pleaseSelectDifferentDate), Toast.LENGTH_SHORT).show();
            }
//                } catch (Exception e) {
//                    Log.e(">>Exception", "onEvent: " + e.getMessage());
//                }
        }
// }
    }

}
