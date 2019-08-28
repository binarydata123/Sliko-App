package app.sliko.owner.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.DialogConfirmation;
import app.sliko.owner.activity.EditPitchActivity;
import app.sliko.owner.activity.OwnerPitchBookingActivity;
import app.sliko.owner.activity.PitchDetailActivity;
import app.sliko.owner.events.SuccessFullyStadiumCreated;
import app.sliko.owner.model.PitchModel;
import app.sliko.utills.M;
import app.sliko.web.Api;
import butterknife.BindView;
import butterknife.ButterKnife;
import hyogeun.github.com.colorratingbarlib.ColorRatingBar;


public class PitchAdapterOwner extends RecyclerView.Adapter<PitchAdapterOwner.MyViewHolder> {
    private Context context;
    private ArrayList<PitchModel> pitchModelArrayList;

    public PitchAdapterOwner(Context context, ArrayList<PitchModel> pitchModelArrayList) {
        this.context = context;
        this.pitchModelArrayList = pitchModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_pitch_owner, viewGroup, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
//        myViewHolder.pitchName.setText(context.getString(R.string.pitchName) + ": " + pitchModelArrayList.get(i).getPitch_name());
//        myViewHolder.pitchPrice.setText(context.getString(R.string.price) + ": " + pitchModelArrayList.get(i).getPrice());
//        myViewHolder.totalBookingOrders.setText(Integer.parseInt(pitchModelArrayList.get(i).getProcess_booking()) + Integer.parseInt(pitchModelArrayList.get(i).getComplete_booking()));
//
        Picasso.get().load(Api.DUMMY_PROFILE).into(myViewHolder.pitchImage);
//        Picasso.get().load(pitchModelArrayList.get(i).getPitch_gallery().get(0)).into(myViewHolder.pitchImage);
//        myViewHolder.pitchRating.setRating(Float.parseFloat(pitchModelArrayList.get(i).getPitch_review_avg()));
        myViewHolder.editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) context).startActivity(new Intent(context, EditPitchActivity.class));
            }
        });

        Picasso.get().load(Api.DUMMY_PROFILE).into(myViewHolder.pitchImage);
        myViewHolder.viewBookingDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) context).startActivity(new Intent(context, OwnerPitchBookingActivity.class));
            }
        });
        myViewHolder.viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) context).startActivity(new Intent(context, PitchDetailActivity.class));
            }
        });
        myViewHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirmation = DialogMethodCaller.openDialogConfirmation(context, R.layout.dialog_confirmation, false);
                dialogConfirmation.getDialog_error().show();
                dialogConfirmation.getDialogConfirmationTitle().setText(context.getString(R.string.deleteStadium));
                dialogConfirmation.getDialogConfirmationMessage().setText(context.getString(R.string.sureDeleteThisStadium));
                dialogConfirmation.getOkButton().setOnClickListener(view12 -> {
                    dialogConfirmation.getDialog_error().cancel();
                    M.updateTrivialInfo(context, Api.IS_STADIUM, Api.STADIUM_REMOVE);
                    EventBus.getDefault().postSticky(new SuccessFullyStadiumCreated(true));
                });
                dialogConfirmation.getCloseButton().setOnClickListener(view1 -> dialogConfirmation.getDialog_error().cancel());

            }
        });
    }

    DialogConfirmation dialogConfirmation;

    @Override
    public int getItemCount() {
        return pitchModelArrayList.size() == 0 ? 4 : pitchModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.editLayout)
        TextView editLayout;
        @BindView(R.id.pitchImage)
        ImageView pitchImage;
        @BindView(R.id.btn_remove)
        TextView btn_remove;
        @BindView(R.id.viewBookingDetailsButton)
        LinearLayout viewBookingDetailsButton;
        @BindView(R.id.pitchName)
        TextView pitchName;
        @BindView(R.id.pitchPrice)
        TextView pitchPrice;
        @BindView(R.id.viewDetailsButton)
        FrameLayout viewDetailsButton;
        @BindView(R.id.totalBookingOrders)
        TextView totalBookingOrders;
        @BindView(R.id.pitchRating)
        ColorRatingBar pitchRating;


        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(MyViewHolder.this, itemView);
        }
    }


}
