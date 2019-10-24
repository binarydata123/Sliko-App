package app.sliko.activity;

import android.app.Dialog;
import android.content.Intent;
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

import com.google.android.material.snackbar.Snackbar;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.booking.VerticalPitchModel;
import app.sliko.booking.model.UserBookingModel;
import app.sliko.events.PayingForPitchEvent;
import app.sliko.events.SuccessFullPaymentEvent;
import app.sliko.fragment.BookPitchPaymentFragment;
import app.sliko.owner.adapter.reports.HeaderTimingAdapter;
import app.sliko.owner.adapter.reports.VerticalPitchAdapter;
import app.sliko.owner.adapter.reports.VerticalTimingAdapter;
import app.sliko.owner.model.AvailabilityModel;
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

public class BookingActivity extends AppCompatActivity{
    private View view;

    @BindView(R.id.timingVerticalRecyclerView)
    RecyclerView timingVerticalRecyclerView;
    @BindView(R.id.toolbarTitle)
    SsMediumTextView toolbarTitle;
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
    @BindView(R.id.bookingLayout)
    LinearLayout bookingLayout;

    String chksunday,chkmonday,chktuesday,chkwednesday,chkthursday,chkfriday,chksaturday;
ArrayList<String>calnderdate = new ArrayList<>();
    String pitch_id;
    String stadium_id;
    String user_id;
    String bookingDate;

    int SELECTED_DAY,SELECTED_DAY_BOOKED;
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
        prepareDataForResponse2();

    }




    private void prepareDataForResponse2() {
        SELECTED_MONTH = (calendarView.getMonth() + 1);
        SELECTED_YEAR = calendarView.getYear();
        SELECTED_DAY = calendarView.getSelectedDay().getDay();

        bookingDate = SELECTED_DAY + "-" + (SELECTED_MONTH < 10 ? "0" + SELECTED_MONTH : SELECTED_MONTH) + "-" + SELECTED_YEAR;
      //  dayOfWeek(bookingDate, SELECTED_DAY);

          fetchBookingData(bookingDate);
    }
    private void prepareDataForResponse() {
        SELECTED_MONTH = (calendarView.getMonth() + 1);
        SELECTED_YEAR = calendarView.getYear();
        SELECTED_DAY = calendarView.getSelectedDay().getDay();
       /* Log.e(">>date", "onCreate: " + (calendarView.getMonth() + 1) + "\n" +
                calendarView.getYear() + "\n" +
                calendarView.getSelectedDay().getDay());*/
        bookingDate = SELECTED_DAY + "-" + (SELECTED_MONTH < 10 ? "0" + SELECTED_MONTH : SELECTED_MONTH) + "-" + SELECTED_YEAR;
        dayOfWeek(bookingDate, SELECTED_DAY);

    }

    public  void dayOfWeek(String date, int SELECTED_DAY) {
        try {
            // int day = 0;
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("dd-MM-yyyy");
            Date now = simpleDateformat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            SELECTED_DAY = calendar.get(Calendar.DAY_OF_WEEK);
            Log.e("<><>", "lllll" + SELECTED_DAY);
            SELECTED_DAY_BOOKED= SELECTED_DAY;


            switch (SELECTED_DAY) {
                case Calendar.SUNDAY:
                    Log.e("---sunday---", "<>>");



                    if (Prefs.getSun(BookingActivity.this).equalsIgnoreCase("1")){
                        fetchBookingData(bookingDate);
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.gree_circle));

                    }else {
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.red_circle));

                        Snackbar snackbar = Snackbar
                                .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    break;
                case Calendar.MONDAY:
                    Log.e("---monday---", "<>>");
                    if (Prefs.getMon(BookingActivity.this).equalsIgnoreCase("1")){
                        fetchBookingData(bookingDate);
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.gree_circle));

                    }else {
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.red_circle));

                        Snackbar snackbar = Snackbar
                                .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    break;
                case Calendar.TUESDAY:
                    Log.e("---TUESDAY---", "<>>");
                    if (Prefs.getTues(BookingActivity.this).equalsIgnoreCase("1")){
                        fetchBookingData(bookingDate);
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.gree_circle));

                    }else {
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.red_circle));

                        Snackbar snackbar = Snackbar
                                .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    break;
                case Calendar.WEDNESDAY:
                    Log.e("---WEDNESDAY---", "<>>");
                    if (Prefs.getWed(BookingActivity.this).equalsIgnoreCase("1")){
                        fetchBookingData(bookingDate);
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.gree_circle));

                    }else {
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.red_circle));

                        Snackbar snackbar = Snackbar
                                .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    break;
                case Calendar.THURSDAY:
                    Log.e("---THURSDAY---", "<>>");
                    if (Prefs.getThurs(BookingActivity.this).equalsIgnoreCase("1")){
                        fetchBookingData(bookingDate);
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.gree_circle));

                    }else {
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.red_circle));

                        Snackbar snackbar = Snackbar
                                .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    break;
                case Calendar.FRIDAY:
                    Log.e("---FRIDAY---", "<>>");
                    if (Prefs.getFri(BookingActivity.this).equalsIgnoreCase("1")){
                        fetchBookingData(bookingDate);
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.gree_circle));

                    }else {
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.red_circle));

                        Snackbar snackbar = Snackbar
                                .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    break;
                case Calendar.SATURDAY:
                    Log.e("---SATURDAY---", "<>>");
                    if (Prefs.getSat(BookingActivity.this).equalsIgnoreCase("1")){
                        fetchBookingData(bookingDate);
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.gree_circle));

                    }else {
                        calendarView.setSelectedItemBackgroundDrawable(getResources().getDrawable(R.drawable.red_circle));

                        Snackbar snackbar = Snackbar
                                .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    break;


            }
        } catch (Exception e) {

        }
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
        availabilityModels = new ArrayList<>();
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_userPitchBooking(stadium_id, bookingDate);
        Log.e(">>bookingData", "fetchBookingData: " + stadium_id + "\n" + bookingDate);
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
                            List<String> stadiumSlotArrayList = Arrays.asList(stadiumSlot.split(","));
                            for (int l = 0; l < stadiumSlotArrayList.size(); l++) {
                                AvailabilityModel availabilityModel = new AvailabilityModel();
                                availabilityModel.setTime(stadiumSlotArrayList.get(l));
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
                            handleBooking();
                            bookingLayout.setVisibility(View.GONE);
                        }
                    } else {
                        handleBooking();
                        bookingLayout.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    handleBooking();
                    bookingLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(BookingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleBooking() {
        noDataLayout.setVisibility(View.VISIBLE);
        text.setText(getString(R.string.noBookingAvailable));
        image.setBackgroundResource(R.drawable.ic_booking);
    }

    SimpleDateFormat df, sdf;
    Date getSelectedDate, getCurrentDate;
    String cost, time, pitchIdReceivedForBooking, stadiumIdReceivedForBooking, userIdToBeSent, pitchName;
    HashMap<String, String> bookPitchData;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(PayingForPitchEvent payingForPitchEvent) {
        if (payingForPitchEvent != null) {
            if (payingForPitchEvent.isStatus()) {
                try {
                    sdf = new SimpleDateFormat("dd-MM-yyyy");
                    getSelectedDate = sdf.parse(bookingDate);
                    Date c = Calendar.getInstance().getTime();
                    df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c);
                    getCurrentDate = sdf.parse(formattedDate);
                    assert getCurrentDate != null;
                    if (!(getCurrentDate.after(getSelectedDate))) {
                        time = payingForPitchEvent.getTime();
                        cost = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getPitchPrice();
                        pitchIdReceivedForBooking = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getPitchId();
                        stadiumIdReceivedForBooking = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getStadiumId();
                        pitchName = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getPitchName();
                        userIdToBeSent = M.fetchUserTrivialInfo(BookingActivity.this, "id");
                        Log.e(">>bookingDetails", "onEvent: " + time + "\n" +
                                cost + "\n" + pitchIdReceivedForBooking + "\n" + stadiumIdReceivedForBooking + "\n" + userIdToBeSent);
                        bookPitchData = new HashMap<>();
                        bookPitchData.put("time", time);
                        bookPitchData.put("cost", cost);
                        bookPitchData.put("p_id", pitch_id);
                        bookPitchData.put("s_id", stadium_id);
                        bookPitchData.put("pitchIdReceivedForBooking", pitchIdReceivedForBooking);
                        bookPitchData.put("stadiumIdReceivedForBooking", stadiumIdReceivedForBooking);
                        bookPitchData.put("userIdToBeSent", userIdToBeSent);
                        bookPitchData.put("pitchName", pitchName);
                        bookPitchData.put("stadiumName", stadiumName);
                        bookPitchData.put("stadium_description", stadium_description);
                        bookPitchData.put("stadium_address", stadium_address);
                        bookPitchData.put("bookingDate", bookingDate);

                        /* not booked pitch if days closed*/


                        switch (SELECTED_DAY_BOOKED) {
                            case Calendar.SUNDAY:
                                Log.e("---sunday---", "<>>");



                                if (Prefs.getSun(BookingActivity.this).equalsIgnoreCase("1")){
                                    BookPitchPaymentFragment bottomSheetFragment = new BookPitchPaymentFragment(BookingActivity.this, bookPitchData);
                                    bottomSheetFragment.show(getSupportFragmentManager(), "pitchBookingDialogFragment");
                                }else {
                                    Snackbar snackbar = Snackbar
                                            .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                                break;
                            case Calendar.MONDAY:
                                Log.e("---monday---", "<>>");
                                if (Prefs.getMon(BookingActivity.this).equalsIgnoreCase("1")){
                                    BookPitchPaymentFragment bottomSheetFragment = new BookPitchPaymentFragment(BookingActivity.this, bookPitchData);
                                    bottomSheetFragment.show(getSupportFragmentManager(), "pitchBookingDialogFragment");
                                }else {
                                    Snackbar snackbar = Snackbar
                                            .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                                break;
                            case Calendar.TUESDAY:
                                Log.e("---TUESDAY---", "<>>");
                                if (Prefs.getTues(BookingActivity.this).equalsIgnoreCase("1")){
                                    BookPitchPaymentFragment bottomSheetFragment = new BookPitchPaymentFragment(BookingActivity.this, bookPitchData);
                                    bottomSheetFragment.show(getSupportFragmentManager(), "pitchBookingDialogFragment");
                                }else {
                                    Snackbar snackbar = Snackbar
                                            .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                                break;
                            case Calendar.WEDNESDAY:
                                Log.e("---WEDNESDAY---", "<>>");
                                if (Prefs.getWed(BookingActivity.this).equalsIgnoreCase("1")){
                                    BookPitchPaymentFragment bottomSheetFragment = new BookPitchPaymentFragment(BookingActivity.this, bookPitchData);
                                    bottomSheetFragment.show(getSupportFragmentManager(), "pitchBookingDialogFragment");
                                }else {
                                    Snackbar snackbar = Snackbar
                                            .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                                break;
                            case Calendar.THURSDAY:
                                Log.e("---THURSDAY---", "<>>");
                                if (Prefs.getThurs(BookingActivity.this).equalsIgnoreCase("1")){
                                    BookPitchPaymentFragment bottomSheetFragment = new BookPitchPaymentFragment(BookingActivity.this, bookPitchData);
                                    bottomSheetFragment.show(getSupportFragmentManager(), "pitchBookingDialogFragment");
                                }else {

                                }
                                break;
                            case Calendar.FRIDAY:
                                Log.e("---FRIDAY---", "<>>");
                                if (Prefs.getFri(BookingActivity.this).equalsIgnoreCase("1")){
                                    BookPitchPaymentFragment bottomSheetFragment = new BookPitchPaymentFragment(BookingActivity.this, bookPitchData);
                                    bottomSheetFragment.show(getSupportFragmentManager(), "pitchBookingDialogFragment");
                                }else {
                                    Snackbar snackbar = Snackbar
                                            .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                                break;
                            case Calendar.SATURDAY:
                                Log.e("---SATURDAY---", "<>>");
                                if (Prefs.getSat(BookingActivity.this).equalsIgnoreCase("1")){
                                    BookPitchPaymentFragment bottomSheetFragment = new BookPitchPaymentFragment(BookingActivity.this, bookPitchData);
                                    bottomSheetFragment.show(getSupportFragmentManager(), "pitchBookingDialogFragment");
                                }else {
                                    Snackbar snackbar = Snackbar
                                            .make(calendarView, "Ngày này đóng cửa bạn không thể đặt sân", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                                break;


                        }

                      /*  BookPitchPaymentFragment bottomSheetFragment = new BookPitchPaymentFragment(BookingActivity.this, bookPitchData);
                        bottomSheetFragment.show(getSupportFragmentManager(), "pitchBookingDialogFragment");*/
                    } else {
                        Toast.makeText(this, getString(R.string.bookingDateCannotBeInPast), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(">>Exception", "onEvent: " + e.getMessage());
                }
            }

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onSuccessFullPaymentEvent(SuccessFullPaymentEvent successFullPaymentEvent) {
        if (successFullPaymentEvent != null) {
            if (successFullPaymentEvent.isPaymentDone()) {
                startActivity(new Intent(BookingActivity.this, AfterPayConfirmActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra("pitchName", pitchName)
                        .putExtra("cost", cost)
                        .putExtra("time", time)
                        .putExtra("bookingDate", bookingDate)
                        .putExtra("stadiumName", stadiumName)
                );
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
