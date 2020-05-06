package com.nimblefix.userapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    Button signup;
    EditText emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signup = findViewById(R.id.next);
        emailText = findViewById(R.id.emailField);

        ((ThisApplication)getApplication()).setCurrentContext(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

    }

    private void signup() {
        ((ThisApplication)getApplication()).connectToServer(emailText.getText().toString());
    }

    public void showOTPScreen(){
        Intent intent = new Intent(SignUp.this, OTP.class);
        startActivity(intent);
    }

}
