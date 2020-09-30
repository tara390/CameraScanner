package com.tara.cameraapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class ResultActivity extends AppCompatActivity {
    TextView tvresult;
    String result, message;
    ImageView ivweb, ivtext;
    AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        result = getIntent().getStringExtra("result");
        message = getIntent().getStringExtra("message");

        init();
        MobileAdsview();

    }

    private void MobileAdsview() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adsview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    private void init() {
        ivweb = findViewById(R.id.ivweb);
        ivtext = findViewById(R.id.ivtext);
        //btncopytext=findViewById(R.id.btn_copytext);

        tvresult = findViewById(R.id.tvresult);
        ImageView ivbackpressed = findViewById(R.id.ivbackpressed);
        if (message != null) {
            tvresult.setHint(message);


        } else {

            tvresult.setText(result);
        }
        tvresult.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(tvresult.getText().toString());
                Toast.makeText(getApplicationContext(), "Copied", Toast.LENGTH_SHORT).show();
                tvresult.setTextIsSelectable(true);
                return true;
            }
        });


        ivbackpressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}