package app.sliko.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Objects;

import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BookPitchPaymentFragment extends BottomSheetDialogFragment {
    String val;
    private View view;
    @BindView(R.id.payNowButton)
    SsRegularButton payNowButton;
    @BindView(R.id.stadiumName)
    SsRegularTextView stadiumName;
    @BindView(R.id.stadiumAddress)
    SsRegularTextView stadiumAddress;
    @BindView(R.id.pitchName)
    SsRegularTextView pitchName;
    @BindView(R.id.pitchCost)
    SsRegularTextView pitchCost;
    @BindView(R.id.pitchTimeSlot)
    SsRegularTextView pitchTimeSlot;
    @BindView(R.id.pitchbookingDate)
    SsRegularTextView pitchbookingDate;
    @BindView(R.id.amountTobePaid)
    SsMediumTextView amountTobePaid;
    @BindView(R.id.closeButton)
    SsRegularButton closeButton;
    Context context;
    private HashMap<String, String> deleverables;

    public BookPitchPaymentFragment(Context context,HashMap<String, String> deleverables) {
        this.deleverables = deleverables;
        this.context = context;
    }

    String pitchIdReceivedForBooking;
    String stadiumIdReceivedForBooking;
    String userIdToBeSent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pay_for_pitch, container, false);
        ButterKnife.bind(BookPitchPaymentFragment.this, view);
        if (context != null) {
            stadiumName.setText(deleverables.get("stadiumName"));
            stadiumAddress.setText(deleverables.get("stadium_address"));
            pitchName.setText(deleverables.get("pitchName"));
            pitchTimeSlot.setText(context.getResources().getString(R.string.bookingTime) + "" + deleverables.get("time"));
            pitchCost.setText(context.getResources().getString(R.string.price) + "" + deleverables.get("cost"));
            amountTobePaid.setText(context.getString(R.string.amountToBePaid)+" "+context.getResources().getString(R.string.price) + "" + deleverables.get("cost"));
            pitchbookingDate.setText(context.getResources().getString(R.string.bookingDate) + "" + deleverables.get("bookingDate"));
            pitchIdReceivedForBooking = deleverables.get("pitchIdReceivedForBooking");
            userIdToBeSent = deleverables.get("userIdToBeSent");
            stadiumIdReceivedForBooking = deleverables.get("stadiumIdReceivedForBooking");

            Log.e(">>bookingReceivedParams", "onEvent: " + pitchIdReceivedForBooking + "\n" +
                    stadiumIdReceivedForBooking + "\n" + userIdToBeSent);

        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        payNowButton.setOnClickListener(view1 -> {

        });


        closeButton.setOnClickListener(view12 -> dismissBottomDialog());
    }

    private void dismissBottomDialog() {
        Fragment prev = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentByTag("pitchBookingDialogFragment");
        if (prev != null) {
            BottomSheetDialogFragment df = (BottomSheetDialogFragment) prev;
            df.dismiss();
        }
    }
}