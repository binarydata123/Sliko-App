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
import app.sliko.UI.SsRegularTextView;
import app.sliko.booking.VerticalPitchModel;

public class VerticalPitchAdapter extends RecyclerView.Adapter<VerticalPitchAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<VerticalPitchModel> verticalPitchArrayList;

    public VerticalPitchAdapter(Context context, ArrayList<VerticalPitchModel> verticalPitchArrayList) {
        this.context = context;
        this.verticalPitchArrayList = verticalPitchArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_vertical_pitch_name, viewGroup, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.pitchName.setText(verticalPitchArrayList.get(position).getPitchName());
    }

    @Override
    public int getItemCount() {
        return verticalPitchArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        SsRegularTextView pitchName;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pitchName = itemView.findViewById(R.id.pitchName);
        }
    }
}
