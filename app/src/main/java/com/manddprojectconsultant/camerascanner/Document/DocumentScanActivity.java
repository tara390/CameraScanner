package com.manddprojectconsultant.camerascanner.Document;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.otaliastudios.cameraview.CameraView;
import com.manddprojectconsultant.camerascanner.R;

import static com.otaliastudios.cameraview.controls.Flash.OFF;
import static com.otaliastudios.cameraview.controls.Flash.TORCH;

public class DocumentScanActivity extends AppCompatActivity {
    CameraView cameraviewfordocument;
    ImageView ivflashoff,ivflashon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_document_scan);

        init();




    }

    private void init() {

        ivflashon = findViewById(R.id.ivflashon);
        ivflashoff = findViewById(R.id.ivflashoff);
        cameraviewfordocument=findViewById(R.id.cameraviewfordocument);

        ivflashon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cameraviewfordocument.setFlash(OFF);
                ivflashoff.setVisibility(View.VISIBLE);
                ivflashon.setVisibility(View.GONE);
            }
        });

        ivflashoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraviewfordocument.setFlash(TORCH);
                ivflashon.setVisibility(View.VISIBLE);
                ivflashoff.setVisibility(View.GONE);
            }
        });
    }
}