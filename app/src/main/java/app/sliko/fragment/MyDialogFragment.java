package app.sliko.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import app.sliko.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDialogFragment extends BottomSheetDialogFragment {
    String val;
    private View view;
    @BindView(R.id.payNowButton)
    Button payNowButton;

    public MyDialogFragment(String val) {
        // Required empty public constructor
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
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(">>data", "onViewCreated: " + val);
            }
        });

    }
}