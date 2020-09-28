package com.tara.cameraapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    String name,phone,email,website,address;
    TextView tvName,tvphone,tvemail,tvwebsite,tvaddress;
    ImageView ivprofilebackpressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name=getIntent().getStringExtra("name");
        phone=getIntent().getStringExtra("phone");
        address=getIntent().getStringExtra("address");
        email=getIntent().getStringExtra("email");
        website=getIntent().getStringExtra("website");
        initviews();
        alldatacapturefromactivity();
        Imagefunction();

    }

    private void Imagefunction() {

        ivprofilebackpressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    private void alldatacapturefromactivity() {

        tvName.setHint(name);
        tvphone.setHint(phone);
        tvaddress.setHint(address);
        tvemail.setHint(email);
        tvwebsite.setText(website);

    }

    private void initviews() {
        tvName=findViewById(R.id.tvname);
        tvphone=findViewById(R.id.tvphone);
        tvaddress=findViewById(R.id.tvaddress);
        tvemail=findViewById(R.id.tvemail);
        tvwebsite=findViewById(R.id.tvwebsite);
        ivprofilebackpressed=findViewById(R.id.ivprofilebackpressed);

    }
}