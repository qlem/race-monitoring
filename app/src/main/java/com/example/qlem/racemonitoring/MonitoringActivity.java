package com.example.qlem.racemonitoring;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

public class MonitoringActivity extends AppCompatActivity {

    List<Location> locations;

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
        TextView distanceUnitView = findViewById(R.id.distance_unit);

        // speed views
        TextView speedMaxView = findViewById(R.id.speed_max_value);
        TextView speedMinView = findViewById(R.id.speed_min_value);
        TextView speedAvgView = findViewById(R.id.speed_avg_value);

        // altitude views
        TextView altMaxView = findViewById(R.id.elevation_max_value);
        TextView altMinView = findViewById(R.id.elevation_min_value);
        TextView altGainView = findViewById(R.id.elevation_gain_value);
        TextView altLossView = findViewById(R.id.elevation_loss_value);

        float distance = 0;

        float speedMax = 0;
        float speedMin = 0;
        float speedTotal = 0;

        double altMax = 0;
        double altMin = 0;
        double altGain = 0;
        double altLoss = 0;

        // loop
        for (int i = 0; i < locations.size(); i++) {

            // init
            Location current = locations.get(i);
            float speed = current.getAccuracy();
            double altitude = current.getAltitude();
            if (i == 0) {
                speedMin = speed;
                speedMax = speed;
                altMax = altitude;
                altMin = altitude;
            }

            // speed
            if (speed > speedMax) {
                speedMax = speed;
            } else if (speed < speedMin) {
                speedMin = speed;
            }
            speedTotal += speed;

            // altitude + distance
            if (altitude > altMax) {
                altMax = altitude;
            } else if (altitude < altMin) {
                altMin = altitude;
            }
            if (i < locations.size() - 1) {
                distance += current.distanceTo(locations.get(i + 1));
                double nextAltitude = locations.get(i + 1).getAltitude();
                if (nextAltitude > altitude) {
                    altGain += (nextAltitude - altitude);
                } else if (nextAltitude < altitude) {
                    altLoss += (nextAltitude - altitude);
                }
            }
        }

        NumberFormat nf = NumberFormat.getInstance();

        // set textView distance
        if (distance >= 1000) {
            distance = distance / 1000;
            nf.setMaximumFractionDigits(2);
            distanceUnitView.setText(R.string.distance_unit_km);
            distanceValueView.setText(nf.format(distance));
        } else {
            nf.setMaximumFractionDigits(0);
            distanceValueView.setText(nf.format(distance));
        }

        // set textView speed
        float avg = speedTotal / locations.size();
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
        setContentView(R.layout.activity_monitoring);

        Intent intent = getIntent();
        locations = intent.getParcelableArrayListExtra("locations");

        RaceGraphView raceGraph = findViewById(R.id.race_graph);
        raceGraph.setCollection(locations);

        displayTimeRace();
        displayRaceOverview();
    }
}
