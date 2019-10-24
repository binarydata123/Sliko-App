package app.sliko.user.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.StadiumInfoDialog;
import app.sliko.location.SearchStadiumAdapter;
import app.sliko.models.HomeStadiumListModel;
import app.sliko.user.activity.StadiumDetailActivity;
import app.sliko.utills.GpsUtils;
import app.sliko.utills.M;
import app.sliko.utills.Prefs;
import app.sliko.web.Api;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UserMapFragment extends Fragment implements LocationListener {
    SupportMapFragment mapFragment;

    /* google map */
    GoogleMap mMap;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public static UserMapFragment newInstance() {

        Bundle args = new Bundle();

        UserMapFragment fragment = new UserMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private SupportMapFragment getSupportMapFragmentId() {
        return (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapfragment);
    }


    // Map map = null;
    // private boolean isMapReady = false;

    StadiumInfoDialog stadiumInfoDialog;
    WeakReference<UserMapFragment> weakReference;
    Dialog dialog;
    LatLng customMarkerLocation;
    ArrayList<LatLng> latlngarraylist = new ArrayList<>();


    @BindView(R.id.etStadiumAddress)
    EditText etStadiumAddress;


    @BindView(R.id.stadiumAddressRecyclerView)
    RecyclerView stadiumAddressRecyclerView;
    @BindView(R.id.progressDialog)
    ProgressBar progressDialog;


    View view;

    FusedLocationProviderClient mFusedLocationClient;

    private void forLocationSetUp() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        lat = location.getLatitude() + "";
                        lng = location.getLongitude() + "";
                    }
                });
    }

    private boolean isGPS = false;

    private boolean ifPermissionGiven() {
        new GpsUtils(getActivity()).turnGPSOn(isGPSEnable -> {
            // turn on GPS
            isGPS = isGPSEnable;
        });
        return isGPS;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Prefs.VAR) {
                isGPS = true;

                forLocationSetUp();
            }
            //fetchAllProvidersList();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (context != null) {
            if (ifPermissionGiven()) {
                forLocationSetUp();
            }
            dialog = M.showDialog(context, "", false);
            weakReference = new WeakReference<>(UserMapFragment.this);
            forLocationSetUp();
            getAllStadiums();
            mapFragment = getSupportMapFragmentId();
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMyLocationEnabled(true);
                repositionLocationButton();
                mMap.setOnMarkerClickListener(marker -> {
                    for (int k = 0; k < markerArrayList.size(); k++) {
                        if (marker.equals(markerArrayList.get(k))) {
                            inflateDialogForBubbleInfo(k);
                            return true;
                        }
                    }
                    return false;
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    mMap.setMyLocationEnabled(true);
                }
            });


            setListeners();
        }
    }

    private void repositionLocationButton() {
        assert mapFragment.getView() != null;
        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 180, 16, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_user_home, container, false);
        ButterKnife.bind(UserMapFragment.this, view);
        return view;
    }


    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    SearchStadiumAdapter searchStadiumAdapter;

    private void setListeners() {
        etStadiumAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etStadiumAddress.length() == 0) {
                    progressDialog.setVisibility(View.GONE);
                    stadiumAddressRecyclerView.setVisibility(View.GONE);
                } else {
                    progressDialog.setVisibility(View.VISIBLE);

                    stadiumAddressRecyclerView.setVisibility(View.VISIBLE);
                    getStadiumName(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }


    private void inflateDialogForBubbleInfo(int index) {
        stadiumInfoDialog = DialogMethodCaller.openStadiumInfoDialog(context, R.layout.dilaog_stadium_info, true);
        stadiumInfoDialog.getAlertDialog().show();
        stadiumInfoDialog.getSd_stadiumName().setText(homeStadiumListModelArrayList.get(index).getStadium_name());
        stadiumInfoDialog.getSD_stadiumAddress().setText(homeStadiumListModelArrayList.get(index).getAddress());
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
                        stadiumInfoDialog.getSd_stadiumProgressDialog().setVisibility(View.GONE);
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
            stadiumInfoDialog.getSd_stadiumPrice().setText("N.A");
        } else {
            stadiumInfoDialog.getSd_stadiumPrice().setText(homeStadiumListModelArrayList.get(index).getPrice());
        }

        stadiumInfoDialog.getSd_stadiumSeeDetails().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stadiumInfoDialog.getAlertDialog().cancel();

                String transitionName = context.getString(R.string.allTransition);
                View transitionView = stadiumInfoDialog.getSd_stadiumImage();
                ViewCompat.setTransitionName(transitionView, transitionName);


                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(((Activity) context), transitionView, transitionName).toBundle();

                startActivity(new Intent(context, StadiumDetailActivity.class)
                                .putExtra("stadium_id", homeStadiumListModelArrayList.get(index).getId())
                                .putExtra("user_id", homeStadiumListModelArrayList.get(index).getUser_id())
                                .putExtra("stadium_name", homeStadiumListModelArrayList.get(index).getStadium_name())
                                .putExtra("lowestPrice", homeStadiumListModelArrayList.get(index).getPrice())
                        , bundle);
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
        Call<ResponseBody> call = service.ep_homeListing(M.fetchUserTrivialInfo(context, "id"), "");
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
                        Log.e(">>AllStadiumLists", "onResponse: " + jsonObject.toString());
                        if (status.equalsIgnoreCase("true")) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            if (dataArray.length() > 0) {
                                for (int k = 0; k < dataArray.length(); k++) {
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
                                        });

                                    }
                                }
                            }
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

                        } else {
                            Log.i(">>log", "onSuccess: " + message);
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.i(">>logError", "onResponse: " + e.getMessage());

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<HomeStadiumListModel>
            searchStadiumHomeArrayList;
    ;

    private void getStadiumName(String query) {
        if (query.equals("")) {
            progressDialog.setVisibility(View.GONE);
            stadiumAddressRecyclerView.setVisibility(View.GONE);
            return;
        }
        Log.e(">>query", "getStadiumName: " + query);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_homeListing(M.fetchUserTrivialInfo(context, "id"), query);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                progressDialog.setVisibility(View.GONE);
                try {
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Log.e(">>E", "onResponse: " + jsonObject.toString());
                        if (status.equalsIgnoreCase("true")) {
                            searchStadiumHomeArrayList = new ArrayList<>();
                            searchStadiumHomeArrayList.clear();
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            if (dataArray.length() > 0) {
                                for (int k = 0; k < dataArray.length(); k++) {
                                    JSONObject innerJsonObject = dataArray.getJSONObject(k);
                                    HomeStadiumListModel homeListModel = new HomeStadiumListModel();
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
                                    searchStadiumHomeArrayList.add(homeListModel);
                                }
                                searchStadiumAdapter = new SearchStadiumAdapter(context, searchStadiumHomeArrayList);
                                stadiumAddressRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                stadiumAddressRecyclerView.setAdapter(weakReference.get().searchStadiumAdapter);
                                searchStadiumAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.i(">>log", "onSuccess: " + message);
                        }
                    } else {
                        Log.i(">>log", "onSuccess: " + response.message());
                    }
                } catch (Exception e) {
                    Log.i(">>error", "onResponse: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                Log.i(">>log", "onSuccess: " + t.getMessage());
                //Toast.makeText(UserMapFragment.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.e(">>location", "onLocationChanged: " + location.getLatitude() + "\n" + location.getLongitude());
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
