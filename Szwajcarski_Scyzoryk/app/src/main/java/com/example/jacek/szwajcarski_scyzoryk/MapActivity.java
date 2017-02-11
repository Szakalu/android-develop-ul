package com.example.jacek.szwajcarski_scyzoryk;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    GoogleMap mapToGps;

    double myCurrentlyLatitude;
    double myCurrentlyLongitude;

    final static String MY_CURRENTLY_LATITUDE = "my_currently_latitude";
    final static String MY_CURRENTLY_LONGITUDE = "my_currently_longitude";

    Location location;
    LocationManager locationManager;

    LatLng myLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragmentMapActivity = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragmentMapActivity.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 10000,1,this);

            Intent intentMainActivity = getIntent();
            myCurrentlyLatitude = intentMainActivity.getDoubleExtra(MY_CURRENTLY_LATITUDE,0);
            myCurrentlyLongitude = intentMainActivity.getDoubleExtra(MY_CURRENTLY_LONGITUDE,0);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapToGps = googleMap;

        if(myCurrentlyLatitude != 200 && myCurrentlyLongitude != 200){
            LatLng myLocation = new LatLng(myCurrentlyLatitude, myCurrentlyLongitude);
            mapToGps.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            mapToGps.animateCamera(CameraUpdateFactory.zoomTo(12));
            myLastKnownLocation = myLocation;
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng myCurrentlyLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if(myLastKnownLocation!=null){
            mapToGps.addPolyline(new PolylineOptions().add(myLastKnownLocation,myCurrentlyLocation).width(5).color(Color.RED));
        }

        myLastKnownLocation = myCurrentlyLocation;

        mapToGps.moveCamera(CameraUpdateFactory.newLatLng(myLastKnownLocation));
        mapToGps.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void backToMainActivity(View view){
        finish();
    }
}
