package com.example.testing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hanks.passcodeview.PasscodeView;

public class Password extends AppCompatActivity {
    PasscodeView passcode;
    EditText passen;
    Button pasv;
    String thepasscode;
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME = "mypass";
    private static final String KEY_PASS = "passcode";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        getSupportActionBar().hide();

        passen=findViewById(R.id.pass);
        pasv = findViewById(R.id.passvery);



        sharedPreferences  = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String pascode =sharedPreferences.getString(KEY_PASS,null);

        if(pascode!=null)
        {
            thepasscode=pascode;
        }

        pasv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String pas=passen.getText().toString();

                if(thepasscode.equals(pas))
                {
                    Intent in = new Intent(Password.this, MainActivity.class);
                    startActivity(in);

                }
                else
                {

                    passen.setError("Wrong PassCode");
                    passen.setText("");

                }
            }
        });



        }

    }
