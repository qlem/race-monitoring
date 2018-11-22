package com.example.qlem.racemonitoring;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_LOCATION_CODE = 1;
    private final int REQUEST_WRITE_EXTERNAL_STORAGE_CODE = 2;
    LocationManager locationManager;
    List<Location> locations;

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            updateUI(location);
            locations.add(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    // For test
    private void updateUI(Location location) {
        TextView latitudeView = findViewById(R.id.latitude);
        TextView longitudeView = findViewById(R.id.longitude);
        latitudeView.setText(String.format("lat: %s", location.getLatitude()));
        longitudeView.setText(String.format("lon: %s", location.getLongitude()));
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO explain why app needs permission (popup)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_CODE);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                    0, locationListener);
            setStopListenerMainButton();
        }
    }

    private void getWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // TODO explain why app needs permission (popup)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE_CODE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE_CODE);
            }
        } else {
            if (locations.size() > 0) {
                GPXFileWriter writer = new GPXFileWriter(MainActivity.this, locations);
                writer.writeGPXFile(new Date());
            }
        }
    }

    private void setStartListenerMainButton() {
        Button mainButton = findViewById(R.id.main_button);
        mainButton.setText(R.string.btn_start);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();
            }
        });
    }

    private void setStopListenerMainButton() {
        Button mainButton = findViewById(R.id.main_button);
        mainButton.setText(R.string.btn_stop);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.removeUpdates(locationListener);
                getWriteExternalStoragePermission();

                Intent intent = new Intent(MainActivity.this, MonitoringActivity.class);
                intent.putParcelableArrayListExtra("locations", (ArrayList<? extends Parcelable>) locations);
                startActivity(intent);

                locations.clear();
                setStartListenerMainButton();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locations = new ArrayList<>();

        setStartListenerMainButton();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted: app can access to location",
                            Toast.LENGTH_SHORT).show();
                    // TODO start location listener
                } else {
                    Toast.makeText(this, "Permission denied: app cannot access to location",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Permission granted: app can write in external storage",
                            Toast.LENGTH_SHORT).show();
                    // TODO call gpx writer
                } else {
                    Toast.makeText(this,
                            "Permission denied: cannot write in external storage",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
