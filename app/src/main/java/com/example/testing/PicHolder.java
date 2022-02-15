package com.example.testing;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class PicHolder extends RecyclerView.ViewHolder{

    public ImageView picture;

    ImageView imgchck;

    PicHolder(@NonNull View itemView) {
        super(itemView);

        picture = itemView.findViewById(R.id.image);

        imgchck=itemView.findViewById(R.id.image_select);
    }
}
