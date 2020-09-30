package com.tara.cameraapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
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
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

import static com.otaliastudios.cameraview.controls.Flash.OFF;
import static com.otaliastudios.cameraview.controls.Flash.TORCH;

public class BarcodeActivity extends AppCompatActivity {

    TextView qrcode, barcode;
    String barcod;

    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;
    boolean isDetected = false;
    CameraView cameraView;
    ImageView button_start_again, gallery, imageflashoff, imageflashon;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barcode);


        init();

        detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {


                        setupCameraforbarcode();
                        //onBackPressed();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();


    }

    private void setupCameraforbarcode() {

        button_start_again.setEnabled(isDetected);
        button_start_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDetected = !isDetected;
            }
        });

        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_CODE_128,
                        FirebaseVisionBarcode.FORMAT_EAN_8,
                        FirebaseVisionBarcode.FORMAT_UPC_E,
                        FirebaseVisionBarcode.TYPE_ISBN,
                        FirebaseVisionBarcode.FORMAT_ITF,
                        FirebaseVisionBarcode.FORMAT_CODE_39,
                        FirebaseVisionBarcode.FORMAT_CODE_93,
                        FirebaseVisionBarcode.FORMAT_EAN_13,
                        FirebaseVisionBarcode.FORMAT_UPC_A)
                .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);


        cameraView.setLifecycleOwner(this);
        cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                processImage(getVisionImageFromFrame(frame));

            }

        });


    }

    private void processImage(FirebaseVisionImage visionImageFromFrame) {


        detector.detectInImage(visionImageFromFrame)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        if (!isDetected) {
                            processResult(firebaseVisionBarcodes);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BarcodeActivity.this, "error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        if (firebaseVisionBarcodes.size() > 0) {
            isDetected = true;
            button_start_again.setEnabled(isDetected);


            for (FirebaseVisionBarcode item : firebaseVisionBarcodes) {
                int value_type = item.getValueType();
                switch (value_type) {
                    case FirebaseVisionBarcode.TYPE_TEXT: {
                        createDialog(item.getRawValue());
                    }
                    break;
                    case FirebaseVisionBarcode.TYPE_URL: {
                        //start browser intent
                        createDialog(item.getRawValue());
                    }
                    break;
                    case FirebaseVisionBarcode.TYPE_CONTACT_INFO: {
                        String info = new StringBuilder("Name:")
                                .append(item.getContactInfo().getName().getFormattedName())
                                .append("\n")
                                .append("Mobile No")
                                .append(item.getContactInfo().getPhones().get(0).getNumber())
                                .append("\n")
                                .append("Address")
                                .append(item.getContactInfo().getAddresses().get(0).getAddressLines())
                                .append("\n")
                                .append("Email:")
                                .append(item.getContactInfo().getEmails().get(0).getAddress())
                                .toString();
                        createDialog(info);


                    }

                    break;
                    default: {
                        createDialog(item.getRawValue());
                    }
                    break;

                }


            }
        }
    }


    private void createDialog(String rawValue) {


        Intent intent = new Intent(BarcodeActivity.this, ResultActivity.class);
        intent.putExtra("result", rawValue);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
                // .setRotation(frame.getRotation())
                .build();
        return FirebaseVisionImage.fromByteArray(data, metadata);
    }

    private void init() {

        button_start_again = findViewById(R.id.button_start_again);
        gallery = findViewById(R.id.imagegallery);

        imageflashoff = findViewById(R.id.imageflashoff);
        imageflashon = findViewById(R.id.imageflashon);

        imageflashoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.setFlash(TORCH);
                imageflashon.setVisibility(View.VISIBLE);
                imageflashoff.setVisibility(View.GONE);
            }
        });

        imageflashon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cameraView.setFlash(OFF);
                imageflashoff.setVisibility(View.VISIBLE);
                imageflashon.setVisibility(View.GONE);

            }
        });


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra("crop", "true");

                intent.putExtra("aspectX", 0);
                intent.putExtra("aspectY", 0);
                intent.putExtra("return-data", true);
                startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"),
                        111);

            }
        });

        cameraView = findViewById(R.id.cameraviewforbarcode);


        qrcode = findViewById(R.id.qrcode);
        barcode = findViewById(R.id.barcode);

        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BarcodeActivity.this, QrcodeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 111:
                if (resultCode == RESULT_OK) {
//doing some uri parsing
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        //getting the image
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    //decoding bitmap
                    Bitmap bMap = BitmapFactory.decodeStream(imageStream);
                    //Scan.setImageURI(selectedImage);// To display selected image in image view
                    int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                    // copy pixel data from the Bitmap into the 'intArray' array
                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                            bMap.getHeight());

                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                            bMap.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                    Reader reader = new MultiFormatReader();// use this otherwise
                    // ChecksumException
                    try {
                        Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
                        decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                        decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

                        Result result = reader.decode(bitmap, decodeHints);
                        //*I have created a global string variable by the name of barcode to easily manipulate data across the application*//
                        barcod = result.getText();

                        //do something with the results for demo i created a popup dialog
                        if (barcod != null) {
                            Intent i = new Intent(this, ResultActivity.class);
                            i.putExtra("message", barcod);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Scan Result");
                            builder.setIcon(R.mipmap.ic_launcher);
                            builder.setMessage("Nothing found try a different image or try again");
                            AlertDialog alert1 = builder.create();
                            alert1.setButton(DialogInterface.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getBaseContext(), BarcodeActivity.class);
                                    startActivity(i);
                                }
                            });

                            alert1.setCanceledOnTouchOutside(false);

                            alert1.show();

                        }
                        //the end of do something with the button statement.

                    } catch (NotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Nothing Found", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (ChecksumException e) {
                        Toast.makeText(getApplicationContext(), "Something weird happen, i was probably tired to solve this issue", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (FormatException e) {
                        Toast.makeText(getApplicationContext(), "Wrong Barcode/QR format", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Toast.makeText(getApplicationContext(), "Something weird happen, i was probably tired to solve this issue", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
        }
    }


}