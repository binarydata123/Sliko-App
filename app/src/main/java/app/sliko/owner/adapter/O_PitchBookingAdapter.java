package app.sliko.owner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.owner.model.BookingModel;
import app.sliko.utills.M;
import app.sliko.web.Api;
import butterknife.BindView;
import butterknife.ButterKnife;
import hyogeun.github.com.colorratingbarlib.ColorRatingBar;


public class O_PitchBookingAdapter extends RecyclerView.Adapter<O_PitchBookingAdapter.MyViewHolder> {
    private String type;
    private Context context;
    private ArrayList<BookingModel> pitchModelArrayList;

    public O_PitchBookingAdapter(Context context, ArrayList<BookingModel> pitchModelArrayList, String type) {
        this.context = context;
        this.pitchModelArrayList = pitchModelArrayList;
        this.type = type;
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
        myViewHolder.PB_phone.setText(pitchModelArrayList.get(i).getPhone());
        myViewHolder.PB_date.setText(M.returnDateOnly(pitchModelArrayList.get(i).getBooking_date()));
        myViewHolder.PB_time.setText(" Time: (" + pitchModelArrayList.get(i).getTime() + ") ");
        myViewHolder.PB_pitchReview.setRating(M.actAccordinglyWithJson(pitchModelArrayList.get(i).getPitch_review_avg()));
        Picasso.get().load(Api.DUMMY_PROFILE).into(myViewHolder.pitchImage);
        if (!type.equalsIgnoreCase("owner")) {
            myViewHolder.userDetailsLayout.setVisibility(View.GONE);
        }
        if (pitchModelArrayList.get(i).getPitch_review_avg().equalsIgnoreCase("null") ||
                pitchModelArrayList.get(i).getPitch_review_avg().equalsIgnoreCase("")) {
            myViewHolder.pitchReviewCount.setText(context.getString(R.string.noReviews));
            myViewHolder.PB_pitchReview.setRating(0);
        } else {
            myViewHolder.pitchReviewCount.setText(pitchModelArrayList.get(i).getPitch_review_avg());
            myViewHolder.PB_pitchReview.setRating(Float.parseFloat(pitchModelArrayList.get(i).getPitch_review_avg()));
        }
        if(pitchModelArrayList.get(i).getBooking_status().equalsIgnoreCase("0")){
            myViewHolder.statusOfBooking.setBackground(context.getResources().getDrawable(R.drawable.edit_bg_red));
            myViewHolder.statusOfBookingText.setText("Processing");
        }  else if(pitchModelArrayList.get(i).getBooking_status().equalsIgnoreCase("1")){
            myViewHolder.statusOfBooking.setBackground(context.getResources().getDrawable(R.drawable.edit_bg_green));
            myViewHolder.statusOfBookingText.setText("Complete");
        }
    }

    @Override
    public int getItemCount() {
        return pitchModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pitchImage)
        ImageView pitchImage;
        @BindView(R.id.PB_cost)
        TextView PB_cost;
        @BindView(R.id.PB_date)
        TextView PB_date;
        @BindView(R.id.PB_name)
        TextView PB_name;
        @BindView(R.id.PB_phone)
        TextView PB_phone;
        @BindView(R.id.PB_time)
        TextView PB_time;
        @BindView(R.id.PB_userName)
        TextView PB_userName;
        @BindView(R.id.PB_pitchReview)
        ColorRatingBar PB_pitchReview;
        @BindView(R.id.pitchReviewCount)
        TextView pitchReviewCount;
        @BindView(R.id.userDetailsLayout)
        LinearLayout userDetailsLayout;
        @BindView(R.id.statusOfBookingText)
        TextView statusOfBookingText;
        @BindView(R.id.statusOfBooking)
        LinearLayout statusOfBooking;


        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(O_PitchBookingAdapter.MyViewHolder.this, itemView);
        }
    }


}
