package com.aimicor.timeteachermodule;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MovableCircleView extends CircleView {

    private float mLastY;
    private float mFirstY;

    public MovableCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                actionCancel(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                actionPointerUp(event);
                break;
        }
        return true;
    }

    private void actionDown(MotionEvent event) {
        Log.i("mydebug", "actionDown");
        mFirstY = event.getY();
        mLastY = mFirstY;
    }

    private void actionMove(MotionEvent event) {
        float newY = event.getY();
        setY(getY() + newY - mFirstY);
        mLastY = newY;
    }

    private void actionUp(MotionEvent event) {
        Log.i("mydebug", "actionUp");
    }

    private void actionCancel(MotionEvent event) {
        Log.i("mydebug", "actionCancel");
    }

    private void actionPointerUp(MotionEvent event) {
        Log.i("mydebug", "actionPointerUp");
    }
}
