package com.aimicor.timeteachermodule;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class MinuteView extends CircleView {

    public MinuteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void draw(Canvas canvas, int diameter) {
        super.draw(canvas, diameter);
        float radius = diameter / 2f;
        float strokeWidth = STROKE_WIDTH_FACTOR * diameter / 100f;
        canvas.drawCircle(radius, radius, radius/5f, getPaint(strokeWidth));
    }
}
