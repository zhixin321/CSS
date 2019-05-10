package com.example.css;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class location extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener{

    public static final int RequestPermissionCode = 1;
    protected GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    protected TextView longitudeText;
    protected TextView latitudeText;
    private Button button;
    TextView textView;
    Geocoder geocoder;
    List<Address> addresses;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);



        button = (Button) findViewById(R.id.next_btn);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){

                String currentLocation = textView.getText().toString();
                Intent intent = new Intent(location.this,MainActivity.class);
                intent.putExtra("LOCATION",currentLocation);
                startActivity(intent);
        }
        });

        latitudeText = (TextView) findViewById(R.id.latitude_txt);
        longitudeText = (TextView) findViewById(R.id.longitude_txt);
        textView=(TextView)findViewById(R.id.address_txt);
        geocoder=new Geocoder(this, Locale.getDefault());

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("MainActivity", "Connection failed: " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("MainActivity", "Connection suspendedd");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                latitudeText.setText(String.valueOf(location.getLatitude()));
                                longitudeText.setText(String.valueOf(location.getLongitude()));

                                String lon = longitudeText.getText().toString();
                                double longtitude = Double.parseDouble(lon);

                                String lat = latitudeText.getText().toString();
                                double latitude = Double.parseDouble(lat);

                                try {

                                    addresses=geocoder.getFromLocation(latitude,longtitude,1);
                                    String address= addresses.get(0).getAddressLine(0);
                                    String area= addresses.get(0).getLocality();
                                    String city= addresses.get(0).getAdminArea();
                                    String country= addresses.get(0).getCountryName();
                                    String postalcode= addresses.get(0).getPostalCode();

                                    String fullAddress= address+", "+area+", "+city+", "+country+", "+postalcode;

                                    textView.setText(fullAddress);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(location.this, new
                String[]{ACCESS_FINE_LOCATION}, RequestPermissionCode);
    }
}
