package app.sliko.owner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.owner.adapter.O_PitchBookingAdapter;
import app.sliko.owner.model.BookingModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingFragment extends Fragment {
    View view;
    @BindView(R.id.bookingRecyclerView)
    RecyclerView bookingRecyclerView;
    O_PitchBookingAdapter o_pitchBookingAdapter;
    ArrayList<BookingModel> bookingModelArrayList = new ArrayList<>();


    @BindView(R.id.pickStartDate)
    LinearLayout pickStartDate;
    @BindView(R.id.pickEndDate)
    LinearLayout pickEndDate;
    @BindView(R.id.searchButton)
    LinearLayout searchButton;

    public static BookingFragment newInstance() {

        Bundle args = new Bundle();
        BookingFragment fragment = new BookingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookings, container, false);
        ButterKnife.bind(BookingFragment.this, view);
        setListeners();
        setAdapter();
        return view;
    }

    private void setListeners() {
        searchButton.setOnClickListener(view -> {

        });
        pickEndDate.setOnClickListener(view -> {

        });
        pickStartDate.setOnClickListener(view -> {

        });

    }

    private void setAdapter() {
        o_pitchBookingAdapter = new O_PitchBookingAdapter(getActivity(), bookingModelArrayList);
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookingRecyclerView.setAdapter(o_pitchBookingAdapter);
        o_pitchBookingAdapter.notifyDataSetChanged();
    }


}
