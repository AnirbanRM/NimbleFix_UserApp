package com.nimblefix.userapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nimblefix.ControlMessages.AboutInventoryMessage;
import com.nimblefix.ControlMessages.ComplaintMessage;
import com.nimblefix.core.Complaint;

import java.util.Date;

public class ComplaintActivity extends AppCompatActivity {

    TextView id,title,desc,org;
    Button next;

    String orgID;
    String invID;

    EditText remarks_box;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        id = findViewById(R.id.data);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        org = findViewById(R.id.org);
        next = findViewById(R.id.submit_c_b);
        remarks_box = findViewById(R.id.Complaint_box);

        next.setOnClickListener(doComplaint);

        Intent i = getIntent();
        final String DATA = i.getStringExtra("DATA");
        id.setText(DATA);

        orgID = DATA.split("/")[0].trim();
        invID = DATA.split("/")[1].trim();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] t = DATA.split("/");
                ((ThisApplication)getApplication()).mobileClient.writeObject(new AboutInventoryMessage(t[0].trim(),t[1].trim()));
                final AboutInventoryMessage o = (AboutInventoryMessage) ((ThisApplication)getApplication()).mobileClient.readNext();
                if(o.getTitle()!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            title.setText(o.getTitle());
                            desc.setText(o.getDescription());
                            org.setText(o.getOrg());
                        }
                    });
                }
                else{
                    finish();
                }
            }
        }).start();
    }

    View.OnClickListener doComplaint = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Complaint complaint = new Complaint(orgID,invID, ((ThisApplication)getApplication()).getUserID(), remarks_box.getText().toString().length()==0?"No remarks":remarks_box.getText().toString(), Complaint.getDTString(new Date()));
            final ComplaintMessage complaintMessage = new ComplaintMessage(complaint);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ((ThisApplication)getApplication()).mobileClient.writeObject(complaintMessage);
                    ComplaintMessage message = (ComplaintMessage) ((ThisApplication)getApplication()).mobileClient.readNext();

                    if(message!=null){
                        final Intent i = new Intent(ComplaintActivity.this, ComplaintDoneActivity.class);
                        i.putExtra("DATA",message);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(i);
                            }
                        });
                    }
                }
            }).start();
        }
    };
}
