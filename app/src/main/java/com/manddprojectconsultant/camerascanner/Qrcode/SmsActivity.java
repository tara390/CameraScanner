
package com.manddprojectconsultant.camerascanner.Qrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.textfield.TextInputEditText;
import com.manddprojectconsultant.camerascanner.R;

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

        AudienceNetworkAds.initialize(this);
        adView=new AdView(this,"910335046161931_910339876161448", AdSize.BANNER_HEIGHT_50);
        LinearLayout ads=findViewById(R.id.adsforsms);
        ads.addView(adView);
        adView.loadAd();




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
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( "sms:" + tvphone_no.getText().toString() ) );
                    intent.putExtra( "sms_body", tvmessage.getText().toString() );
                    startActivity(intent);
                  /*  Toast.makeText(getApplicationContext(), "Message Sent",
                            Toast.LENGTH_LONG).show();*/
                } catch (Exception ErrVar) {
                    Toast.makeText(getApplicationContext(), ErrVar.getMessage(),
                            Toast.LENGTH_LONG).show();
                    ErrVar.printStackTrace();
                }


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