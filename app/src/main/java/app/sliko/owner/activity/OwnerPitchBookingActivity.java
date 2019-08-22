package app.sliko.owner.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.BookPitchMauallyDialog;
import app.sliko.owner.adapter.O_PitchBookingAdapter;
import app.sliko.owner.model.BookingModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OwnerPitchBookingActivity extends AppCompatActivity {
    @BindView(R.id.addBookingButton)
    Button addBookingButton;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_pitch_booking);
        ButterKnife.bind(OwnerPitchBookingActivity.this);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.bookingDetail));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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


    private void setAdapter() {
        o_pitchBookingAdapter = new O_PitchBookingAdapter(OwnerPitchBookingActivity.this, bookingModelArrayList);
        pitchBookingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pitchBookingRecyclerView.setAdapter(o_pitchBookingAdapter);
        o_pitchBookingAdapter.notifyDataSetChanged();
    }
}
