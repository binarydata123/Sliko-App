package app.sliko.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularTextView;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.AfterConfirmPaymentModels;
import app.sliko.utills.M;
import app.sliko.utills.Prefs;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookPitchPaymentFragment extends BottomSheetDialogFragment {
    private View view;
    @BindView(R.id.payNowButton)
    SsRegularButton payNowButton;
    @BindView(R.id.stadiumName)
    SsRegularTextView stadiumName;
    @BindView(R.id.stadiumAddress)
    SsRegularTextView stadiumAddress;
    @BindView(R.id.pitchName)
    SsRegularTextView pitchName;
    @BindView(R.id.pitchCost)
    SsRegularTextView pitchCost;
    @BindView(R.id.pitchTimeSlot)
    SsRegularTextView pitchTimeSlot;
    @BindView(R.id.pitchbookingDate)
    SsRegularTextView pitchbookingDate;
    @BindView(R.id.amountTobePaid)
    SsMediumTextView amountTobePaid;
    @BindView(R.id.closeButton)
    SsRegularButton closeButton;
    private Context context;
    private HashMap<String, String> bookPitchData;
    @BindView(R.id.radioG)
    RadioGroup radioGroup;
    @BindView(R.id.radio_momo)
    RadioButton radio_momo;
    @BindView(R.id.radio_cash)
    RadioButton radio_cash;
    @BindView(R.id.radio_zalo)
    RadioButton radio_zalo;

    public BookPitchPaymentFragment(Context context, HashMap<String, String> bookPitchData) {
        this.bookPitchData = bookPitchData;
        this.context = context;
    }

    String payment_type = "offline", pitch_id, stadium_id;
    private String pitchIdReceivedForBooking;
    private String stadiumIdReceivedForBooking;
    private String userIdToBeSent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pay_for_pitch, container, false);
        ButterKnife.bind(BookPitchPaymentFragment.this, view);
        if (context != null) {
            pitch_id = bookPitchData.get("p_id");
            stadium_id = bookPitchData.get("s_id");
            stadiumName.setText(bookPitchData.get("stadiumName"));
            stadiumAddress.setText(bookPitchData.get("stadium_address"));
            pitchName.setText(bookPitchData.get("pitchName"));
            Log.e("time slot", "==" + bookPitchData.get("time"));
            String first = bookPitchData.get("time");
            String[] separated = first.split("-");
            String s1 = separated[0].trim();
            String s2 = separated[1];


            pitchTimeSlot.setText(context.getResources().getString(R.string.bookingTime) + "" + s1);
            pitchCost.setText(context.getResources().getString(R.string.price) + "" + bookPitchData.get("cost"));
            amountTobePaid.setText(context.getString(R.string.amountToBePaid) + " " + context.getResources().getString(R.string.price) + "" + bookPitchData.get("cost"));
            pitchbookingDate.setText(context.getResources().getString(R.string.bookingDate) + "" + bookPitchData.get("bookingDate"));
            pitchIdReceivedForBooking = bookPitchData.get("pitchIdReceivedForBooking");
            userIdToBeSent = bookPitchData.get("userIdToBeSent");
            stadiumIdReceivedForBooking = bookPitchData.get("stadiumIdReceivedForBooking");
            Log.e(">>bookingReceivedParams", "onEvent: " + pitchIdReceivedForBooking + "\n" +
                    stadiumIdReceivedForBooking + "\n" + userIdToBeSent);
        }
        radioGroupListeber();
        return view;
    }

    public void radioGroupListeber() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_cash:
                    payment_type = "offline";
                    break;
                case R.id.radio_momo:
                    payment_type = "momo";

                    break;
                case R.id.radio_zalo:
                    payment_type = "zalo";

                    break;
            }
        });
    }

    private Dialog dialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = M.showDialog(context, "loading", false);
        payNowButton.setOnClickListener(view1 -> {
            //  dismissBottomDialog();
            //  startActivity(new Intent(context, AfterPayConfirmActivity.class));

//            if (payment_type.equalsIgnoreCase("")) {
//                Toast.makeText(context, "Please select payment method", Toast.LENGTH_SHORT).show();
//            } else {
            createBookingApi();

            //  }
        });


        closeButton.setOnClickListener(view12 -> dismissBottomDialog());
    }

    private void dismissBottomDialog() {
        Fragment prev = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentByTag("pitchBookingDialogFragment");
        if (prev != null) {
            BottomSheetDialogFragment df = (BottomSheetDialogFragment) prev;
            df.dismiss();
        }
    }

    AfterConfirmPaymentModels afterConfirmPaymentModels;


    private void createBookingApi() {
        dialog.show();
        Log.e(">>payment", "getAllBookings: " + payment_type);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_createBooking(M.fetchUserTrivialInfo(context, "id"), payment_type,
                bookPitchData.get("cost"), bookPitchData.get("bookingDate"), bookPitchData.get("time"), stadium_id, pitch_id);
        Log.e(">>stadiumId", "getAllBookings: " + Prefs.getStadiumId(context));
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
                        Log.e(">>payment response", "onResponse: " + sResponse);
                        if (status.equalsIgnoreCase("true")) {
                            dismissBottomDialog();
                            JSONObject jsondata = jsonObject.getJSONObject("data");

                            //  EventBus.getDefault().postSticky(new SuccessFullPaymentEvent(true));

                            afterConfirmPaymentModels = DialogMethodCaller.afterConfirmPayment(context, R.layout.activity_confirm_payment, false);
                            afterConfirmPaymentModels.getDialog_error().show();
                            afterConfirmPaymentModels.getPitchName().setText(message);
                            afterConfirmPaymentModels.getPitchbookingDate().setText(jsondata.getString("booking_date"));
                            afterConfirmPaymentModels.getPitchTimeSlot().setText(jsondata.getString("time"));
                            afterConfirmPaymentModels.getPitchCost().setText(jsondata.getString("cost"));
                            afterConfirmPaymentModels.getCloseButton().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    afterConfirmPaymentModels.getDialog_error().dismiss();
                                }
                            });


                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(">>logError", "onResponse: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}