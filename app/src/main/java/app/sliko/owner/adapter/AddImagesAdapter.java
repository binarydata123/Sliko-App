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

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.owner.model.ImagesModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddImagesAdapter extends RecyclerView.Adapter<AddImagesAdapter.MyViewHolder> {

    private String type;
    private Context context;
    private ArrayList<String> imagesModelArrayList;

    public AddImagesAdapter(Context context, ArrayList<String> imagesModelArrayList, String type) {
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

        Glide.with(context).load(imagesModelArrayList.get(i)).into(myViewHolder.imageViewDocument);
        myViewHolder.deleteImage.setOnClickListener(v -> {
            imagesModelArrayList.remove(i);
            AddImagesAdapter.this.notifyDataSetChanged();
        });
    }

    public ArrayList<String> getUpdatedList() {
        return imagesModelArrayList;
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
