package com.example.cssdemoclient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cssdemoclient.object.Client;
import com.example.cssdemoclient.object.Partner;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PairedActivity extends AppCompatActivity implements LocationListener,
        OnMapReadyCallback {

    private Button call;
    private Client mClient = new Client();
    private Partner mPartner = new Partner();
    private ImageView partnerAvt, clientAvt;
    private TextView partnerName, address, price, title;
    private Boolean check = true;
    private Marker maker[] = new Marker[2];

    //Khai báo cho Map
    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private int REQUEST_GPS = 100;

    private DatabaseReference clientDb = FirebaseDatabase.getInstance().getReference("Client");
    private DatabaseReference partnerDb = FirebaseDatabase.getInstance().getReference("Partner");


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        call = findViewById(R.id.call);
        clientAvt = findViewById(R.id.avatar);
        partnerAvt = findViewById(R.id.partnerAvt);
        partnerName = findViewById(R.id.partnerName);
        address = findViewById(R.id.address);
        price = findViewById(R.id.price);
        title = findViewById(R.id.title);

        title.setText("Chờ hỗ trợ");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.pairMap);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locateGPS();

        mapFragment.getMapAsync(this);

        getInfo();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 100:
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    locateGPS();
                }
                return;
        }
    }

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
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        }
        MarkerOptions option = new MarkerOptions();
        option.position(latLng);
        maker[0] = myMap.addMarker(option);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_GPS);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
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

    private String locationName(LatLng latLng) throws IOException {
        Geocoder geocode = new Geocoder(PairedActivity.this, Locale.getDefault());
        List<Address> listAddress = geocode.getFromLocation(latLng.latitude, latLng.longitude, 100);
        Address address = listAddress.get(0);

        return address.getAddressLine(0);
    }

    protected void getInfo(){
        clientDb.child(mClient.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mClient = dataSnapshot.getValue(Client.class);
                price.setText("Giá: "+mClient.getPrice());
                Picasso.get().load(mClient.getUrl()).into(clientAvt);
                if (check && mClient.getStatus().equals("available")){
                    check = false;
                    Intent intent = new Intent(PairedActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                }
                partnerDb.child(mClient.getStatus()).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (check){
                            mPartner = dataSnapshot.getValue(Partner.class);
                            Picasso.get().load(mPartner.getUrl()).into(partnerAvt);
                            partnerName.setText("Hỗ trợ viên: "+mPartner.getName()+"\n"+mPartner.getInfo());
                            LatLng latLng = new LatLng(Double.valueOf(mPartner.getLat()),Double.valueOf(mPartner.getLng()));
                            try {
                                address.setText("Địa chỉ: "+locationName(latLng));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            MarkerOptions option = new MarkerOptions();
                            option.position(latLng);
                            option.icon(BitmapDescriptorFactory.fromResource(R.mipmap.partner));
                            if (maker[1] != null){
                                maker[1].remove();
                            }
                            maker[1] = myMap.addMarker(option);
                            call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    check = false;
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mPartner.getPhone(), null));
                                    startActivity(intent);
                                }
                            });
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (Marker marker : maker) {
                                builder.include(marker.getPosition());
                            }
                            LatLngBounds bounds = builder.build();
                            int padding = 160; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            myMap.moveCamera(cu);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
