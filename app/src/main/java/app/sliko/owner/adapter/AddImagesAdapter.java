package app.sliko.owner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.models.StadiumImagesModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddImagesAdapter extends RecyclerView.Adapter<AddImagesAdapter.MyViewHolder> {

    private String type;
    private Context context;
    private ArrayList<StadiumImagesModel> imagesModelArrayList;

    public AddImagesAdapter(Context context, ArrayList<StadiumImagesModel> imagesModelArrayList, String type) {
        this.context = context;
        this.imagesModelArrayList = imagesModelArrayList;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_document, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        Picasso.get().load(imagesModelArrayList.get(i).getImageName()).into(myViewHolder.imageViewDocument);
        myViewHolder.deleteImage.setOnClickListener(v -> {
            if (imagesModelArrayList.get(i).getImageId().equalsIgnoreCase("")) {
                //remove normally from list
                imagesModelArrayList.remove(i);
            } else {
                //delete Image from server
            }
            AddImagesAdapter.this.notifyDataSetChanged();
        });
        if(type.equalsIgnoreCase("1")){
            myViewHolder.deleteImage.setVisibility(View.VISIBLE);
        }else{
            myViewHolder.deleteImage.setVisibility(View.GONE);
        }

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
