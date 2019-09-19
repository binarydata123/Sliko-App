package app.sliko.location;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.UI.SsRegularTextView;
import app.sliko.user.activity.StadiumDetailActivity;
import app.sliko.models.HomeStadiumListModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchStadiumAdapter extends RecyclerView.Adapter<SearchStadiumAdapter.MyViewHolder> {


    private ArrayList<HomeStadiumListModel> addressList;
    Context context;

    public SearchStadiumAdapter(Context context, ArrayList<HomeStadiumListModel> addressList) {
        ;
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_searched_address, viewGroup, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Log.e(">>name", "onBindViewHolder: " + addressList.get(position).getStadium_name());
        myViewHolder.placeName.setText(addressList.get(position).getStadium_name());
        myViewHolder.address.setVisibility(View.VISIBLE);
        myViewHolder.address.setText(addressList.get(position).getAddress());
        myViewHolder.tap.setOnClickListener(view -> context.startActivity(new Intent(context, StadiumDetailActivity.class)
                .putExtra("stadium_id", addressList.get(position).getId())
                .putExtra("user_id", addressList.get(position).getUser_id())
                .putExtra("stadium_name", addressList.get(position).getStadium_name())
                .putExtra("lowestPrice", addressList.get(position).getPrice())));
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.placeName)
        SsRegularTextView placeName;
        @BindView(R.id.address)
        SsRegularTextView address;
        @BindView(R.id.tap)
        LinearLayout tap;


        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(MyViewHolder.this, itemView);
        }
    }
}
