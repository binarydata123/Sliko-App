package app.sliko.owner.fragment;

import android.app.Dialog;
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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.sliko.R;
import app.sliko.activity.BookingActivity;
import app.sliko.booking.VerticalPitchModel;
import app.sliko.booking.model.UserBookingModel;
import app.sliko.owner.adapter.reports.HeaderTimingAdapter;
import app.sliko.owner.adapter.reports.VerticalPitchAdapter;
import app.sliko.owner.adapter.reports.VerticalTimingAdapter;
import app.sliko.owner.model.AvailabilityModel;
import app.sliko.utills.M;
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


    String pitch_id;
    String stadium_id;
    String user_id;
    String bookingDate;

    int SELECTED_DAY;
    int SELECTED_MONTH;
    int SELECTED_YEAR;

    public static BookingManagementFragment newInstance() {
        BookingManagementFragment fragment = new BookingManagementFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
    private VerticalPitchAdapter verticalPitchAdapter;
    private ArrayList<VerticalPitchModel> verticalPitchArrayList;
    private VerticalTimingAdapter verticalTimingAdapter;

    ArrayList<AvailabilityModel> availabilityModels = new ArrayList<>();
    ArrayList<UserBookingModel> timeListInside;
    HashMap<Integer, ArrayList<UserBookingModel>> timingData;
    HeaderTimingAdapter headerTimingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reports, container, false);
        ButterKnife.bind(BookingManagementFragment.this, view);

        dialog = M.showDialog(getActivity(), "", false);

        timingVerticalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                headerRecyclerViewForTimeSlot.scrollBy(dx, dy);
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
        return view;
    }

    private Dialog dialog;


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
                                verticalPitchModel.setStadiumId(pitchObject.getString("stadium_id"));
                                verticalPitchModel.setUserId(pitchObject.getString("user_id"));
                                verticalPitchArrayList.add(verticalPitchModel);
                                JSONArray bookedDataArray = pitchObject.getJSONArray("booked_data");
                                for (int m = 0; m < bookedDataArray.length(); m++) {
                                    JSONObject object = bookedDataArray.getJSONObject(m);
                                    UserBookingModel userBookingModel = new UserBookingModel();
                                    userBookingModel.setId("");
                                    userBookingModel.setBooked_status(object.getString("booked_status"));
                                    userBookingModel.setTime(object.getString("time"));
                                    timeListInside.add(userBookingModel);
                                }
                                timingData.put(k, timeListInside);
                            }
                            verticalPitchAdapter = new VerticalPitchAdapter(getActivity(), verticalPitchArrayList);
                            pitchVerticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            pitchVerticalRecyclerView.setAdapter(verticalPitchAdapter);
                            verticalPitchAdapter.notifyDataSetChanged();

                            headerTimingAdapter = new HeaderTimingAdapter(getActivity(), availabilityModels);
                            headerRecyclerViewForTimeSlot.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            headerRecyclerViewForTimeSlot.setAdapter(headerTimingAdapter);
                            headerTimingAdapter.notifyDataSetChanged();
                            verticalTimingAdapter = new VerticalTimingAdapter(getActivity(), timingData);
                            timingVerticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleBooking(String messs) {
        noDataLayout.setVisibility(View.VISIBLE);
        text.setText(messs.equalsIgnoreCase("") ? getString(R.string.noBookingAvailable) : messs);
        image.setBackgroundResource(R.drawable.ic_booking);
    }
}
