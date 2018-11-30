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

public class RaceReportActivity extends AppCompatActivity {

    private List<Location> locations;
    private List<Altitude> altitudes;
    private List<Speed> speeds;
    private float altMax;
    private float altMin;
    private float speedMax;
    private float speedMin;

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
            timeView.setText(String.format("%dh %dm %ds", hours, minutes, seconds));
        } else if (minutes > 0 ) {
            timeView.setText(String.format("%dm %ds", minutes, seconds));
        } else {
            timeView.setText(String.format("%ds", seconds));
        }
    }

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

            // distance
            if (i < locations.size() - 1) {
                distance += current.distanceTo(locations.get(i + 1));
            }

            // speed
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

            // altitude
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

        // set textView distance
        if (distance >= 1000) {
            distance = distance / 1000;
            nf.setMaximumFractionDigits(2);
            distanceValueView.setText(String.format("%skm", nf.format(distance)));
        } else {
            nf.setMaximumFractionDigits(0);
            distanceValueView.setText(String.format("%sm", nf.format(distance)));
        }

        // set textView speed
        float avg = 0;
        if (speeds.size() != 0 && speedTotal != 0) {
            avg = speedTotal / speeds.size();
        }
        speeds.add(0, new Speed(avg));
        nf.setMaximumFractionDigits(1);
        speedMaxView.setText(nf.format(speedMax));
        speedMinView.setText(nf.format(speedMin));
        speedAvgView.setText(nf.format(avg));

        // set textView altitude
        nf.setMaximumFractionDigits(0);
        altMaxView.setText(nf.format(altMax));
        altMinView.setText(nf.format(altMin));
        altGainView.setText(nf.format(altGain));
        altLossView.setText(nf.format(altLoss));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_report);

        Intent intent = getIntent();
        locations = intent.getParcelableArrayListExtra("locations");

        altitudes = new ArrayList<>();
        speeds = new ArrayList<>();

        displayTimeRace();
        displayRaceOverview();

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
