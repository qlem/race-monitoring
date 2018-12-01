package com.example.qlem.racemonitoring;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates the service that records the location points of the race.
 */
public class LocationService extends Service {

    /**
     * The locations manager that allows to handle the device location.
     */
    private LocationManager locationManager;

    /**
     * The list of the location points.
     */
    private List<Location> locations;

    /**
     * Some constant variables that represent the actions sent between the service
     * and the main activity for communicate.
     */
    public static final String ACTION_PING = LocationService.class.getName() + ".PING";
    public static final String ACTION_PONG = LocationService.class.getName() + ".PONG";
    public static final String ACTION_UPDATE = LocationService.class.getName() + ".UPDATE";
    public static final String ACTION_STOP = LocationService.class.getName() + ".STOP";

    /**
     * This variable initializes the location listener.
     */
    LocationListener locationListener = new LocationListener() {

        /**
         * Triggered when location changes, stores the new location point.
         * @param location the location point to store
         */
        @Override
        public void onLocationChanged(Location location) {
            locations.add(location);
            sendBroadcast(new Intent(ACTION_UPDATE));
        }

        /**
         * Triggered when the status of the location provider changes.
         * @param provider the location provider
         * @param status the status
         * @param extras object that provides extra information
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        /**
         * Triggered when location provider is enabled.
         * @param provider the provider
         */
        @Override
        public void onProviderEnabled(String provider) {

        }

        /**
         * Triggered when location provider is disabled.
         * @param provider the provider
         */
        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * This function initializes the broadcast receiver for handle
     * the sent messages by the main activity.
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        /**
         * Triggered when the receiver receive an intent.
         * @param context the context
         * @param intent the received intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(LocationService.ACTION_PING)) {
                sendBroadcast(new Intent(ACTION_PONG));
            }
        }
    };

    /**
     * This function starts the service in the foreground by creating a notification.
     */
    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String CHANNEL_ID = "RMSChannel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.drawable.ic_service)
                .setContentIntent(pendingIntent)
                .setTicker(getString(R.string.notification_ticker))
                .build();

        startForeground(42, notification);
    }

    /**
     * This function is called at the creation of the service.
     */
    @Override
    public void onCreate() {

        // start the service in foreground
        startForeground();

        // register the broadcast receiver
        registerReceiver(broadcastReceiver, new IntentFilter(ACTION_PING));

        // init the locations list
        locations = new ArrayList<>();

        // start recording locations
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    /**
     * This function is called when the service is started
     * @param intent the sent intent
     * @param flags some flags
     * @param startId the id that identifies that call
     * @return a flag that describes how the android system have to handle the service
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    /**
     * This function is called for bind the service (not used).
     * @param intent the sent intent
     * @return 42
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * This function is called when the service is stopped.
     */
    @Override
    public void onDestroy() {

        Intent intent = new Intent(ACTION_STOP);
        intent.putParcelableArrayListExtra("locations", (ArrayList<? extends Parcelable>) locations);
        sendBroadcast(intent);

        locationManager.removeUpdates(locationListener);
        unregisterReceiver(broadcastReceiver);
    }
}
