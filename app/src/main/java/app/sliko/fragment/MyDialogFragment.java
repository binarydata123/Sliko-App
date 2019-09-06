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

import java.util.Objects;

import app.sliko.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDialogFragment extends BottomSheetDialogFragment {
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

    public MyDialogFragment(String val) {
        this.val = val;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_pay_for_pitch, container, false);
        ButterKnife.bind(MyDialogFragment.this, view);
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