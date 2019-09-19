package app.sliko.user.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.sliko.R;

public class RewardsFragment extends Fragment {
    public static RewardsFragment newInstance() {

        Bundle args = new Bundle();

        RewardsFragment fragment = new RewardsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_rewards, container, false);
        return view;
    }
}
