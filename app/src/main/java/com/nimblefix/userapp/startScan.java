package com.nimblefix.userapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Collections;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class startScan extends AppCompatActivity {
    Button flash;
    ZXingScannerView scanner_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_scan);
        flash = findViewById(R.id.flash);

        ((ThisApplication)getApplication()).setCurrentContext(this);

        scanner_view = findViewById(R.id.qrScanner);

        if(!checkPermission())
            return;

        scanner_view.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        scanner_view.setAutoFocus(true);
        scanner_view.setLaserEnabled(false);

        scanner_view.setMaskColor(Color.parseColor("#66481380"));
        scanner_view.setBorderColor(Color.parseColor("#481380"));
        scanner_view.setBorderStrokeWidth(10);
        scanner_view.setMinimumHeight(100);
        scanner_view.setBorderCornerRadius(20);

        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner_view.setFlash(!scanner_view.getFlash());
            }
        });

        scanner_view.setResultHandler(new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                Intent i = new Intent(startScan.this,ComplaintActivity.class);
                i.putExtra("DATA",result.getText());
                startActivity(i);
            }
        });
    }

    private boolean checkPermission() {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) != PackageManager.PERMISSION_GRANTED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA},10);
                if(ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) != PackageManager.PERMISSION_GRANTED)return false;
                else{
                    scanner_view.startCamera();
                    return true;}
            }
        scanner_view.startCamera();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i : grantResults){
            if(i==PermissionChecker.PERMISSION_DENIED){
                Log.e("NOT GRNNTED","LOL");
            }
            else
                scanner_view.startCamera();
        }
    }
}
