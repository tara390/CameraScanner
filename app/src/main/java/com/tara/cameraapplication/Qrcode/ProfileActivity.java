package com.tara.cameraapplication.Qrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
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

        AudienceNetworkAds.initialize(this);
        adsforprofile=new AdView(this,"910335046161931_910339876161448", AdSize.BANNER_HEIGHT_50);
        LinearLayout ads=findViewById(R.id.adsforprofile);
        ads.addView(adsforprofile);
        adsforprofile.loadAd();


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
        Intent onback=new Intent(this, QrcodeActivity.class);
        onback.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(onback);
        overridePendingTransition(0, 0);
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