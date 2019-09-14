package app.sliko.owner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.owner.model.AvailabilityModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StadiumOpeningAdapter extends RecyclerView.Adapter<StadiumOpeningAdapter.MyViewHolder> {
    int PICKED = 1, NOT_PICKED = 0;
    private Context context;
    private String typeOfView;
    private ArrayList<AvailabilityModel> imagesModelArrayList;

    public StadiumOpeningAdapter(Context context, ArrayList<AvailabilityModel> imagesModelArrayList,String typeOfView) {
        this.context = context;
        this.imagesModelArrayList = imagesModelArrayList;
        this.typeOfView = typeOfView;
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
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_stadium_timing
                , viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        if(!typeOfView.equalsIgnoreCase("detailView")){
            myViewHolder.changeBackground.setOnClickListener(view -> {
                AvailabilityModel availabilityModel = imagesModelArrayList.get(i);
                availabilityModel.setPicked(!availabilityModel.isPicked());
                imagesModelArrayList.set(i, availabilityModel);
                StadiumOpeningAdapter.this.notifyDataSetChanged();
            });
        }
        Log.e(">>de", "onBindViewHolder: "+imagesModelArrayList.get(i).isPicked() );
        myViewHolder.timingText.setText(imagesModelArrayList.get(i).getTime());
        if (getItemViewType(i) == PICKED) {
            myViewHolder.changeBackground.setBackground(context.getDrawable(R.drawable.edit_bg_green));
            myViewHolder.timingText.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            myViewHolder.changeBackground.setBackground(context.getDrawable(R.drawable.edit_bg_green_strokes));
            myViewHolder.timingText.setTextColor(context.getResources().getColor(R.color.black));
        }
    }
    StringBuilder stringBuilder = null;

    public StringBuilder getSelectedArrayList() {
        stringBuilder = new StringBuilder();
        for (int k = 0; k < imagesModelArrayList.size(); k++) {
            if (imagesModelArrayList.get(k).isPicked()) {
                stringBuilder.append(imagesModelArrayList.get(k).getTime()).append(",");
            }
        }
        return stringBuilder;
    }

    @Override
    public int getItemCount() {
        return imagesModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.changeBackground)
        LinearLayout changeBackground;
        @BindView(R.id.timingText)
        TextView timingText;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(MyViewHolder.this, itemView);
        }
    }
}
