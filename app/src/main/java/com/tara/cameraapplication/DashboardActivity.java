package com.tara.cameraapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.tara.cameraapplication.Document.DocumentScanActivity;
import com.tara.cameraapplication.Qrcode.QrcodeActivity;

public class DashboardActivity extends AppCompatActivity {

    CardView documentscan, qrcodescan;

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        documentscan = findViewById(R.id.documentscan);
        qrcodescan = findViewById(R.id.qrcodescan);

        qrcodescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, QrcodeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
                overridePendingTransition(0,0);
            }
        });

        documentscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, DocumentScanActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_ANIMATION );
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();


            }
        });




    }
}