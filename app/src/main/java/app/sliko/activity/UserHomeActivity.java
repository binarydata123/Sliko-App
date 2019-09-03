package app.sliko.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.models.HomeStadiumListModel;
import app.sliko.utills.M;
import app.sliko.web.Api;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UserHomeActivity extends AppCompatActivity {
    SupportMapFragment mapFragment;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.ivUserImage)
    CircleImageView ivUserImage;
    Image myImage;

    private SupportMapFragment getSupportMapFragmentId() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    Map map = null;
    private boolean isMapReady = false;

    app.navizinhanca.utils.alerts.models.StadiumInfoDialog stadiumInfoDialog;
    WeakReference<UserHomeActivity> weakReference;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        ButterKnife.bind(UserHomeActivity.this);
        dialog = M.showDialog(UserHomeActivity.this, "", false);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbarTitle.setText(getString(R.string.app_name));
        weakReference = new WeakReference<>(UserHomeActivity.this);
        toolbar.setNavigationOnClickListener(v -> {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        mapFragment = getSupportMapFragmentId();
        if (mapFragment != null) {
            mapFragment.init(error -> {
                if (error == OnEngineInitListener.Error.NONE) {
                    Log.i("map initialized", "onEngineInitializationCompleted: ");
                    map = mapFragment.getMap();
                    isMapReady = true;
                    myImage = new com.here.android.mpa.common.Image();
                    try {
                        myImage.setImageResource(R.drawable.stadium);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(">>image", "onCreate: " + e.getMessage());
                    }
                    MapMarker myMapMarker =
                            new MapMarker(new GeoCoordinate(30.7093,
                                    76.7076))
                                    .setTitle("Binary Data")
                                    .setDescription("Web and Mobile Development Company")
                                    .setCoordinate(new GeoCoordinate(30.7093,
                                            76.7076))
                                    .setIcon(myImage);

                    map.addMapObject(myMapMarker);
                } else {
                    isMapReady = false;

                    System.out.println("ERROR: Cannot initialize SupportMapFragment");
                }
                Log.i(">>map", "onEngineInitializationCompleted: " + error.getDetails());
                setMapCenter();
                mapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener.OnGestureListenerAdapter() {
                    @Override
                    public boolean onMapObjectsSelected(List<ViewObject> list) {
                        for (ViewObject viewObject : list) {
                            if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                                MapObject mapObject = (MapObject) viewObject;
                                if (mapObject.getType() == MapObject.Type.MARKER) {
                                    dialog.show();
                                    MapMarker window_marker = ((MapMarker) mapObject);
                                    for (int u = 0; u < mapMarkerArrayList.size(); u++) {
                                        if (window_marker.equals(mapMarkerArrayList.get(u))) {
                                            final int index = u;
                                            new Handler().postDelayed(() -> {
                                                        inflateDialogForBubbleInfo(index);
                                                        dialog.cancel();
                                                    },
                                                    500);
                                        }
                                    }
                                    return false;
                                }
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onTapEvent(PointF pointF) {

                        return false;
                    }
                });
            });
        }

        getAllStadiums();

    }

    private void inflateDialogForBubbleInfo(int index) {
        stadiumInfoDialog = DialogMethodCaller.openStadiumInfoDialog(UserHomeActivity.this, R.layout.dilaog_stadium_info, false);
        stadiumInfoDialog.getAlertDialog().show();
        stadiumInfoDialog.getSd_stadiumName().setText(homeStadiumListModelArrayList.get(index).getStadium_name());
        stadiumInfoDialog.getSD_stadiumAddress().setText(homeStadiumListModelArrayList.get(index).getAddress());
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
                        stadiumInfoDialog.getSd_stadiumImage().setVisibility(View.GONE);
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
                startActivity(new Intent(UserHomeActivity.this, StadiumDetailActivity.class));
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
    ArrayList<MapMarker> mapMarkerArrayList = new ArrayList<>();

    MapMarker myMapMarker;

    private void getAllStadiums() {
        homeStadiumListModelArrayList.clear();
        mapMarkerArrayList.clear();
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
                        if (status.equalsIgnoreCase("true")) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            if (dataArray.length() > 0) {
                                for (int k = 0; k < dataArray.length(); k++) {
                                    HomeStadiumListModel homeListModel = new HomeStadiumListModel();
                                    JSONObject innerJsonObject = dataArray.getJSONObject(k);
                                    homeListModel.setStadium_name(innerJsonObject.getString("stadium_name"));
                                    homeListModel.setDescription(innerJsonObject.getString("description"));
                                    homeListModel.setAddress(innerJsonObject.getString("address"));
                                    homeListModel.setLat(innerJsonObject.getString("lat"));
                                    homeListModel.setLng(innerJsonObject.getString("lng"));
                                    homeListModel.setPrice(innerJsonObject.getString("price"));
                                    homeListModel.setStadium_image(innerJsonObject.getString("stadium_image"));
                                    homeListModel.setRating(innerJsonObject.getString("rating"));
                                    if (!(innerJsonObject.getString("lat").equals("") || innerJsonObject.getString("lng").equals(""))) {
                                        myImage = new com.here.android.mpa.common.Image();
                                        myImage.setImageResource(R.drawable.placeholder);
                                        myMapMarker =
                                                new MapMarker(new GeoCoordinate(Double.parseDouble(innerJsonObject.getString("lat")),
                                                        Double.parseDouble(innerJsonObject.getString("lng"))))
                                                        .setTitle(innerJsonObject.getString("stadium_name"))
                                                        .setDescription(innerJsonObject.getString("description"))
                                                        .setCoordinate(new GeoCoordinate(Double.parseDouble(innerJsonObject.getString("lat")), Double.parseDouble(innerJsonObject.getString("lng"))))
                                                        .setIcon(myImage);
                                        map.addMapObject(myMapMarker);
                                        homeStadiumListModelArrayList.add(homeListModel);
                                        mapMarkerArrayList.add(myMapMarker);
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

    private void setMapCenter() {
        if (map != null) {
            if (lat.equalsIgnoreCase("") || lng.equalsIgnoreCase("")) {
                map.setCenter(new GeoCoordinate(Api.LAT, Api.LNG, 0.0),
                        Map.Animation.LINEAR);
            } else {
                map.setCenter(new GeoCoordinate(Double.parseDouble(lat), Double.parseDouble(lng), 0.0),
                        Map.Animation.LINEAR);
            }
            map.setZoomLevel(Api.ZOOM_LEVEL);
        }
    }

}
