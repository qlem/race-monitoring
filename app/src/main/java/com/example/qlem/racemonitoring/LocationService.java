package com.example.qlem.racemonitoring;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service {

    LocationManager locationManager;
    List<Location> locations;
    public static final String ACTION_PING = LocationService.class.getName() + ".PING";
    public static final String ACTION_PONG = LocationService.class.getName() + ".PONG";
    public static final String ACTION_UPDATE = LocationService.class.getName() + ".UPDATE";
    public static final String ACTION_STOP = LocationService.class.getName() + ".STOP";

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            locations.add(location);
            sendBroadcast(new Intent(ACTION_UPDATE));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        // start the service in foreground
        startForeground();

        // register the broadcast receiver
        registerReceiver(broadcastReceiver, new IntentFilter(ACTION_PING));

        // start recording location
        locations = new ArrayList<>();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                0, locationListener);
    }

    private void createNotificationChannel() {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("test", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        createNotificationChannel();

        Notification notification =
                new Notification.Builder(this, "test")
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(getText(R.string.notification_text))
                        .setSmallIcon(R.drawable.ic_service)
                        .setContentIntent(pendingIntent)
                        .setTicker(getText(R.string.notification_ticker))
                        .build();

        startForeground(42, notification);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(LocationService.ACTION_PING)) {
                sendBroadcast(new Intent(ACTION_PONG));
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getIntExtra("STOP", 0) == 1) {
            Intent stopIntent = new Intent(ACTION_STOP);
            stopIntent.putParcelableArrayListExtra("locations", (ArrayList<? extends Parcelable>) locations);
            sendBroadcast(stopIntent);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);
        unregisterReceiver(broadcastReceiver);
    }
}
