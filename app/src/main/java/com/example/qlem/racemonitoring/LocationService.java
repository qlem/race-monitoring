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

public class LocationService extends Service {

    private LocationManager locationManager;
    private List<Location> locations;
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
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(LocationService.ACTION_PING)) {
                sendBroadcast(new Intent(ACTION_PONG));
            }
        }
    };

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

    @Override
    public void onCreate() {

        // start the service in foreground
        startForeground();

        // register the broadcast receiver
        registerReceiver(broadcastReceiver, new IntentFilter(ACTION_PING));

        // start recording location
        locations = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        Intent intent = new Intent(ACTION_STOP);
        intent.putParcelableArrayListExtra("locations", (ArrayList<? extends Parcelable>) locations);
        sendBroadcast(intent);

        locationManager.removeUpdates(locationListener);
        unregisterReceiver(broadcastReceiver);
    }
}
