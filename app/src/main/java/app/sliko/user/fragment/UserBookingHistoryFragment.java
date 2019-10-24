package app.sliko.user.fragment;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import app.sliko.R;
import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularTextView;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.BookPitchMauallyDialog;
import app.sliko.events.SuccessFullReviewEvent;
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

public class UserBookingHistoryFragment extends Fragment {
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
    @BindView(R.id.text)
    SsRegularTextView text;
    @BindView(R.id.startDateText)
    SsRegularTextView startDateText;
    @BindView(R.id.endDateText)
    SsRegularTextView endDateText;
    @BindView(R.id.backToBookings)
    SsRegularButton backToBookings;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    boolean isFirstTime = true;

    private Context context;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(SuccessFullReviewEvent successFullReviewEvent) {
        if (successFullReviewEvent != null) {
            if (successFullReviewEvent.isReviewGiven()) {
                getBookingForUser();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static UserBookingHistoryFragment newInstance() {

        Bundle args = new Bundle();

        UserBookingHistoryFragment fragment = new UserBookingHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (context != null) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }

            dialog = M.showDialog(context, "", false);
            setListeners();
            getBookingForUser();

            addBookingButton.setVisibility(View.GONE);
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
            backToBookings.setOnClickListener(view1 -> {
                start_date = "";
                end_date = "";
                getBookingForUser();
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_user_booking_list, container, false);
        ButterKnife.bind(UserBookingHistoryFragment.this, view);
        return view;
    }


    private Dialog dialog;

    private void getBookingForUser() {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_bookingList(M.fetchUserTrivialInfo(context, "id"),
                "", "", start_date, end_date);
        Log.e(">>stadiumId", "getBookingForUser: " + Prefs.getStadiumId(context));
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
                        Log.e(">>bookingData", "onResponse: " + jsonObject.toString());
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
                                    bookingModel.setBooking_date(dataObject.getString("booking_date"));
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
                                o_pitchBookingAdapter.notifyDataSetChanged();
                                noDataLayout.setVisibility(View.GONE);
                                bookingRecyclerView.setVisibility(View.VISIBLE);
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

    private void handleNoData() {
        noDataLayout.setVisibility(View.VISIBLE);
        bookingRecyclerView.setVisibility(View.GONE);
        image.setBackgroundResource(R.drawable.ic_booking);
        text.setText(getString(R.string.noBookingAvailableUSer));
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

    private BookPitchMauallyDialog bookPitchMauallyDialog;

    private void setListeners() {

        searchButton.setOnClickListener(view -> {

//            Log.i("date","date== "+start_date);
            if (startDateText.getText().toString().length() == 0) {
                Toast.makeText(context, getString(R.string.pleaseSelectStartDate), Toast.LENGTH_SHORT).show();
            } else if (endDateText.getText().toString().length() == 0) {
                Toast.makeText(context, getString(R.string.pleaseSelectEndDate), Toast.LENGTH_SHORT).show();
            } else {
                start_date = startDateText.getText().toString();
                end_date = endDateText.getText().toString();
                Log.i("date", "date== " + start_date + " end== " + end_date);
                getBookingForUser();
            }
        });
        pickEndDate.setOnClickListener(view ->
                getDate(endDateText, 1)
        );
        pickStartDate.setOnClickListener(view ->
                getDate(startDateText, 2)
        );


        addBookingButton.setOnClickListener(view -> {
            bookPitchMauallyDialog = DialogMethodCaller.openBookPitchMauallyDialog(context, R.layout.dialog_add_booking_manually, false);
            bookPitchMauallyDialog.getDialog_error().show();
            bookPitchMauallyDialog.getCancelButton().setOnClickListener(view1 -> bookPitchMauallyDialog.getDialog_error().cancel());
        });
    }

    private void setAdapter() {
        o_pitchBookingAdapter = new O_PitchBookingAdapter(context, bookingModelArrayList, "user");
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        bookingRecyclerView.setAdapter(o_pitchBookingAdapter);
        bookingRecyclerView.setNestedScrollingEnabled(false);

    }
}
