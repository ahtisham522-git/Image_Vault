package com.example.testing;

import static java.lang.String.valueOf;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class pictureFolderAdapter extends RecyclerView.Adapter<FolderHolder> {

    Activity activity;
    private ArrayList<imageFolder> folders;
    MainViewModel mainViewModel;

    SharedPreferences sharedPreferences;
    private Context folderContx;
    private itemClickListener listenToClick;
    boolean isSelected = false;
    boolean isenable = false;

    private ArrayList<String> sa;
    private ArrayList<String> selectlist = new ArrayList<String>();

    public pictureFolderAdapter(ArrayList<imageFolder> folders, Context folderContx, itemClickListener listen) {
        this.folders = folders;
        this.folderContx = folderContx;
        this.listenToClick = listen;
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.picture_folder_item, parent, false);
        return new FolderHolder(cell);

        //  mainViewModel= ViewModelProviders.of(FragmentActivity) activity).folders.get();

    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, int position) {
        final imageFolder folder = folders.get(position);
//        Glide.with(folderContx)
//                .load(folder.getFirstPic())
//                .apply(new RequestOptions().centerCrop())
//                .into(holder.folderPic);
        //setting the number of images
        String text = "" + folder.getFolderName();
        String folderSizeString = "" + folder.getNumberOfPics() + " Images";


        holder.folderSize.setText(folderSizeString);
        holder.folderName.setText(text);

//        holder.folderPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                listenToClick.onPicClicked(folder.getPath(), folder.getFolderName());
//            }
//        });
        holder.folderPic.setOnLongClickListener(new View.OnLongClickListener() {

            private boolean signal = false;

            @Override
            public boolean onLongClick(View view) {
                if (!isenable) {
                    ActionMode.Callback callback = new ActionMode.Callback() {

                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

                            MenuInflater menuInflater = actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.foldermenu, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            isenable = true;
                            Clickitems(holder);
//                         //  mainViewModel.getText().observe((LifecycleOwner) activity,new Observer<String>()
//                           {
//                               public void onChanged(String s)
//                               {
//                                   actionMode.setTitle(String.format("%s Selected ","12"));
//                               }
//                           });
                            return false;
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            Dialog dialogimg = new Dialog(view.getContext());
                            dialogimg.setContentView(R.layout.deleteimagepopup);
                            dialogimg.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialogimg.setCancelable(false);
                            dialogimg.getWindow().getAttributes().windowAnimations = R.style.Theme_Testing;
                            TextView cancl = dialogimg.findViewById(R.id.canclimgbtn);
                            TextView del = dialogimg.findViewById(R.id.delimgbtn);

                            switch (id) {
                                case R.id.menu_delete:

                                    dialogimg.show();
                                    cancl.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogimg.hide();
                                            actionMode.finish();
                                            holder.ivcheckbox.setVisibility(View.GONE);
                                            holder.itemView.setBackgroundColor(Color.TRANSPARENT);

                                        }
                                    });

                                    del.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

//                                            Intent intent = getIntent();
//                                            overridePendingTransition(0, 0);
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                            finish();
//                                            overridePendingTransition(0, 0);
//                                            startActivity(intent);
                                            holder.ivcheckbox.setVisibility(View.GONE);
                                            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                                            actionMode.finish();

                                            dialogimg.dismiss();
                                        }
                                    });

                                    for (int i = 0; i < selectlist.size(); i++)
                                    {
                                        File fileOrDirectory = new File(selectlist.get(i));
                                        deletefolder(fileOrDirectory);

                                    }
//                                    for (int i = 0; i < selectlist.size(); i++) {
//                                        File file = new File(selectlist.get(i));
////                                        deleteimgRecursive(file);
//                                         selectlist.remove(i);
//                                    }
//                                    if (folders.size() == 0) {
//                                        Toast.makeText(folderContx, "avv", Toast.LENGTH_LONG).show();
//                                    }
                                    actionMode.finish();
                                    break;

                                case R.id.selctall:
                                    if (selectlist.size() == folders.size()) {
                                        isSelected = false;
                                        selectlist.clear();
                                    } else {
                                        isSelected = true;
                                        selectlist.clear();
                                        //   selectlist.addAll(folders.t);

                                    }
                                    //  mainViewModel.setText(valueOf(selectlist.size()));
                                    notifyDataSetChanged();
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isenable = false;
                            isSelected = false;
                            selectlist.clear();
                            notifyDataSetChanged();
                        }
                    };

                    ((AppCompatActivity) view.getContext()).startActionMode(callback);
                } else {
                    Clickitems(holder);
                }
                return true;


            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isenable)
                {
                    Clickitems(holder);

                }
                else
                    {
                        listenToClick.onPicClicked(folder.getPath(), folder.getFolderName());
                    }
            }
        });

        if(isSelected)
        {
            holder.ivcheckbox.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.WHITE);

        }
        else
            {
                holder.ivcheckbox.setVisibility(View.GONE);
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);

        }


    }

    private void Clickitems(FolderHolder holder) {
        String s = folders.get(holder.getAdapterPosition()).getPath();
        if (holder.ivcheckbox.getVisibility() == View.GONE) {
            holder.ivcheckbox.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.WHITE);
            selectlist.add(s);
        } else {
            holder.ivcheckbox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            selectlist.remove(s);
        }

//        mainViewModel.setText(valueOf(selectlist.size()));
    }

    void deletefolder(File fileOrDirectory) {


            if (fileOrDirectory.exists()) {
                if (fileOrDirectory.delete()) {
                    System.out.println("file Deleted :" + fileOrDirectory.getPath());
                } else {
                    System.out.println("file not Deleted :" + fileOrDirectory.getPath());
                }
            }

        }


    @Override
    public int getItemCount() {
        return folders.size();
    }
}

