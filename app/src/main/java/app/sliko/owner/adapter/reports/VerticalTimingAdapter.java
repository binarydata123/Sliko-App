package app.sliko.owner.adapter.reports;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import app.sliko.R;
import app.sliko.booking.model.UserBookingModel;

public class VerticalTimingAdapter extends RecyclerView.Adapter<VerticalTimingAdapter.MyViewHolder> {

    private Context context;
    private String type;
    HashMap<Integer, ArrayList<UserBookingModel>> timingArrayList;
    HorizontalTimingForVerticallyAdapter horizontalTimingForVerticallyAdapter;

    public VerticalTimingAdapter(Context context, HashMap<Integer, ArrayList<UserBookingModel>> timingArrayList, String type) {
        this.context = context;
        this.timingArrayList = timingArrayList;
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    View itemView;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_horizontal_timing
                , viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
    //    Log.e("size vertical time","=="+timingArrayList.size());
        if (timingArrayList.get(position).size() > 0) {
            myViewHolder.noBookingLayout.setVisibility(View.GONE);
            horizontalTimingForVerticallyAdapter = new HorizontalTimingForVerticallyAdapter(context, timingArrayList.get(position), position, type);
            myViewHolder.insideTimingRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            myViewHolder.insideTimingRecyclerView.setAdapter(horizontalTimingForVerticallyAdapter);
            horizontalTimingForVerticallyAdapter.notifyDataSetChanged();
        } else {
            myViewHolder.noBookingLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return timingArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView insideTimingRecyclerView;
        LinearLayout noBookingLayout;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            insideTimingRecyclerView = itemView.findViewById(R.id.insideTimingRecyclerView);
            noBookingLayout = itemView.findViewById(R.id.noBookingLayout);
        }
    }
}
