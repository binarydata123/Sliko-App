package app.sliko.user;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import app.sliko.EditProfileActivity;
import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularTextView;
import app.sliko.events.ProfileUploadedSuccessEvent;
import app.sliko.user.fragment.FindPitchAndMatch;
import app.sliko.user.fragment.RewardsFragment;
import app.sliko.user.fragment.UserBookingHistoryFragment;
import app.sliko.user.fragment.UserMapFragment;
import app.sliko.user.fragment.UserProfileFragment;
import app.sliko.utills.M;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserDashboard extends AppCompatActivity {
    @BindView(R.id.bottomLayout)
    BottomNavigationView bottomLayout;
    @BindView(R.id.toolbarTitle)
    SsMediumTextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.drawer_layout)
//    DrawerLayout mDrawerLayout;
//    @BindView(R.id.ivUserImage)
//    CircleImageView ivUserImage;
//    @BindView(R.id.etName)
//    SsRegularTextView etName;
//    @BindView(R.id.etEmail)
//    SsRegularTextView etEmail;
//    @BindView(R.id.etPhone)
//    SsRegularTextView etPhone;
//    @BindView(R.id.etUserBookingLayout)
//    LinearLayout etUserBookingLayout;
//    @BindView(R.id.profileLayout)
//    LinearLayout profileLayout;
//    @BindView(R.id.editProfileLayout)
//    LinearLayout editProfileLayout;
    Dialog dialog;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashbaord);
        fragmentManager = getSupportFragmentManager();
        ButterKnife.bind(UserDashboard.this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        dialog = M.showDialog(UserDashboard.this, "", false);
//        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbarTitle.setText(getString(R.string.Stadiums));
        swapContentFragment(UserMapFragment.newInstance(), true, R.id.frameContainer);
        setListeners();
//        toolbar.setNavigationOnClickListener(v -> {
//            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
//                mDrawerLayout.closeDrawer(GravityCompat.START);
//            } else {
//                mDrawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
        setUpLayout();
        setListeners();
    }


    private void setUpLayout() {
        Log.e(">>id", "setUpLayout: " + M.fetchUserTrivialInfo(UserDashboard.this, "id"));
        Log.e(">>id", "setUpLayout: " + M.fetchUserTrivialInfo(UserDashboard.this, "profilepic"));
//        Picasso.get().load(M.fetchUserTrivialInfo(UserDashboard.this, "profilepic")).error(R.drawable.app_icon).into(ivUserImage);
//        etEmail.setText(M.actAccordingly(UserDashboard.this, "email"));
//        etName.setText(M.actAccordingly(UserDashboard.this, "fullname"));
//        etPhone.setText(M.actAccordingly(UserDashboard.this, "phone"));
    }

    public void swapContentFragment(final Fragment i_newFragment, final boolean i_addToStack, final int container) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(container, i_newFragment);
        if (i_addToStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void setListeners() {
        bottomLayout.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_bookings:
                    toolbarTitle.setText(getString(R.string.home));
                    swapContentFragment(UserMapFragment.newInstance(), true, R.id.frameContainer);
                    return true;
                case R.id.action_booking_history:
                    toolbarTitle.setText(getString(R.string.bookingHistory));
                    swapContentFragment(UserBookingHistoryFragment.newInstance(), true, R.id.frameContainer);
                    return true;
                case R.id.action_rewards:
                    toolbarTitle.setText(getString(R.string.rewards));
                    swapContentFragment(RewardsFragment.newInstance(), true, R.id.frameContainer);
                    return true;
                case R.id.action_find:
                    toolbarTitle.setText(getString(R.string.find));
                    swapContentFragment(FindPitchAndMatch.newInstance(), true, R.id.frameContainer);
                    return true;
                case R.id.action_profile:
                    toolbarTitle.setText(getString(R.string.profile));
                    swapContentFragment(UserProfileFragment.newInstance(), true, R.id.frameContainer);
                    return true;
            }
            return false;
        });

//        profileLayout.setOnClickListener(view -> {
//                    mDrawerLayout.closeDrawer(GravityCompat.START);
//                    bottomLayout.setSelectedItemId(R.id.action_profile);
//                }
//        );
//        editProfileLayout.setOnClickListener(view -> startActivity(new Intent(UserDashboard.this, EditProfileActivity.class)));
//        etUserBookingLayout.setOnClickListener(view -> startActivity(new Intent(UserDashboard.this, UserBookingHistoryFragment.class)));
//
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ProfileUploadedSuccessEvent profileUploadedSuccessEvent) {
        if (profileUploadedSuccessEvent != null) {
            if (profileUploadedSuccessEvent.isStatus()) {
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
