package com.tara.cameraapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.otaliastudios.cameraview.controls.Flash.OFF;
import static com.otaliastudios.cameraview.controls.Flash.ON;
import static com.otaliastudios.cameraview.controls.Flash.TORCH;

public class QrcodeActivity extends AppCompatActivity {

    CameraView camera_view;
    boolean isDetected = false;
    String barcode;
    ImageView flashon, gallery, flashoff, btn_start_again;
    TextView tvbarcode;
    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_qrcode);


        init();
        Dexter.withActivity(this).withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                setupCamera();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();

        tvbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent barcode = new Intent(QrcodeActivity.this, BarcodeActivity.class);
                barcode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(barcode);
            }
        });


    }

    private void init() {

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

                    case FirebaseVisionBarcode.TYPE_PHONE:{

                        String mobile=item.getPhone().getNumber();
                        Uri number = Uri.parse("tel:" + mobile);
                        Intent dial = new Intent(Intent.ACTION_DIAL);
                        dial.setData(number);
                        startActivity(dial);

                    }
                    break;

                    case FirebaseVisionBarcode.TYPE_SMS: {
                        String message = Objects.requireNonNull(item.getSms()).getMessage();
                        String phone = item.getSms().getPhoneNumber();

                        createforsms(message,phone);


                    }
                    break;


                    case FirebaseVisionBarcode.TYPE_GEO: {
                        double lat = Objects.requireNonNull(item.getGeoPoint()).getLat();
                        double longitude = item.getGeoPoint().getLng();
                        createforlocation(lat, longitude);
                    }
                    break;


                    case FirebaseVisionBarcode.TYPE_CONTACT_INFO: {

                        String name, phone, address, email, website;

                        name = (" " +
                                item.getContactInfo().getName().getFormattedName());

                        phone = (" " +
                                item.getContactInfo().getPhones().get(0).getNumber());

                        address = " " +
                                Arrays.toString(item.getContactInfo().getAddresses().get(0).getAddressLines());

                        email = (" " +
                                item.getContactInfo().getEmails().get(0).getAddress());


                        website = " " +
                                Arrays.toString(item.getContactInfo().getUrls());


                        createforResult(name, phone, address, email, website);


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

        Intent sms=new Intent(QrcodeActivity.this,SmsActivity.class);
        sms.putExtra("phone",phone);
        sms.putExtra("message",message);
        startActivity(sms);

    }

    private void createforlocation(double lat, double longitude) {

        Intent geolocation = new Intent(QrcodeActivity.this, GeoLocationActivity.class);
        geolocation.putExtra("lat", lat);
        geolocation.putExtra("longitude", longitude);
        startActivity(geolocation);

    }

    private void createforResult(String name, String phone, String address, String email, String website) {

        Intent profile = new Intent(QrcodeActivity.this, ProfileActivity.class);
        profile.putExtra("name", name);
        profile.putExtra("phone", phone);
        profile.putExtra("address", address);
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


    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        //   finish();
        super.onBackPressed();
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


}