package com.tara.cameraapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.textfield.TextInputEditText;

public class SmsActivity extends AppCompatActivity {

    TextInputEditText tvphone_no, tvmessage;
    String phone, message;
    Button btnsms;
    ImageView ivsmsbackpressed;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sms);

        phone = getIntent().getStringExtra("phone");
        message = getIntent().getStringExtra("message");

        MobileAdsview();
        initviews();
        setSms();
        btnsmsclick();





    }

    private void MobileAdsview() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.adsinsmsview);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    private void setSms() {

        tvphone_no.setText(phone);
        tvmessage.setText(message);
    }

    private void btnsmsclick() {

        //Image CLick

        ivsmsbackpressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });







        //Button Click
        btnsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    SmsManager smsMgrVar = SmsManager.getDefault();
                    smsMgrVar.sendTextMessage(tvphone_no.getText().toString(), null, tvmessage.getText().toString(), null, null);
                    Toast.makeText(getApplicationContext(), "Message Sent",
                            Toast.LENGTH_LONG).show();
                } catch (Exception ErrVar) {
                    Toast.makeText(getApplicationContext(), ErrVar.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                    ErrVar.printStackTrace();
                }


            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    private void initviews() {

        //EditTextViews
        tvmessage = findViewById(R.id.tvmessage);
        tvphone_no = findViewById(R.id.tvphone_no);

        //Button
        btnsms = findViewById(R.id.btnsms);

        //ImageView
        ivsmsbackpressed=findViewById(R.id.ivsmsbackpressed);

    }
}