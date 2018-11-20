package com.example.qlem.racemonitoring;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class HandlerGPXFile {

    private Context context;
    private List<Location> locations;

    HandlerGPXFile(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    HandlerGPXFile(Context context) {
        this.context = context;
    }

    public void writeGPXFile() {

        boolean state = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (!state) {
            Toast.makeText(context, "External storage is not available for read and write",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "Race Monitoring GPX");
        if (!file.mkdirs()) {
            Toast.makeText(context, "Cannot create GPX file into external storage",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < locations.size(); i++) {
            // TODO
        }
    }
}
