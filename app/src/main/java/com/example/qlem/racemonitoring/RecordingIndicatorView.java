package com.example.qlem.racemonitoring;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * This class creates an indicator that describes the state
 * of the race recording (stopped, running or updating).
 */
public class RecordingIndicatorView extends View {

    /**
     * The pen used for draw.
     */
    private Paint pen;

    /**
     * Class constructor.
     * @param context a context
     */
    public RecordingIndicatorView(Context context) {
        super(context);
        init();
    }

    /**
     * Class constructor.
     * @param context a context
     * @param attrs a set of attributes
     */
    public RecordingIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Class constructor.
     * @param context a context
     * @param attrs a set of attributes
     * @param defStyleAttr a style attribute
     */
    public RecordingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Function that is called when a location update occur.
     */
    public void locationUpdate() {
        pen.setColor(Color.GREEN);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                pen.setColor(Color.BLUE);
                invalidate();
            }
        }, 1500);
        invalidate();
    }

    /**
     * Function that is called when the recording is stopped.
     */
    public void stopRecording() {
        pen.setColor(Color.RED);
        invalidate();
    }

    /**
     * Function that is called when the recording is started.
     */
    public void startRecording() {
        pen.setColor(Color.BLUE);
        invalidate();
    }

    /**
     * Function that is called in each constructor for initializes some variables.
     */
    private void init() {
        pen = new Paint(Paint.ANTI_ALIAS_FLAG);
        pen.setColor(Color.RED);
    }

    /**
     * Function that is called for draw the view.
     * @param canvas the canvas of the view
     */
    @Override
    protected void onDraw (Canvas canvas) {
        canvas.drawCircle(30, 30, 30, pen);
    }

    /**
     * Function that is called at the creation of the view allows to handle and set the size
     * and measures of the view.
     * @param widthMeasureSpec the width screen
     * @param heightMeasureSpec the height screen
     */
    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(60, 60);
    }
}
