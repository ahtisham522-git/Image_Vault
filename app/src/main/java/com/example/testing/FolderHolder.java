package com.example.testing;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class FolderHolder extends RecyclerView.ViewHolder {
    ImageView folderPic;
    TextView folderName;
    //set textview for foldersize
    TextView folderSize;
    CardView folderCard;

    TextView textview;
    ImageView ivcheckbox;

    public FolderHolder(@NonNull View itemView) {
        super(itemView);
        folderPic = itemView.findViewById(R.id.folderPic);
        folderName = itemView.findViewById(R.id.folderName);
        folderSize = itemView.findViewById(R.id.folderSize);
        folderCard = itemView.findViewById(R.id.folderCard);
        ivcheckbox=itemView.findViewById(R.id.iv_checkbox);
    }
}
