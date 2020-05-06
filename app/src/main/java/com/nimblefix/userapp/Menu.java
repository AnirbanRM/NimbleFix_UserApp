package com.nimblefix.userapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity {

    ImageView scan,history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ((ThisApplication)getApplication()).setCurrentContext(this);

        scan = findViewById(R.id.report);
        history = findViewById(R.id.history);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userReports();
            }
        });
    }

    public void startScan(){
        Intent i = new Intent(this,startScan.class);
        startActivity(i);
    }

    public void userReports(){

    }
}