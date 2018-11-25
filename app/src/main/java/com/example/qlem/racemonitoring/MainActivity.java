package com.example.qlem.racemonitoring;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import com.example.qlem.racemonitoring.service.LocationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MonitoringState monitoringState;
    private final int REQUEST_PERMISSION_CODE = 1;
    LocationManager locationManager;
    List<Location> locations;
    Button mainButton;
    RecordingIndicatorView recordingIndicator;

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            recordingIndicator.locationUpdate();
            locations.add(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private void recordingLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "App cannot access to location: permission denied",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                0, locationListener);
        monitoringState = MonitoringState.RECORDING;
        recordingIndicator.startRecording();
        switchButtonStateToStop();
    }

    private void writeGPXFile() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "App cannot save GPX file: permission denied",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        GPXFileWriter writer = new GPXFileWriter(MainActivity.this, locations);
        writer.writeGPXFile(new Date());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
        // TODO use ActivityCompat.shouldShowRequestPermissionRationale
    }

    private void switchButtonStateToStart() {
        mainButton.setText(R.string.btn_start);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // recordingLocation();

                switchButtonStateToStop();

                Intent intent = new Intent(MainActivity.this, LocationService.class);
                startService(intent);
            }
        });
    }

    private void switchButtonStateToStop() {
        mainButton.setText(R.string.btn_stop);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, LocationService.class);
                stopService(intent);

                // locationManager.removeUpdates(locationListener);
                monitoringState = MonitoringState.STOPPED;
                recordingIndicator.stopRecording();
                switchButtonStateToStart();

                if (locations.size() == 0) {
                    Toast.makeText(MainActivity.this,
                            "No location data, nothing to display", Toast.LENGTH_SHORT).show();
                    return;
                }

                /* writeGPXFile();

                Intent intent = new Intent(MainActivity.this, RaceReportActivity.class);
                intent.putParcelableArrayListExtra("locations", (ArrayList<? extends Parcelable>) locations);
                startActivity(intent);

                locations.clear(); */
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(LocationService.ACTION_PONG)) {
                    switchButtonStateToStop();
                } else if (action.equals(LocationService.ACTION_STOP)) {
                    locations = intent.getParcelableArrayListExtra("locations");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordingIndicator = findViewById(R.id.recording_indicator);
        mainButton = findViewById(R.id.main_button);

        // check permissions
        checkPermissions();

        // init broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationService.ACTION_PONG);
        intentFilter.addAction(LocationService.ACTION_STOP);
        registerReceiver(broadcastReceiver, intentFilter);

        // ping service for check if service is running
        sendBroadcast(new Intent(LocationService.ACTION_PING));

        monitoringState = MonitoringState.STOPPED;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locations = new ArrayList<>();
        switchButtonStateToStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission denied: app cannot access to location",
                            Toast.LENGTH_SHORT).show();
                }
                if (grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission denied: app cannot access to external storage",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        outState.putString("monitoringState", monitoringState.name());
        if (monitoringState == MonitoringState.RECORDING) {
            outState.putParcelableArrayList("locations", (ArrayList<? extends Parcelable>) locations);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        String state = savedInstanceState.getString("monitoringState");
        if (state != null && state.equals(MonitoringState.RECORDING.name())) {
            locations = savedInstanceState.getParcelableArrayList("locations");
            recordingLocation();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
