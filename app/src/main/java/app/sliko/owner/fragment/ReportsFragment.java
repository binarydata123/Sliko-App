package app.sliko.owner.fragment;

import android.app.DatePickerDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import app.sliko.R;
import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularTextView;
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

public class ReportsFragment extends Fragment {
    View view;
    @BindView(R.id.bookingRecyclerView)
    RecyclerView bookingRecyclerView;
    O_PitchBookingAdapter o_pitchBookingAdapter;
    ArrayList<BookingModel> bookingModelArrayList = new ArrayList<>();


    @BindView(R.id.pickStartDate)
    LinearLayout pickStartDate;
    @BindView(R.id.addBookingButton)
    FloatingActionButton addBookingButton;
    @BindView(R.id.pickEndDate)
    LinearLayout pickEndDate;
    @BindView(R.id.searchButton)
    LinearLayout searchButton;
    @BindView(R.id.noDataLayout)
    LinearLayout noDataLayout;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.startDateText)
    SsRegularTextView startDateText;
    @BindView(R.id.endDateText)
    SsRegularTextView endDateText;
    @BindView(R.id.backToBookings)
    SsRegularButton backToBookings;

    public static ReportsFragment newInstance() {
        Bundle args = new Bundle();
        ReportsFragment fragment = new ReportsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (context != null) {
            dialog = M.showDialog(context, "", false);
            setListeners();
            getAllBookings();
            addBookingButton.setVisibility(View.VISIBLE);
            bookingRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0 || dy < 0 && addBookingButton.isShown())
                        addBookingButton.hide();
                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE)
                        addBookingButton.show();
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
            addBookingButton.setVisibility(View.GONE);
            backToBookings.setVisibility(View.GONE);
            backToBookings.setText(getString(R.string.backToReports));
            backToBookings.setOnClickListener(view1 -> {

                start_date = "";
                end_date = "";
                getAllBookings();
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookings, container, false);
        ButterKnife.bind(ReportsFragment.this, view);

        return view;
    }

    private Dialog dialog;

    private void getAllBookings() {
        dialog.show();
        Log.i(">>stadiumId", "getAllBookings: " + Prefs.getStadiumId(context));
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_bookingList(M.fetchUserTrivialInfo(context, "id"),
                Prefs.getStadiumId(context), "", start_date, end_date);
        Log.e(">>stadiumId", "getAllBookings: " + Prefs.getStadiumId(context));
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
                        Log.e(">>reports", "onResponse: " + jsonObject.toString());
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
                                    bookingModel.setBooking_status(dataObject.getString("booking_status"));
                                    bookingModel.setStadium_id(dataObject.getString("stadium_id"));
                                    bookingModel.setPitch_id(dataObject.getString("pitch_id"));
                                    bookingModel.setBooking_date(dataObject.getString("booking_date"));
                                    bookingModel.setTime(dataObject.getString("time"));
                                    bookingModel.setUser_id(dataObject.getString("user_id"));
                                    if (dataObject.getString("booking_status").equalsIgnoreCase("1")) {

                                        bookingModel.setFeedback_message(dataObject.getString("feedback_message"));
                                    }
                                    bookingModelArrayList.add(bookingModel);
                                }
                                setAdapter();
                                noDataLayout.setVisibility(View.GONE);
                                bookingRecyclerView.setVisibility(View.VISIBLE);
                                image.setBackgroundResource(R.drawable.ic_booking);
                            } else {
                                handleNoData();
                            }
                        } else {
                            handleNoData();
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

    boolean isFirstTime = true;

    private void handleNoData() {
        bookingRecyclerView.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.VISIBLE);
        if (isFirstTime) {
            isFirstTime = false;
            //backToBookings.setVisibility(View.GONE);
        } else {

            //backToBookings.setVisibility(View.VISIBLE);
        }
        image.setBackgroundResource(R.drawable.ic_booking);
    }

    private String start_date = "", end_date = "";

    private void getDate(TextView et, int whichDate) {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(context, R.style.DialogTheme, (datepicker, selectedyear, selectedmonth, selectedday) -> {
            selectedmonth = selectedmonth + 1;
            et.setText("" + selectedyear + "-" + selectedmonth + "-" + selectedday);
            if (whichDate == 0) {

                start_date = startDateText.getText().toString();

            } else {
                end_date = endDateText.getText().toString();

            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Pick Date");
        mDatePicker.show();
    }


    private void setListeners() {

        searchButton.setOnClickListener(view -> {
            if (startDateText.getText().toString().length() == 0) {
                Toast.makeText(context, getString(R.string.pleaseSelectStartDate), Toast.LENGTH_SHORT).show();
            } else if (endDateText.getText().toString().length() == 0) {
                Toast.makeText(context, getString(R.string.pleaseSelectEndDate), Toast.LENGTH_SHORT).show();
            } else {
                start_date = startDateText.getText().toString();
                end_date = endDateText.getText().toString();
                getAllBookings();
            }
        });
        pickEndDate.setOnClickListener(view ->
                getDate(endDateText, 1)
        );
        pickStartDate.setOnClickListener(view ->
                getDate(startDateText, 2)
        );


    }


    private void setAdapter() {
        o_pitchBookingAdapter = new O_PitchBookingAdapter(context, bookingModelArrayList, "owner");
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        bookingRecyclerView.setAdapter(o_pitchBookingAdapter);
        bookingRecyclerView.setNestedScrollingEnabled(false);
        o_pitchBookingAdapter.notifyDataSetChanged();
    }
}
