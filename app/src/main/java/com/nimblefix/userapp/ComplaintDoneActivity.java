package com.nimblefix.userapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nimblefix.ControlMessages.AboutInventoryMessage;
import com.nimblefix.ControlMessages.ComplaintMessage;

public class ComplaintDoneActivity extends AppCompatActivity {

    TextView comp_id,comp_dt,ititle,iID,oName,idescription,remarks;
    ImageView status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_done);

        comp_id = findViewById(R.id.complaint_id);
        comp_dt = findViewById(R.id.complaint_dt);
        ititle = findViewById(R.id.title);
        iID = findViewById(R.id.inv);
        oName = findViewById(R.id.org);
        idescription = findViewById(R.id.desc);
        remarks = findViewById(R.id.remarks);
        status = findViewById(R.id.icon);

        Intent i = getIntent();
        final ComplaintMessage complaintMessage = (ComplaintMessage) i.getSerializableExtra("DATA");
        final AboutInventoryMessage aboutInventoryMessage = new AboutInventoryMessage(complaintMessage.getComplaint().getOrganizationID(),complaintMessage.getComplaint().getInventoryID());

        new Thread(new Runnable() {
            @Override
            public void run() {
                ((ThisApplication)getApplication()).mobileClient.writeObject(aboutInventoryMessage);
                final AboutInventoryMessage response = (AboutInventoryMessage) ((ThisApplication)getApplication()).mobileClient.readNext();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ComplaintDoneActivity.this.showStatus(response,complaintMessage);
                    }
                });
            }
        }).start();
    }

    private void showStatus(AboutInventoryMessage aboutInventoryMessage, ComplaintMessage complaintMessage){
        ititle.setText(aboutInventoryMessage.getTitle());
        iID.setText(aboutInventoryMessage.getId());
        oName.setText(aboutInventoryMessage.getOrg());
        idescription.setText(aboutInventoryMessage.getDescription());
        comp_dt.setText(complaintMessage.getComplaint().getComplaintDateTime());
        remarks.setText(complaintMessage.getComplaint().getUserRemarks());

        if(complaintMessage.getBody().equals("SUCCESS")) {
            status.setImageDrawable(getResources().getDrawable(R.drawable.green_tick, null));
            comp_id.setText("Complaint ID: " + complaintMessage.getComplaint().getComplaintID());
        }
        else{
            status.setImageDrawable(getResources().getDrawable(R.drawable.red_cross, null));
            comp_id.setText("Complaint Unsuccessful");
        }
    }
}
