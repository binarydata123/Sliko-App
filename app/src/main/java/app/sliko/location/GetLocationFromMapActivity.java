package app.sliko.location;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularEditText;
import app.sliko.web.Api;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GetLocationFromMapActivity extends AppCompatActivity implements LocationListener {
    @BindView(R.id.toolbarTitle)
    SsMediumTextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pickLocation)
    SsRegularButton pickLocation;
    @BindView(R.id.etAddress)
    SsRegularEditText etAddress;
    SupportMapFragment mapFragment;

    GoogleMap mMap;

    WeakReference<GetLocationFromMapActivity> weakReference;

    private SupportMapFragment getSupportMapFragmentId() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    FusedLocationProviderClient mFusedLocationClient;


    private void forLocationSetUp() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(GetLocationFromMapActivity.this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(GetLocationFromMapActivity.this, location -> {
                    if (location != null) {
                        lat = location.getLatitude() + "";
                        lng = location.getLongitude() + "";
                    }
                });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        ButterKnife.bind(GetLocationFromMapActivity.this);
        forLocationSetUp();
        weakReference = new WeakReference<>(GetLocationFromMapActivity.this);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.findLocation));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        mapFragment = getSupportMapFragmentId();
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(marker -> false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(GetLocationFromMapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                mMap.setMyLocationEnabled(true);
            }


        });
        Handler handler = new Handler();
        Runnable runnable = () -> {
            if (mMap != null) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                if (lat.equals("") || lng.equals("")) {
                    builder.include(new LatLng(Api.LAT, Api.LNG)); //Taking Point A (First LatLng)
                    builder.include(new LatLng(Api.LAT, Api.LNG)); //Taking Point B (Second LatLng)
                } else {
                    builder.include(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))); //Taking Point A (First LatLng)
                    builder.include(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))); //Taking Point B (Second LatLng)
                }
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                mMap.moveCamera(cu);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(Api.ZOOM_LEVEL));
                mMap.setOnCameraMoveStartedListener(i -> {
                    switch (i) {
                        case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE:
                            Log.e(">>camera", "The user gestured on the map.");
                            break;
                        case GoogleMap.OnCameraMoveStartedListener
                                .REASON_API_ANIMATION:
                            Log.e(">>camera", "The user tapped something on the map.");
                            break;
                        case GoogleMap.OnCameraMoveStartedListener
                                .REASON_DEVELOPER_ANIMATION:
                            Log.e(">>camera", "The app moved the camera.");
                            break;
                    }
                });
                mMap.setOnCameraIdleListener(() -> {
                    latLng = mMap.getCameraPosition().target;
                    Log.e(">>data", "configureCameraIdle: " + latLng.latitude + "\n" + latLng.longitude);
                    geocoder = new Geocoder(GetLocationFromMapActivity.this);
                    new SetAddress(weakReference).execute();
                });

            }
        };

        handler.postDelayed(runnable, 500);

        pickLocation.setOnClickListener(view -> {
            Intent returnIntent = new Intent();
            returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            returnIntent.putExtra("lat", lat);
            returnIntent.putExtra("lng", lng);
            returnIntent.putExtra("addressSelected", etAddress.getText().toString());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });


    }

    String lat = "", lng = "";

    LatLng latLng;

    Geocoder geocoder;

    private static class SetAddress extends AsyncTask<Void, Void, List<Address>> {
        WeakReference<GetLocationFromMapActivity> weakReference;
        List<Address> addressList;

        private SetAddress(WeakReference<GetLocationFromMapActivity> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakReference.get().runOnUiThread(() -> weakReference.get().etAddress.setHint(weakReference.get().getString(R.string.fetchingAddress)));
        }

        @Override
        protected List<Address> doInBackground(Void... voids) {
            try {
                addressList = weakReference.get().geocoder.getFromLocation(weakReference.get().latLng.latitude,
                        weakReference.get().latLng.longitude, 1);
                weakReference.get().lat = String.valueOf(weakReference.get().latLng.latitude);
                weakReference.get().lng = String.valueOf(weakReference.get().latLng.longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addressList;
        }

        @Override
        protected void onPostExecute(List<Address> addressList) {
            super.onPostExecute(addressList);
            if (addressList != null && addressList.size() > 0) {
                String locality = addressList.get(0).getAddressLine(0);
                String country = addressList.get(0).getCountryName();
                if (locality != null && country != null)
                    weakReference.get().runOnUiThread(() -> {
                        weakReference.get().etAddress.setText(locality + "  " + country);
                        weakReference.get().etAddress.setSelection(weakReference.get().etAddress.length());
                    });
            }


        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(GetLocationFromMapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(GetLocationFromMapActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(GetLocationFromMapActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(GetLocationFromMapActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
//                createCustomMarker(UserMapFragment.this, R.drawable.stadium, "marker", 0)));
//        mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
