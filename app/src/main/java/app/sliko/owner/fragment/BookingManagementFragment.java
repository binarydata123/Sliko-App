package app.sliko.owner.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.UUID;

import app.sliko.R;
import app.sliko.activity.BookingActivity;
import app.sliko.adapter.StadiumDetailOpeningAdapter;
import app.sliko.adapter.StadiumImagesAdapter;
import app.sliko.adapter.StadiumOffDaysAdapter;
import app.sliko.booking.VerticalPitchModel;
import app.sliko.booking.model.UserBookingModel;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.BookPitchMauallyDialog;
import app.sliko.events.PayingForPitchEvent;
import app.sliko.owner.adapter.reports.HeaderTimingAdapter;
import app.sliko.owner.adapter.reports.VerticalPitchAdapter;
import app.sliko.owner.adapter.reports.VerticalTimingAdapter;
import app.sliko.owner.model.AvailabilityModel;
import app.sliko.user.activity.StadiumDetailActivity;
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

public class BookingManagementFragment extends Fragment {
    private View view;

    @BindView(R.id.timingVerticalRecyclerView)
    RecyclerView timingVerticalRecyclerView;
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
    @BindView(R.id.addBookingButton)
    FloatingActionButton addBookingButton;


    String pitch_id;
    String stadium_id;
    String user_id;
    String bookingDate, time;

    int SELECTED_DAY,SELECTED_DAY_BOOKED;
    int SELECTED_MONTH;
    int SELECTED_YEAR;

    public static BookingManagementFragment newInstance() {
        BookingManagementFragment fragment = new BookingManagementFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        SELECTED_MONTH = calendarView.getMonth() + 1;
        SELECTED_YEAR = calendarView.getYear();
        SELECTED_DAY = calendarView.getSelectedDay().getDay();
        Log.e(">>date", "onCreate: " + SELECTED_MONTH + "\n" +
                calendarView.getYear() + "\n" +
                calendarView.getSelectedDay().getDay());
        bookingDate = SELECTED_DAY + "-" + (SELECTED_MONTH < 10 ? "0" + SELECTED_MONTH : SELECTED_MONTH) + "-" + SELECTED_YEAR;
        //  Log.e(">>params", "prepareDataForResponse: " + user_id + "\n" + stadium_id + "\n" + bookingDate);
        bookPitchMauallyDialog.getTxtdate().setText(bookingDate);
       // dayOfWeek(bookingDate, SELECTED_DAY);
        fetchBookingData(bookingDate);
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



                    if (Prefs.getSuno(getActivity()).equalsIgnoreCase("1")){
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
                    if (Prefs.getMono(getActivity()).equalsIgnoreCase("1")){
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
                    if (Prefs.getTueso(getActivity()).equalsIgnoreCase("1")){
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
                    if (Prefs.getWedo(getActivity()).equalsIgnoreCase("1")){
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
                    if (Prefs.getThurso(getActivity()).equalsIgnoreCase("1")){
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
                    if (Prefs.getFrio(getActivity()).equalsIgnoreCase("1")){
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
                    if (Prefs.getSato(getActivity()).equalsIgnoreCase("1")){
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

    private VerticalPitchAdapter verticalPitchAdapter;
    private ArrayList<VerticalPitchModel> verticalPitchArrayList;
    private VerticalTimingAdapter verticalTimingAdapter;

    private ArrayList<AvailabilityModel> availabilityModels = new ArrayList<>();
    private ArrayList<UserBookingModel> timeListInside;
    private HashMap<Integer, ArrayList<UserBookingModel>> timingData;
    private HeaderTimingAdapter headerTimingAdapter;
    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private BookPitchMauallyDialog bookPitchMauallyDialog;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (context != null) {
            dialog = M.showDialog(context, "", false);
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            bookPitchMauallyDialog = DialogMethodCaller.openBookPitchMauallyDialog(context, R.layout.dialog_add_booking_manually, false);
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
            addBookingButton.setOnClickListener(view2 -> {


                bookPitchMauallyDialog.getDialog_error().show();
                bookPitchMauallyDialog.getBookButtonLayout().setOnClickListener(view12 -> {

                    if (M.matchValidation(bookPitchMauallyDialog.getEtName())) {
                        Toast.makeText(context, getString(R.string.please_enter_username), Toast.LENGTH_SHORT).show();
                    } else if (M.matchValidation(bookPitchMauallyDialog.getEtPhone())) {
                        Toast.makeText(context, getString(R.string.please_enter_phone), Toast.LENGTH_SHORT).show();

                    } else if ((bookPitchMauallyDialog.getEtPhone().length() > 15)) {
                        Toast.makeText(context, getString(R.string.please_enter_valid_phone), Toast.LENGTH_SHORT).show();

                    } else if (bookPitchMauallyDialog.getTxtdate().length() == 0) {
                        Toast.makeText(context, getString(R.string.pleaseSelectStartDate), Toast.LENGTH_SHORT).show();

                    } else if (bookPitchMauallyDialog.getTxttime().length() == 0) {
                        Toast.makeText(context, getString(R.string.please_select_atleast_one_time_slot), Toast.LENGTH_SHORT).show();

                    }


                    /*else if (M.matchValidation(bookPitchMauallyDialog.getEtEmail())) {
                        Toast.makeText(context, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();

                    } else if (!M.validateEmail(bookPitchMauallyDialog.getEtEmail().getText().toString())) {
                        Toast.makeText(context, getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show();

                    }*/
                    else {
                        bookPitchMauallyDialog.getTxtdate().setText(bookingDate);
                        bookPitchMauallyDialog.getTxttime().setText(time);
                        bookPitchMauallyDialog.getDialog_error().cancel();
                        registerUserManually(bookPitchMauallyDialog.getEtName().getText().toString(),
                                bookPitchMauallyDialog.getEtPhone().getText().toString(),
                                bookPitchMauallyDialog.getEtEmail().getText().toString(), UUID.randomUUID() + "", bookPitchMauallyDialog.getTxtdate().toString(), bookPitchMauallyDialog.getTxttime().toString());
                    }
                });
                bookPitchMauallyDialog.getCancelButton().setOnClickListener(view1 -> bookPitchMauallyDialog.getDialog_error().cancel());
            });


        }
    }


    private void registerUserManually(String name, String phone, String email, String password, String date, String time) {
        String role = Api.USER;
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        String social_id = "";
        String fcmToken = "";
        Call<ResponseBody> call = service.ep_register(name,
                email, phone, password, role, social_id, fcmToken,
                "", "", "",
                "", date, time, "");
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
                        //    Log.e(">>data", "onResponse: " + jsonObject.toString());
                        if (status.equalsIgnoreCase("true")) {
                            Toast.makeText(context, getString(R.string.bookingCreatedSuccessfully), Toast.LENGTH_SHORT).show();
                            createBookingApi();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("login1 response", "==" + response.message() + "<><>" + response.body());
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("login2 response", "==" + e.getMessage());

                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reports, container, false);
        ButterKnife.bind(BookingManagementFragment.this, view);
        return view;
    }

    private Dialog dialog;


    private void fetchBookingData(String bookingDate) {
        verticalPitchArrayList = new ArrayList<>();
        availabilityModels = new ArrayList<>();
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_userPitchBooking(Prefs.getStadiumId(context), bookingDate);
        //   Log.e(">>data", "fetchBookingData: " + Prefs.getStadiumId(context) + "\n" + bookingDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
                try {
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        String message = jsonObject.getString("message");
                        Log.e(">>pitchBookingData", "onResponse: " + jsonObject.toString());
                        if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String stadiumSlot = data.getString("stadium_slot");
                            Log.e(">>stadiumSlot", "onResponse: " + stadiumSlot);
                            List<String> stadiumSlotArray = Arrays.asList(stadiumSlot.split(","));
                            Log.e(">>stadiumSlot", "onResponse: " + stadiumSlotArray.size());
                            for (int l = 0; l < stadiumSlotArray.size(); l++) {
                                AvailabilityModel availabilityModel = new AvailabilityModel();
                                availabilityModel.setTime(stadiumSlotArray.get(l));
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
                                verticalPitchModel.setPitchPrice(pitchObject.getString("price"));
                                verticalPitchModel.setPitchId(pitchObject.getString("id"));
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
                            verticalPitchAdapter = new VerticalPitchAdapter(context, verticalPitchArrayList);
                            pitchVerticalRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                            pitchVerticalRecyclerView.setAdapter(verticalPitchAdapter);
                            verticalPitchAdapter.notifyDataSetChanged();

                            headerTimingAdapter = new HeaderTimingAdapter(context, availabilityModels);
                            headerRecyclerViewForTimeSlot.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                            headerRecyclerViewForTimeSlot.setAdapter(headerTimingAdapter);
                            headerTimingAdapter.notifyDataSetChanged();

                            verticalTimingAdapter = new VerticalTimingAdapter(context, timingData, "ownerView");
                            timingVerticalRecyclerView.setLayoutManager(new LinearLayoutManager(context));
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
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleBooking() {
        noDataLayout.setVisibility(View.VISIBLE);
        text.setText(getString(R.string.noBookingAvailable));
        image.setBackgroundResource(R.drawable.ic_booking);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(PayingForPitchEvent payingForPitchEvent) {

        Log.e("gettime slots", "<><>");
        if (payingForPitchEvent != null) {
            if (payingForPitchEvent.isStatus()) {
                try {

                    time = payingForPitchEvent.getTime();
                    Log.e("gettime slots", "<><>" + time);
                    bookPitchMauallyDialog.getTxttime().setText(time);
                    Snackbar snackbar = Snackbar
                            .make(addBookingButton, "you selected  " + time + "   slots for add booking", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } catch (Exception e) {
                    Log.e(">>Exception", "onEvent: " + e.getMessage());
                }
            }

        }
    }

    ////////////////
    SimpleDateFormat df, sdf;
    Date getSelectedDate, getCurrentDate;
    String cost, gettime, pitchIdReceivedForBooking, stadiumIdReceivedForBooking, userIdToBeSent, pitchName;

    //  HashMap<String, String> bookPitchData;
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventSucess(PayingForPitchEvent payingForPitchEvent) {
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
                        gettime = payingForPitchEvent.getTime();
                        stadium_id = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getStadiumId();

                        pitch_id = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getPitchId();
                        cost = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getPitchPrice();
                        pitchIdReceivedForBooking = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getPitchId();
                        stadiumIdReceivedForBooking = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getStadiumId();
                        pitchName = verticalPitchArrayList.get(payingForPitchEvent.getWithThisGetPitchPosition()).getPitchName();
                        userIdToBeSent = M.fetchUserTrivialInfo(getActivity(), "id");
                        //   Log.e(">>bookingDetails", "onEvent: " + time + "\n" +
                        //     cost + "\n" + pitchIdReceivedForBooking + "\n" + stadiumIdReceivedForBooking + "\n" + userIdToBeSent);
                     /*   bookPitchData = new HashMap<>();
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
                        BookPitchPaymentFragment bottomSheetFragment = new BookPitchPaymentFragment(BookingActivity.this, bookPitchData);
                        bottomSheetFragment.show(getSupportFragmentManager(), "pitchBookingDialogFragment");*/
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.bookingDateCannotBeInPast), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(">>Exception", "onEvent: " + e.getMessage());
                }
            }

        }
    }


    String payment_type = "offline";

    private void createBookingApi() {
        dialog.show();
        //   Log.e(">>payment", "getAllBookings: " + payment_type);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_createBooking(M.fetchUserTrivialInfo(context, "id"), payment_type,
                cost, bookingDate, time, stadium_id, pitch_id);
        //  Log.e(">>stadiumId", "getAllBookings: " + Prefs.getStadiumId(context)+stadium_id+"\n"+pitch_id+"\n"+cost);
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
                        //  Log.e(">>paymentSS", "onResponse: " + sResponse);
                        if (status.equalsIgnoreCase("true")) {
                            Snackbar snackbar = Snackbar
                                    .make(addBookingButton, "" + message, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            fetchBookingData(bookingDate);

                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(">>logError", "onResponse: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void fetchStadiumInfo(String user_id) {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_stadium_detail(user_id);
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
                        Log.e(">>stadiumDetails", "onResponse: " + jsonObject.toString());
                        if (status.equalsIgnoreCase("true")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String stadium_name = data.getString("stadium_name");
                            String id = data.getString("id");
                            String description = data.getString("description");
                            String address = data.getString("address");
                            String pitch_review_avg = data.getString("review_avg");

                            String check_mon = data.getString("check_mon");
                            String phone_number = data.getString("phone_number");
                           // stadiumPhone.setText(M.actAccordinglyWithJson(StadiumDetailActivity.this, phone_number));
                            String check_tue = data.getString("check_tue");
                            String check_wed = data.getString("check_wed");
                            String check_thu = data.getString("check_thu");
                            String check_fri = data.getString("check_fri");
                            String check_sat = data.getString("check_sat");
                            String check_sun = data.getString("check_sun");
                            ArrayList<String> offDayCheck = new ArrayList<>();
                            offDayCheck.add(check_mon);
                            offDayCheck.add(check_tue);
                            offDayCheck.add(check_wed);
                            offDayCheck.add(check_thu);
                            offDayCheck.add(check_fri);
                            offDayCheck.add(check_sat);
                            offDayCheck.add(check_sun);

                            Log.e("values check days","==="+offDayCheck);
                            ArrayList<String> offDays = new ArrayList<String>() {
                                {
                                    add("Mon");
                                    add("Tues");
                                    add("Wed");
                                    add("Thurs");
                                    add("Fri");
                                    add("Sat");
                                    add("Sun");
                                }
                            };



                            Prefs.saveDaysowner(check_sun,check_mon,check_tue,check_wed,check_thu,check_fri,check_sat,getActivity());

                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
