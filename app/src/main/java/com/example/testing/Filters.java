package com.example.testing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;

public class Filters extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    ImageView mainimage, filter1, filter2, filter3, filter4;
    String uriofimage;
    Bitmap imagefilter;
    Bitmap outputImage;
    byte[] byteArray;

    Uri uri,fltruri;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        ActionBar actionBar = getSupportActionBar();
         actionBar.setHomeButtonEnabled(true);
         actionBar.setTitle("Filters");

        Intent i=getIntent();
       uriofimage= i.getStringExtra("filterimage");

            uri = Uri.parse(uriofimage);


        try {
             imagefilter = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainimage=findViewById(R.id.mainimage);
        mainimage.setImageBitmap(imagefilter);

        filter1=findViewById(R.id.filter1);
        filter1.setImageBitmap(imagefilter);

        filter2=findViewById(R.id.filter2);
        filter2.setImageBitmap(imagefilter);

        filter3=findViewById(R.id.filter3);
        filter3.setImageBitmap(imagefilter);

        filter4=findViewById(R.id.filter4);
        filter4.setImageBitmap(imagefilter);


        filter1.setOnClickListener(new View.OnClickListener()
        {
        @Override
        public void onClick(View view) {
            Filter myFilter = new Filter();
            myFilter.addSubFilter(new SaturationSubFilter(1.2f));
            myFilter.addSubFilter(new ContrastSubFilter(1.2f));

            Bitmap image=imagefilter.copy(Bitmap.Config.ARGB_8888,true);
            Bitmap outputImage = myFilter.processFilter(image);
            mainimage.setImageBitmap(outputImage);

            getImageUri(Filters.this,outputImage);

        }
    });
        filter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Filter myFilter = new Filter();
                myFilter.addSubFilter(new SaturationSubFilter(0f));
                myFilter.addSubFilter(new ContrastSubFilter(1.5f));

                Bitmap image=imagefilter.copy(Bitmap.Config.ARGB_8888,true);
                outputImage = myFilter.processFilter(image);
                mainimage.setImageBitmap(outputImage);

                getImageUri(Filters.this,outputImage);
                //will call function here that submit byte array


            }
        });
        filter3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Filter foofilter= SampleFilters.getBlueMessFilter();

                Bitmap image=imagefilter.copy(Bitmap.Config.ARGB_8888,true);
                Bitmap outputImage = foofilter.processFilter(image);
                mainimage.setImageBitmap(outputImage);


            }
        });
        filter4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Filter foofilter= SampleFilters.getLimeStutterFilter();

                Bitmap image=imagefilter.copy(Bitmap.Config.ARGB_8888,true);
                Bitmap outputImage = foofilter.processFilter(image);
                mainimage.setImageBitmap(outputImage);
            }
        });

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        fltruri= Uri.parse(path);
        return fltruri;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId())
        {
            case R.id.Done:

                Intent intent = new Intent( this,PictureEdit.class);
                intent.putExtra("filterresult", fltruri);
                startActivity(intent);
//                Intent intent = new Intent();
//                intent.putExtra("filterresult", fltruri);
//                setResult(78,intent);
//                Filters.super.onBackPressed();

                break;

            case android.R.id.home:
                Intent i = new Intent(Filters.this,PictureEdit.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}