package com.aimicor.timeteachermodule;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MinuteViewVerticalMoverImpl implements View.OnTouchListener, Animation.AnimationListener {

    private final View mMinuteView;
    private final float mOpenPosition;
    private final TranslateAnimationFactory mAnimationFactory;
    private float mTouchDownPosition;

    private static final float ANIMATION_DURATION = 1000;

    public MinuteViewVerticalMoverImpl(View minuteView, float openPosition) {
        this(minuteView, openPosition, new TranslateAnimationFactory());
    }

    MinuteViewVerticalMoverImpl(View minuteView, float openPosition, TranslateAnimationFactory animationFactory) {
        mMinuteView = minuteView;
        mOpenPosition = openPosition;
        mAnimationFactory = animationFactory;
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
                float viewPosition = mMinuteView.getY();
                TranslateAnimation animation = mAnimationFactory.createWithDeltas(0, 0, 0, getToYDelta(viewPosition));
                animation.setDuration(getDurationMillis(viewPosition));
                animation.setAnimationListener(this);
                mMinuteView.startAnimation(animation);
        }
        return true;
    }

    private long getDurationMillis(float viewPosition) {
        if (viewPosition < mOpenPosition / 2) {
            return (long) (viewPosition / mOpenPosition * ANIMATION_DURATION);
        }
        return (long) ((mOpenPosition - viewPosition) / mOpenPosition * ANIMATION_DURATION);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (mMinuteView.getY() < mOpenPosition / 2) {
            mMinuteView.setY(0);
        } else {
            mMinuteView.setY(mOpenPosition);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private float getToYDelta(float viewPosition) {
        if (viewPosition < mOpenPosition / 2) {
            return -viewPosition;
        } else {
            return mOpenPosition - viewPosition;
        }
    }
}
