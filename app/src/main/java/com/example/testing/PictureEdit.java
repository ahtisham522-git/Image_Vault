package com.example.testing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PictureEdit extends AppCompatActivity {

    private static final int URIG = 7;
    PhotoView phtoedt;
    Button crop, edt, Save,back;
    String imageLink;
    Uri uri;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_edit);
        getSupportActionBar().hide();
        phtoedt = findViewById(R.id.photoedt);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            imageLink = bundle.getString("image");
        }
        phtoedt.setImageURI(Uri.parse(imageLink));

        Uri myUri = Uri.parse(imageLink);
        crop = findViewById(R.id.cropi);
        edt = findViewById(R.id.filters);
        Save = findViewById(R.id.save);
        back = findViewById(R.id.back);
        crop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CropImage.activity(myUri)
                        .start(PictureEdit.this);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                onBackPressed();

            }
        });

        Save.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try {
                    saveImage();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void saveImage()throws Exception{

        verifyStoragePermissions(this);
        if( uri != null) {
            phtoedt.setImageURI(uri);
            BitmapDrawable drawable = (BitmapDrawable) phtoedt.getDrawable();
            Bitmap bitmap = drawable.getBitmap();


            Intent i = new Intent(PictureEdit.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            try {
                verifyStoragePermissions(PictureEdit.this);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "IMG_CROP_" + timeStamp+".jpg";

                    File dir = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    {
                        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Private Storage/"+"CropImages/");
                    }
                    // Make sure the path directory exists.
                    if (!dir.exists())
                    {
                        boolean success = dir.mkdirs();
                        if (!success)
                        {
                            dir = null;
                        }
                    }
                String root = dir.toString();
                File file = new File(root +File.separator+ imageFileName);
                FileOutputStream out = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                out.flush();
                out.close();
            }
            catch
            (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

public static void verifyStoragePermissions(Activity activity) {
    // Check if we have write permission
    int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    if (permission != PackageManager.PERMISSION_GRANTED) {
        // When we don't have permission so prompt the user
        ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
        );
    }
}
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                 uri=result.getUri();
                phtoedt.setImageURI(uri);
            }
        }
    }
}