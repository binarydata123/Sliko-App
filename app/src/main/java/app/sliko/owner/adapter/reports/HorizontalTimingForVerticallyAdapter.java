package app.sliko.owner.adapter.reports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.booking.model.UserBookingModel;


public class HorizontalTimingForVerticallyAdapter extends RecyclerView.Adapter<HorizontalTimingForVerticallyAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<UserBookingModel> verticalPitchArrayList;

    public HorizontalTimingForVerticallyAdapter(Context context, ArrayList<UserBookingModel> verticalPitchArrayList) {
        this.context = context;
        this.verticalPitchArrayList = verticalPitchArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_horizontal_timing_text_view, viewGroup, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.timingView.setText(verticalPitchArrayList.get(position).getBooked_status());
//        if (position == 2) {
//            myViewHolder.timingView.setBackgroundColor(context.getResources().getColor(R.color.greyColor));
//            myViewHolder.timingView.setText("Vacant");
//            myViewHolder.timingView.setTextColor(context.getResources().getColor(R.color.black));
//        } else if (position == 4) {
//            myViewHolder.timingView.setBackgroundColor(context.getResources().getColor(R.color.lightGreen));
//            myViewHolder.timingView.setTextColor(context.getResources().getColor(R.color.white));
//        }
    }

    @Override
    public int getItemCount() {
        return verticalPitchArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView timingView;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            timingView = itemView.findViewById(R.id.timingView);
        }
    }
}
