package app.sliko.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.UI.SsRegularTextView;
import app.sliko.owner.model.AvailabilityModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StadiumOffDaysAdapter extends RecyclerView.Adapter<StadiumOffDaysAdapter.MyViewHolder> {
    int PICKED = 1, NOT_PICKED = 0;
    private Context context;
    private ArrayList<String> offDays;
    private ArrayList<String> offDayCheck;
    ArrayList<AvailabilityModel> availabilityModelArrayList = new ArrayList<>();
    String starttime, endtime, strvalue = "";
    boolean flag = true,chkrecyvisibilityslot=false;
    TimeSlotListnerCustom timeSlotListnerCustom;
String chksunday,chkmonday,chktuesday,chkwednesday,chkthursday,chkfriday,chksaturday;
    public StadiumOffDaysAdapter(Context context, ArrayList<String> offDayCheck, ArrayList<String> offDays, String starttime, String endtime,TimeSlotListnerCustom timeSlotListnerCustom) {
        this.context = context;
        this.offDayCheck = offDayCheck;
        this.offDays = offDays;
        this.starttime = starttime;
        this.endtime = endtime;
        this.timeSlotListnerCustom=timeSlotListnerCustom;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView;

        itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_off_day

                , viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
      //    Log.e("stadiumofday", "====" + offDays.get(i) + "========offdaychk" + offDayCheck.get(i));

    //  Log.e("Availbility", "====" + starttime + "  <><>" + '\n' + "<><>" + endtime);


        String firstval = offDayCheck.get(0);
        for (int j = 1; j < offDayCheck.size() && flag; j++) {
            if (offDayCheck.get(j) != firstval) {
                flag = false;
                //   Log.e("flag", "==" + flag + '\n' + offDays.get(j));

            } else {
                //Log.e("elseflag", "==" + flag);

            }
        }




        if (flag == false) {
chkrecyvisibilityslot= true;
            timeSlotListnerCustom.timeSetMethod(i, strvalue,chkrecyvisibilityslot);

            if (offDayCheck.get(i).equalsIgnoreCase("1")) {
                if (offDays.get(i).equalsIgnoreCase("Wed")) {
                    myViewHolder.offDays.setText(offDays.get(2));
                } else if (offDays.get(i).equalsIgnoreCase("Thurs")) {
                    myViewHolder.offDays.setText(offDays.get(3));
                } else if (offDays.get(i).equalsIgnoreCase("Mon")) {
                    myViewHolder.offDays.setText(offDays.get(0));
                } else if (offDays.get(i).equalsIgnoreCase("Tues")) {
                    myViewHolder.offDays.setText(offDays.get(1));
                } else if (offDays.get(i).equalsIgnoreCase("Fri")) {
                    myViewHolder.offDays.setText(offDays.get(4));
                } else if (offDays.get(i).equalsIgnoreCase("Sat")) {
                    myViewHolder.offDays.setText(offDays.get(5));
                } else if (offDays.get(i).equalsIgnoreCase("Sun")) {
                    myViewHolder.offDays.setText(offDays.get(6));


                }
            } else {
              //  myViewHolder.cardid.setVisibility(View.GONE);
                myViewHolder.cardid.setBackground(context.getResources().getDrawable(R.drawable.shadecolor));
                myViewHolder.offDays.setTextColor(context.getResources().getColor(R.color.white));
                if (offDays.get(i).equalsIgnoreCase("Wed")) {
                    myViewHolder.offDays.setText(offDays.get(2));
                    chkwednesday = offDays.get(2);
                    timeSlotListnerCustom.chkDaysNotBooked("","","",chkwednesday,"","","");
                } else if (offDays.get(i).equalsIgnoreCase("Thurs")) {
                    myViewHolder.offDays.setText(offDays.get(3));
                    chkthursday = offDays.get(3);
                    timeSlotListnerCustom.chkDaysNotBooked("","","","",chkthursday,"","");

                } else if (offDays.get(i).equalsIgnoreCase("Mon")) {
                    myViewHolder.offDays.setText(offDays.get(0));
                    chkmonday = offDays.get(0);
                    timeSlotListnerCustom.chkDaysNotBooked("",chkmonday,"","","","","");

                } else if (offDays.get(i).equalsIgnoreCase("Tues")) {
                    myViewHolder.offDays.setText(offDays.get(1));
                    chktuesday = offDays.get(1);
                    timeSlotListnerCustom.chkDaysNotBooked("","",chktuesday,"","","","");

                } else if (offDays.get(i).equalsIgnoreCase("Fri")) {
                    myViewHolder.offDays.setText(offDays.get(4));
                    chkfriday = offDays.get(4);
                    timeSlotListnerCustom.chkDaysNotBooked("","","","","",chkfriday,"");

                } else if (offDays.get(i).equalsIgnoreCase("Sat")) {
                    myViewHolder.offDays.setText(offDays.get(5));
                    chksaturday = offDays.get(5);
                    timeSlotListnerCustom.chkDaysNotBooked("","","","","","",chksaturday);

                } else if (offDays.get(i).equalsIgnoreCase("Sun")) {
                    myViewHolder.offDays.setText(offDays.get(6));
                    chksunday = offDays.get(6);
                    timeSlotListnerCustom.chkDaysNotBooked(chksunday,"","","","","","");

                }
              //  myViewHolder.offDays.setText("đóng cửa");// closed
            }


        } else {
            chkrecyvisibilityslot=false;
            //  if (i==0) {
            myViewHolder.itemView.setVisibility(View.GONE);
            myViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            if (starttime.equalsIgnoreCase("00:00-1:00") && endtime.equalsIgnoreCase("23:00-00:00")) {
                strvalue = "Cả tuần";

                myViewHolder.offDays.setText(strvalue);
                timeSlotListnerCustom.timeSetMethod(i, strvalue,chkrecyvisibilityslot);
            } else {
                strvalue = "Cả ngày"; // all day

                myViewHolder.offDays.setText(strvalue);// all week
                timeSlotListnerCustom.timeSetMethod(i, strvalue,chkrecyvisibilityslot);


            }
            //  }
        }


    }

    @Override
    public int getItemCount() {
        /*ArrayList<String> returnList = new ArrayList<>();
        for (String l : offDayCheck) {
            if (l.equalsIgnoreCase("1")) {// change 0 into 1
                returnList.add(l);
            }
        }*/
        return offDayCheck.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.offDays)
        SsRegularTextView offDays;
        @BindView(R.id.cardid)
        CardView cardid;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(MyViewHolder.this, itemView);
        }
    }

    public interface TimeSlotListnerCustom {
        public void timeSetMethod(int position, String str,boolean chkrecyvisibilityslot);
        public  void chkDaysNotBooked(String sun,String mon,String tues,String wed,String thurs,String fri,String sat);

    }


}
