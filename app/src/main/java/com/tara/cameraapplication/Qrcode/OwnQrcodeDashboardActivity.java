package com.tara.cameraapplication.Qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.tara.cameraapplication.databinding.ActivityOwnQrcodeDashboardBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class OwnQrcodeDashboardActivity extends AppCompatActivity {

    ActivityOwnQrcodeDashboardBinding qrcodeDashboardBinding;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qrcodeDashboardBinding = ActivityOwnQrcodeDashboardBinding.inflate(getLayoutInflater());
        setContentView(qrcodeDashboardBinding.getRoot());


        qrcodeDashboardBinding.generateqrcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = qrcodeDashboardBinding.entertextTextview.getText().toString();


                MultiFormatWriter formatWriter = new MultiFormatWriter();

                if (qrcodeDashboardBinding.rbQrcode.isChecked()) {
                    try {
                        BitMatrix matrix = formatWriter.encode(name, BarcodeFormat.QR_CODE, 350, 350);
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        bitmap = encoder.createBitmap(matrix);
                        qrcodeDashboardBinding.displayImageview.setImageBitmap(bitmap);

                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(qrcodeDashboardBinding.entertextTextview.getApplicationWindowToken(), 0);


                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                }
                if (qrcodeDashboardBinding.rbBarcode.isChecked()) {
                    try {
                        BitMatrix matrix = formatWriter.encode(name, BarcodeFormat.CODE_128, 350, 350);
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        bitmap = encoder.createBitmap(matrix);
                        qrcodeDashboardBinding.displayImageview.setImageBitmap(bitmap);

                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(qrcodeDashboardBinding.entertextTextview.getApplicationWindowToken(), 0);


                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        qrcodeDashboardBinding.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveToInternalStorage(bitmap);


            }
        });


    }

    private void saveToInternalStorage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}