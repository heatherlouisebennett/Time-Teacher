package com.aimicor.timeteachermodule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Draws a simple white circle on which the numbers will be drawn.
 */
public class CircleView extends View {

    private final Paint mPaint;

    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int diameter = Math.min(getWidth(), getHeight());
        getLayoutParams().height = diameter;
        getLayoutParams().width = diameter;

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int viewWidth = getWidth();
        if (viewWidth == 0) {
            return;
        }

        int strokeWidth = 10;
        int radius = viewWidth / 2;

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(radius, radius, radius - strokeWidth/2, mPaint);
    }
}
