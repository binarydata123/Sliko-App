package app.sliko.owner.activity;

import android.app.Dialog;
import android.os.Bundle;
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

import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.BookPitchMauallyDialog;
import app.sliko.owner.adapter.O_PitchBookingAdapter;
import app.sliko.owner.model.BookingModel;
import app.sliko.utills.M;
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

    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_pitch_booking);
        ButterKnife.bind(OwnerPitchBookingActivity.this);
        dialog = M.showDialog(OwnerPitchBookingActivity.this, "", false);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.bookingDetail));
        toolbar.setNavigationOnClickListener(view -> finish());
        setAdapter();
        setListeners();
    }

    BookPitchMauallyDialog bookPitchMauallyDialog;

    private void setListeners() {
        searchButton.setOnClickListener(view -> {

        });
        pickEndDate.setOnClickListener(view -> {

        });
        pickStartDate.setOnClickListener(view -> {

        });
        addBookingButton.setOnClickListener(view -> {
            bookPitchMauallyDialog = DialogMethodCaller.openBookPitchMauallyDialog(OwnerPitchBookingActivity.this, R.layout.dialog_add_booking_manually, false);
            bookPitchMauallyDialog.getDialog_error().show();
            bookPitchMauallyDialog.getCancelButton().setOnClickListener(view1 -> bookPitchMauallyDialog.getDialog_error().cancel());
        });
    }

    private void getAllBookingData() {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_ownerBookingList("", "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject("response");
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("true")) {
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("data");
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
                    } else {
                        Toast.makeText(OwnerPitchBookingActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

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
