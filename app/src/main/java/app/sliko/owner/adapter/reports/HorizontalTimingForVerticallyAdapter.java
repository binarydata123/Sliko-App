package app.sliko.owner.adapter.reports;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.booking.model.UserBookingModel;
import app.sliko.events.PayingForPitchEvent;
import app.sliko.web.Api;


public class HorizontalTimingForVerticallyAdapter extends RecyclerView.Adapter<HorizontalTimingForVerticallyAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<UserBookingModel> verticalPitchArrayList;
    private int withThisGetPitchPosition;
    private String type;

    public HorizontalTimingForVerticallyAdapter(Context context, ArrayList<UserBookingModel> verticalPitchArrayList, int position, String type) {
        this.context = context;
        this.verticalPitchArrayList = verticalPitchArrayList;
        this.withThisGetPitchPosition = position;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_horizontal_timing_text_view, viewGroup, false);
        return new MyViewHolder(itemView);
    }


    /**
     * getId() >> changes to getShowSlots() will
     * return null
     **/
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.timingView.setText(verticalPitchArrayList.get(position).getBooked_status());

        Log.e(">>slots", "onBindViewHolder: " + verticalPitchArrayList.get(position).getShow_slot() );
        if (type.equalsIgnoreCase("userView")) {
            if(verticalPitchArrayList.get(position).getShow_slot().equalsIgnoreCase("0")){
                myViewHolder.timingView.setText("Can't book");
                myViewHolder.timingView.setTextColor(context.getResources().getColor(R.color.black));
                myViewHolder.timingView.setBackgroundColor(context.getResources().getColor(R.color.bit_grey));
                myViewHolder.timingView.setAlpha(.5f);

            }else{
                if (verticalPitchArrayList.get(position).getBooked_status().equalsIgnoreCase("") ||
                        verticalPitchArrayList.get(position).getBooked_status().equalsIgnoreCase(Api.VACCANT)) {
                    myViewHolder.timingView.setText("Vacant");
                    myViewHolder.timingView.setTextColor(context.getResources().getColor(R.color.black));
                    myViewHolder.timingView.setBackgroundColor(context.getResources().getColor(R.color.bit_dark_grey));
                } else {
                    myViewHolder.timingView.setText("Occupied");
                    myViewHolder.timingView.setTextColor(context.getResources().getColor(R.color.white));
                    myViewHolder.timingView.setBackgroundColor(context.getResources().getColor(R.color.toolbarColor));
                }
            }
        } else {
            if (verticalPitchArrayList.get(position).getBooked_status().equalsIgnoreCase(Api.BOOKED)) {
                myViewHolder.timingView.setText("Booked");
                myViewHolder.timingView.setTextColor(context.getResources().getColor(R.color.white));
                myViewHolder.timingView.setBackgroundColor(context.getResources().getColor(R.color.lightGreen));
            } else if (verticalPitchArrayList.get(position).getBooked_status().equalsIgnoreCase(Api.TIME_UP)) {
                myViewHolder.timingView.setText("Time UP");
                myViewHolder.timingView.setTextColor(context.getResources().getColor(R.color.white));
                myViewHolder.timingView.setBackgroundColor(context.getResources().getColor(R.color.toolbarColor));
            } else if (verticalPitchArrayList.get(position).getBooked_status().equalsIgnoreCase(Api.PLAYING)) {
                myViewHolder.timingView.setText("Playing");
                myViewHolder.timingView.setTextColor(context.getResources().getColor(R.color.white));
                myViewHolder.timingView.setBackgroundColor(context.getResources().getColor(R.color.lightGreen));
            } else if (verticalPitchArrayList.get(position).getBooked_status().equalsIgnoreCase(Api.MIN_TO_GO_15)) {
                myViewHolder.timingView.setText("15 min remaining");
                myViewHolder.timingView.setTextColor(context.getResources().getColor(R.color.white));
                myViewHolder.timingView.setBackgroundColor(context.getResources().getColor(R.color.orange));
            } else if (verticalPitchArrayList.get(position).getBooked_status().equalsIgnoreCase("") ||
                    verticalPitchArrayList.get(position).getBooked_status().equalsIgnoreCase(Api.VACCANT)) {
                myViewHolder.timingView.setText("Vacant");
                myViewHolder.timingView.setTextColor(context.getResources().getColor(R.color.black));
                myViewHolder.timingView.setBackgroundColor(context.getResources().getColor(R.color.bit_dark_grey));
            }
        }
        myViewHolder.bookPitchAction.setOnClickListener(view -> {
            if (verticalPitchArrayList.get(position).getBooked_status().equalsIgnoreCase("") ||
                    verticalPitchArrayList.get(position).getBooked_status().equalsIgnoreCase(Api.VACCANT)) {
                EventBus.getDefault().postSticky(new PayingForPitchEvent(verticalPitchArrayList.get(position).getTime(), withThisGetPitchPosition, true));
            } else {
                Toast.makeText(context, context.getString(R.string.pleaseSelectSomeOtherPitch), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return verticalPitchArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView timingView;
        LinearLayout bookPitchAction;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            timingView = itemView.findViewById(R.id.timingView);
            bookPitchAction = itemView.findViewById(R.id.bookPitchAction);
        }
    }
}
