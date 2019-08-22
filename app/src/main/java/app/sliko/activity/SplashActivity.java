package app.sliko.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import app.sliko.R;
import app.sliko.owner.activity.StadiumOwnerHomeActivity;
import app.sliko.utills.M;
import app.sliko.web.Api;

public class SplashActivity extends AppCompatActivity {
    private static final int DELAY = 1500;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler = new Handler();
        runnable = () -> {
            Log.i(">>Data", "onCreate: " +M.fetchUserTrivialInfo(SplashActivity.this, "id") );
            if (M.fetchUserTrivialInfo(SplashActivity.this, "id").equalsIgnoreCase("")) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            } else {
                if (M.hasTrivialInfo(SplashActivity.this, Api.IS_STADIUM)) {
                    startActivity(new Intent(SplashActivity.this, StadiumOwnerHomeActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, UserHomeActivity.class));
                    finish();
                }
            }

        };
        handler.postDelayed(runnable, DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
