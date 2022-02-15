package com.example.testing;

import static com.example.testing.PictureEdit.verifyStoragePermissions;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImageDisplay extends AppCompatActivity implements itemClickListener {

    private static final int PICK_IMAGE_MULTIPLE = 1;
    private static final int PERMISSION_REQUEST_CODE = 7;
    private static final int GALLERY_REQUEST = 1;
    RecyclerView imageRecycler;
    ArrayList<pictureFacer> allpictures;
    ProgressBar load;
    String foldePath;
    Dialog dialogdel;
    TextView cancl,del;

    TextView folderName,eepty;
    String fldrname;
    Button add, back;
    File dst;
    File src;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                maindash();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        //getActionBar().setIcon(R.drawable.my_icon);
        back = findViewById(R.id.back);
        eepty =findViewById(R.id.empy);
        fldrname = (getIntent().getStringExtra("folderName"));
        actionBar.setTitle(fldrname);
        foldePath = getIntent().getStringExtra("folderPath");
        allpictures = new ArrayList<>();
        imageRecycler = findViewById(R.id.recycler);
        add = findViewById(R.id.btnadd2);
        imageRecycler.addItemDecoration(new MarginDecoration(this));
        imageRecycler.hasFixedSize();
        load = findViewById(R.id.loader);
        dialogdel = new Dialog(this);
        dialogdel.setContentView(R.layout.deletefolderpopup);
        dialogdel.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogdel.setCancelable(false);
        dialogdel.getWindow().getAttributes().windowAnimations= R.style.Theme_Testing;
        cancl=dialogdel.findViewById(R.id.canclbtn);
        del=dialogdel.findViewById(R.id.delbtn);

        if (allpictures.isEmpty())
        {
            allpictures = getAllImagesByFolder(foldePath);
            if(allpictures.isEmpty())
            {
                eepty.setVisibility(View.VISIBLE);
                load.setVisibility(View.GONE);
            }
            else
                {
                load.setVisibility(View.VISIBLE);
                imageRecycler.setAdapter(new picture_Adapter(allpictures, ImageDisplay.this, this));
                load.setVisibility(View.GONE);
            }
        }
//        trsh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogdel.show();
//            }
//        });
        cancl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogdel.hide();
            }
        });

        del.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                File file = new File(root + "/Private Storage/"+fldrname+"/");
                deleteRecursive(file);
                dialogdel.hide();
                maindash();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPickImage();
            }
        });


    }
    // Export Image Functions
    public void buttonPickImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        if (selectedImage != null)
                        {
                            verifyStoragePermissions(this);
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            String imageFileName = "IMG_" + timeStamp + ".jpg";
                            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                            File file = new File(root + "/Private Storage/"+fldrname+"/" + imageFileName);
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                            out.flush();
                            out.close();

                            maindash();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
    }


    private void askPermission()
{
    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
}



    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {
        pictureBrowserFragment browser = pictureBrowserFragment.newInstance(pics,position,ImageDisplay.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //browser.setEnterTransition(new Slide());
            //browser.setExitTransition(new Slide()); remove comnt to use slide transition and comment the two lines below
            browser.setEnterTransition(new Fade());
            browser.setExitTransition(new Fade());
        }
        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(holder.picture, position+"picture")
                .add(R.id.displayContainer, browser)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onPicClicked(String pictureFolderPath,String folderName)
    {
    }

    public ArrayList<pictureFacer> getAllImagesByFolder(String path) {
        ArrayList<pictureFacer> images = new ArrayList<>();

        String pth = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + "Private Storage/test2";

        ArrayList<String> picturename = new ArrayList();
        ArrayList<String> picturePaths = new ArrayList();
        ArrayList<Integer> picturesize = new ArrayList();

        File f = new File(path);
        File[] files = f.listFiles();

        for (File inFile : files)
        {
                picturename.add(inFile.getName());
                picturePaths.add(inFile.getPath());
        //        picturesize.add(inFile.listFiles().length);
            }
            for (int s = 0; s < files.length; s++)
            {
                pictureFacer pic = new pictureFacer();
                String picname = picturename.get(s);
                String pcpaths = picturePaths.get(s);
             //   String numofpc = picturesize.get(s).toString();

                if(pcpaths==null)
                {
                    eepty.setVisibility(View.VISIBLE);
                }
                else
                    {
                pic.setPicturePath(pcpaths);
                pic.setPicturName(picname);
              //  pic.setPictureSize(numofpc);
                images.add(pic);
                }
            }
        return images;

        }
    void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }

    void maindash()
    {
        Intent intent=new Intent(ImageDisplay.this, MainActivity.class);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);

    }

    }


