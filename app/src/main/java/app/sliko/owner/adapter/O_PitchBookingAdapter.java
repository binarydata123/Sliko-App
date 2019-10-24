package app.sliko.owner.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularButton;
import app.sliko.UI.SsRegularTextView;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.DialogGiveFeedback;
import app.sliko.events.SuccessFullReviewEvent;
import app.sliko.owner.model.BookingModel;
import app.sliko.utills.M;
import app.sliko.utills.Prefs;
import app.sliko.web.Api;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import hyogeun.github.com.colorratingbarlib.ColorRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class O_PitchBookingAdapter extends RecyclerView.Adapter<O_PitchBookingAdapter.MyViewHolder> {
    private String type;
    private Context context;
    private ArrayList<BookingModel> pitchModelArrayList;
    Dialog dialog;

    public O_PitchBookingAdapter(Context context, ArrayList<BookingModel> pitchModelArrayList, String type) {
        this.context = context;
        this.pitchModelArrayList = pitchModelArrayList;
        this.type = type;
        this.dialog = M.showDialog(context, "loading", false);
    }

    @NonNull
    @Override
    public O_PitchBookingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_booking, viewGroup, false);
        return new O_PitchBookingAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final O_PitchBookingAdapter.MyViewHolder myViewHolder, final int i) {
        myViewHolder.PB_name.setText(context.getString(R.string.pitchName) + ": " + pitchModelArrayList.get(i).getPitch_name());
        myViewHolder.PB_cost.setText(context.getString(R.string.paid) + ": " + pitchModelArrayList.get(i).getPrice());
        myViewHolder.PB_userName.setText(pitchModelArrayList.get(i).getFullname());
        myViewHolder.PB_phone.setText(M.actAccordinglyWithJson(context, pitchModelArrayList.get(i).getPhone()));
        myViewHolder.PB_date.setText(context.getString(R.string.date) + pitchModelArrayList.get(i).getBooking_date());
        myViewHolder.PB_time.setText(context.getString(R.string.time) + "(" + pitchModelArrayList.get(i).getTime() + ") ");
        myViewHolder.PB_pitchReview.setRating(M.actAccordinglyWithJson(pitchModelArrayList.get(i).getPitch_review_avg()));
        Picasso.get().load(Api.DUMMY_PROFILE).into(myViewHolder.pitchImage);
        if (!type.equalsIgnoreCase("owner")) {
            myViewHolder.userDetailsLayout.setVisibility(View.GONE);
        }
//        if (pitchModelArrayList.get(i).getPitch_review_avg().equalsIgnoreCase("null") ||
//                pitchModelArrayList.get(i).getPitch_review_avg().equalsIgnoreCase("")) {
//            myViewHolder.pitchReviewCount.setText(context.getString(R.string.noReviews));
//            myViewHolder.PB_pitchReview.setRating(0);
//        } else {
//            myViewHolder.pitchReviewCount.setText(pitchModelArrayList.get(i).getPitch_review_avg());
//            myViewHolder.PB_pitchReview.setRating(Float.parseFloat(pitchModelArrayList.get(i).getPitch_review_avg()));
//        }
        if (pitchModelArrayList.get(i).getBooking_status().equalsIgnoreCase("0")) {
            myViewHolder.statusOfBooking.setBackgroundColor(context.getResources().getColor(R.color.toolbarColor));
            myViewHolder.statusOfBookingText.setText("Processing");
            myViewHolder.feedbackLayout.setVisibility(View.GONE);
            myViewHolder.giveFeedbackButton.setVisibility(View.GONE);

        } else if (pitchModelArrayList.get(i).getBooking_status().equalsIgnoreCase("1")) {
            myViewHolder.statusOfBooking.setBackgroundColor(context.getResources().getColor(R.color.lightGreen));
            myViewHolder.statusOfBookingText.setText("Complete");
            myViewHolder.feedbackLayout.setVisibility(View.VISIBLE);
            myViewHolder.giveFeedbackButton.setVisibility(View.VISIBLE);
            if (!pitchModelArrayList.get(i).getReview().equalsIgnoreCase("null") ||
                    pitchModelArrayList.get(i).getReview() != null ||
                    !pitchModelArrayList.get(i).getReview().equalsIgnoreCase("")) {
                myViewHolder.PB_pitchReview.setRating(M.actAccordinglyWithJson(pitchModelArrayList.get(i).getPitch_review_avg()));
                myViewHolder.pitchReviewCount.setText("(" + M.actAccordinglyWithJson(pitchModelArrayList.get(i).getPitch_review_avg()) + ")");
            } else {
                myViewHolder.PB_pitchReview.setRating(0);
                myViewHolder.pitchReviewCount.setText("(" + 0 + ")");
            }
            myViewHolder.giveFeedbackButton.setOnClickListener(view -> giveFeedback(pitchModelArrayList.get(i).getId(), pitchModelArrayList.get(i).getStadium_id(), pitchModelArrayList.get(i).getPitch_id()));
        }
    }

    private DialogGiveFeedback dialogGiveFeedback;

    private void giveFeedback(String pitchBookingId, String stadiumId, String pitchId) {
        dialogGiveFeedback = DialogMethodCaller.openDialogGiveFeedback(context, R.layout.dialog_give_feedback);
        dialogGiveFeedback.getAlertDialog().show();
        dialogGiveFeedback.getF_CloseButton().setOnClickListener(v -> dialogGiveFeedback.getAlertDialog().cancel());
        dialogGiveFeedback.getF_submitButton().setOnClickListener(v -> {
            if (dialogGiveFeedback.getF_userRating().getRating() == 0.0f) {
                Toast.makeText(context, "Please put a rating", Toast.LENGTH_SHORT).show();
            } else {
                dialogGiveFeedback.getAlertDialog().cancel();
                dialog.show();

                ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
                Call<ResponseBody> call = service.giveReview(M.fetchUserTrivialInfo(context, "id"),
                        pitchBookingId, dialogGiveFeedback.getF_description().getText().toString(), dialogGiveFeedback.getF_userRating().getRating() + "", stadiumId, pitchId);
                Log.e(">>stadiumId", "getBookingForUser: " + Prefs.getStadiumId(context));
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
                                if (status.equalsIgnoreCase("true")) {
                                    EventBus.getDefault().postSticky(new SuccessFullReviewEvent(true));
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
        });
    }

    @Override
    public int getItemCount() {
        return pitchModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pitchImage)
        ImageView pitchImage;
        @BindView(R.id.PB_cost)
        SsMediumTextView PB_cost;
        @BindView(R.id.PB_date)
        SsRegularTextView PB_date;
        @BindView(R.id.PB_name)
        SsRegularTextView PB_name;
        @BindView(R.id.PB_phone)
        SsRegularTextView PB_phone;
        @BindView(R.id.PB_time)
        SsRegularTextView PB_time;
        @BindView(R.id.PB_userName)
        SsRegularTextView PB_userName;
        @BindView(R.id.PB_pitchReview)
        ColorRatingBar PB_pitchReview;
        @BindView(R.id.pitchReviewCount)
        SsRegularTextView pitchReviewCount;
        @BindView(R.id.userDetailsLayout)
        LinearLayout userDetailsLayout;
        @BindView(R.id.statusOfBookingText)
        SsRegularTextView statusOfBookingText;
        @BindView(R.id.statusOfBooking)
        LinearLayout statusOfBooking;
        @BindView(R.id.feedbackLayout)
        LinearLayout feedbackLayout;
        @BindView(R.id.giveFeedbackButton)
        SsRegularButton giveFeedbackButton;


        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(O_PitchBookingAdapter.MyViewHolder.this, itemView);
        }
    }


}
