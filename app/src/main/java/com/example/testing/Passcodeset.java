package com.example.testing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class Passcodeset extends AppCompatActivity {
    EditText pincode,cpincode;
    Button setcode;
    String passcodeset ;
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME = "mypass";
    private static final String KEY_PASS = "passcode";

    String prevStarted = "yes";
//    @Override
//    protected void onResume() {
//        super.onResume();
//        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
//        if (!sharedpreferences.getBoolean(prevStarted, false)) {
//            SharedPreferences.Editor editor = sharedpreferences.edit();
//            editor.putBoolean(prevStarted, Boolean.TRUE);
//            editor.apply();
//        } else
//            {
//            moveToSecondary();
//        }
//    }
//
//    private void moveToSecondary()
//    {
//        Intent intent = new Intent(this,Password.class);
//        startActivity(intent);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String pascode =sharedPreferences.getString(KEY_PASS,null);

        if(pascode!=null)
        {
            Intent i = new Intent(Passcodeset.this, Password.class);
            startActivity(i);
        }

        else{
            setContentView(R.layout.activity_passcodeset);

        pincode = findViewById(R.id.pst1);
        cpincode = findViewById(R.id.pst2);
        setcode = findViewById(R.id.pascodesetbtn);

        pincode.addTextChangedListener(textWatcher);
        cpincode.addTextChangedListener(textWatcher);

        setcode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String p1 = "";
                String p2 = "";

               p1=pincode.getText().toString();
               p2=cpincode.getText().toString();
               if(p1.equals(p2))
               {
                   passcodeset=p2;
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(KEY_PASS,passcodeset);
                    editor.apply();
                   Intent i = new Intent(Passcodeset.this, Password.class);
                   startActivity(i);
               }
               else
                   {
                       Toast.makeText(Passcodeset.this,"Password Not Matched",Toast.LENGTH_LONG).show();

               }

            }
        });

        }

    }



    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // get the content of both the edit text
            String passwordInput = pincode.getText().toString();
            setcode.setEnabled(!passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };



}