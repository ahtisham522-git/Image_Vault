package com.example.testing;

import static android.widget.Toast.LENGTH_LONG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import static androidx.core.view.ViewCompat.setTransitionName;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.move;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

//adapter of folder that hold images

public class picture_Adapter extends RecyclerView.Adapter<PicHolder> {

    private ArrayList<pictureFacer> pictureList;
    private Context pictureContx;
    boolean isSelected =false;
    boolean isenable =false;
    String signal ="";
    public static final int ITEM_TYPE_ONE = 0;
    public static final int ITEM_TYPE_TWO = 1;


    private ArrayList<String> selectedpicturelist= new ArrayList<String>();


    private final itemClickListener picListerner;

    public picture_Adapter(ArrayList<pictureFacer> pictureList, Context pictureContx,itemClickListener picListerner) {
        this.pictureList = pictureList;
        this.pictureContx = pictureContx;
        this.picListerner = picListerner;
    }

    @NonNull
    @Override
    public PicHolder onCreateViewHolder(@NonNull ViewGroup container, int position) {

        // check here the viewType and return RecyclerView.ViewHolder based on view type

            LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(R.layout.pic_holder_item,container, false);
            return new PicHolder(view);


        //define second view here on long press



    }

    @Override
    public void onBindViewHolder(@NonNull final PicHolder holder, @SuppressLint("RecyclerView") int position) {

        final pictureFacer image = pictureList.get(position);


        Glide.with(pictureContx).load(image.getPicturePath())
                .into(holder.picture);
        setTransitionName(holder.picture, String.valueOf(position) + "_image");
        holder.picture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(isenable)
                {
                    clickedpictures(holder);

                }
                else{
                    picListerner.onPicClicked(holder,position, pictureList);
                }

           //     Toast.makeText(pictureContx, holder.getAdapterPosition(), LENGTH_LONG).show();
            }

        });

        if(isSelected)
        {
            holder.imgchck.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.RED);
        }
        else
            {
                holder.imgchck.setVisibility(View.GONE);
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);


        }

        holder.picture.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view)
            {
                if(!isSelected)
                {
                    ActionMode.Callback callback = new ActionMode.Callback()
                    {

                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

                            MenuInflater menuInflater=actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.menu,menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
                        {
                            isSelected=true;

                            clickedpictures(holder);

                            return false;
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
                        {
                            int id =menuItem.getItemId();
                            Dialog dialogimg = new Dialog(view.getContext());
                            dialogimg.setContentView(R.layout.deleteimagepopup);
                            dialogimg.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialogimg.setCancelable(false);
                            dialogimg.getWindow().getAttributes().windowAnimations= R.style.Theme_Testing;
                            TextView cancl = dialogimg.findViewById(R.id.canclimgbtn);
                            TextView del = dialogimg.findViewById(R.id.delimgbtn);




                            switch(id)
                            {
                                case R.id.menu_delete:

                                    dialogimg.show();


                                    cancl.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogimg.hide();
                                        }
                                    });

                                    del.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            signal="ok";

                                            dialogimg.hide();
                                        }
                                    });

                                for (int i=0;i<selectedpicturelist.size();i++)
                                    {


                                        File file = new File(selectedpicturelist.get(i));
                                        deleteimgRecursive(file);
                                    }

                                    if(selectedpicturelist.size() == 0)
                                    {

                                    }


                                    actionMode.finish();
                                    break;

                                case R.id.Copy:

                                    for (int i=0;i<selectedpicturelist.size();i++)
                                    {

                                        Path file =Paths.get(selectedpicturelist.get(i));
                                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                        String name = "COPIED" + timeStamp + ".jpg";

                                        Path dst = Paths.get(Paths.get(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)  + "/Restored/"+name).toString());

                                        copyimg(file,dst);

                                    }

                                    if(selectedpicturelist.size() == 0)
                                    {

                                    }
                                    actionMode.finish();
                                    break;

                                case R.id.Move:

                                    for (int i=0;i<selectedpicturelist.size();i++)
                                    {
                                        Path file =Paths.get(selectedpicturelist.get(i));
                                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                        String name = "MOVED" + timeStamp + ".jpg";
                                        Path dst = Paths.get(Paths.get(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)  + "/Restored/"+name).toString());
                                        Moveimg(file,dst);
                                    }


                                    if(selectedpicturelist.size() == 0)
                                    {

                                    }
                                    actionMode.finish();
                                    break;

                                case R.id.selctall:
                                    if(selectedpicturelist.size()==pictureList.size())
                                {
                                    isSelected=false;
                                    selectedpicturelist.clear();

                                }
                                    else
                                        {
                                            isSelected=true;
                                            selectedpicturelist.clear();
                                         //   selectedpicturelist.addAll(pictureList.addAll());


                                }
                                notifyDataSetChanged();
                                break;

                            }


                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode)
                        {
                            isenable =false;
                            isSelected= false;
                            selectedpicturelist.clear();
                            notifyDataSetChanged();

                        }
                    };

                    ((AppCompatActivity)view.getContext()).startActionMode(callback);
                }
                else
                    {
                        clickedpictures(holder);


                    }

                return true;
            }
        });

        holder.getAdapterPosition();

    }

    void Moveimg(Path src,Path dst)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                move(src,dst);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

   void copyimg(Path src,Path dst)
   {

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           try {
               copy(src,dst);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

   }

    void deleteimgRecursive(File fileOrDirectory) {
        if (fileOrDirectory.exists()) {
            if (fileOrDirectory.delete()) {
                System.out.println("file Deleted :" + fileOrDirectory.getPath());
            } else {
                System.out.println("file not Deleted :" + fileOrDirectory.getPath());
            }
        }

    }

    private void clickedpictures(PicHolder holder)
    {
        pictureFacer s = pictureList.get(holder.getAdapterPosition());

        if(holder.imgchck.getVisibility()== View.GONE)
        {
            holder.imgchck.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.BLUE);
            selectedpicturelist.add(s.getPicturePath());
        }
        else
            {

               holder.imgchck.setVisibility(View.GONE);
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                selectedpicturelist.remove(s);
        }


    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }



}
