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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import com.facebook.ads.AudienceNetworkAds;*/
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.tara.cameraapplication.R;

public class ResultforbarcodeActivity extends AppCompatActivity {

    String  result;
    TextView tvbarcoderesult;
    ImageView ivbarcodebackpressed;
   // AdView mAdView;

    //AdView madview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultforbarcode);

       /* getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/


        result = getIntent().getStringExtra("barcode");

        initforbarcode();


        //madview=findViewById(R.id.adsforbarcode);

        MobileAdsview();
        setOnClick();

        tvbarcoderesult.setText(result);


    }




    private void MobileAdsview() {

      /*  AudienceNetworkAds.initialize(this);
        mAdView=new AdView(this,"910335046161931_910339876161448", AdSize.BANNER_HEIGHT_50);
        LinearLayout ads=findViewById(R.id.adsforbarcode);
        ads.addView(mAdView);
        mAdView.loadAd();*/

       /* MobileAds.initialize(this, "ca-app-pub-8674673470489334~6991560436");
        AdRequest adRequest = new AdRequest.Builder().build();
        madview.loadAd(adRequest);*/



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