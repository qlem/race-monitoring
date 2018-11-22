package com.example.qlem.racemonitoring;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class RaceGraphView extends View {

    private int WIDTH_VIEW;
    private int HEIGHT_VIEW;
    private List<Location> locations;
    private Paint pen;
    private double maxAltitude;
    private double minAltitude;
    private double maxSpeed;
    private double minSpeed;


    public RaceGraphView(Context context) {
        super(context);
        init();
    }

    public RaceGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RaceGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        pen = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setCollection(List<Location> locations) {
        this.locations = locations;
        double currentAltitude;
        double currentSpeed;
        for (int i = 0; i < locations.size(); i++) {
            currentAltitude = locations.get(i).getAltitude();
            currentSpeed = locations.get(i).getAccuracy();
            if (i == 0) {
                maxAltitude = currentAltitude;
                minAltitude = currentAltitude;
                maxSpeed = currentSpeed;
                minSpeed = currentSpeed;
            }
            if (currentAltitude > maxAltitude) {
                maxAltitude = currentAltitude;
            } else if (currentAltitude < minAltitude) {
                minAltitude = currentAltitude;
            }
            if (currentSpeed > maxSpeed) {
                maxSpeed = currentSpeed;
            } else if  (currentSpeed < minSpeed) {
                minSpeed = currentSpeed;
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw (Canvas canvas) {

        pen.setStrokeWidth(10);
        pen.setColor(Color.rgb(181, 59, 118));
        canvas.drawLine(0, HEIGHT_VIEW - 5, WIDTH_VIEW, HEIGHT_VIEW - 5, pen);
        canvas.drawLine(5, 0, 5, HEIGHT_VIEW, pen);

        if (locations == null) {
            return;
        }

        float startX;
        float stopX;

        float startYAlt;
        float stopYAlt;
        float currentAltitude;
        float nextAltitude;
        float diffAltitude = (float) (maxAltitude - minAltitude);

        float startYSpeed;
        float stopYSpeed;
        float currentSpeed;
        float nextSpeed;
        float diffSpeed = (float) (maxSpeed - minSpeed);

        int nbPoints = locations.size();
        pen.setStrokeWidth(5);
        for (int i = 0; i < nbPoints; i++) {
            if (i < nbPoints - 1) {

                startX = i * WIDTH_VIEW / (nbPoints - 1);
                stopX = (i + 1) * WIDTH_VIEW / (nbPoints - 1);

                currentAltitude = (float) locations.get(i).getAltitude();
                nextAltitude = (float) locations.get(i + 1).getAltitude();
                startYAlt = (currentAltitude - (float) minAltitude) * HEIGHT_VIEW / diffAltitude;
                stopYAlt = (nextAltitude - (float) minAltitude) * HEIGHT_VIEW / diffAltitude;
                pen.setColor(Color.rgb(84, 108, 54));
                canvas.drawLine(startX, HEIGHT_VIEW - startYAlt, stopX, HEIGHT_VIEW - stopYAlt, pen);

                currentSpeed = locations.get(i).getAccuracy();
                nextSpeed = locations.get(i + 1).getAccuracy();
                startYSpeed = (currentSpeed - (float) minSpeed) * HEIGHT_VIEW / diffSpeed;
                stopYSpeed = (nextSpeed - (float) minSpeed) * HEIGHT_VIEW / diffSpeed;
                pen.setColor(Color.rgb(221, 149, 48));
                canvas.drawLine(startX, HEIGHT_VIEW - startYSpeed, stopX, HEIGHT_VIEW - stopYSpeed, pen);
            }
        }
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        int widthScreen = MeasureSpec.getSize(widthMeasureSpec);
        int heightScreen = MeasureSpec.getSize(heightMeasureSpec);
        if (widthScreen < heightScreen) {
            WIDTH_VIEW = (int) (widthScreen * 0.85);
            HEIGHT_VIEW = (int) (widthScreen * 0.45);
        } else {
            WIDTH_VIEW = (int) (widthScreen * 0.45);
            HEIGHT_VIEW = (int) (heightScreen * 0.5);
        }
        setMeasuredDimension(WIDTH_VIEW, HEIGHT_VIEW);
    }
}
