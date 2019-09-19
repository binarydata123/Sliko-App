package app.sliko.owner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.UI.SsRegularTextView;
import app.sliko.owner.model.ReviewModel;
import app.sliko.web.Api;
import butterknife.BindView;
import butterknife.ButterKnife;
import hyogeun.github.com.colorratingbarlib.ColorRatingBar;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<ReviewModel> pitchReviewList;

    public ReviewsAdapter(Context context, ArrayList<ReviewModel> pitchReviewList) {
        this.context = context;
        this.pitchReviewList = pitchReviewList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_review, viewGroup, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        //Picasso.get().load(Api.DUMMY_PROFILE).into(myViewHolder.reviewPitchImage);
        Picasso.get().load(pitchReviewList.get(i).getPitch_image()).into(myViewHolder.reviewPitchImage);
        myViewHolder.reviewPitchName.setText(pitchReviewList.get(i).getPitch_name());
        myViewHolder.reviewPitchUserName.setText(pitchReviewList.get(i).getFullname());
        myViewHolder.reviewDateGiven.setText(pitchReviewList.get(i).getCreated_at());
        myViewHolder.reviewMessage.setText(pitchReviewList.get(i).getMessage());
        myViewHolder.pitchRating.setRating(Float.parseFloat(pitchReviewList.get(i).getRating()));
    }


    @Override
    public int getItemCount() {
        return pitchReviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.reviewPitchImage)
        ImageView reviewPitchImage;
        @BindView(R.id.reviewPitchName)
        SsRegularTextView reviewPitchName;
        @BindView(R.id.reviewDateGiven)
        SsRegularTextView reviewDateGiven;
        @BindView(R.id.reviewPitchUserName)
        SsRegularTextView reviewPitchUserName;
        @BindView(R.id.reviewMessage)
        SsRegularTextView reviewMessage;
        @BindView(R.id.pitchRating)
        ColorRatingBar pitchRating;


        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(MyViewHolder.this, itemView);
        }
    }


}
