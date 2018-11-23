package com.example.qlem.racemonitoring;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RecordingIndicatorView extends View {

    private Paint pen;

    public RecordingIndicatorView(Context context) {
        super(context);
        init();
    }

    public RecordingIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

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

    public void stopRecording() {
        pen.setColor(Color.RED);
        invalidate();
    }

    public void startRecording() {
        pen.setColor(Color.BLUE);
        invalidate();
    }

    private void init() {
        pen = new Paint(Paint.ANTI_ALIAS_FLAG);
        pen.setColor(Color.RED);
    }

    @Override
    protected void onDraw (Canvas canvas) {
        canvas.drawCircle(30, 30, 30, pen);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(60, 60);
    }
}
