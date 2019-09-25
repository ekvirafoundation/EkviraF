package com.avcoe.ekvira.ekvirafoundation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //CALLING ACTIVITY LOGIN
//        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//        startActivity(intent);
    }
}
