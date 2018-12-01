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

/**
 * This class allows to write GPX file into the public external storage.
 */
class GPXFileWriter {

    /**
     * The context.
     */
    private Context context;

    /**
     * The locations list to write.
     */
    private List<Location> locations;

    /**
     * The default constructor.
     * @param context the context
     * @param locations the locations list
     */
    GPXFileWriter(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    /**
     * Function that is called for write GPX files from the locations list.
     * @param now the current date used for the name file
     */
    void writeGPXFile(Date now) {

        // checks if the external storage is available
        boolean state = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (!state) {
            Toast.makeText(context, "External storage is not available",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // tries to creates the GPX folder
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "GPStracks");
        if (!dir.exists() && !dir.mkdirs()) {
            Toast.makeText(context, "Cannot create GPX directory into external storage",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // creates the new GPX file
        SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        String fileName = String.format("%s Race Monitoring.gpx", isoDate.format(now));
        File file = new File(dir, fileName);

        // init some lines
        String header = "<gpx creator=\"Race Monitoring\" version=\"1.1\">\n";
        String trkHeader = "\t<trk>\n\t\t<name>Race Monitoring GPX File</name>\n\t\t<trkseg>\n";
        String footer = "\t\t</trkseg>\n\t</trk>\n</gpx>\n";

        try {

            // init a writer
            Writer writer = new FileWriter(file, true);
            writer.append(header);
            writer.append(trkHeader);

            // loop through locations and write GPS points
            for (int i = 0; i < locations.size(); i++) {
                String time = isoDate.format(locations.get(i).getTime());
                String latitude = String.valueOf(locations.get(i).getLatitude());
                String longitude = String.valueOf(locations.get(i).getLongitude());
                String elevation = String.valueOf(locations.get(i).getAltitude());
                writer.append(String.format("\t\t\t<trkpt lat=\"%s\" lon=\"%s\">\n", latitude, longitude));
                writer.append(String.format("\t\t\t\t<ele>%s</ele>\n", elevation));
                writer.append(String.format("\t\t\t\t<time>%s</time>\n\t\t\t</trkpt>\n", time));
            }

            // writes footer and closes the file
            writer.append(footer);
            writer.flush();
            writer.close();
            Toast.makeText(context ,"GPX file saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving GPX file", Toast.LENGTH_SHORT).show();
        }
    }
}
