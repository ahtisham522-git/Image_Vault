package com.example.testing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Changepassword extends AppCompatActivity implements LifecycleEventObserver {
    EditText pincode;
    Button setcode;
    SharedPreferences sharedPreferences;
    Boolean signal =false;
    private static final String SHARED_PREF_NAME = "mypass";
    private static final String KEY_PASS = "passcode";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        getSupportActionBar().hide();

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String pascode =sharedPreferences.getString(KEY_PASS,null);

        pincode = findViewById(R.id.ent1);
        setcode = findViewById(R.id.pascodeok);
        pincode.addTextChangedListener(textWatcher);

        setcode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String p1 = "";
                p1= pincode.getText().toString();
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(KEY_PASS,p1);
                editor.apply();
                Toast.makeText(Changepassword.this,"Password Changed with "+p1,Toast.LENGTH_LONG).show();

            }
        });
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

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_PAUSE) {
          //  Log.e("Lifecycle", "ON_PAUSE");

        } else if (event == Lifecycle.Event.ON_RESUME) {
        //    Log.e("Lifecycle", "Foreground");

            if (signal==true)
            {
                Intent in =new Intent(this ,Password.class);
                startActivity(in);
            }
        }
        else if (event == Lifecycle.Event.ON_STOP) {
       //     Log.e("Lifecycle", "Foreground");

            signal=true;
            //    Toast.makeText(this, "ON_STOP", Toast.LENGTH_SHORT).show();
        }

    }

    }




