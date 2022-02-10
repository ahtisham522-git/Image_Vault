package com.example.testing;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements itemClickListener, LifecycleEventObserver {

    private static final int REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 7;
    RecyclerView folderRecycler;
    TextView empty;
    Button addfolder;
    Dialog dialog;
    Button cancel,ok;
    EditText medit;
    String foldername;
    Lockstate ps;
    Boolean signal =false;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main);

//        getSupportActionBar().hide();
        commonDocumentDirPath("Private Storage");

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.foldernamepopup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations= R.style.Theme_Testing;
        cancel=dialog.findViewById(R.id.cancelbtn);
        ok=dialog.findViewById(R.id.okbtn);
        medit=dialog.findViewById(R.id.fnameedt);

        ProcessLifecycleOwner.get().getLifecycle().addObserver((LifecycleObserver) MainActivity.this);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        empty =findViewById(R.id.empty);

        folderRecycler = findViewById(R.id.folderRecycler);
        folderRecycler.addItemDecoration(new MarginDecoration(this));
        folderRecycler.hasFixedSize();

        addfolder = findViewById(R.id.addfold);

        ArrayList<imageFolder> folds = getPicturePaths();

        if(folds.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else{
            RecyclerView.Adapter folderAdapter = new pictureFolderAdapter(folds,MainActivity.this,this);
            folderRecycler.setAdapter(folderAdapter);
            folderAdapter.notifyDataSetChanged();
        }
        changeStatusBarColor();
        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                 foldername = medit.getText().toString();

                if(TextUtils.isEmpty(foldername))
                {
                    medit.setError("Enter folder Name");
                    return;
                }
                else
                {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                        createDirectory(foldername);

                    }else
                    {
                        askPermission();
                    }
                       dialog.dismiss();
                  //  medit.setText("");

                    Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                }
            }
        });

        addfolder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
              dialog.show();
                medit.setText("");
            }
        });


     cancel.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            dialog.cancel();
            medit.setText("");

        }
    });
}


    private void askPermission()
    {

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                createDirectory(foldername);
            }else
            {
                Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static File createDirectory(String FolderName)
    {
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Private Storage/"+FolderName);
        }
        else
        {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
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
        return dir;
    }

    private ArrayList<imageFolder> getPicturePaths() {
        ArrayList<imageFolder> picFolders = new ArrayList<>();

        String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/"+"Private Storage";
        ArrayList<String> fldernames = new ArrayList();
        ArrayList<String> picPaths = new ArrayList();
        ArrayList<Integer> filecount = new ArrayList();

        File f = new File(path);
        String folder = f.getParent();
        File[] files = f.listFiles();

        for (File inFile : files) {
            if (inFile.isDirectory()) {
                fldernames.add(inFile.getName());
                picPaths.add(inFile.getPath());
                filecount.add(inFile.listFiles().length);
            }
        }
        if (picPaths != null)
        {
            for (int s = 0; s < fldernames.size(); s++) {
                imageFolder folds = new imageFolder();
                String foldername = fldernames.get(s);
                String folderpaths = picPaths.get(s);
                int numofpc = filecount.get(s);
                folds.setPath(folderpaths);
                folds.setFolderName(foldername);
                folds.setNumberOfPics(numofpc);
                picFolders.add(folds);

                //   folds.setFirstPic(datapath);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                //  folds.addpics();
            }
        }
        return picFolders;
    }

    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics)
    {
    }
    @Override
    public void onPicClicked(String pictureFolderPath,String folderName) {
        Intent move = new Intent(MainActivity.this,ImageDisplay.class);
        move.putExtra("folderPath",pictureFolderPath);
        move.putExtra("folderName",folderName);
        //move.putExtra("recyclerItemSize",getCardsOptimalWidth(4));
        startActivity(move);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    private void changeStatusBarColor()
    {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId())
        {
            case R.id.seting:
                Intent intent = new Intent(this,Changepassword.class) ;
                startActivity(intent);
                
                break;
            case R.id.help:
                Toast.makeText(this, "Software Craft Pvt Ltd", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static File commonDocumentDirPath(String FolderName)
    {
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + FolderName);
        }
        else
        {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
        }
        // Make sure the path directory exists.
        if (!dir.exists())
        {
            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
            if (!success)
            {
                dir = null;
            }
        }
        return dir;
    }
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        this.finishAffinity();

    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event)
    {
        if (event == Lifecycle.Event.ON_PAUSE) {

          //  Toast.makeText(this, "ON_PAUSE", Toast.LENGTH_SHORT).show();

        } else if (event == Lifecycle.Event.ON_RESUME)
        {
        if (signal==true)
    {
        Intent in =new Intent(this ,Password.class);
        //Toast.makeText(this, "ON_RESUME", Toast.LENGTH_SHORT).show();
        startActivity(in);
    }
        }
        else if (event == Lifecycle.Event.ON_CREATE) {

          //Toast.makeText(this, "ON_CREATE", Toast.LENGTH_SHORT).show();
        }
        else if (event == Lifecycle.Event.ON_START) {
            Log.e("Lifecycle", "Foreground");

      //    Toast.makeText(this, "ON_START", Toast.LENGTH_SHORT).show();
        }
        else if (event == Lifecycle.Event.ON_STOP) {

            signal=true;
        //    Toast.makeText(this, "ON_STOP", Toast.LENGTH_SHORT).show();
        }

    }
}
