package app.sliko.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.adapter.PitchAdapterUser;
import app.sliko.adapter.StadiumImagesAdapter;
import app.sliko.models.PitchModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class StadiumDetailActivity extends AppCompatActivity {
    @BindView(R.id.pitchesRecyclerView)
    RecyclerView pitchesRecyclerView;
    @BindView(R.id.stadiumImagesViewPager)
    ViewPager viewPager;
    @BindView(R.id.circleIndicator)
    CircleIndicator circleIndicator;
    @BindView(R.id.backButton)
    LinearLayout backButton;
    PitchAdapterUser pitchAdapterUser;
    ArrayList<PitchModel> pitchModelArrayList = new ArrayList<>();

    private ArrayList<String> reviewsModelArrayList = new ArrayList<>();
    StadiumImagesAdapter stadiumImagesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_staidium_detail);
        ButterKnife.bind(StadiumDetailActivity.this);
        setAdapter();
        reviewsModelArrayList.clear();
        reviewsModelArrayList.add("https://images2.minutemediacdn.com/image/upload/c_fill,w_912,h_516,f_auto,q_auto,g_auto/shape/cover/sport/5c0fcde7d2f4cdf8c9000003.jpeg");
        reviewsModelArrayList.add("https://images2.minutemediacdn.com/image/upload/c_fill,w_912,h_516,f_auto,q_auto,g_auto/shape/cover/sport/5c0fcde7d2f4cdf8c9000003.jpeg");
        reviewsModelArrayList.add("https://www.insidesport.co/wp-content/uploads/2018/04/4-11-1.jpg");
        stadiumImagesAdapter = new StadiumImagesAdapter(StadiumDetailActivity.this, reviewsModelArrayList);
        viewPager.setAdapter(stadiumImagesAdapter);
        circleIndicator.setViewPager(viewPager);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void setAdapter() {
        pitchAdapterUser = new PitchAdapterUser(StadiumDetailActivity.this, pitchModelArrayList);
        pitchesRecyclerView.setLayoutManager(new LinearLayoutManager(StadiumDetailActivity.this));
        pitchesRecyclerView.setAdapter(pitchAdapterUser);
        pitchAdapterUser.notifyDataSetChanged();
    }
}
