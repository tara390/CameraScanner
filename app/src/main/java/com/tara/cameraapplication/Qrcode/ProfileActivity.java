package com.tara.cameraapplication.Qrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.tara.cameraapplication.R;

public class ProfileActivity extends AppCompatActivity {

    String name,phone,email,website;
    TextView tvName,tvphone,tvemail,tvwebsite;
    ImageView ivprofilebackpressed;
    AdView adsforprofile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name=getIntent().getStringExtra("name");
        phone=getIntent().getStringExtra("phone");
        email=getIntent().getStringExtra("email");
        website=getIntent().getStringExtra("website");

        MobileAdsforprofile();
        initviews();
        alldatacapturefromactivity();
        Imagefunction();

    }

    private void MobileAdsforprofile() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adsforprofile = findViewById(R.id.adsinprofileview);
        AdRequest adRequest = new AdRequest.Builder().build();
        adsforprofile.loadAd(adRequest);

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
        tvemail.setHint(email);
        tvwebsite.setText(website);

    }

    private void initviews() {
        tvName=findViewById(R.id.tvname);
        tvphone=findViewById(R.id.tvphone);
        tvemail=findViewById(R.id.tvemail);
        tvwebsite=findViewById(R.id.tvwebsite);
        ivprofilebackpressed=findViewById(R.id.ivprofilebackpressed);

    }
}