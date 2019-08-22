package app.sliko.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.fragment.MyDialogFragment;
import app.sliko.models.PitchModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PitchAdapterUser extends RecyclerView.Adapter<PitchAdapterUser.MyViewHolder> {

    private Context context;
    private ArrayList<PitchModel> notificationModelArrayList;

    public PitchAdapterUser(Context context, ArrayList<PitchModel> notificationModelArrayList) {
        this.context = context;
        this.notificationModelArrayList = notificationModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_pitch, viewGroup, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.bookStadiumButton.setOnClickListener(view -> {
//            View pay = LayoutInflater.from(context).inflate(R.layout.fragment_pay_for_pitch, null);
//            BottomSheetDialog dialog = new BottomSheetDialog(context);
//            dialog.setContentView(pay);
//            dialog.show();

            MyDialogFragment bottomSheetFragment = new MyDialogFragment("valuye Hello");
            bottomSheetFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    @Override
    public int getItemCount() {
        return notificationModelArrayList.size() > 0 ? notificationModelArrayList.size() : 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.stadiumName)
        TextView stadiumName;
        @BindView(R.id.stadiumBookingPrice)
        TextView stadiumBookingPrice;
        @BindView(R.id.bookStadiumButton)
        Button bookStadiumButton;


        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(MyViewHolder.this, itemView);
        }
    }
}
