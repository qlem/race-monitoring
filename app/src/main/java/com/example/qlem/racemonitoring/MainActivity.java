package com.example.qlem.racemonitoring;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.qlem.racemonitoring.dialogs.AppHelpDialog;
import com.example.qlem.racemonitoring.dialogs.LocationProviderDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is the main activity class, allows to start and stop the service that records the location
 * points of the race. When the user stops the recording, main activity start the report activity.
 */
public class MainActivity extends AppCompatActivity implements LocationProviderDialog.NoticeDialogListener {

    /**
     * Code of the request permission.
     */
    private final int REQUEST_PERMISSION_CODE = 1;

    /**
     * The list of the locations points.
     */
    private List<Location> locations;

    /**
     * The main button that allows to start and stop the recording.
     */
    private Button mainButton;

    /**
     * A small indicator that indicates the state of the service and when a location update occurs.
     */
    private RecordingIndicatorView recordingIndicator;

    /**
     * The variable provides some information about the location provider.
     */
    private LocationManager locationManager;

    /**
     * Function that switch the main button to the "recording" mode.
     */
    private void switchToRecordingMode() {
        mainButton.setText(R.string.btn_stop);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // switch button to stop
                recordingIndicator.stopRecording();
                switchToReadyToGoMode();

                // stop the service
                sendBroadcast(new Intent(LocationService.ACTION_STOP));
            }
        });
    }

    /**
     * Function that switch the main button to the "ready to go" mode.
     */
    private void switchToReadyToGoMode() {
        mainButton.setText(R.string.btn_start);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check location permission
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "App cannot access to location: permission denied",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // check location provider state
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(MainActivity.this, "Device location disabled",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // switch button to stop
                recordingIndicator.startRecording();
                switchToRecordingMode();

                // start service
                Intent intent = new Intent(MainActivity.this, LocationService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }
        });
    }

    /**
     * This function is used initialize the GPX file writer class for save the GPX file.
     */
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

    /**
     * This function initializes the broadcast receiver that allows to communicate with
     * the service.
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        /**
         * Triggered when the receiver receive an intent from the service.
         * @param context the context
         * @param intent the received intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // get action
            String action = intent.getAction();

            // excepted action after the sending of ACTION_PING for check if the service is alive
            if (action != null && action.equals(LocationService.ACTION_ALIVE)) {
                recordingIndicator.startRecording();
                switchToRecordingMode();
            }

            // warns that a location update occur
            else if (action != null && action.equals(LocationService.ACTION_UPDATE)) {
                recordingIndicator.locationUpdate();
            }

            // excepted action when the service is stopped, contains the locations list
            else if (action != null && action.equals(LocationService.ACTION_RESULT)) {
                locations = intent.getParcelableArrayListExtra("locations");
                if (locations == null || locations.size() == 0) {
                    Toast.makeText(MainActivity.this,
                            "Nothing to display", Toast.LENGTH_SHORT).show();
                    return;
                }

                writeGPXFile();

                intent = new Intent(MainActivity.this, RaceReportActivity.class);
                intent.putParcelableArrayListExtra("locations", (ArrayList<? extends Parcelable>) locations);
                startActivity(intent);

                locations.clear();
            }
        }
    };

    /**
     * This function checks if the device location is enabled, if not show a pop-up dialog.
     */
    private void checkDeviceLocation() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            DialogFragment dialog = new LocationProviderDialog();
            dialog.show(getSupportFragmentManager(), "location_provider");
        }
    }

    /**
     * This function checks if the app has permission for location access and
     * external storage access. If not, requests the permission(s).
     */
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

    /**
     * Function that is called at the creation of the activity.
     * @param savedInstanceState the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordingIndicator = findViewById(R.id.recording_indicator);
        mainButton = findViewById(R.id.main_button);

        // switch view to ready to start mode
        switchToReadyToGoMode();

        // ask permissions
        askPermissions();

        // check location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkDeviceLocation();

        // init broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationService.ACTION_ALIVE);
        intentFilter.addAction(LocationService.ACTION_UPDATE);
        intentFilter.addAction(LocationService.ACTION_RESULT);
        registerReceiver(broadcastReceiver, intentFilter);

        // ping the service for check if it is running
        sendBroadcast(new Intent(LocationService.ACTION_PING));
    }

    /**
     * This function is called when user answers to the request permission dialogs.
     * @param requestCode the request code
     * @param permissions the asked permissions
     * @param grantResults the grant result (the user's decisions)
     */
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

    /**
     * Function called when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    /**
     * Event listener triggered when user performs click on the "open settings" of the location
     * provider dialog. Opens the location settings.
     * @param dialog the triggered dialog
     */
    @Override
    public void onOpenSettingsClick(DialogFragment dialog) {
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    /**
     * Function that is called at the creation of the menu, initializes the menu.
     * @param menu the menu to inflate
     * @return true or false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Triggered when the user performs click on the help menu, show a dialog helper.
     * @param item the button help
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_action:
                DialogFragment dialog = new AppHelpDialog();
                dialog.show(getSupportFragmentManager(), "app_help");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
