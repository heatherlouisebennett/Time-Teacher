package com.aimicor.timeteachermodule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

public class CircleView extends SquareView {

    private static final int STROKE_WIDTH = 10;
    private final Paint mPaint = getPaint();

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void draw(Canvas canvas, int diameter) {
        int radius = diameter / 2;
        canvas.drawCircle(radius, radius, radius - STROKE_WIDTH / 2, mPaint);
    }

    @NonNull
    private Paint getPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setColor(Color.BLACK);
        return paint;
    }
}
