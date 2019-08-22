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
import app.sliko.owner.model.PitchModel;
import app.sliko.web.Api;
import butterknife.BindView;
import butterknife.ButterKnife;


public class O_PitchBookingAdapter extends RecyclerView.Adapter<O_PitchBookingAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<BookingModel> pitchModelArrayList;

    public O_PitchBookingAdapter(Context context, ArrayList<BookingModel> pitchModelArrayList) {
        this.context = context;
        this.pitchModelArrayList = pitchModelArrayList;
    }

    @NonNull
    @Override
    public O_PitchBookingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_booking, viewGroup, false);
        return new O_PitchBookingAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final O_PitchBookingAdapter.MyViewHolder myViewHolder, final int i) {
//        myViewHolder.pitchName.setText(context.getString(R.string.pitchName) + ": " + pitchModelArrayList.get(i).getName());
//        myViewHolder.pitchPrice.setText(context.getString(R.string.price) + ": " + pitchModelArrayList.get(i).getPrice());
//        myViewHolder.pitchPrice.setText(context.getString(R.string.price) + ": " + pitchModelArrayList.get(i).getPrice());
        Picasso.get().load(Api.DUMMY_PROFILE).into(myViewHolder.pitchImage);

    }


    @Override
    public int getItemCount() {
        return pitchModelArrayList.size() == 0 ? 5 : pitchModelArrayList.size();
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


        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(O_PitchBookingAdapter.MyViewHolder.this, itemView);
        }
    }


}
