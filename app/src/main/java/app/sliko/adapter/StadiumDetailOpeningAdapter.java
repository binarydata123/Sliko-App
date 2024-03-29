package app.sliko.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.UI.SsRegularTextView;
import app.sliko.owner.model.AvailabilityModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StadiumDetailOpeningAdapter extends RecyclerView.Adapter<StadiumDetailOpeningAdapter.MyViewHolder> {
    int PICKED = 1, NOT_PICKED = 0;
    private Context context;
    private String typeOfView;
    private ArrayList<AvailabilityModel> imagesModelArrayList;

    public StadiumDetailOpeningAdapter(Context context, ArrayList<AvailabilityModel> imagesModelArrayList, String typeOfView) {
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
        View itemView;
        if(!typeOfView.equalsIgnoreCase("detailView")){
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_stadium_opening
                    , viewGroup, false);
        }else{
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_stadium_opening_view
                    , viewGroup, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

Log.e("get position","===="+imagesModelArrayList.get(0).getTime()+"<><>"+imagesModelArrayList.get(imagesModelArrayList.size()-1).getTime());
String first = imagesModelArrayList.get(0).getTime();
        String[] separated = first.split(":");
        String s1=     separated[0].trim();
        String s2=    separated[1];
String last =imagesModelArrayList.get(imagesModelArrayList.size()-1).getTime();
        String[] separated2 = last.split(":");
        String s11=     separated2[0];
        String s12=    separated2[1].trim();

        myViewHolder.timingText.setText(s1+" : "+s12);

       // myViewHolder.timingText.setText(imagesModelArrayList.get(i).getTime());

        if(!typeOfView.equalsIgnoreCase("detailView")){
            myViewHolder.changeBackground.setOnClickListener(view -> {
                AvailabilityModel availabilityModel = imagesModelArrayList.get(i);
                availabilityModel.setPicked(!availabilityModel.isPicked());
                imagesModelArrayList.set(i, availabilityModel);
                StadiumDetailOpeningAdapter.this.notifyDataSetChanged();
            });
        }
    }

    public JSONArray getSelectedArrayList() {
        JSONArray jsonArray = new JSONArray();
        for (int k = 0; k < imagesModelArrayList.size(); k++) {
            if (imagesModelArrayList.get(k).isPicked()) {
                jsonArray.put(imagesModelArrayList.get(k).getTime());
            }
        }
        return jsonArray;
    }

    @Override
    public int getItemCount() {
        return imagesModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.changeBackground)
        LinearLayout changeBackground;
        @BindView(R.id.timingText)
        SsRegularTextView timingText;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(MyViewHolder.this, itemView);
        }
    }
}
