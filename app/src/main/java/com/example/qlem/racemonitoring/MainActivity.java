package com.example.qlem.racemonitoring;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION_CODE = 1;
    List<Location> locations;
    Button mainButton;
    RecordingIndicatorView recordingIndicator;

    private void switchToRecordingMode() {
        mainButton.setText(R.string.btn_stop);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // switch button to stop
                recordingIndicator.stopRecording();
                switchToReadyToGoMode();

                // stop the service
                Intent intent = new Intent(MainActivity.this, LocationService.class);
                stopService(intent);
            }
        });
    }

    private void switchToReadyToGoMode() {
        mainButton.setText(R.string.btn_start);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "App cannot access to location: permission denied",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // switch button to stop
                recordingIndicator.startRecording();
                switchToRecordingMode();

                // start service
                Intent intent = new Intent(MainActivity.this, LocationService.class);
                startService(intent);

                // TODO start service with startForegroundService(intent);
            }
        });
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(LocationService.ACTION_PONG)) {
                    recordingIndicator.startRecording();
                    switchToRecordingMode();
                } else if (action.equals(LocationService.ACTION_UPDATE)) {
                    recordingIndicator.locationUpdate();
                } else if (action.equals(LocationService.ACTION_STOP)) {
                    locations = intent.getParcelableArrayListExtra("locations");
                    if (locations == null || locations.size() == 0) {
                        Toast.makeText(MainActivity.this,
                                "No location data, nothing to display", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    writeGPXFile();

                    intent = new Intent(MainActivity.this, RaceReportActivity.class);
                    intent.putParcelableArrayListExtra("locations", (ArrayList<? extends Parcelable>) locations);
                    startActivity(intent);

                    locations.clear();
                }
            }
        }
    };

    private void askPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
        // TODO use ActivityCompat.shouldShowRequestPermissionRationale
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordingIndicator = findViewById(R.id.recording_indicator);
        mainButton = findViewById(R.id.main_button);

        // ask permissions
        askPermissions();

        // init broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationService.ACTION_PONG);
        intentFilter.addAction(LocationService.ACTION_STOP);
        intentFilter.addAction(LocationService.ACTION_UPDATE);
        registerReceiver(broadcastReceiver, intentFilter);

        // ping the service for check if it is running
        sendBroadcast(new Intent(LocationService.ACTION_PING));

        // switch view to ready to start mode
        switchToReadyToGoMode();
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
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
