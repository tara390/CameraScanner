package com.tara.cameraapplication.Qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.tara.cameraapplication.databinding.ActivityQRCODEDashboardBinding;

public class QRCODEDashboardActivity extends AppCompatActivity {


    ActivityQRCODEDashboardBinding qrcodeDashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qrcodeDashboardBinding = ActivityQRCODEDashboardBinding.inflate(getLayoutInflater());
        setContentView(qrcodeDashboardBinding.getRoot());


        qrcodeDashboardBinding.ownqrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent own = new Intent(QRCODEDashboardActivity.this, OwnQrcodeDashboardActivity.class);
                own.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(own);
                finish();
                overridePendingTransition(0, 0);


            }
        });


        qrcodeDashboardBinding.scanqrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(QRCODEDashboardActivity.this, QrcodeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);

            }
        });


    }
}