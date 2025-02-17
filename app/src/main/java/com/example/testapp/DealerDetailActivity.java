package com.example.testapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.location.Geocoder;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Locale;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class DealerDetailActivity extends AppCompatActivity {

    TextView gstin,trade_nm,legal_nm,address;

    String userid="",map_address="",map_tradenm="",gstn,tradenm,legalnm,addrs,user_nm="";

    double lat,lon;
    ImageButton showmap,upload_pic_btn;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dealer_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        gstin = (TextView) findViewById(R.id.gstinno);

        trade_nm = (TextView) findViewById(R.id.trade_nm);

        legal_nm= (TextView) findViewById(R.id.legal_nm);

        address = (TextView) findViewById(R.id.address);
        showmap = (ImageButton)findViewById(R.id.imageButton2);
        upload_pic_btn = (ImageButton)findViewById(R.id.upload_pic_btn);



        userid = getIntent().getStringExtra("userid");
        user_nm = getIntent().getStringExtra("user_nm");
        TextView userid_view = (TextView)findViewById(R.id.userid_text);
        userid_view.setText(user_nm);

        gstn = getIntent().getStringExtra("gstin_no");
        tradenm = getIntent().getStringExtra("trade_nm");
        legalnm = getIntent().getStringExtra("legal_nm");
        addrs = getIntent().getStringExtra("address");

        gstin.setText(gstn);
        trade_nm.setText(tradenm);
        legal_nm.setText(legalnm);
        address.setText(addrs);
        this.map_tradenm = tradenm;
        this.map_address = addrs;
        getLatLongFromAddress(this.map_address,this.map_tradenm);
        //checkIfWithinLocationRange(fusedLocationClient,lat,lon);
        showmap.setOnClickListener(view -> {

            getLatLongFromAddress(this.map_address,this.map_tradenm);


            Intent m =new Intent(DealerDetailActivity.this,MapsActivity.class);

            m.putExtra("lat", lat);
            m.putExtra("lon",lon);
            m.putExtra("tradenm",map_tradenm);

            startActivity(m);

        });


        upload_pic_btn.setOnClickListener(view -> {

            Intent i = new Intent(DealerDetailActivity.this, UploadPicActivity.class);
            i.putExtra("userid",userid);
            i.putExtra("user_nm",user_nm);
            i.putExtra("gstn",gstn);
            startActivity(i);

        });
    }


    private void getLatLongFromAddress(String address,String tradenm) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        //tradenm = "Directorate of Commercial Taxes";
        //address ="14 Beliaghata Main Rd, Kolkata 700015";

        try {
            // Get a list of matching locations

            List<Address> addresses = geocoder.getFromLocationName(tradenm+", "+address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                this.lat = latitude;
                this.lon = longitude;
                System.out.println("created>>>>>"+latitude+"======="+longitude);



            } else {
                Toast.makeText(this,"cannot create latlon found from address",Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    public void checkIfWithinLocationRange(FusedLocationProviderClient fusedLocationClient,double dealer_lat,double dealer_lon){


        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Get current location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double currentLat = location.getLatitude();
                            double currentLon = location.getLongitude();

                            // Target location (e.g., user's home location)
                            double targetLat = dealer_lat; // dealer target latitude
                            double targetLon = dealer_lon; // dealer target longitude

                            // Check if current location is within a specific radius of the target
                            float[] results = new float[1];
                            Location.distanceBetween(currentLat, currentLon, targetLat, targetLon, results);

                            float distanceInMeters = results[0]; // Distance in meters
                            float radiusInMeters = 1000; // Target radius in meters

                            if (distanceInMeters <= radiusInMeters) {
//                                Toast.makeText(DealerDetailActivity.this, "You are within range of the field location!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DealerDetailActivity.this, "You are outside the range of field location.", Toast.LENGTH_LONG).show();
                                Intent i =new Intent(DealerDetailActivity.this,MainActivity.class);
                                startActivity(i);
                            }
                        }
                    }
                });

    }
}