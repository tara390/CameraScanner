package com.tara.cameraapplication.Barcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.tara.cameraapplication.R;

public class ResultforbarcodeActivity extends AppCompatActivity {

    String  result;
    TextView tvbarcoderesult;
    ImageView ivbarcodebackpressed;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultforbarcode);

       /* getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/

        result = getIntent().getStringExtra("barcode");

        initforbarcode();
        MobileAdsview();
        setOnClick();

        tvbarcoderesult.setText(result);


    }




    private void MobileAdsview() {


        AdView adView=new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-8674673470489334/2613331722");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adsbarocdeview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public void onBackPressed() {
        Intent onback=new Intent(this,BarcodeActivity.class);
        onback.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(onback);
        overridePendingTransition(0, 0);


        super.onBackPressed();
    }

    private void setOnClick() {


        tvbarcoderesult.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(tvbarcoderesult.getText().toString());
                Toast.makeText(getApplicationContext(), "Copied", Toast.LENGTH_SHORT).show();
                tvbarcoderesult.setTextIsSelectable(true);
                return true;
            }
        });




        ivbarcodebackpressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }



    private void initforbarcode() {

        ivbarcodebackpressed=findViewById(R.id.ivbarcodebackpressed);
        tvbarcoderesult=findViewById(R.id.tvbarcoderesult);


    }
}