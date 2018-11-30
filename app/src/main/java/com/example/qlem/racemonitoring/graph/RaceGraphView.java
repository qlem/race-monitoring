package com.example.qlem.racemonitoring.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import java.text.NumberFormat;
import java.util.List;

public class RaceGraphView extends View {

    private List<Altitude> altitudes;
    private List<Speed> speeds;

    private int VIEW_WIDTH;
    private float GRAPH_WIDTH;
    private float GRAPH_HEIGHT;
    private float GRAPH_START_X;
    private float GRAPH_START_Y;

    private float maxAltitude;
    private float minAltitude;
    private float diffAltitude;
    private float maxSpeed;
    private float minSpeed;
    private float diffSpeed;

    private Paint pen;

    private int altitudeColor;
    private int speedColor;
    private int axesColor;


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
        altitudeColor = Color.rgb(84, 108, 54);
        speedColor = Color.rgb(221, 149, 48);
        axesColor = Color.rgb(181, 59, 118);
    }

    public void setCollection(Bundle collection) {
        altitudes = collection.getParcelableArrayList("altitudes");
        speeds = collection.getParcelableArrayList("speeds");
        maxAltitude = collection.getFloat("altMax");
        minAltitude = collection.getFloat("altMin");
        maxSpeed = collection.getFloat("speedMax");
        minSpeed = collection.getFloat("speedMin");
        diffAltitude = maxAltitude - minAltitude;
        diffSpeed = maxSpeed - minSpeed;
        invalidate();
    }

    private void drawLabels(Canvas canvas) {

        // draw altitude label
        pen.setTextSize((float) (GRAPH_START_Y * 0.4));
        pen.setFakeBoldText(true);
        float textY = (GRAPH_START_Y / 2) + (pen.getTextSize() * 1/3);
        pen.setTextAlign(Paint.Align.LEFT);
        pen.setColor(altitudeColor);
        canvas.drawText("ALT m", 0, textY, pen);

        // init number format
        pen.setTextAlign(Paint.Align.RIGHT);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);

        // draw min and max value for altitude
        pen.setTextSize((float) (VIEW_WIDTH * 0.025));
        pen.setFakeBoldText(false);
        pen.setColor(axesColor);
        canvas.drawText(nf.format(maxAltitude), GRAPH_START_X - 12, GRAPH_START_Y, pen);
        canvas.drawText(nf.format(minAltitude), GRAPH_START_X - 12, GRAPH_START_Y + GRAPH_HEIGHT, pen);

        // draw speed label
        pen.setTextSize((float) (GRAPH_START_Y * 0.4));
        pen.setFakeBoldText(true);
        pen.setTextAlign(Paint.Align.RIGHT);
        pen.setColor(speedColor);
        canvas.drawText("m/s SPE", VIEW_WIDTH, textY, pen);

        // draw min and max value for speed
        pen.setTextAlign(Paint.Align.LEFT);
        pen.setTextSize((float) (VIEW_WIDTH * 0.025));
        pen.setFakeBoldText(false);
        pen.setColor(axesColor);
        canvas.drawText(nf.format(maxSpeed), GRAPH_START_X + GRAPH_WIDTH + 12, GRAPH_START_Y, pen);
        canvas.drawText(nf.format(minSpeed), GRAPH_START_X + GRAPH_WIDTH + 12, GRAPH_START_Y + GRAPH_HEIGHT, pen);

        // draw a horizontal line corresponding to the max value for altitude and speed
        pen.setStrokeWidth(2);
        canvas.drawLine(GRAPH_START_X, GRAPH_START_Y, GRAPH_START_X + GRAPH_WIDTH, GRAPH_START_Y, pen);

        // draw horizontal lines and their corresponding altitudes and speeds values
        float offset = GRAPH_HEIGHT / 5;
        for (float y = offset; y < GRAPH_HEIGHT; y += offset) {

            // draw a horizontal line
            canvas.drawLine(GRAPH_START_X, GRAPH_START_Y + y, GRAPH_START_X + GRAPH_WIDTH, GRAPH_START_Y + y, pen);

            // draw corresponding altitude values
            float altitude = diffAltitude * (y - GRAPH_HEIGHT) / - GRAPH_HEIGHT + minAltitude;
            pen.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(nf.format(altitude), GRAPH_START_X - 12, GRAPH_START_Y + y, pen);

            // draw corresponding speed values
            float speed = diffSpeed * (y - GRAPH_HEIGHT) / - GRAPH_HEIGHT + minSpeed;
            pen.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(nf.format(speed), GRAPH_START_X + GRAPH_WIDTH + 12, GRAPH_START_Y + y, pen);
        }
    }

    private void drawCurves(Canvas canvas) {

        float startX;
        float stopX;
        float startY;
        float stopY;
        float currentAltitude;
        float nextAltitude;
        float currentSpeed;
        float nextSpeed;

        pen.setStrokeWidth(5);

        int sizeList = altitudes.size();
        for (int i = 0; i < sizeList - 1; i++) {
            startX = i * GRAPH_WIDTH / (sizeList - 1) + GRAPH_START_X;
            stopX = (i + 1) * GRAPH_WIDTH / (sizeList - 1) + GRAPH_START_X;

            currentAltitude = altitudes.get(i).altitude;
            nextAltitude = altitudes.get(i + 1).altitude;
            startY = (currentAltitude - minAltitude) * -GRAPH_HEIGHT / diffAltitude + GRAPH_HEIGHT + GRAPH_START_Y;
            stopY = (nextAltitude - minAltitude) * -GRAPH_HEIGHT / diffAltitude + GRAPH_HEIGHT + GRAPH_START_Y;
            pen.setColor(altitudeColor);
            canvas.drawLine(startX, startY, stopX, stopY, pen);

            currentSpeed = speeds.get(i).speed;
            nextSpeed = speeds.get(i + 1).speed;
            startY = (currentSpeed - minSpeed) * -GRAPH_HEIGHT / diffSpeed + GRAPH_HEIGHT + GRAPH_START_Y;
            stopY = (nextSpeed - minSpeed) * -GRAPH_HEIGHT / diffSpeed + GRAPH_HEIGHT + GRAPH_START_Y;
            pen.setColor(speedColor);
            canvas.drawLine(startX, startY, stopX, stopY, pen);
        }
    }

    @Override
    protected void onDraw (Canvas canvas) {

        if (altitudes == null || speeds == null) {
            return;
        }

        float left = GRAPH_START_X;
        float top = GRAPH_START_Y;
        float right = GRAPH_START_X + GRAPH_WIDTH;
        float bottom = GRAPH_START_Y + GRAPH_HEIGHT;

        pen.setStrokeWidth(6);
        pen.setColor(axesColor);
        canvas.drawLine(left - 3, top - 10, left - 3, bottom + 6, pen);
        canvas.drawLine(left - 6, bottom + 3, right + 6, bottom + 3, pen);
        canvas.drawLine(right + 3, bottom + 6, right + 3, top - 10, pen);

        drawLabels(canvas);
        drawCurves(canvas);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        int widthScreen = MeasureSpec.getSize(widthMeasureSpec);
        int heightScreen = MeasureSpec.getSize(heightMeasureSpec);
        int viewHeight;
        if (widthScreen < heightScreen) {
            VIEW_WIDTH = (int) (widthScreen * 0.95);
            viewHeight = (int) (heightScreen * 0.35);
        } else {
            VIEW_WIDTH = (int) (widthScreen * 0.45);
            viewHeight = (int) (heightScreen * 0.5);
        }
        GRAPH_WIDTH = (float) (VIEW_WIDTH * 0.80);
        GRAPH_HEIGHT = (float) (viewHeight * 0.75);
        GRAPH_START_X = (VIEW_WIDTH - GRAPH_WIDTH) / 2;
        GRAPH_START_Y = (float) ((viewHeight - GRAPH_HEIGHT) * 0.7);
        setMeasuredDimension(VIEW_WIDTH, viewHeight);
    }
}
