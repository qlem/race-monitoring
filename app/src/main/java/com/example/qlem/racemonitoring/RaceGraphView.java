package com.example.qlem.racemonitoring;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;

import java.text.NumberFormat;
import java.util.List;

public class RaceGraphView extends View {

    private List<Location> locations;

    private int VIEW_WIDTH;
    private int VIEW_HEIGHT;
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
    private RectF background;

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
        background = new RectF();
        altitudeColor = Color.rgb(84, 108, 54);
        speedColor = Color.rgb(221, 149, 48);
        axesColor = Color.rgb(181, 59, 118);
    }

    public void setCollection(List<Location> locations) {
        this.locations = locations;
        float currentAltitude;
        float currentSpeed;
        for (int i = 0; i < locations.size(); i++) {
            currentAltitude = (float) locations.get(i).getAltitude();
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
        diffAltitude = maxAltitude - minAltitude;
        diffSpeed = maxSpeed - minSpeed;
        invalidate();
    }

    private void drawLabels(Canvas canvas) {

        // draw altitude label
        pen.setTextSize((float) (GRAPH_START_Y * 0.4));
        pen.setFakeBoldText(true);
        float textY = (GRAPH_START_Y / 2) + (pen.getTextSize() * 1/3);
        pen.setTextAlign(Paint.Align.RIGHT);
        pen.setColor(altitudeColor);
        canvas.drawText("ALT", GRAPH_START_X, textY, pen);

        // init number format
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
        pen.setTextAlign(Paint.Align.LEFT);
        pen.setColor(speedColor);
        canvas.drawText("SPE", GRAPH_START_X + GRAPH_WIDTH, textY, pen);

        // draw min and max value for speed
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

        float startYAlt;
        float stopYAlt;
        float currentAltitude;
        float nextAltitude;

        float startYSpeed;
        float stopYSpeed;
        float currentSpeed;
        float nextSpeed;

        int nbPoints = locations.size();
        pen.setStrokeWidth(5);
        for (int i = 0; i < nbPoints - 1; i++) {
            startX = i * GRAPH_WIDTH / (nbPoints - 1) + GRAPH_START_X;
            stopX = (i + 1) * GRAPH_WIDTH / (nbPoints - 1) + GRAPH_START_X;

            currentAltitude = (float) locations.get(i).getAltitude();
            nextAltitude = (float) locations.get(i + 1).getAltitude();
            startYAlt = (currentAltitude - minAltitude) * -GRAPH_HEIGHT / diffAltitude + GRAPH_HEIGHT + GRAPH_START_Y;
            stopYAlt = (nextAltitude - minAltitude) * -GRAPH_HEIGHT / diffAltitude + GRAPH_HEIGHT + GRAPH_START_Y;
            pen.setColor(altitudeColor);
            canvas.drawLine(startX, startYAlt, stopX, stopYAlt, pen);

            currentSpeed = locations.get(i).getAccuracy();
            nextSpeed = locations.get(i + 1).getAccuracy();
            startYSpeed = (currentSpeed - minSpeed) * -GRAPH_HEIGHT / diffSpeed + GRAPH_HEIGHT + GRAPH_START_Y;
            stopYSpeed = (nextSpeed - minSpeed) * -GRAPH_HEIGHT / diffSpeed + GRAPH_HEIGHT + GRAPH_START_Y;
            pen.setColor(speedColor);
            canvas.drawLine(startX, startYSpeed, stopX, stopYSpeed, pen);
        }
    }

    @Override
    protected void onDraw (Canvas canvas) {

        if (locations == null) {
            return;
        }

        // TODO remove background (test)
        pen.setColor(Color.rgb(210, 210, 210));
        background.set(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
        // canvas.drawRect(background, pen);

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
        if (widthScreen < heightScreen) {
            VIEW_WIDTH = (int) (widthScreen * 0.95);
            VIEW_HEIGHT = (int) (heightScreen * 0.35);
        } else {
            VIEW_WIDTH = (int) (widthScreen * 0.45);
            VIEW_HEIGHT = (int) (heightScreen * 0.5);
        }
        GRAPH_WIDTH = (float) (VIEW_WIDTH * 0.80);
        GRAPH_HEIGHT = (float) (VIEW_HEIGHT * 0.75);
        GRAPH_START_X = (VIEW_WIDTH - GRAPH_WIDTH) / 2;
        GRAPH_START_Y = (float) ((VIEW_HEIGHT - GRAPH_HEIGHT) * 0.7);
        setMeasuredDimension(VIEW_WIDTH, VIEW_HEIGHT);
    }
}
