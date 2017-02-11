package com.example.jacek.szwajcarski_scyzoryk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener, OnMapReadyCallback {

    final static String NO_LOCALIZATION_FIND_TEXT = "No Localization found";
    final static String MY_CURRENTLY_LATITUDE = "my_currently_latitude";
    final static String MY_CURRENTLY_LONGITUDE = "my_currently_longitude";

    TextView textViewLightIntensity;
    TextView textViewGyroscope;
    TextView textViewCompassAzimuth;
    TextView textViewLocation;

    SensorManager sensorManager;
    Sensor lightSensor;
    Sensor gyroscopeSensor;
    Sensor orientationSensor;

    Camera camera;
    Camera.Parameters cameraParameters;

    boolean cameraFlashlightCheck;

    ToggleButton toggleButtonFlashlight;

    Location location;
    LocationManager locationManager;

    double lastKnownLatitude = 200;
    double lastKnownLongitude = 200;

    GoogleMap mapMyCurrentlyLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askForPermissions();

        SupportMapFragment mapFragmentMainActivity = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentMapMyCurrentlyLocation);
        mapFragmentMainActivity.getMapAsync(this);

        textViewLightIntensity = (TextView) findViewById(R.id.textViewLightIntensity);
        textViewGyroscope = (TextView) findViewById(R.id.textViewGyroscope);
        textViewCompassAzimuth = (TextView) findViewById(R.id.textViewCompassAzimuth);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        toggleButtonFlashlight = (ToggleButton) findViewById(R.id.toggleButtonFlashlight);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        cameraFlashlightCheck = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        checkIfDeviceHaveFlash(cameraFlashlightCheck);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 10000, 1, this);

            try {
                lastKnownLatitude = location.getLatitude();
                lastKnownLongitude = location.getLongitude();
                textViewLocation.setText("GPS: X: " + lastKnownLongitude + "\nY: " + lastKnownLatitude);
            } catch (NullPointerException gpsnpe) {
                textViewLocation.setText(NO_LOCALIZATION_FIND_TEXT);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapMyCurrentlyLocation = googleMap;

        if (lastKnownLatitude != 200 && lastKnownLongitude != 200) {
            LatLng myCurrentlyLocation = new LatLng(lastKnownLatitude, lastKnownLongitude);
            mapMyCurrentlyLocation.addMarker(new MarkerOptions().position(myCurrentlyLocation).title("My Currently Location"));
            mapMyCurrentlyLocation.moveCamera(CameraUpdateFactory.newLatLng(myCurrentlyLocation));
            mapMyCurrentlyLocation.animateCamera(CameraUpdateFactory.zoomTo(16));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor sensor = sensorEvent.sensor;

        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            textViewLightIntensity.setText("Light Intesity: " + sensorEvent.values[0]);
        }

        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            textViewGyroscope.setText("Gyroscope:" + "\nX: " + sensorEvent.values[0] + "\nY: " + sensorEvent.values[1] + "\nZ: " + sensorEvent.values[0]);
        }

        if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float degree = Math.round(sensorEvent.values[0]);
            degree = (degree+360)%360;
            textViewCompassAzimuth.setText("Azimuth: " + degree);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SENSOR_DELAY_NORMAL);
        } else {
            textViewLightIntensity.setText("Light sensor not found");
        }

        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SENSOR_DELAY_NORMAL);
        } else {
            textViewGyroscope.setText("Gyroscope sensor not found");
        }

        if (orientationSensor != null) {
            sensorManager.registerListener(this, orientationSensor, SENSOR_DELAY_NORMAL);
        } else {
            textViewCompassAzimuth.setText("Orientation sensor not found");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownLatitude = location.getLatitude();
        lastKnownLongitude = location.getLongitude();
        textViewLocation.setText("GPS: X: " + lastKnownLongitude + "\nY: " + lastKnownLatitude);

        mapMyCurrentlyLocation.clear();

        LatLng myNewCurrentlyLocation = new LatLng(lastKnownLatitude, lastKnownLongitude);
        mapMyCurrentlyLocation.addMarker(new MarkerOptions().position(myNewCurrentlyLocation).title("My Currently Location"));
        mapMyCurrentlyLocation.moveCamera(CameraUpdateFactory.newLatLng(myNewCurrentlyLocation));
        mapMyCurrentlyLocation.animateCamera(CameraUpdateFactory.zoomTo(16));
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

    public void startFlashlight() {
        cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(cameraParameters);
        camera.startPreview();
    }

    public void stopFlashlight() {
        cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(cameraParameters);
        camera.stopPreview();
    }

    private void checkIfDeviceHaveFlash(boolean flashCheck) {
        if (!flashCheck) {
            toggleButtonFlashlight.setEnabled(false);
        } else {
            camera = Camera.open();
            cameraParameters = camera.getParameters();
            setStartStopFlashlight();
        }
    }

    public void setStartStopFlashlight() {
        toggleButtonFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButtonFlashlight.isChecked()) {
                    startFlashlight();
                } else {
                    stopFlashlight();
                }
            }
        });
    }

    public void goToMapActivity(View view) {
        Intent intentMapActivity = new Intent(this, MapActivity.class);
        intentMapActivity.putExtra(MY_CURRENTLY_LATITUDE, lastKnownLatitude);
        intentMapActivity.putExtra(MY_CURRENTLY_LONGITUDE, lastKnownLongitude);
        startActivity(intentMapActivity);
    }

    private void askForPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA}, 1);

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
