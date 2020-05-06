package com.nimblefix.userapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.nimblefix.ControlMessages.AccountUpdationMessage;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class userinfo extends AppCompatActivity {

    CircleImageView dp;
    ImageButton img_add_but;
    Button next;
    EditText Name;
    Uri cropper_image_uri;
    Bitmap DP_bmp=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        dp = findViewById(R.id.dp);
        img_add_but = findViewById(R.id.add);
        Name = findViewById(R.id.NameField);
        next = findViewById(R.id.next);
        img_add_but.setOnClickListener(image_add_clicked);
        next.setOnClickListener(next_clicked);

        try {
            DP_bmp = savedInstanceState.getParcelable("DP_IMG");
            if(DP_bmp!=null)
                dp.setImageBitmap(DP_bmp);
        }catch(Exception e){;}

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(DP_bmp!=null)
            outState.putParcelable("DP_IMG",DP_bmp);
        super.onSaveInstanceState(outState);
    }

    View.OnClickListener image_add_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            requestPermission();
            Intent i = new Intent(userinfo.this, dp_opt_dialog.class);
            startActivityForResult(i, 1000);
        }
    };

    View.OnClickListener next_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(Name.getText().toString().length()==0){
                Toast.makeText(userinfo.this,"Name cannot be blank.",Toast.LENGTH_SHORT).show(); return;}

            String dp_base64_temp="";
            if(DP_bmp!=null) {
                ByteArrayOutputStream byteA = new ByteArrayOutputStream();
                DP_bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteA);
                dp_base64_temp = Base64.encodeToString(byteA.toByteArray(), Base64.DEFAULT);
            }

            final String finalDp_base64_temp = dp_base64_temp;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ((ThisApplication)getApplication()).mobileClient.writeObject(new AccountUpdationMessage(((ThisApplication)getApplication()).getUserID(),Name.getText().toString(), finalDp_base64_temp));
                    Object o = ((ThisApplication)getApplication()).mobileClient.readNext();
                    if(o instanceof AccountUpdationMessage){
                        savetoLocalPref(Name.getText().toString(),((ThisApplication)getApplication()).getUserID(), ((AccountUpdationMessage) o).getToken(), finalDp_base64_temp);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(userinfo.this,Menu.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        });
                    }
                }
            }).start();
        }
    };

    private void savetoLocalPref(String name, String userID, String token, String finalDp_base64_temp) {

        SharedPreferences shrdPref = getSharedPreferences("NimbleFixAppData",MODE_PRIVATE);
        SharedPreferences.Editor shrdPrefsEditor = shrdPref.edit();

        shrdPrefsEditor.putString("ID",userID);
        shrdPrefsEditor.putString("NAME",name);
        shrdPrefsEditor.putString("TOKEN",token);
        shrdPrefsEditor.putString("DP",finalDp_base64_temp);

        shrdPrefsEditor.apply();
    }

    public void setName(String n){
        final String nam = n;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Name.setText(nam);
            }
        });
    }

    public void setDP(Bitmap bmp){
        final Bitmap b = bmp;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dp.setImageBitmap(b);
            }
        });
    }

    private void requestPermission(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 15);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 15: {
                for (int i : grantResults)
                    if (i == PackageManager.PERMISSION_GRANTED) {
                    } else {
                    }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1000){
            if(resultCode == Activity.RESULT_OK){
                if(data!=null) {
                    if (data.getStringExtra("ACTION").equals("CAM"))
                        open_camera();
                    if (data.getStringExtra("ACTION").equals("STO"))
                        open_storage();
                    if (data.getStringExtra("ACTION").equals("REM")) {
                        dp.setImageResource(R.drawable.person);
                        DP_bmp=null;
                    }
                }
            }
        }

        if(requestCode==200){
            Uri uri = Uri.parse(temp);
            File file = getImageFile();
            cropper_image_uri = Uri.fromFile(file);
            UCrop.Options opts = new UCrop.Options();
            opts.setCircleDimmedLayer(true);
            opts.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            opts.setToolbarColor(Color.parseColor("#F1E0FD"));
            opts.withMaxResultSize(500,500);
            opts.setCompressionQuality(100);
            opts.withAspectRatio(1,1);
            opts.setToolbarTitle("Crop your image");
            opts.setRootViewBackgroundColor(Color.parseColor("#F1E0FD"));
            opts.setStatusBarColor(Color.parseColor("#9907F5"));
            UCrop.of(uri,cropper_image_uri).withOptions(opts).start(this);
        }

        if(requestCode==100) {
            if(resultCode == Activity.RESULT_OK) {
                if(data!=null) {
                    Uri srcuri =data.getData();
                    File file = getImageFile();
                    cropper_image_uri = Uri.fromFile(file);
                    UCrop.Options opts = new UCrop.Options();
                    opts.setCircleDimmedLayer(true);
                    opts.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                    opts.setToolbarColor(Color.parseColor("#F1E0FD"));
                    opts.withMaxResultSize(500,500);
                    opts.setCompressionQuality(100);
                    opts.withAspectRatio(1,1);
                    opts.setToolbarTitle("Crop your image");
                    opts.setRootViewBackgroundColor(Color.parseColor("#F1E0FD"));
                    opts.setStatusBarColor(Color.parseColor("#9907F5"));
                    UCrop.of(srcuri,cropper_image_uri).withOptions(opts).start(this);
                }
            }
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            try {
                DP_bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), UCrop.getOutput(data));
                dp.setImageBitmap(DP_bmp);
            }catch(Exception e){ Log.e("LOL",e.getMessage());}
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    String temp;
    private File getImageFile() {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(getCacheDir(),"image");
        if(!storageDir.exists())storageDir.mkdirs();
        File file=null;
        try{
            file = File.createTempFile(imageFileName, ".jpg", storageDir);}catch(IOException e){ Log.e("TEST",e.toString());}
        temp = "file:" + file.getAbsolutePath();
        return file;
    }

    private void open_camera(){
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile();
        Uri uri = FileProvider.getUriForFile(this , BuildConfig.APPLICATION_ID.concat(".provider"), file);
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(i , 200);
    }
    private void open_storage(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Choose your picture"),100);
    }

}
