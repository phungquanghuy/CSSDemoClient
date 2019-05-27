package com.example.cssdemoclient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
<<<<<<< HEAD
import android.widget.EditText;
=======
>>>>>>> origin/master
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cssdemoclient.object.Client;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap myMap;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private DatabaseReference clientDb = FirebaseDatabase.getInstance().getReference("Client");
    private DatabaseReference partnerDb = FirebaseDatabase.getInstance().getReference("Partner");

    private Button btn_book;
    private EditText request;
    private TextView price;
    private String charge = "50.000Đ";

    private Client mClient = new Client();
    private boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mapFragment.getMapAsync(this);

<<<<<<< HEAD
        btn_book = findViewById(R.id.btn_book);
        request = findViewById(R.id.request);
        price = findViewById(R.id.price);


        getInfo();

=======
        final Button bookButton = findViewById(R.id.btn_book);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookButton.setBackgroundResource(R.drawable.offline_btn);
                bookButton.setText("Hủy dịch vụ");
                bookButton.setTextColor(0xFF000000);
            }
        });
>>>>>>> origin/master
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        setupGoogleMapScreenSettings(myMap);

        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
            }
        });
        LatLng latLng = new LatLng(locateGPS().getLatitude(),locateGPS().getLongitude());
        if (checkPermission()){
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        if (checkPermission()){
            mMap.setMyLocationEnabled(true);
        }
    }

    private static final int REQUEST_CODE_GPS_PERMISSION = 100;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //TODO: Get current location
            locateGPS();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_GPS_PERMISSION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 100:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    locateGPS();
                }
                return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_CODE_GPS_PERMISSION);
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Location locateGPS(){
        Location localGpsLocation = null;
        if (checkPermission()){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,this);
            localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(localGpsLocation == null){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,3000,0,this);
                localGpsLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        return localGpsLocation;
    }

    private String locationName(LatLng latLng) throws IOException {
        Geocoder geocode = new Geocoder(MapsActivity.this, Locale.getDefault());
        List<Address> listAddress = geocode.getFromLocation(latLng.latitude, latLng.longitude, 100);
        Address address = listAddress.get(0);

        return address.getAddressLine(0);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void btnStatus(){
        if(mClient.getStatus().equals("available")){
            btn_book.setBackgroundResource(R.drawable.active_btn);
            btn_book.setTextColor(0xFFFFFFFF);
            btn_book.setText("Đặt dịch vụ");
        }
        else{
            btn_book.setBackgroundResource(R.drawable.offline_btn);
            btn_book.setTextColor(0xFF000000);
            btn_book.setText("Hủy dịch vụ");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void toggleBtn(){
        if(mClient.getStatus().equals("available")){
            if (!request.getText().toString().equals("")){
                Location location = locateGPS();
                if (check){
                    clientDb.child(mClient.getUsername()).child("status").setValue("waiting");
                    clientDb.child(mClient.getUsername()).child("request").setValue(request.getText().toString());
                    clientDb.child(mClient.getUsername()).child("price").setValue(charge);
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    clientDb.child(mClient.getUsername()).child("lat").setValue(location.getLatitude()+"");
                    clientDb.child(mClient.getUsername()).child("lng").setValue(location.getLongitude()+"");
                    try {
                        clientDb.child(mClient.getUsername()).child("address").setValue(locationName(latLng));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
            else {
                Toast.makeText(MapsActivity.this,"Vui lòng nhập yêu cầu",Toast.LENGTH_LONG).show();
            }
        }
        else{
            clientDb.child(mClient.getUsername()).child("status").setValue("available");
        }
    }

    protected void getInfo(){
        clientDb.child(mClient.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mClient = dataSnapshot.getValue(Client.class);
                btnStatus();
                if (check){
                    if ( !mClient.getStatus().contains("waiting")
                            && !mClient.getStatus().equals("busy")
                            && !mClient.getStatus().equals("available")
                    ){
                        check = false;
                        Intent intent = new Intent(MapsActivity.this, PairedActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                btn_book.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        toggleBtn();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
