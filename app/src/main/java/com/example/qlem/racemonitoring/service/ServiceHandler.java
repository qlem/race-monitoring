package com.example.qlem.racemonitoring.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ServiceHandler extends Handler {

    private LocationService service;

    ServiceHandler(Looper looper, LocationService service) {
        super(looper);
        this.service = service;
    }

    @Override
    public void handleMessage(Message msg) {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        service.stopSelf(msg.arg1);
        service.stopForeground(true);
    }
}
