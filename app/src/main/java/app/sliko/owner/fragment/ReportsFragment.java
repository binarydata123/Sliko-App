package app.sliko.owner.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import java.util.ArrayList;
import java.util.HashMap;

import app.sliko.R;
import app.sliko.owner.adapter.reports.HeaderTimingAdapter;
import app.sliko.owner.adapter.reports.VerticalPitchAdapter;
import app.sliko.owner.adapter.reports.VerticalTimingAdapter;
import app.sliko.owner.model.AvailabilityModel;
import app.sliko.web.Api;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportsFragment extends Fragment {
    private View view;

    @BindView(R.id.timingVerticalRecyclerView)
    RecyclerView timingVerticalRecyclerView;
    @BindView(R.id.pitchVerticalRecyclerView)
    RecyclerView pitchVerticalRecyclerView;
    @BindView(R.id.originalTimingForStadium)
    RecyclerView originalTimingForStadium;    @BindView(R.id.calendarView)
    CollapsibleCalendar calendarView;

    public static ReportsFragment newInstance() {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reports, container, false);
        ButterKnife.bind(ReportsFragment.this, view);

        verticalPitchArrayList.add("Pitch 1");
        verticalPitchArrayList.add("Pitch 2");
        verticalPitchArrayList.add("Pitch 3");
        verticalPitchArrayList.add("Pitch 4");
        verticalPitchArrayList.add("Pitch 5");
        setPitchAdapter();
        prepareData();
        setTimingAdapter();
        prepareDataForOriginalList();
        timingVerticalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                originalTimingForStadium.scrollBy(dx, dy);
            }
        });
        return view;
    }

    Dialog dialog;

    private VerticalPitchAdapter verticalPitchAdapter;
    private ArrayList<String> verticalPitchArrayList = new ArrayList<>();
    private VerticalTimingAdapter verticalTimingAdapter;

    ArrayList<AvailabilityModel> availabilityModels = new ArrayList<>();
    ArrayList<String> timeListInside;
    HashMap<Integer, ArrayList<String>> timingData;
    HeaderTimingAdapter headerTimingAdapter;

    private void prepareDataForOriginalList() {
        availabilityModels.clear();
        for (int k = 0; k < Api.timingArray.length; k++) {
            AvailabilityModel availabilityModel = new AvailabilityModel();
            availabilityModel.setPicked(false);
            availabilityModel.setTime(Api.timingArray[k]);
            availabilityModels.add(availabilityModel);
        }
        headerTimingAdapter = new HeaderTimingAdapter(getActivity(), availabilityModels);
        originalTimingForStadium.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        originalTimingForStadium.setAdapter(headerTimingAdapter);
        headerTimingAdapter.notifyDataSetChanged();
    }


    private void prepareData() {

        timingData = new HashMap<>();
        for (int k = 0; k < verticalPitchArrayList.size(); k++) {
            timeListInside = new ArrayList<>();
            for (int l = 0; l < 5; l++) {
                if (k == 2) {
                    timeListInside.add("Team Name");
                } else {
                    timeListInside.add("Closed");
                }
            }
            timingData.put(k, timeListInside);
        }
    }

    private void setTimingAdapter() {
        verticalTimingAdapter = new VerticalTimingAdapter(getActivity(), timingData);
        timingVerticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        timingVerticalRecyclerView.setAdapter(verticalTimingAdapter);
        verticalTimingAdapter.notifyDataSetChanged();
    }

    private void setPitchAdapter() {
        verticalPitchAdapter = new VerticalPitchAdapter(getActivity(), verticalPitchArrayList);
        pitchVerticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pitchVerticalRecyclerView.setAdapter(verticalPitchAdapter);
        verticalPitchAdapter.notifyDataSetChanged();
    }

}
