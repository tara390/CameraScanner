package com.tara.cameraapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.otaliastudios.cameraview.controls.Flash.OFF;
import static com.otaliastudios.cameraview.controls.Flash.TORCH;

public class QrcodeActivity extends AppCompatActivity {

    CameraView camera_view;
    boolean isDetected = false;

    ImageView flashon, gallery, flashoff, btn_start_again, camerafront, cameraback;
    private static final String TAG = "MainActivity";
    TextView tvqrcode, tvbarcode;
    ProgressDialog pDialog;
    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_qrcode);


        init();
        Dexter.withActivity(this).withPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                setupCamera();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();

    }

    private void init() {

        //TextView
        tvbarcode = findViewById(R.id.tvbarcode);
        tvbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent barcode = new Intent(QrcodeActivity.this, BarcodeActivity.class);
                startActivity(barcode);
            }
        });

        //Button
        btn_start_again = findViewById(R.id.btn_start_again);

        //Camera
        camera_view = findViewById(R.id.cameraviewforqrcode);
    }


    private void setupCamera() {

        btn_start_again.setEnabled(isDetected);

        btn_start_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDetected = !isDetected;

            }
        });
        camera_view.setLifecycleOwner(QrcodeActivity.this);
        camera_view.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                processimage(getVisioImagefromframe(frame));
            }
        });

        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE, FirebaseVisionBarcode.FORMAT_DATA_MATRIX, FirebaseVisionBarcode.FORMAT_AZTEC)
                .build();

        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

    }

    private void processimage(FirebaseVisionImage image) {


        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {

                if (!isDetected) {
                    qrcodeResult(firebaseVisionBarcodes);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(QrcodeActivity.this, "error message :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void qrcodeResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {

        if (firebaseVisionBarcodes.size() > 0) {
            isDetected = true;
            btn_start_again.setEnabled(isDetected);
            for (FirebaseVisionBarcode item : firebaseVisionBarcodes) {
                int value = item.getValueType();
                switch (value) {
                    case FirebaseVisionBarcode.TYPE_TEXT: {
                        createResult(item.getRawValue());
                    }
                    break;
                    case FirebaseVisionBarcode.TYPE_URL: {
                        createResult(item.getRawValue());
                    }
                    break;

                    case FirebaseVisionBarcode.TYPE_CONTACT_INFO: {

                        String info = new StringBuilder("First Name:").append(item.getContactInfo().getName().getFirst())
                                .append("\n")
                                .append("Last Name:")
                                .append(item.getContactInfo().getName().getLast())
                                .append("\n")
                                .append(item.getContactInfo().getAddresses().get(0).getAddressLines())
                                .append("\n")
                                .append("Email ID:")
                                .append(item.getContactInfo().getEmails().get(0).getAddress()).toString();
                        createResult(info);


                    }
                    break;

                    default:
                        createResult(item.getRawValue());
                        break;


                }
            }
        }

    }

    private void createResult(String rawValue) {

        Intent i = new Intent(QrcodeActivity.this, ResultActivity.class);
        i.putExtra("result", rawValue);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);


    }

    private FirebaseVisionImage getVisioImagefromframe(Frame frame) {
        byte[] data = frame.getData();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder().setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
                .build();
        return FirebaseVisionImage.fromByteArray(data, metadata);
    }


    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }


}