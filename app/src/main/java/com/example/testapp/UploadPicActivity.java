package com.example.testapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

import android.Manifest;

import com.example.testapp.apiservice.SaveSurveyAsyncTask;
import com.example.testapp.apiservice.UploadAsysncTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class UploadPicActivity extends AppCompatActivity {

    private static final int pic_id = 123;
    // Define the button and imageview type variable
    ImageButton btn_cam_open;
    ImageView camera_frame;
    Bitmap captured_photo;

    String filename = "", latlon_str = "";

    SharedPreferences sharedpreferences;
    Button upload,submit;

    private String latlon_str1 = null;

    String userid = "", user_nm = "", gstn = "";

    private LocationCallback locationCallback;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    double lat, lon;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_pic);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedpreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        btn_cam_open = (ImageButton) findViewById(R.id.open_cam_btn);
        camera_frame = (ImageView) findViewById(R.id.camera_frame);

        upload = (Button) findViewById(R.id.upload_btn);
        submit = (Button) findViewById(R.id.submit_btn);
        btn_cam_open.setOnClickListener(view -> {

            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(camera_intent, pic_id);
            upload.setVisibility(view.VISIBLE);

        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request current location
        currentLatLon(fusedLocationClient, new LocationCallbackInterface() {
            @Override
            public void onLocationReceived(String latLon) {
                latlon_str = latLon;
                System.out.println("inside latlon>>>>>>" + latLon);
                // You can now use the latLon value as needed
            }
        });


        userid = getIntent().getStringExtra("userid");
        user_nm = getIntent().getStringExtra("user_nm");
        gstn = getIntent().getStringExtra("gstn");

        TextView userid_view = (TextView) findViewById(R.id.userid_text);
        userid_view.setText(user_nm);

        upload.setOnClickListener(view -> {
            String dir = saveToGallery(captured_photo);
            camera_frame.setImageDrawable(null);
            uploadImagetoServer(this); // upload image to server
            upload.setVisibility(view.INVISIBLE);
        });

        submit.setOnClickListener(view -> {


            String[] str = latlon_str.split(",");


           String phy_add = getAddressFromLatLon(this,Double.parseDouble(str[0]),Double.parseDouble(str[1]));

            new SaveSurveyAsyncTask(this).execute(userid,gstn,phy_add,str[0],str[1]);

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id with requestCode
        if (requestCode == pic_id) {
            // BitMap is data structure of image file which store the image in memory
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // Set the image in imageview for display
            captured_photo = photo;

            camera_frame.setImageBitmap(photo);

        }
    }


    private String saveToGallery(Bitmap bitmapImage) {

//        if (!filename.endsWith(".png")) {
//            filename += ".png";
//        }

        Random random = new Random();


        long randomNumber = 1_000_000_000L + (Math.abs(random.nextLong()) % 9_000_000_000L);
        filename = gstn + "_" + String.valueOf(randomNumber) + ".png";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);  // Set the image name
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DealerInfo"); // Save to "Pictures/DealerInfo"

        // Get the content resolver to interact with MediaStore
        OutputStream fos = null;
        try {
            // Insert the new image into the MediaStore, this returns the URI
            Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // If URI is not null, open an OutputStream to write the bitmap
            if (imageUri != null) {
                fos = getContentResolver().openOutputStream(imageUri);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos); // Save as PNG

                // Commit changes to SharedPreferences (assuming sharedpreferences is already defined)
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("saved", "true");  // Change the value accordingly
                editor.apply(); // Using apply() instead of commit() for better performance


            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Ensure that the OutputStream is closed after the operation
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "Saved to gallery successfully!";
    }


    public void uploadImagetoServer(Context context) {
        String full_img_path = "/storage/emulated/0/Pictures/DealerInfo/" + filename;
        System.out.println("Image path >>>>>>" + full_img_path);
        new UploadAsysncTask(this).execute(userid, gstn, full_img_path, filename);

    }



    @SuppressLint("MissingPermission")
    public void currentLatLon(FusedLocationProviderClient fusedLocationClient, LocationCallbackInterface callback) {

        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        // Get current location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            String latLon = lat + "," + lon;

                            callback.onLocationReceived(latLon);  // Return the value through callback
                        } else {
                            requestNewLocation(callback);
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocation(LocationCallbackInterface callback) {
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)   // Update every 5 seconds
                .setFastestInterval(2000)
                .setNumUpdates(1);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    String latLon = location.getLatitude() + "," + location.getLongitude();

                    callback.onLocationReceived(latLon);
                }
                fusedLocationClient.removeLocationUpdates(this);
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                currentLatLon(fusedLocationClient, new LocationCallbackInterface() {
                    @Override
                    public void onLocationReceived(String latLon) {

                    }
                });
            } else {

            }
        }
    }

    public interface LocationCallbackInterface {
        void onLocationReceived(String latLon);
    }


    public String getAddressFromLatLon(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String addressText = "Address not found";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                addressText = address.getAddressLine(0); // Get full address
            }
        } catch (IOException e) {

        }

        return addressText;
    }


    public void returnToMain(){

        Intent i =new Intent(UploadPicActivity.this, MainActivity.class);
        startActivity(i);

    }

}