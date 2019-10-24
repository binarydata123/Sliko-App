package app.sliko.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import app.sliko.R;
import app.sliko.owner.activity.StadiumOwnerHomeActivity;
import app.sliko.user.UserDashboard;
import app.sliko.utills.M;

public class SplashActivity extends AppCompatActivity {
    private static final int DELAY = 1500;
    private Handler handler;
    private Runnable runnable;
    private final static int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1545;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashnew);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            } else {
                exitSplash();
            }
        } else {
            exitSplash();
        }


    }

    private void exitSplash() {
        handler = new Handler();
        runnable = () -> {
            Log.i(">>Data", "onCreate: " + M.fetchUserTrivialInfo(SplashActivity.this, "id"));
            if (M.fetchUserTrivialInfo(SplashActivity.this, "id").equalsIgnoreCase("")) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            } else {
                if (M.fetchUserTrivialInfo(SplashActivity.this, "role").equalsIgnoreCase("owner")) {
                    startActivity(new Intent(SplashActivity.this, StadiumOwnerHomeActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, UserDashboard.class));
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null)
            handler.removeCallbacks(runnable);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exitSplash();
            }
        }
    }

}
