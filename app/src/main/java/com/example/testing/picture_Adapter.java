package com.example.testing;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import static androidx.core.view.ViewCompat.setTransitionName;

public class picture_Adapter extends RecyclerView.Adapter<PicHolder> {

    private ArrayList<pictureFacer> pictureList;
    private Context pictureContx;
    private final itemClickListener picListerner;

    public picture_Adapter(ArrayList<pictureFacer> pictureList, Context pictureContx,itemClickListener picListerner) {
        this.pictureList = pictureList;
        this.pictureContx = pictureContx;
        this.picListerner = picListerner;
    }

    @NonNull
    @Override
    public PicHolder onCreateViewHolder(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View cell = inflater.inflate(R.layout.pic_holder_item, container, false);
        return new PicHolder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull final PicHolder holder,int position) {

        final pictureFacer image = pictureList.get(position);

//       Glide.with(pictureContx)
//                .load(image.getPicturePath())
//                .apply(new RequestOptions().centerCrop())
//                        .placeholder(R.drawable.ic_baseline_error_24)
//                 .into(holder.picture);


        Glide.with(pictureContx).load(image.getPicturePath())
                .into(holder.picture);

        setTransitionName(holder.picture, String.valueOf(position) + "_image");

        holder.picture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                picListerner.onPicClicked(holder,position, pictureList);


            }
        });
        holder.getAdapterPosition();

    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }
}
