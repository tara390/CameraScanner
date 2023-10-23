
package com.tara.cameraapplication.Qrcode;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.tara.cameraapplication.Barcode.BarcodeActivity;
import com.tara.cameraapplication.R;

import org.jsoup.Jsoup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import static com.otaliastudios.cameraview.controls.Flash.OFF;
import static com.otaliastudios.cameraview.controls.Flash.TORCH;

public class QrcodeActivity extends AppCompatActivity {

    CameraView camera_view;
    boolean isDetected = false;
    String barcode;
    ImageView flashon, gallery, flashoff, btn_start_again;
    TextView tvbarcode;
    int Request_code = 100;
    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;
    LinearLayout linearLayout;

    public static final int RequestPermissionCode = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_qrcode);


        init();
        requestPermission();


    }


    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (Exception e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }


    private void init() {

        //Linear layout for snackbar
        linearLayout = findViewById(R.id.linearLayout);


        //TextView
        tvbarcode = findViewById(R.id.tvbarcode);

        //ImageView
        btn_start_again = findViewById(R.id.btn_start_again);
        flashoff = findViewById(R.id.flashoff);
        flashon = findViewById(R.id.flashon);
        gallery = findViewById(R.id.ivgallery);


        //Camera
        camera_view = findViewById(R.id.cameraviewforqrcode);


        //Image CLick

        flashon.setOnClickListener(v -> {

            camera_view.setFlash(OFF);
            flashoff.setVisibility(View.VISIBLE);
            flashon.setVisibility(View.GONE);

        });

        tvbarcode.setOnClickListener(v -> {
            Intent qrcode = new Intent(this, BarcodeActivity.class);
            qrcode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(qrcode);
            finish();

        });

        flashoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                camera_view.setFlash(TORCH);
                flashon.setVisibility(View.VISIBLE);
                flashoff.setVisibility(View.GONE);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                startActivityForResult(pickIntent, 111);
            }
        });

    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
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
                        barcode = result.getText();

                        //do something with the results for demo i created a popup dialog
                        if (barcode != null) {
                            Intent i = new Intent(this, ResultActivity.class);
                            i.putExtra("message", barcode);
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
                                    Intent i = new Intent(getBaseContext(), QrcodeActivity.class);
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
                    case FirebaseVisionBarcode.TYPE_TEXT:
                    case FirebaseVisionBarcode.TYPE_URL: {

                        createResult(item.getRawValue());
                    }
                    break;

                    case FirebaseVisionBarcode.TYPE_PHONE: {

                        String mobile = item.getPhone().getNumber();
                        Uri number = Uri.parse("tel:" + mobile);
                        Intent dial = new Intent(Intent.ACTION_DIAL);
                        dial.setData(number);
                        dial.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(dial);
                        overridePendingTransition(0, 0);


                    }
                    break;

                    case FirebaseVisionBarcode.TYPE_SMS: {
                        String message = Objects.requireNonNull(item.getSms()).getMessage();
                        String phone = item.getSms().getPhoneNumber();

                        createforsms(message, phone);


                    }
                    break;


                    case FirebaseVisionBarcode.TYPE_GEO: {
                        double lat = Objects.requireNonNull(item.getGeoPoint()).getLat();
                        double longitude = item.getGeoPoint().getLng();
                        createforlocation(lat, longitude);
                    }
                    break;


                    case FirebaseVisionBarcode.TYPE_CONTACT_INFO: {

                        String name, phone, email, website;

                        name = (" " +
                                item.getContactInfo().getName().getFormattedName());

                        phone = (" " +
                                item.getContactInfo().getPhones().get(0).getNumber());


                        email = (" " +
                                item.getContactInfo().getEmails().get(0).getAddress());


                        website = " " +
                                Arrays.toString(item.getContactInfo().getUrls());


                        createforResult(name, phone, email, website);


                    }
                    break;

                    default:
                        createResult(item.getRawValue());
                        break;


                }
            }
        }

    }

    private void createforsms(String message, String phone) {

        Intent sms = new Intent(QrcodeActivity.this, SmsActivity.class);
        sms.putExtra("phone", phone);
        sms.putExtra("message", message);
        sms.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sms);
        overridePendingTransition(0, 0);

    }

    private void createforlocation(double lat, double longitude) {

        Intent geolocation = new Intent(QrcodeActivity.this, GeoLocationActivity.class);
        geolocation.putExtra("lat", lat);
        geolocation.putExtra("longitude", longitude);
        geolocation.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(geolocation);
        overridePendingTransition(0, 0);


    }

    private void createforResult(String name, String phone, String email, String website) {

        Intent profile = new Intent(QrcodeActivity.this, ProfileActivity.class);
        profile.putExtra("name", name);
        profile.putExtra("phone", phone);
        profile.putExtra("email", email);
        profile.putExtra("website", website);
        profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profile);
        overridePendingTransition(0, 0);

    }

    private void createResult(String rawValue) {

        Intent i = new Intent(QrcodeActivity.this, ResultActivity.class);
        i.putExtra("result", rawValue);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(0, 0);


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

     /* Intent i=new Intent(this, DashboardActivity.class);
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NO_ANIMATION);
      startActivity(i);
      overridePendingTransition(0,0);*/
        finish();
        super.onBackPressed();
    }


    @SuppressLint("StaticFieldLeak")
    private class VersionChecker extends AsyncTask<String, String, String> {

        private String newVersion;

        @Override
        protected String doInBackground(String... strings) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName())
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
                return newVersion;

            } catch (IOException e) {
                Log.e("Camerascannerforupdate", "doInBackground: ", e);
                return newVersion;
            }

        }

    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withActivity(this)
                    // below line is use to request the number of permissions which are required in our app.
                    .withPermissions(android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_MEDIA_IMAGES,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.RECORD_AUDIO)
                    // after adding permissions we are calling an with listener method.
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            // this method is called when all permissions are granted
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                // do you work now
                                setupCamera();
                            }
                            // check for permanent denial of any permission
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permanently, we will show user a dialog message.
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            // this method is called when user grants some permission and denies some of them.
                            permissionToken.continuePermissionRequest();
                        }
                    }).withErrorListener(error -> {
                        // we are displaying a toast message for error message.
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    })
                    // below line is use to run the permissions on same thread and to check the permissions
                    .onSameThread().check();

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withActivity(this)
                    // below line is use to request the number of permissions which are required in our app.
                    .withPermissions(android.Manifest.permission.CAMERA,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.RECORD_AUDIO)
                    // after adding permissions we are calling an with listener method.
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            // this method is called when all permissions are granted
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                // do you work now
                                setupCamera();
                            }
                            // check for permanent denial of any permission
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permanently, we will show user a dialog message.
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            // this method is called when user grants some permission and denies some of them.
                            permissionToken.continuePermissionRequest();
                        }
                    }).withErrorListener(error -> {
                        // we are displaying a toast message for error message.
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    })
                    // below line is use to run the permissions on same thread and to check the permissions
                    .onSameThread().check();

        }

    }

    private void showSettingsDialog() {

    }


}