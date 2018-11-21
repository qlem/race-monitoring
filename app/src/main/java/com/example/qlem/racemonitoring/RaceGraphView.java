package com.example.qlem.racemonitoring;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RaceGraphView extends View {

    public RaceGraphView(Context context) {
        super(context);
    }

    public RaceGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RaceGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {

    }

    @Override
    protected void onDraw (Canvas canvas) {

    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.i("DEBUG", String.format("w: %d h: %d", width, height));
        setMeasuredDimension(width, height);
    }
}
