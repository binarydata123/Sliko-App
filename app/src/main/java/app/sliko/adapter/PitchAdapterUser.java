package app.sliko.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.activity.BookingActivity;
import app.sliko.owner.activity.PitchDetailActivity;
import app.sliko.utills.M;
import butterknife.BindView;
import butterknife.ButterKnife;
import hyogeun.github.com.colorratingbarlib.ColorRatingBar;

public class PitchAdapterUser extends RecyclerView.Adapter<PitchAdapterUser.MyViewHolder> {

    private Context context;
    private ArrayList<app.sliko.owner.model.PitchModel> notificationModelArrayList;

    public PitchAdapterUser(Context context, ArrayList<app.sliko.owner.model.PitchModel> notificationModelArrayList) {
        this.context = context;
        this.notificationModelArrayList = notificationModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_pitch, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    int height, width;

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.bookStadiumButton.setOnClickListener(view -> {
            ((Activity) context)
                    .startActivity(new Intent(context, BookingActivity.class)
                            .putExtra("pitch_id", notificationModelArrayList.get(position).getId())
                            .putExtra("user_id", notificationModelArrayList.get(position).getUser_id())
                            .putExtra("stadium_id", notificationModelArrayList.get(position).getStadium_id()));
        });
        myViewHolder.tap.setOnClickListener(view -> {
            ((Activity) context)
                    .startActivity(new Intent(context, PitchDetailActivity.class)
                            .putExtra("type","user")
                            .putExtra("pitch_id", notificationModelArrayList.get(position).getId())
                            .putExtra("user_id", notificationModelArrayList.get(position).getUser_id())
                            .putExtra("stadium_id", notificationModelArrayList.get(position).getStadium_id()));
        });
        myViewHolder.imageProgress.setVisibility(View.VISIBLE);
        myViewHolder.stadiumBookingPrice.setText(context.getString(R.string.price) + M.actAccordinglyWithJson(context, notificationModelArrayList.get(position).getPrice()));
        myViewHolder.stadiumName.setText(M.actAccordinglyWithJson(context, notificationModelArrayList.get(position).getPitch_name()));

        Picasso.get().load(notificationModelArrayList.get(position).getPitch_gallery().get(0))
                .fit().centerCrop().into(myViewHolder.pitchImage, new Callback() {
            @Override
            public void onSuccess() {
                myViewHolder.imageProgress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                myViewHolder.imageProgress.setVisibility(View.GONE);
            }
        });
        if (notificationModelArrayList.get(position).getPitch_review_avg().equalsIgnoreCase("null")) {
            myViewHolder.stadiumRatingCount.setText(context.getString(R.string.noReviews));
            myViewHolder.stadiumRating.setVisibility(View.GONE);
        } else {
            String count = notificationModelArrayList.get(position).getPitch_review_avg().equalsIgnoreCase("null") ?
                    "0"
                    : notificationModelArrayList.get(position).getPitch_review_avg();
            myViewHolder.stadiumRatingCount.setText(count + " " + context.getString(R.string.reviews));
        }
        myViewHolder.stadiumRating.setRating(notificationModelArrayList.get(position).getPitch_review_avg().equalsIgnoreCase("null") ?
                Float.parseFloat("0")
                : Float.parseFloat(notificationModelArrayList.get(position).getPitch_review_avg()));
    }

    @Override
    public int getItemCount() {
        return notificationModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.stadiumName)
        TextView stadiumName;
        @BindView(R.id.stadiumBookingPrice)
        TextView stadiumBookingPrice;
        @BindView(R.id.bookStadiumButton)
        Button bookStadiumButton;
        @BindView(R.id.pitchImage)
        ImageView pitchImage;
        @BindView(R.id.imageProgress)
        ProgressBar imageProgress;
        @BindView(R.id.stadiumRatingCount)
        TextView stadiumRatingCount;
        @BindView(R.id.stadiumRating)
        ColorRatingBar stadiumRating;
        @BindView(R.id.tap)
        LinearLayout tap;


        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(MyViewHolder.this, itemView);
        }
    }
}
