package com.example.testing;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.sarthakdoshi.textonimage.TextOnImage;
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
    Button crop, flter, Save, back, addtext;
    String imageLink;
    Uri uri;
    Dialog dialogbox;
    TextView canclps, okps;
    EditText textonimage;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    ActivityResultLauncher<Intent> activitylauncher = registerForActivityResult
            (new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {

                            if (result.getResultCode() == 78) {
                                Intent intent = result.getData();

                                if (intent != null) {
                                    Uri resultImage = Uri.parse(intent.getStringExtra("filterresult"));
                                    phtoedt.setImageURI(resultImage);
                                }
                            }

                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_edit);
        getSupportActionBar().hide();
        phtoedt = findViewById(R.id.photoedt);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            imageLink = bundle.getString("image");
            phtoedt.setImageURI(Uri.parse(imageLink));
        }
        Uri myUri = Uri.parse(imageLink);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageLink = bundle.getString("filterresult");
            
        }


        BottomSheetDialog dialogbox = new BottomSheetDialog(PictureEdit.this);
        dialogbox.setContentView(R.layout.picturetext);


        dialogbox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogbox.setCancelable(false);
        dialogbox.getWindow().getAttributes().windowAnimations = R.style.Theme_Testing;

        canclps = dialogbox.findViewById(R.id.adtcnclbtn);
        okps = dialogbox.findViewById(R.id.adtokbtn);
        textonimage = dialogbox.findViewById(R.id.ptext);



        okps.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent intent = new Intent(PictureEdit.this, TextOnImage.class);
                Bundle bundle = new Bundle();
                bundle.putString(TextOnImage.IMAGE_IN_URI,myUri.toString()); //image uri
                bundle.putString(TextOnImage.TEXT_COLOR,"#F14F4F");                 //initial color of the text
                bundle.putFloat(TextOnImage.TEXT_FONT_SIZE,35.0f);                  //initial text size
                bundle.putString(TextOnImage.TEXT_TO_WRITE,textonimage.getText().toString());                   //text to be add in the image
                intent.putExtras(bundle);
                startActivityForResult(intent, TextOnImage.TEXT_ON_IMAGE_REQUEST_CODE);

                dialogbox.dismiss();
                textonimage.setText("");

            }
        });

        canclps.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialogbox.dismiss();
                textonimage.setText("");

            }
        });


        crop = findViewById(R.id.cropi);
        flter = findViewById(R.id.filters);
        addtext=findViewById(R.id.adtext);
        Save = findViewById(R.id.save);
        back = findViewById(R.id.back);
        crop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CropImage.activity(myUri)
                        .start(PictureEdit.this);

            }
        });

        flter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //All the edit functinalities
                Intent intent = new Intent( PictureEdit.this,Filters.class);

                intent.putExtra("filterimage",myUri.toString());
                activitylauncher.launch(intent);
            }
        });

        addtext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogbox.show();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();

            }
        });


        Save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    saveImage();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void saveImage() throws Exception {

        verifyStoragePermissions(this);
        if (uri != null) {
            phtoedt.setImageURI(uri);
            BitmapDrawable drawable = (BitmapDrawable) phtoedt.getDrawable();
            Bitmap bitmap = drawable.getBitmap();


            Intent i = new Intent(PictureEdit.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            try {
                verifyStoragePermissions(PictureEdit.this);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "IMG_CROP_" + timeStamp + ".jpg";

                File dir = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Private Storage/" + "CropImages/");
                }
                // Make sure the path directory exists.
                if (!dir.exists()) {
                    boolean success = dir.mkdirs();
                    if (!success) {
                        dir = null;
                    }
                }
                String root = dir.toString();
                File file = new File(root + File.separator + imageFileName);
                FileOutputStream out = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                out.flush();
                out.close();
            } catch
            (Exception ex) {
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

    public void checkSelfPermission(Activity activity) {

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // Check if we have write permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        } else {
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                phtoedt.setImageURI(uri);
            }
        }

        else if(requestCode == TextOnImage.TEXT_ON_IMAGE_REQUEST_CODE)
        {
            if(resultCode == TextOnImage.TEXT_ON_IMAGE_RESULT_OK_CODE)
            {
                Uri resultImageUri = Uri.parse(data.getStringExtra(TextOnImage.IMAGE_OUT_URI));

                try {
                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultImageUri);
                    phtoedt.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(resultCode == TextOnImage.TEXT_ON_IMAGE_RESULT_FAILED_CODE)
            {
                String errorInfo = data.getStringExtra(TextOnImage.IMAGE_OUT_ERROR);
                Log.d("MainActivity", "onActivityResult: "+errorInfo);
            }
        }
    }



}


