package app.sliko.location;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.UI.SsRegularTextView;
import app.sliko.utills.M;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    Dialog dialog;
    private Context context;
    private ArrayList<String> addressList;

    public SearchAdapter(Context context, ArrayList<String> addressList) {
        this.context = context;
        this.addressList = addressList;
        dialog = M.showDialog(context, "", false);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_searched_address, viewGroup, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.placeName.setText(addressList.get(position));
        myViewHolder.tap.setOnClickListener(view -> {
            getLatLong(addressList.get(position));
        });
    }

    String lat, lng;

    private void getLatLong(String address) {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_getLatLng(address);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
                try {
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();

                        JSONObject jsonObject = new JSONObject(sResponse);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("true")) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            lat = dataObject.getString("lat");
                            lng = dataObject.getString("lng");
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("lat", lat);
                            returnIntent.putExtra("lng", lng);
                            returnIntent.putExtra("addressSelected", address);
                            ((Activity) context).setResult(Activity.RESULT_OK, returnIntent);
                            ((Activity) context).finish();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.placeName)
        SsRegularTextView placeName;
        @BindView(R.id.tap)
        LinearLayout tap;


        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(MyViewHolder.this, itemView);
        }
    }
}
