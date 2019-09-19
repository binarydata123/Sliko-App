package app.sliko.owner.adapter.reports;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.UI.SsRegularTextView;
import app.sliko.owner.model.AvailabilityModel;


public class HeaderTimingAdapter extends RecyclerView.Adapter<HeaderTimingAdapter.MyViewHolder> {
    int PICKED = 1, NOT_PICKED = 0;
    private Context context;
    private ArrayList<AvailabilityModel> imagesModelArrayList;

    public HeaderTimingAdapter(Context context, ArrayList<AvailabilityModel> imagesModelArrayList) {
        this.context = context;
        this.imagesModelArrayList = imagesModelArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        if (imagesModelArrayList.get(position).isPicked()) {
            return PICKED;
        } else {
            return NOT_PICKED;
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_header
                , viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        Log.e(">>time", "onBindViewHolder: " + imagesModelArrayList.get(i).getTime() );
        myViewHolder.timingText.setText(imagesModelArrayList.get(i).getTime());
    }

    @Override
    public int getItemCount() {
        return imagesModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout changeBackground;
        SsRegularTextView timingText;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            changeBackground = itemView.findViewById(R.id.changeBackground);
            timingText = itemView.findViewById(R.id.timingText);
        }
    }
}
