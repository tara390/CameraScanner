package com.tara.cameraapplication.Qrcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tara.cameraapplication.R;

import java.util.List;
import java.util.Locale;

public class GeoLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    LatLng position;
    double lat, longitude;
    ImageView ivgeolocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_geo_location);

        lat = getIntent().getDoubleExtra("lat", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        MobileAdsforgeo();
        initviews();

        Imagebuttonclick();
    }

    private void MobileAdsforgeo() {



    }

    private void Imagebuttonclick() {

        ivgeolocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    private void initviews() {

        //Fragment for map
        SupportMapFragment fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(GeoLocationActivity.this);

        //Imageview
        ivgeolocation=findViewById(R.id.ivlocationbackpressed);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

       double latitude2 = 73.998;

        // Receiving longitude from MainActivity screen
      double  longitude2 = 22.4567;




        position=new LatLng(lat,longitude);
        // Instantiating MarkerOptions class
        MarkerOptions options = new MarkerOptions();

        // Setting position for the MarkerOptions
        options.position(position);

        // Setting title for the MarkerOptions
        options.title("Location");

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat,
                    longitude, 1);
            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);
            options.snippet(cityName + "," + stateName);

        } catch (Exception ex) {
            ex.getMessage();
            options.snippet("Latitude:" + lat + ",Longitude:" + longitude);

        }

        googleMap.addMarker(options);
        googleMap.addMarker(options.position(new LatLng(latitude2,
                longitude2)));


        CameraUpdate updatePosition = CameraUpdateFactory.newLatLng(position);

        // Creating CameraUpdate object for zoom
        CameraUpdate updateZoom = CameraUpdateFactory.zoomTo(10);

        // Updating the camera position to the user input latitude and longitude
        googleMap.moveCamera(updatePosition);

        // Applying zoom to the marker position
        googleMap.animateCamera(updateZoom);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}