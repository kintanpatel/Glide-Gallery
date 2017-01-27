package kvp.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sai on 06/01/2017.
 */

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.MyViewHolder> {
    private ArrayList<String> alPath;
    private Context mContext;
    private RecylerClickIntegration clickIntegration;

    public GalleryImageAdapter(Context mContext, RecylerClickIntegration clickIntegration) {
        this.mContext = mContext;
        this.clickIntegration = clickIntegration;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.lay_image, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(mContext).load(new File(alPath.get(position))).animate(android.R.anim.fade_in).thumbnail(0.2f).placeholder(R.drawable.default_profile_pic).into(holder.imgGallery);

    }
    public String getImageSelectedImagePath(int position){
        return alPath.get(position);
    }

    @Override
    public int getItemCount() {
        return alPath.size();
    }

    public void addData(ArrayList<String> alPath) {
        this.alPath = alPath;
        this.notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgGallery;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgGallery = (ImageView) itemView.findViewById(R.id.img_gallery);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickIntegration != null) {
                clickIntegration.onItemClick(getAdapterPosition());
            }
        }
    }
}
