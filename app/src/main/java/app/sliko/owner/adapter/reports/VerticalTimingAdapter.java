package app.sliko.owner.adapter.reports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.HashMap;

import app.sliko.R;

public class VerticalTimingAdapter extends RecyclerView.Adapter<VerticalTimingAdapter.MyViewHolder> {

    private Context context;
    HashMap<Integer, ArrayList<String>> timingArrayList;
    HorizontalTimingForVerticallyAdapter horizontalTimingForVerticallyAdapter;

    public VerticalTimingAdapter(Context context, HashMap<Integer, ArrayList<String>> timingArrayList) {
        this.context = context;
        this.timingArrayList = timingArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_horizontal_timing
                , viewGroup, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        horizontalTimingForVerticallyAdapter = new HorizontalTimingForVerticallyAdapter(context, timingArrayList.get(position));
        myViewHolder.insideTimingRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        myViewHolder.insideTimingRecyclerView.setAdapter(horizontalTimingForVerticallyAdapter);
        horizontalTimingForVerticallyAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return timingArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView insideTimingRecyclerView;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            insideTimingRecyclerView = itemView.findViewById(R.id.insideTimingRecyclerView);
        }
    }
}
