package com.example.qlem.racemonitoring;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class GPXFileWriter {

    private Context context;
    private List<Location> locations;

    GPXFileWriter(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    void writeGPXFile(Date now) {
        boolean state = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (!state) {
            Toast.makeText(context, "External storage is not available for write",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Race Monitoring GPX");
        if (!dir.exists() && !dir.mkdirs()) {
            Toast.makeText(context, "Cannot create GPX directory into external storage",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        String fileName = String.format("Race Monitoring GPX %s.gpx", isoDate.format(now));
        File file = new File(dir, fileName);

        String header = "<gpx creator=\"Race Monitoring\" version=\"1.1\">\n";
        String trkHeader = "\t<trk>\n\t\t<name>Race Monitoring GPX File</name>\n\t\t<trkseg>\n";
        String footer = "\t\t</trkseg>\n\t</trk>\n</gpx>\n";

        try {
            Writer writer = new FileWriter(file, true);
            writer.append(header);
            writer.append(trkHeader);
            for (int i = 0; i < locations.size(); i++) {
                String time = isoDate.format(locations.get(i).getTime());
                String latitude = String.valueOf(locations.get(i).getLatitude());
                String longitude = String.valueOf(locations.get(i).getLongitude());
                String elevation = String.valueOf(locations.get(i).getAltitude());
                writer.append(String.format("\t\t\t<trkpt lat=\"%s\" lon=\"%s\">\n", latitude, longitude));
                writer.append(String.format("\t\t\t\t<ele>%s</ele>\n", elevation));
                writer.append(String.format("\t\t\t\t<time>%s</time>\n\t\t\t</trkpt>\n", time));
            }
            writer.append(footer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
