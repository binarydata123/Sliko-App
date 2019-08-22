package app.sliko.owner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import app.sliko.R;
import app.sliko.owner.fragment.AllReviewsFragment;
import app.sliko.owner.fragment.BookingFragment;
import app.sliko.owner.fragment.StadiumDetailsFragment;
import app.sliko.web.Api;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class StadiumOwnerHomeActivity extends AppCompatActivity {
    @BindView(R.id.bottomLayout)
    BottomNavigationView bottomLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.imageProfile)
    CircleImageView imageProfile;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.addPitchLayout)
    LinearLayout addPitchLayout;
    @BindView(R.id.myProfileLayout)
    LinearLayout myProfileLayout;
    @BindView(R.id.signOutLayout)
    LinearLayout signOutLayout;
    @BindView(R.id.editStadiumLayout)
    LinearLayout editStadiumLayout;
    @BindView(R.id.etName)
    TextView etName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stadium_owner_home);
        ButterKnife.bind(StadiumOwnerHomeActivity.this);
        toolbarTitle.setText(getString(R.string.addedStadium));
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        swapContentFragment(StadiumDetailsFragment.newInstance(), true, R.id.frameContainer);
        setListeners();
        toolbar.setNavigationOnClickListener(view -> {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        Picasso.get().load(Api.DUMMY_PROFILE).into(imageProfile);

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
                    toolbarTitle.setText(getString(R.string.addedStadium));
                    swapContentFragment(StadiumDetailsFragment.newInstance(), true, R.id.frameContainer);
                    return true;
                case R.id.action_pitches:
//                    toolbarTitle.setText(getString(R.string.menu_pitches));
//                    swapContentFragment(AllReviewsFragment.newInstance(), true, R.id.frameContainer);
                    return true;
                case R.id.action_bookings:
                    toolbarTitle.setText(getString(R.string.menu_bookings));
                    swapContentFragment(BookingFragment.newInstance(), true, R.id.frameContainer);
                    return true;
                case R.id.action_all_reviews:
                    toolbarTitle.setText(getString(R.string.reviews));
                    swapContentFragment(AllReviewsFragment.newInstance(), true, R.id.frameContainer);
                    return true;
            }
            return false;
        });
        editStadiumLayout.setOnClickListener(view -> {
            handleTransition(EditStadiumActivity.class);

        });
        addPitchLayout.setOnClickListener(view -> {
            handleTransition(AddPitchActivity.class);
        });
    }

    private void handleTransition(Class<?> navigateTo) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(StadiumOwnerHomeActivity.this, navigateTo));
    }
}
