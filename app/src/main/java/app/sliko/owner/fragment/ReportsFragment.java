package app.sliko.owner.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.UUID;

import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.BookPitchMauallyDialog;
import app.sliko.owner.adapter.O_PitchBookingAdapter;
import app.sliko.owner.model.BookingModel;
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
    TextView startDateText;
    @BindView(R.id.endDateText)
    TextView endDateText;
    @BindView(R.id.backToBookings)
    Button backToBookings;

    public static ReportsFragment newInstance() {
        Bundle args = new Bundle();
        ReportsFragment fragment = new ReportsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookings, container, false);
        ButterKnife.bind(ReportsFragment.this, view);
        dialog = M.showDialog(getActivity(), "", false);
        setListeners();
        getAllBookings();
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
        backToBookings.setText(getString(R.string.backToReports));
        backToBookings.setOnClickListener(view -> {

            start_date = "";
            end_date = "";
            getAllBookings();
        });
        return view;
    }

    private Dialog dialog;

    private void getAllBookings() {
        dialog.show();
        Log.i(">>stadiumId", "getAllBookings: " + Prefs.getStadiumId(getActivity()));
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_bookingList(M.fetchUserTrivialInfo(getActivity(), "id"),
                Prefs.getStadiumId(getActivity()), "", start_date, end_date);
        Log.e(">>stadiumId", "getAllBookings: " + Prefs.getStadiumId(getActivity()));
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
                                    bookingModelArrayList.add(bookingModel);
                                }
                                setAdapter();
                                noDataLayout.setVisibility(View.GONE);
                                image.setBackgroundResource(R.drawable.ic_booking);
                            } else {
                                handleNoData();
                            }
                        } else {
                            handleNoData();
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

    boolean isFirstTime = true;

    private void handleNoData() {
        noDataLayout.setVisibility(View.VISIBLE);
        if (isFirstTime) {
            isFirstTime = false;
            backToBookings.setVisibility(View.GONE);
        } else {

            backToBookings.setVisibility(View.VISIBLE);
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
        mDatePicker = new DatePickerDialog(getActivity(), R.style.DialogTheme, (datepicker, selectedyear, selectedmonth, selectedday) -> {
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
            if (startDateText.getText().toString().length() == 0) {
                Toast.makeText(getActivity(), getString(R.string.pleaseSelectStartDate), Toast.LENGTH_SHORT).show();
            } else if (endDateText.getText().toString().length() == 0) {
                Toast.makeText(getActivity(), getString(R.string.pleaseSelectEndDate), Toast.LENGTH_SHORT).show();
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


        addBookingButton.setOnClickListener(view -> {
            bookPitchMauallyDialog = DialogMethodCaller.openBookPitchMauallyDialog(getActivity(), R.layout.dialog_add_booking_manually, false);
            bookPitchMauallyDialog.getDialog_error().show();
            bookPitchMauallyDialog.getBookButtonLayout().setOnClickListener(view12 -> {

                if (M.matchValidation(bookPitchMauallyDialog.getEtName())) {
                    Toast.makeText(getActivity(), getString(R.string.please_enter_username), Toast.LENGTH_SHORT).show();
                } else if (M.matchValidation(bookPitchMauallyDialog.getEtPhone())) {
                    Toast.makeText(getActivity(), getString(R.string.please_enter_phone), Toast.LENGTH_SHORT).show();

                } else if ((bookPitchMauallyDialog.getEtPhone().length() > 15)) {
                    Toast.makeText(getActivity(), getString(R.string.please_enter_valid_phone), Toast.LENGTH_SHORT).show();

                } else if (M.matchValidation(bookPitchMauallyDialog.getEtEmail())) {
                    Toast.makeText(getActivity(), getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();

                } else if (!M.validateEmail(bookPitchMauallyDialog.getEtEmail().getText().toString())) {
                    Toast.makeText(getActivity(), getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show();

                } else {
                    bookPitchMauallyDialog.getDialog_error().cancel();
                    registerUserManually(bookPitchMauallyDialog.getEtName().getText().toString(),
                            bookPitchMauallyDialog.getEtPhone().getText().toString(),
                            bookPitchMauallyDialog.getEtEmail().getText().toString(), UUID.randomUUID() + "");
                }
            });
            bookPitchMauallyDialog.getCancelButton().setOnClickListener(view1 -> bookPitchMauallyDialog.getDialog_error().cancel());
        });
    }

    private void registerUserManually(String name, String phone, String email, String password) {
        String role = Api.USER;
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        String social_id = "";
        String fcmToken = "";
        Call<ResponseBody> call = service.ep_register(name,
                email, phone, password, role, social_id, fcmToken,
                "", "", "",
                "", "");
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
                        Log.e(">>data", "onResponse: " + jsonObject.toString());
                        if (status.equalsIgnoreCase("true")) {
                            Toast.makeText(getActivity(), getString(R.string.bookingCreatedSuccessfully), Toast.LENGTH_SHORT).show();
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

    private void setAdapter() {
        o_pitchBookingAdapter = new O_PitchBookingAdapter(getActivity(), bookingModelArrayList, "owner");
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookingRecyclerView.setAdapter(o_pitchBookingAdapter);
        bookingRecyclerView.setNestedScrollingEnabled(false);
        o_pitchBookingAdapter.notifyDataSetChanged();
    }
}
