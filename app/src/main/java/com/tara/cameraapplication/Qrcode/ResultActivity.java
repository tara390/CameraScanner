package com.tara.cameraapplication.Qrcode;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import com.facebook.ads.Ad;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkActivity;
import com.facebook.ads.AudienceNetworkAds;*/
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.tara.cameraapplication.R;

public class ResultActivity extends AppCompatActivity {
    TextView tvresult;
    String result, message;
    ImageView ivweb, ivtext;
   // AdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        result = getIntent().getStringExtra("result");
        message = getIntent().getStringExtra("message");

        //adView=findViewById(R.id.adsforresult);
        init();
        MobileAdsview();

    }

    @Override
    protected void onDestroy() {

       /* if (adView!=null){
            adView.destroy();
        }*/
        super.onDestroy();
    }

    private void MobileAdsview() {

       /* AudienceNetworkAds.initialize(this);
        adView=new AdView(this,"910335046161931_910339876161448", AdSize.BANNER_HEIGHT_50);
        LinearLayout ads=findViewById(R.id.adsforresult);
        ads.addView(adView);
        adView.loadAd();*/

       /* MobileAds.initialize(this, "ca-app-pub-8674673470489334~6991560436");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);*/




    }


    private void init() {
        ivtext = findViewById(R.id.ivtext);
        //btncopytext=findViewById(R.id.btn_copytext);


        tvresult = findViewById(R.id.tvresult);
        ImageView ivbackpressed = findViewById(R.id.ivbackpressed);
        if (message != null) {
            tvresult.setText(message);


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
        Intent onback=new Intent(this, QrcodeActivity.class);
        onback.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        onback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(onback);
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }
}