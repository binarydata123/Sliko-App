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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.dialogs.DialogMethodCaller;
import app.sliko.dialogs.models.DialogConfirmation;
import app.sliko.owner.activity.AddPitchActivity;
import app.sliko.owner.activity.OwnerPitchBookingActivity;
import app.sliko.owner.activity.PitchDetailActivity;
import app.sliko.owner.model.PitchModel;
import app.sliko.utills.M;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import hyogeun.github.com.colorratingbarlib.ColorRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
        myViewHolder.pitchName.setText(context.getString(R.string.pitchName) + ": " + pitchModelArrayList.get(i).getPitch_name());
        myViewHolder.pitchPrice.setText(context.getString(R.string.price) + ": " + pitchModelArrayList.get(i).getPrice());
        myViewHolder.totalBookingOrders.setText(context.getString(R.string.totalBooking) + (Integer.parseInt(pitchModelArrayList.get(i).getProcess_booking()) + Integer.parseInt(pitchModelArrayList.get(i).getComplete_booking())) + "");

        Picasso.get().load(pitchModelArrayList.get(i).getPitch_gallery().get(0)).into(myViewHolder.pitchImage);
        myViewHolder.pitchRating.setRating(M.actAccordinglyWithJson(pitchModelArrayList.get(i).getPitch_review_avg()));
        myViewHolder.editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) context).startActivity(new Intent(context, AddPitchActivity.class)
                        .putExtra("pitch_id", pitchModelArrayList.get(i).getId())
                        .putExtra("stadium_id", pitchModelArrayList.get(i).getStadium_id()));
            }
        });

        myViewHolder.viewBookingDetailsButton.setOnClickListener(view ->
                ((Activity) context).startActivity(new Intent(context, OwnerPitchBookingActivity.class)
                        .putExtra("pitch_id", pitchModelArrayList.get(i).getId())
                        .putExtra("stadium_id", pitchModelArrayList.get(i).getStadium_id()))
        );
        myViewHolder.viewDetailsButton.setOnClickListener(view ->
                ((Activity) context)
                        .startActivity(new Intent(context, PitchDetailActivity.class)
                                .putExtra("type","owner")
                                .putExtra("pitch_id", pitchModelArrayList.get(i).getId())
                                .putExtra("stadium_id", pitchModelArrayList.get(i).getStadium_id())));
        myViewHolder.btn_remove.setOnClickListener(view -> {
            dialogConfirmation = DialogMethodCaller.openDialogConfirmation(context, R.layout.dialog_confirmation, false);
            dialogConfirmation.getDialog_error().show();
            dialogConfirmation.getDialogConfirmationTitle().setText(context.getString(R.string.deletePitch));
            dialogConfirmation.getDialogConfirmationMessage().setText(context.getString(R.string.sureDeleteThisPitch));
            dialogConfirmation.getOkButton().setText(context.getString(R.string.yes));
            dialogConfirmation.getOkButton().setOnClickListener(view12 -> {
                dialogConfirmation.getDialog_error().cancel();
                ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
                Call<ResponseBody> call = service.ep_deletePitch(pitchModelArrayList.get(i).getId(), pitchModelArrayList.get(i).getStadium_id());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.isSuccessful()) {
                                String sResponse = response.body().string();
                                JSONObject jsonObject = new JSONObject(sResponse);
                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");
                                if (status.equalsIgnoreCase("true")) {
                                    pitchModelArrayList.remove(i);
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            });
            dialogConfirmation.getCloseButton().setOnClickListener(view1 -> dialogConfirmation.getDialog_error().cancel());

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
