package com.aimicor.timeteachermodule;

import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.max;
import static java.lang.Math.min;

class MinuteViewVerticalMoverImpl implements View.OnTouchListener {

    private final View mMinuteView;
    private final float mOpenPosition;
    private float mTouchDownPosition;

    MinuteViewVerticalMoverImpl(View minuteView, float openPosition) {
        mMinuteView = minuteView;
        mOpenPosition = openPosition;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownPosition = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMinuteView.setY(min(max(mMinuteView.getY() + event.getY() - mTouchDownPosition, 0), mOpenPosition));
                break;
            case MotionEvent.ACTION_UP:
                mMinuteView.startAnimation(new MoveAnimation(0, 0, 0, -30f));
        }
        return false;
    }
}
