package app.sliko.owner.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import app.sliko.EditProfileActivity;
import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.owner.events.StadiumExistEventOrNot;
import app.sliko.owner.events.SuccessFullyStadiumCreated;
import app.sliko.owner.fragment.AllReviewsFragment;
import app.sliko.owner.fragment.BookingManagementFragment;
import app.sliko.owner.fragment.ProfileFragment;
import app.sliko.owner.fragment.ReportsFragment;
import app.sliko.owner.fragment.StadiumDetailsFragment;
import app.sliko.utills.M;
import app.sliko.utills.Prefs;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StadiumOwnerHomeActivity extends AppCompatActivity {
    @BindView(R.id.bottomLayout)
    BottomNavigationView bottomLayout;
//    @BindView(R.id.drawer_layout)
//    DrawerLayout mDrawerLayout;
//    @BindView(R.id.ivUserImage)
//    CircleImageView ivUserImage;
    @BindView(R.id.toolbarTitle)
    SsMediumTextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.myProfileLayout)
//    LinearLayout myProfileLayout;
//
//    @BindView(R.id.etName)
//    TextView etName;
//    @BindView(R.id.etEmail)
//    TextView etEmail;
//    @BindView(R.id.etPhone)
//    TextView etPhone;
//    @BindView(R.id.editProfileLayout)
//    LinearLayout editProfileLayout;

    Dialog dialog;
    FragmentManager fragmentManager;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stadium_owner_home);
        fragmentManager = getSupportFragmentManager();
        ButterKnife.bind(StadiumOwnerHomeActivity.this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        dialog = M.showDialog(StadiumOwnerHomeActivity.this, "", false);
        toolbarTitle.setText(getString(R.string.home));
//        toolbar.setNavigationIcon(R.drawable.ic_menu);
        swapContentFragment(StadiumDetailsFragment.newInstance(), true, R.id.frameContainer);
        setListeners();
//        toolbar.setNavigationOnClickListener(view -> {
//            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
//                mDrawerLayout.closeDrawer(GravityCompat.START);
//            } else {
//                mDrawerLayout.openDrawer(GravityCompat.START);
//            }
//        });

        setUpLayout();
        if (!M.fetchUserTrivialInfo(StadiumOwnerHomeActivity.this, "id").equalsIgnoreCase("")) {
            fetchStadiumInfo();
        }
//        myProfileLayout.setOnClickListener(view -> {
//                    mDrawerLayout.closeDrawer(GravityCompat.START);
//                    bottomLayout.setSelectedItemId(R.id.action_profile);
//                }
//        );
    }

    private void setUpLayout() {
        Log.e(">>id", "setUpLayout: " + M.fetchUserTrivialInfo(StadiumOwnerHomeActivity.this, "id"));
        Log.e(">>id", "setUpLayout: " + M.fetchUserTrivialInfo(StadiumOwnerHomeActivity.this, "profilepic"));
//        Picasso.get().load(M.fetchUserTrivialInfo(StadiumOwnerHomeActivity.this, "profilepic")).error(R.drawable.app_icon).into(ivUserImage);
//        etEmail.setText(M.actAccordingly(StadiumOwnerHomeActivity.this, "email"));
//        etName.setText(M.actAccordingly(StadiumOwnerHomeActivity.this, "fullname"));
//        etPhone.setText(M.actAccordingly(StadiumOwnerHomeActivity.this, "phone"));
    }

    public void swapContentFragment(final Fragment i_newFragment, final boolean i_addToStack, final int container) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(container, i_newFragment);
        if (!i_addToStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void setListeners() {
        bottomLayout.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_stadium:
                    toolbarTitle.setText(getString(R.string.home));
                    swapContentFragment(StadiumDetailsFragment.newInstance(), true, R.id.frameContainer);
                    return true;
                case R.id.action_overview:
                    toolbarTitle.setText(getString(R.string.bookingManagement));
                    swapContentFragment(BookingManagementFragment.newInstance(), true, R.id.frameContainer);
                    return true;
                case R.id.action_bookings:
                    toolbarTitle.setText(getString(R.string.reports));
                    swapContentFragment(ReportsFragment.newInstance(), true, R.id.frameContainer);
                    return true;
                case R.id.action_all_reviews:
                    toolbarTitle.setText(getString(R.string.reviews));
                    swapContentFragment(AllReviewsFragment.newInstance(), true, R.id.frameContainer);
                    return true;
                case R.id.action_profile:
                    toolbarTitle.setText(getString(R.string.profile));
                    swapContentFragment(ProfileFragment.newInstance(), true, R.id.frameContainer);
                    return true;
            }
            return false;
        });


//        editProfileLayout.setOnClickListener(view ->
//                startActivity(new Intent(StadiumOwnerHomeActivity.this, EditProfileActivity.class).putExtra("typeOfProfile", "user")));
//
    }

    String stadiumId = "";

    private void fetchStadiumInfo() {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_stadium_detail(M.fetchUserTrivialInfo(StadiumOwnerHomeActivity.this, "id"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
                try {
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(sResponse);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Log.e(">>stadium_data", "onResponse: " + jsonObject.toString());
                        if (status.equalsIgnoreCase("true")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            stadiumId = data.getString("id");
                            Prefs.saveStadiumId(stadiumId, StadiumOwnerHomeActivity.this);
                            EventBus.getDefault().postSticky(new StadiumExistEventOrNot(true, jsonObject.toString()));
                        } else {

                            EventBus.getDefault().postSticky(new StadiumExistEventOrNot(false, message));
                        }
                    } else {

                        Toast.makeText(StadiumOwnerHomeActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                    Toast.makeText(StadiumOwnerHomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();

                Toast.makeText(StadiumOwnerHomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(SuccessFullyStadiumCreated successFullyStadiumCreated) {
        if (successFullyStadiumCreated != null) {
            if (successFullyStadiumCreated.isStatus()) {
                fetchStadiumInfo();
                setUpLayout();

            }
        }
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = bottomLayout.getMenu().getItem(0);
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            Toast.makeText(this, getString(R.string.press_once_more), Toast.LENGTH_SHORT).show();
            for (int k = 0; k < fragmentManager.getBackStackEntryCount(); k++) {
                fragmentManager.popBackStack();
            }
            bottomLayout.setSelectedItemId(homeItem.getItemId());
        }
    }

}
