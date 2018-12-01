package com.example.qlem.racemonitoring;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.qlem.racemonitoring.graph.Altitude;
import com.example.qlem.racemonitoring.graph.RaceGraphView;
import com.example.qlem.racemonitoring.graph.Speed;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is the report activity that provides an overview of the race.
 */
public class RaceReportActivity extends AppCompatActivity {

    /**
     * The list that contains all GPS points of the race.
     */
    private List<Location> locations;

    /**
     * The list to fill of the altitude of each GPS point.
     */
    private List<Altitude> altitudes;

    /**
     * The list to fill of the speed of each GPS point.
     */
    private List<Speed> speeds;

    /**
     * Some other important values.
     */
    private float altMax;
    private float altMin;
    private float speedMax;
    private float speedMin;

    /**
     * Function that sets the view of the race duration.
     */
    private void displayTimeRace() {
        TextView timeView = findViewById(R.id.time_value);

        if (locations.size() == 1) {
            return;
        }

        Location fromLocation = locations.get(0);
        Location toLocation = locations.get(locations.size() - 1);

        long diff = toLocation.getTime() - fromLocation.getTime();

        int seconds = (int) (diff / 1000) % 60;
        int minutes = (int) ((diff / (1000*60)) % 60);
        int hours   = (int) ((diff / (1000*60*60)) % 24);

        if (hours > 0) {
            timeView.setText(String.format(Locale.getDefault(), "%dh %dm %ds", hours, minutes, seconds));
        } else if (minutes > 0 ) {
            timeView.setText(String.format(Locale.getDefault(), "%dm %ds", minutes, seconds));
        } else {
            timeView.setText(String.format(Locale.getDefault(), "%ds", seconds));
        }
    }

    /**
     * Function that sets the distance information view, the altitude information view and
     * the speed information view.
     */
    private void displayRaceOverview() {

        // distance views
        TextView distanceValueView = findViewById(R.id.distance_value);

        // speed views
        TextView speedMaxView = findViewById(R.id.speed_max_value);
        TextView speedMinView = findViewById(R.id.speed_min_value);
        TextView speedAvgView = findViewById(R.id.speed_avg_value);

        // altitude views
        TextView altMaxView = findViewById(R.id.elevation_max_value);
        TextView altMinView = findViewById(R.id.elevation_min_value);
        TextView altGainView = findViewById(R.id.elevation_gain_value);
        TextView altLossView = findViewById(R.id.elevation_loss_value);

        // init vars
        float distance = 0;
        speedMax = 0;
        speedMin = 0;
        float speedTotal = 0;
        altMax = 0;
        altMin = 0;
        float altGain = 0;
        float altLoss = 0;

        // loop
        for (int i = 0; i < locations.size(); i++) {

            // init current location
            Location current = locations.get(i);

            // compute distance
            if (i < locations.size() - 1) {
                distance += current.distanceTo(locations.get(i + 1));
            }

            // compute speed
            if (i < locations.size() - 1) {
                float time = (locations.get(i + 1).getTime() - current.getTime()) / 1000;
                float dist = current.distanceTo(locations.get(i + 1));
                Speed speed = new Speed(dist / time);
                if (i == 0) {
                    speedMin = speed.speed;
                    speedMax = speed.speed;
                }
                if (speed.speed > speedMax) {
                    speedMax = speed.speed;
                } else if (speed.speed < speedMin) {
                    speedMin = speed.speed;
                }
                speedTotal += speed.speed;
                speeds.add(speed);
            }

            // compute altitude
            Altitude altitude = new Altitude((float) current.getAltitude());
            if (i == 0) {
                altMax = altitude.altitude;
                altMin = altitude.altitude;
            }
            if (altitude.altitude > altMax) {
                altMax = altitude.altitude;
            } else if (altitude.altitude < altMin) {
                altMin = altitude.altitude;
            }
            if (i < locations.size() - 1) {
                double nextAltitude = locations.get(i + 1).getAltitude();
                if (nextAltitude > altitude.altitude) {
                    altGain += (nextAltitude - altitude.altitude);
                } else if (nextAltitude < altitude.altitude) {
                    altLoss += (nextAltitude - altitude.altitude);
                }
            }
            altitudes.add(altitude);
        }

        NumberFormat nf = NumberFormat.getInstance();

        // set distance views
        if (distance >= 1000) {
            distance = distance / 1000;
            nf.setMaximumFractionDigits(2);
            distanceValueView.setText(String.format("%skm", nf.format(distance)));
        } else {
            nf.setMaximumFractionDigits(0);
            distanceValueView.setText(String.format("%sm", nf.format(distance)));
        }

        // set speed views
        float avg = 0;
        if (speeds.size() != 0 && speedTotal != 0) {
            avg = speedTotal / speeds.size();
        }
        speeds.add(0, new Speed(avg));
        nf.setMaximumFractionDigits(1);
        speedMaxView.setText(nf.format(speedMax));
        speedMinView.setText(nf.format(speedMin));
        speedAvgView.setText(nf.format(avg));

        // set altitude views
        nf.setMaximumFractionDigits(0);
        altMaxView.setText(nf.format(altMax));
        altMinView.setText(nf.format(altMin));
        altGainView.setText(nf.format(altGain));
        altLossView.setText(nf.format(altLoss));
    }

    /**
     * Function called at the creation of the activity, initializes some variables.
     * @param savedInstanceState the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_report);

        // recover locations list from main activity
        Intent intent = getIntent();
        locations = intent.getParcelableArrayListExtra("locations");

        // init altitudes and speeds lists
        altitudes = new ArrayList<>();
        speeds = new ArrayList<>();

        // display race overview
        displayTimeRace();
        displayRaceOverview();

        // init an object that contains altitudes / speeds lists and other important variables
        // and set the collection of the graph view.
        RaceGraphView raceGraph = findViewById(R.id.race_graph);
        Bundle collection = new Bundle();
        collection.putParcelableArrayList("speeds", (ArrayList<? extends Parcelable>) speeds);
        collection.putParcelableArrayList("altitudes", (ArrayList<? extends Parcelable>) altitudes);
        collection.putFloat("altMax", altMax);
        collection.putFloat("altMin", altMin);
        collection.putFloat("speedMax", speedMax);
        collection.putFloat("speedMin", speedMin);
        raceGraph.setCollection(collection);
    }
}
