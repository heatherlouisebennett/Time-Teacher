package com.aimicor.timeteachermodule;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static java.lang.Math.min;

public class SquareView extends View {

    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        draw(canvas, getWidth(), getHeight());
    }

    protected void draw(Canvas canvas, int sideLength) {
        // empty
    }

    private void draw(Canvas canvas, int width, int height) {
        if(width > 0) {
            if (width == height) {
                draw(canvas, width);
            } else {
                resize(width, height);
            }
        }
    }

    private void resize(int width, int height) {
        int sideLength = min(width, height);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = sideLength;
        layoutParams.width = sideLength;
        setLayoutParams(layoutParams);
    }
}
