package com.example.testing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.hanks.passcodeview.PasscodeView;

public class Password extends AppCompatActivity {
    PasscodeView passcode;
    String thepasscode;
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME = "mypass";
    private static final String KEY_PASS = "passcode";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        getSupportActionBar().hide();


        sharedPreferences  = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String pascode =sharedPreferences.getString(KEY_PASS,null);

        if(pascode!=null)
        {
            thepasscode=pascode;
        }

            passcode = findViewById(R.id.passcodeView);
            passcode.setPasscodeLength(4).setLocalPasscode(thepasscode)
                    .setListener(new PasscodeView.PasscodeViewListener() {
                        public void onFail() {
                            //if password is wrong
                        }

                        @Override
                        public void onSuccess(String number) {
                            String rue = "tru";

                            Intent in = new Intent(Password.this, MainActivity.class);
                            startActivity(in);

                        }
                    });


        }

    }
