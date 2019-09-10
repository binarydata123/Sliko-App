package app.sliko.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.sliko.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(SettingActivity.this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.setting));
    }
}
