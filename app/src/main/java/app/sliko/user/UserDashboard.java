package app.sliko.user;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import org.json.JSONObject;

import app.sliko.EditProfileActivity;
import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularTextView;
import app.sliko.activity.LoginActivity;
import app.sliko.activity.SettingActivity;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.DialogConfirmation;
import app.sliko.owner.events.StadiumExistEventOrNot;
import app.sliko.user.fragment.FindPitchAndMatch;
import app.sliko.user.fragment.RewardsFragment;
import app.sliko.user.fragment.UserBookingHistoryFragment;
import app.sliko.user.fragment.UserMapFragment;
import app.sliko.user.fragment.UserProfileFragment;
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

public class UserDashboard extends AppCompatActivity {
    @BindView(R.id.bottomLayout)
    BottomNavigationView bottomLayout;

    @BindView(R.id.toolbarTitle)
    SsMediumTextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.ivUserImage)
    CircleImageView ivUserImage;
    @BindView(R.id.etName)
    SsRegularTextView etName;
    @BindView(R.id.etEmail)
    SsRegularTextView etEmail;
    @BindView(R.id.etPhone)
    SsRegularTextView etPhone;
    @BindView(R.id.signOutLayout)
    LinearLayout signOutLayout;
    @BindView(R.id.etUserBookingLayout)
    LinearLayout etUserBookingLayout;
    @BindView(R.id.settingLayout)
    LinearLayout settingLayout;
    @BindView(R.id.profileLayout)
    LinearLayout profileLayout;
    @BindView(R.id.editProfileLayout)
    LinearLayout editProfileLayout;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashbaord);
        ButterKnife.bind(UserDashboard.this);

        dialog = M.showDialog(UserDashboard.this, "", false);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbarTitle.setText(getString(R.string.Stadiums));
        swapContentFragment(UserMapFragment.newInstance(), true, R.id.frameContainer);
        setListeners();
        toolbar.setNavigationOnClickListener(v -> {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });


        setUpLayout();
        setListeners();
        if (!M.fetchUserTrivialInfo(UserDashboard.this, "id").equalsIgnoreCase("")) {
            fetchStadiumInfo();
        }
    }


    private void setUpLayout() {
        Log.e(">>id", "setUpLayout: " + M.fetchUserTrivialInfo(UserDashboard.this, "id"));
        Log.e(">>id", "setUpLayout: " + M.fetchUserTrivialInfo(UserDashboard.this, "profilepic"));
        Picasso.get().load(M.fetchUserTrivialInfo(UserDashboard.this, "profilepic")).error(R.drawable.ic_user).into(ivUserImage);
        etEmail.setText(M.actAccordingly(UserDashboard.this, "email"));
        etName.setText(M.actAccordingly(UserDashboard.this, "fullname"));
        etPhone.setText(M.actAccordingly(UserDashboard.this, "phone"));
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
        signOutLayout.setOnClickListener(view -> {
            dialogConfirmation = DialogMethodCaller.openDialogConfirmation(UserDashboard.this, R.layout.dialog_confirmation, false);
            dialogConfirmation.getDialog_error().show();
            dialogConfirmation.getDialogConfirmationMessage().setText(getString(R.string.doYouWantToSignOut));
            dialogConfirmation.getDialogConfirmationTitle().setText(getString(R.string.signout));
            dialogConfirmation.getCloseButton().setOnClickListener(view12 -> dialogConfirmation.getDialog_error().dismiss());
            dialogConfirmation.getOkButton().setOnClickListener(view1 -> {
                dialogConfirmation.getDialog_error().dismiss();
                logoutApi();
            });
        });
        profileLayout.setOnClickListener(view -> startActivity(new Intent(UserDashboard.this, UserProfileFragment.class)));
        settingLayout.setOnClickListener(view -> {
//            Uri uri = Uri.parse("https://test-payment.momo.vn/gw_payment/payment/qr?partnerCode=MOMOIQA420180417&accessKey=SvDmj2cOTYZmQQ3H&requestId=1568809378&amount=1000&orderId=1568809378&signature=56c09dc31306ca6209314ee97225b30840c25b3fda1b73cc019a27410b1c4dbe&requestType=captureMoMoWallet"); // missing 'http://' will cause crashed
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);

            startActivity(new Intent(UserDashboard.this, SettingActivity.class));
        });
        editProfileLayout.setOnClickListener(view -> {
//            Uri uri = Uri.parse("momo://?action=payWithAppToken&amount=1000&requestType=payment&partnerCode=MOMOIQA420180417&orderId=1568809378&extraData=nitin01@mailinator.com&fee=0&orderLabel=M%C3%A3+%C4%91%C6%A1n+h%C3%A0ng&packageId=&description=PaymentWithMoMo&extras=&language=vi&merchantnamelabel=Nh%C3%A0+cung+c%E1%BA%A5p&gatewayMerchantCode=MOMOIQA420180417&createdAt=1568809378064&merchantcode=MOMOIQA420180417&requestId=1568809378&deeplinkCallback=https%3A%2F%2Ftest-payment.momo.vn%2Fgw_payment%2Fm2%3Fid%3DX1GKME&urlSubmitToken=https%3A%2F%2Fmomo.vn&appScheme=&hmac=6712b615333fc76802e049821913c937369b989fc33b4edfdabdc7c9fdfb85f2&merchantname=MoMoTest&callbackUrl=https%3A%2F%2Ftest-payment.momo.vn%2Fgw_payment%2Fm2%3Fid%3DX1GKME"); // missing 'http://' will cause crashed
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);
            startActivity(new Intent(UserDashboard.this, EditProfileActivity.class));
        });
        etUserBookingLayout.setOnClickListener(view -> {
            startActivity(new Intent(UserDashboard.this, UserBookingHistoryFragment.class));
        });

    }

    Handler handler;
    Runnable runnable;

    private void logoutApi() {
//        Uri uri = Uri.parse("http://momo//?type=webinapp&action=payment&requestId=1568809378&billId=1568809378&partnerCode=MOMOIQA420180417&partnerName=MoMoTest&amount=1000&description=PaymentWithMoMo&notifyUrl=http://slikosoccer.com&returnUrl=http://slikosoccer.com&code=MoMo1&extraData=eyJzaWduYXR1cmUiOiI2MmU5ZTA1YWFhMjZjOTAwYWZiZmFhNDUzMTFhZGJiNWM5OGEyODNhZDZmNGJjZWZlMTE0M2ViYmE4ZWE2NDMxIn0=&signature=62e9e05aaa26c900afbfaa45311adbb5c98a283ad6f4bcefe1143ebba8ea6431"); // missing 'http://' will cause crashed
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);

        dialog.show();
        handler = new Handler();
        runnable = () -> {
            dialog.cancel();
            Prefs.clearUserData(UserDashboard.this);
            startActivity(new Intent(UserDashboard.this, LoginActivity.class));
            finish();
        };
        handler.postDelayed(runnable, 500);
    }

    DialogConfirmation dialogConfirmation;

    String stadiumId = "";

    private void fetchStadiumInfo() {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_stadium_detail(M.fetchUserTrivialInfo(UserDashboard.this, "id"));
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
                            Prefs.saveStadiumId(stadiumId, UserDashboard.this);
                            EventBus.getDefault().postSticky(new StadiumExistEventOrNot(true, jsonObject.toString()));
                        } else {
                            EventBus.getDefault().postSticky(new StadiumExistEventOrNot(false, message));
                        }
                    } else {
                        Toast.makeText(UserDashboard.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(UserDashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(UserDashboard.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
