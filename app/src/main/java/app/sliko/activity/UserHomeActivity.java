package app.sliko.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.web.Api;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        ButterKnife.bind(UserHomeActivity.this);
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
//                        "R.drawable.stadium"
                        //https://www.navizinhanca.com/public/images/categoryimage/1559810722.Fisica.
                        //new AsyncTaskLoadImage(weakReference).doInBackground("https://www.navizinhanca.com/public/images/categoryimage/1559810722.Fisica.png")
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
                                    stadiumInfoDialog = DialogMethodCaller.openStadiumInfoDialog(UserHomeActivity.this, R.layout.dilaog_stadium_info, false);
                                    stadiumInfoDialog.getAlertDialog().show();
                                    Picasso.get().load("https://www.insidesport.co/wp-content/uploads/2018/04/4-11-1.jpg")
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
                                    Picasso.get().load("https://www.insidesport.co/wp-content/uploads/2018/04/4-11-1.jpg")
                                            .into(ivUserImage);


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
    }

    private static class AsyncTaskLoadImage extends AsyncTask<String, String, byte[]> {
        WeakReference<UserHomeActivity> weakReference;

        private AsyncTaskLoadImage(WeakReference<UserHomeActivity> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        protected byte[] doInBackground(String... params) {
            Bitmap bitmap = null;
            byte[] byteArray = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            } catch (IOException e) {
                Log.i(">>stat", "doInBackground: ");
            }
            return byteArray;
        }

        @Override
        protected void onPostExecute(final byte[] bitmap) {
            super.onPostExecute(bitmap);
        }
    }

    private String lat = "", lng = "";

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
