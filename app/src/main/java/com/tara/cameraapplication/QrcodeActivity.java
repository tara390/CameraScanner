package com.tara.cameraapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jsoup.Jsoup;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.otaliastudios.cameraview.controls.Flash.OFF;
import static com.otaliastudios.cameraview.controls.Flash.TORCH;

public class QrcodeActivity extends AppCompatActivity {

    CameraView camera_view;
    boolean isDetected = false;
    String barcode;
    ImageView flashon, gallery, flashoff, btn_start_again;
    TextView tvbarcode;
    private static final int SELECT_PHOTO = 100;
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

        if (CheckingPermissionIsEnabledOrNot()) {
            //Entered in if Permission is granted
            checkupdateproject();

            setupCamera();


            Snackbar snackbar = Snackbar
                    .make(linearLayout, "Please rate our application ", Snackbar.LENGTH_LONG)
                    .setAction("Go", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            launchMarket();

                        }
                    });

            snackbar.setDuration(8000);
            snackbar.setActionTextColor(getColor(R.color.colorblue));
            snackbar.show();


            tvbarcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent barcode = new Intent(QrcodeActivity.this, BarcodeActivity.class);
                    barcode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(barcode);
                }
            });
        } else {

            //Calling method to enable permission.
            RequestMultiplePermission();

        }
/*
        Dexter.withActivity(this).withPermissions(Manifest.permission.CAMERA,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                setupCamera();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();*/


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


    private void checkupdateproject() {


   /*     VersionChecker versionChecker = new VersionChecker();
        try
        {   String appVersionName = BuildConfig.VERSION_NAME;
            String mLatestVersionName = versionChecker.execute().get();
            if(!appVersionName.equals(mLatestVersionName)){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(QrcodeActivity.this);
                alertDialog.setTitle("Please update your app");
                alertDialog.setMessage("This app version is no longer supported. Please update your app from the Play Store.");
                alertDialog.setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });
                alertDialog.show();
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
   */


        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(QrcodeActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, QrcodeActivity.this, 11);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    private void init() {

        //Linear layout for snackbar
        linearLayout=findViewById(R.id.linearLayout);


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

        flashon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                camera_view.setFlash(OFF);
                flashoff.setVisibility(View.VISIBLE);
                flashon.setVisibility(View.GONE);

            }
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
                pickIntent.setDataAndType( android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                startActivityForResult(pickIntent, 111);        }
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
                        barcode = result.getText();

                        //do something with the results for demo i created a popup dialog
                        if (barcode != null) {
                            Intent i = new Intent(this, ResultActivity.class);
                            i.putExtra("message", barcode);
                            startActivity(i);

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
                        startActivity(dial);

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
        startActivity(sms);

    }

    private void createforlocation(double lat, double longitude) {

        Intent geolocation = new Intent(QrcodeActivity.this, GeoLocationActivity.class);
        geolocation.putExtra("lat", lat);
        geolocation.putExtra("longitude", longitude);
        startActivity(geolocation);

    }

    private void createforResult(String name, String phone,  String email, String website) {

        Intent profile = new Intent(QrcodeActivity.this, ProfileActivity.class);
        profile.putExtra("name", name);
        profile.putExtra("phone", phone);
        profile.putExtra("email", email);
        profile.putExtra("website", website);
        startActivity(profile);

    }

    private void createResult(String rawValue) {

        Intent i = new Intent(QrcodeActivity.this, ResultActivity.class);
        i.putExtra("result", rawValue);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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


   /* @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        //   finish();
        super.onBackPressed();
    }*/





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

    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        //int  = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourthPermissionResult == PackageManager.PERMISSION_GRANTED;
               // FifthPermissionResult == PackageManager.PERMISSION_GRANTED;
    }


    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(QrcodeActivity.this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        ACCESS_FINE_LOCATION,
                        RECORD_AUDIO
                }, RequestPermissionCode);

        Snackbar snackbar = Snackbar
                .make(linearLayout, "Please rate our application ", Snackbar.LENGTH_LONG)
                .setAction("Go", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        launchMarket();

                    }
                });

        snackbar.setDuration(8000);
        snackbar.setActionTextColor(getColor(R.color.colorblue));
        snackbar.show();


    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED
               ) {
            // now, you have permission go ahead

           /* Intent reOpen = new Intent (QrcodeActivity.this, QrcodeActivity.class);
            startActivity(reOpen);
            finish();
            overridePendingTransition( 0, 0);
            startActivity(getIntent());
            overridePendingTransition( 0, 0);*/
           setupCamera();

        } else {

            Toast.makeText(this, "Please allow all the permission ", Toast.LENGTH_LONG).show();
            finish();


     /*       if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CALL_LOG)) {
                // now, user has denied permission (but not permanently!)

            } else {

                // now, user has denied permission permanently!

                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "You have previously declined this permission.\n" +
                        "You must approve this permission in \"Permissions\" in the app settings on your device.", Snackbar.LENGTH_LONG).setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));

                    }
                });
                View snackbarView = snackbar.getView();
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(5);  //Or as much as you need
                snackbar.show();

            }
*/
        }
        return;
    }
}