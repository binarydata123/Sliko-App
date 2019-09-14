package app.sliko.owner.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import app.sliko.R;
import app.sliko.models.StadiumImagesModel;
import app.sliko.utills.M;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddImagesAdapter extends RecyclerView.Adapter<AddImagesAdapter.MyViewHolder> {

    private String type;
    private Context context;
    private ArrayList<StadiumImagesModel> imagesModelArrayList;

    Dialog dialog;

    public AddImagesAdapter(Context context, ArrayList<StadiumImagesModel> imagesModelArrayList, String type) {
        this.context = context;
        this.imagesModelArrayList = imagesModelArrayList;
        this.type = type;
        dialog = M.showDialog(context, "", false);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_document, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        Log.e(">>id", "onBindViewHolder: " + imagesModelArrayList.get(i).getImageId() + "\n" + imagesModelArrayList.size());

        if (imagesModelArrayList.get(i).getImageId().equals("")) {
            try {
                File fileimg = new File(imagesModelArrayList.get(i).getImageName());
                Picasso.get().load(fileimg).into(myViewHolder.imageViewDocument);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            Picasso.get().load(imagesModelArrayList.get(i).getImageName()).into(myViewHolder.imageViewDocument);

        }

        myViewHolder.deleteImage.setOnClickListener(v -> {
            if (imagesModelArrayList.get(i).getImageId().equalsIgnoreCase("")) {
                imagesModelArrayList.remove(i);
                AddImagesAdapter.this.notifyDataSetChanged();
            } else {

                deleteFromServer(i);


            }

        });
        if (!imagesModelArrayList.get(i).getImageId().equalsIgnoreCase("")) {
            myViewHolder.deleteImage.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.deleteImage.setVisibility(View.GONE);
        }

    }

    Call<ResponseBody> call;

    private void deleteFromServer(int pos) {
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        if (type.equalsIgnoreCase("stadium")) {
            call = service.ep_stadiumGalleryDelete(imagesModelArrayList.get(pos).getImageId());
        } else {
            call = service.ep_pitchGalleryDelete(imagesModelArrayList.get(pos).getImageId());
        }

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

                            imagesModelArrayList.remove(pos);
                            AddImagesAdapter.this.notifyDataSetChanged();

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

    ArrayList<StadiumImagesModel> updatedList;

    public ArrayList<StadiumImagesModel> getUpdatedList() {
        updatedList = new ArrayList<>();
        for (int k = 0; k < imagesModelArrayList.size(); k++) {
            if (imagesModelArrayList.get(k).getImageId().equalsIgnoreCase("")) {
                updatedList.add(imagesModelArrayList.get(k));
            }
        }
        return updatedList;
    }


    @Override
    public int getItemCount() {
        return imagesModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageViewDocument)
        ImageView imageViewDocument;
        @BindView(R.id.deleteImage)
        LinearLayout deleteImage;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(MyViewHolder.this, itemView);
        }
    }
}
