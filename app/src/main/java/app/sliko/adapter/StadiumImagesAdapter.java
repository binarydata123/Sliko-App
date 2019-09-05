package app.sliko.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.sliko.R;

public class StadiumImagesAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> reviewsModelArrayList;

    public StadiumImagesAdapter(Context context, ArrayList<String> newsModelArrayList) {
        this.context = context;
        this.reviewsModelArrayList = newsModelArrayList;
    }

    @Override
    public int getCount() {
        return reviewsModelArrayList.size();
    }

    int width;
    int height;

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return object == view;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_image, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        FrameLayout layout = (FrameLayout) view;
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = layout.getMeasuredWidth();
                height = layout.getMeasuredHeight();
                Picasso.get().load(reviewsModelArrayList.get(position))
                        .resize(width, height).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
