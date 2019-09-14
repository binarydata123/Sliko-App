package app.sliko.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import app.sliko.EditProfileActivity;
import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.DialogConfirmation;
import app.sliko.models.HomeStadiumListModel;
import app.sliko.owner.events.SuccessFullyStadiumCreated;
import app.sliko.utills.M;
import app.sliko.utills.Prefs;
import app.sliko.web.Api;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UserHomeActivity extends AppCompatActivity implements LocationListener {
    SupportMapFragment mapFragment;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.ivUserImage)
    CircleImageView ivUserImage;
    /* google map */
    GoogleMap mMap;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private SupportMapFragment getSupportMapFragmentId() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    // Map map = null;
    // private boolean isMapReady = false;

    app.navizinhanca.utils.alerts.models.StadiumInfoDialog stadiumInfoDialog;
    WeakReference<UserHomeActivity> weakReference;
    Dialog dialog;
    LatLng customMarkerLocation;
    ArrayList<LatLng> latlngarraylist = new ArrayList<>();

    @BindView(R.id.etName)
    TextView etName;
    @BindView(R.id.etEmail)
    TextView etEmail;
    @BindView(R.id.etPhone)
    TextView etPhone;
    @BindView(R.id.signOutLayout)
    LinearLayout signOutLayout;
    @BindView(R.id.etUserBookingLayout)
    LinearLayout etUserBookingLayout;
    @BindView(R.id.settingLayout)
    LinearLayout settingLayout;

    private void setUpLayout() {
        Log.e(">>id", "setUpLayout: " + M.fetchUserTrivialInfo(UserHomeActivity.this, "id"));
        Log.e(">>id", "setUpLayout: " + M.fetchUserTrivialInfo(UserHomeActivity.this, "profilepic"));
        if (M.fetchUserTrivialInfo(UserHomeActivity.this, "profilepic").equalsIgnoreCase("")) {
            Picasso.get().load(Api.DUMMY_PROFILE).into(ivUserImage);
        } else {
            Picasso.get().load(M.fetchUserTrivialInfo(UserHomeActivity.this, "profilepic")).into(ivUserImage);
        }
        etEmail.setText(M.actAccordingly(UserHomeActivity.this, "email"));
        etName.setText(M.actAccordingly(UserHomeActivity.this, "fullname"));
        etPhone.setText(M.actAccordingly(UserHomeActivity.this, "phone"));

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(SuccessFullyStadiumCreated successFullyStadiumCreated) {
        if (successFullyStadiumCreated != null) {
            if (successFullyStadiumCreated.isStatus()) {
                setUpLayout();
            }
        }

    }


    @BindView(R.id.profileLayout)
    LinearLayout profileLayout;
    @BindView(R.id.editProfileLayout)
    LinearLayout editProfileLayout;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        Log.e("jj", "==");
        ButterKnife.bind(UserHomeActivity.this);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        dialog = M.showDialog(UserHomeActivity.this, "", false);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbarTitle.setText(getString(R.string.Stadiums));
        weakReference = new WeakReference<>(UserHomeActivity.this);
        toolbar.setNavigationOnClickListener(v -> {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        getAllStadiums();

        mapFragment = getSupportMapFragmentId();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        for (int k = 0; k < markerArrayList.size(); k++) {
                            if (marker.equals(markerArrayList.get(k))) {
                                inflateDialogForBubbleInfo(k);
                                return true;
                            }
                        }
                        return false;
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(UserHomeActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    mMap.setMyLocationEnabled(true);
                }
            }
        });
        setUpLayout();

        setListeners();
    }

    DialogConfirmation dialogConfirmation;

    private void setListeners() {
        signOutLayout.setOnClickListener(view -> {
            dialogConfirmation = DialogMethodCaller.openDialogConfirmation(UserHomeActivity.this, R.layout.dialog_confirmation, false);
            dialogConfirmation.getDialog_error().show();
            dialogConfirmation.getDialogConfirmationMessage().setText(getString(R.string.doYouWantToSignOut));
            dialogConfirmation.getDialogConfirmationTitle().setText(getString(R.string.signout));
            dialogConfirmation.getCloseButton().setOnClickListener(view12 -> dialogConfirmation.getDialog_error().dismiss());
            dialogConfirmation.getOkButton().setOnClickListener(view1 -> {
                dialogConfirmation.getDialog_error().dismiss();
                logoutApi();
            });
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomeActivity.this, ProfileActivity.class));
            }
        });
        settingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomeActivity.this, SettingActivity.class));
            }
        });
        editProfileLayout.setOnClickListener(view -> {
            startActivity(new Intent(UserHomeActivity.this, EditProfileActivity.class));
        });
        etUserBookingLayout.setOnClickListener(view -> {
            startActivity(new Intent(UserHomeActivity.this, UserBookingActivity.class));
        });
    }

    Handler handler;
    Runnable runnable;

    private void logoutApi() {
        dialog.show();
        handler = new Handler();
        runnable = () -> {
            dialog.cancel();
            Prefs.clearUserData(UserHomeActivity.this);
            startActivity(new Intent(UserHomeActivity.this, LoginActivity.class));
            finish();
        };
        handler.postDelayed(runnable, 500);
    }
//    private void logoutApi() {
//        dialog.show();
//        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
//        Call<ResponseBody> call = service.ep_logout(M.fetchUserTrivialInfo(UserHomeActivity.this, "id"));
//        call.enqueue(new retrofit2.Callback<ResponseBody>() {
//            @Override
//            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//                dialog.cancel();
//                try {
//                    if (response.isSuccessful()) {
//                        String sResponse = response.body().string();
//                        JSONObject jsonObject = new JSONObject(sResponse);
//                        String status = jsonObject.getString("status");
//                        String message = jsonObject.getString("message");
//                        if (status.equalsIgnoreCase("true")) {
//                            Prefs.clearUserData(UserHomeActivity.this);
//                            Toast.makeText(UserHomeActivity.this, message, Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(UserHomeActivity.this, LoginActivity.class));
//                            finish();
//                        } else {
//                            Toast.makeText(UserHomeActivity.this, message, Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(UserHomeActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    Log.i(">>error", "onResponse: " + e.getMessage());
//                    Toast.makeText(UserHomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
//                dialog.cancel();
//                Toast.makeText(UserHomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    private void inflateDialogForBubbleInfo(int index) {
        stadiumInfoDialog = DialogMethodCaller.openStadiumInfoDialog(UserHomeActivity.this, R.layout.dilaog_stadium_info, true);
        stadiumInfoDialog.getAlertDialog().show();
        stadiumInfoDialog.getSd_stadiumName().setText(homeStadiumListModelArrayList.get(index).getStadium_name());
        stadiumInfoDialog.getSD_stadiumAddress().setText("Address: " + homeStadiumListModelArrayList.get(index).getAddress());
        stadiumInfoDialog.getSd_stadiumProgressDialog().setVisibility(View.VISIBLE);
        Picasso.get().load(homeStadiumListModelArrayList.get(index).getStadium_image())
                .into(stadiumInfoDialog.getSd_stadiumImage(), new Callback() {
                    @Override
                    public void onSuccess() {
                        stadiumInfoDialog.getSd_stadiumProgressDialog().setVisibility(View.GONE);
                        stadiumInfoDialog.getSd_stadiumImage().setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        stadiumInfoDialog.getSd_stadiumProgressDialog().setVisibility(View.VISIBLE);
                    }
                });


        if (homeStadiumListModelArrayList.get(index).getRating().equalsIgnoreCase("")) {
            stadiumInfoDialog.getSd_stadiumRating().setRating(0);
            stadiumInfoDialog.getSd_stadiumReviews().setText(getString(R.string.noReviews));
        } else {
            stadiumInfoDialog.getSd_stadiumRating().setRating(Float.parseFloat(homeStadiumListModelArrayList.get(index).getRating()));
            stadiumInfoDialog.getSd_stadiumReviews().setVisibility(View.GONE);
        }

        if (homeStadiumListModelArrayList.get(index).getPrice().equalsIgnoreCase("")) {
            stadiumInfoDialog.getSd_stadiumPrice().setText("Price: " + "N.A");
        } else {
            stadiumInfoDialog.getSd_stadiumPrice().setText("Price: " + homeStadiumListModelArrayList.get(index).getPrice());
        }

        stadiumInfoDialog.getSd_stadiumSeeDetails().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stadiumInfoDialog.getAlertDialog().cancel();
                startActivity(new Intent(UserHomeActivity.this, StadiumDetailActivity.class)
                        .putExtra("stadium_id", homeStadiumListModelArrayList.get(index).getId())
                        .putExtra("user_id", homeStadiumListModelArrayList.get(index).getUser_id())
                        .putExtra("lowestPrice", homeStadiumListModelArrayList.get(index).getPrice()));
            }
        });
        stadiumInfoDialog.getSd_stadiumCloseButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stadiumInfoDialog.getAlertDialog().cancel();
            }
        });


    }


    private String lat = "", lng = "";
    ArrayList<HomeStadiumListModel> homeStadiumListModelArrayList = new ArrayList<>();
    ArrayList<Marker> markerArrayList = new ArrayList<>();


    private void getAllStadiums() {
        dialog.show();
        homeStadiumListModelArrayList.clear();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_homeListing(M.fetchUserTrivialInfo(UserHomeActivity.this, "id"));
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
                try {
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Log.e(">>E", "onResponse: " + jsonObject.toString());
                        if (status.equalsIgnoreCase("true")) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            if (dataArray.length() > 0) {
                                for (int k = 1; k < dataArray.length(); k++) {
                                    HomeStadiumListModel homeListModel = new HomeStadiumListModel();
                                    JSONObject innerJsonObject = dataArray.getJSONObject(k);
                                    if (!(innerJsonObject.getString("lat").equals("") || innerJsonObject.getString("lat").equals("null") || innerJsonObject.getString("lng").equals("") || innerJsonObject.getString("lng").equals("null"))) {
                                        homeListModel.setStadium_name(innerJsonObject.getString("stadium_name"));
                                        homeListModel.setDescription(innerJsonObject.getString("description"));
                                        homeListModel.setAddress(innerJsonObject.getString("address"));
                                        homeListModel.setLat(innerJsonObject.getString("lat"));
                                        homeListModel.setId(innerJsonObject.getString("id"));
                                        homeListModel.setUser_id(innerJsonObject.getString("user_id"));
                                        homeListModel.setLng(innerJsonObject.getString("lng"));
                                        homeListModel.setPrice(innerJsonObject.getString("price"));
                                        homeListModel.setStadium_image(innerJsonObject.getString("stadium_image"));
                                        homeListModel.setRating(innerJsonObject.getString("rating"));
                                        customMarkerLocation = new LatLng(Double.parseDouble(innerJsonObject.getString("lat")), Double.parseDouble(innerJsonObject.getString("lng")));
                                        homeStadiumListModelArrayList.add(homeListModel);
                                        latlngarraylist.add(customMarkerLocation);
                                        mMap.setOnMapLoadedCallback(() -> {
                                            for (int i = 0; i < homeStadiumListModelArrayList.size(); i++) {
                                                MarkerOptions cMarker = null;
                                                try {
                                                    cMarker = (new MarkerOptions()
                                                            .position(new LatLng(Double.valueOf(homeStadiumListModelArrayList.get(i).getLat()), Double.valueOf(homeStadiumListModelArrayList.get(i).getLng())))
                                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon)));

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                Marker marker = mMap.addMarker(cMarker);
                                                markerArrayList.add(marker);

                                            }
                                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            builder.include(new LatLng(Api.LAT, Api.LNG)); //Taking Point A (First LatLng)
                                            builder.include(new LatLng(Api.LAT, Api.LNG)); //Taking Point B (Second LatLng)
                                            LatLngBounds bounds = builder.build();
                                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                                            mMap.moveCamera(cu);
                                            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                                        });

                                    }
                                }
                            }
                        } else {
                            Log.i(">>log", "onSuccess: " + message);
                        }
                    } else {
                        Toast.makeText(UserHomeActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.i(">>error", "onResponse: " + e.getMessage());
                    Toast.makeText(UserHomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(UserHomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
//                createCustomMarker(UserHomeActivity.this, R.drawable.stadium, "marker", 0)));
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


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name, int position) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custommarkerlayout, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        markerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "This", Toast.LENGTH_SHORT).show();
            }
        });


        TextView txt_name = (TextView) marker.findViewById(R.id.name);
        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

}
