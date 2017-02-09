package com.aimicor.timeteachermodule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import static android.graphics.Color.BLACK;
import static android.graphics.Paint.Style.STROKE;

public class CircleView extends SquareView {

    private static final float STROKE_WIDTH_FACTOR = 1f;

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void draw(Canvas canvas, int diameter) {
        float radius = diameter / 2f;
        float strokeWidth = STROKE_WIDTH_FACTOR * diameter / 100f;
        canvas.drawCircle(radius, radius, radius - strokeWidth / 2f, getPaint(strokeWidth));
    }

    @NonNull
    private Paint getPaint(float strokeWidth) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(BLACK);
        return paint;
    }
}
