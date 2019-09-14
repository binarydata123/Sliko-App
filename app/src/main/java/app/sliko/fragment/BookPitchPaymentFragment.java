package app.sliko.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Objects;

import app.sliko.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BookPitchPaymentFragment extends BottomSheetDialogFragment {
    String val;
    private View view;
    @BindView(R.id.payNowButton)
    Button payNowButton;
    @BindView(R.id.stadiumName)
    TextView stadiumName;
    @BindView(R.id.stadiumAddress)
    TextView stadiumAddress;
    @BindView(R.id.pitchName)
    TextView pitchName;
    @BindView(R.id.pitchCost)
    TextView pitchCost;
    @BindView(R.id.pitchTimeSlot)
    TextView pitchTimeSlot;
    @BindView(R.id.pitchbookingDate)
    TextView pitchbookingDate;
    @BindView(R.id.amountTobePaid)
    TextView amountTobePaid;
    @BindView(R.id.closeButton)
    Button closeButton;

    private HashMap<String, String> deleverables;

    public BookPitchPaymentFragment(HashMap<String, String> deleverables) {
        this.deleverables = deleverables;
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
        if (getActivity() != null) {
            stadiumName.setText(deleverables.get("stadiumName"));
            stadiumAddress.setText(deleverables.get("stadium_address"));
            pitchName.setText(deleverables.get("pitchName"));
            pitchTimeSlot.setText(getActivity().getResources().getString(R.string.bookingTime) + "" + deleverables.get("time"));
            pitchCost.setText(getActivity().getResources().getString(R.string.price) + "" + deleverables.get("cost"));
            amountTobePaid.setText(getActivity().getString(R.string.amountToBePaid)+" "+getActivity().getResources().getString(R.string.price) + "" + deleverables.get("cost"));
            pitchbookingDate.setText(getActivity().getResources().getString(R.string.bookingDate) + "" + deleverables.get("bookingDate"));
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