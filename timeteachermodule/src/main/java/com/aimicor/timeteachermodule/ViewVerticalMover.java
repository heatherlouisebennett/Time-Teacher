package com.aimicor.timeteachermodule;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class ViewVerticalMover implements View.OnTouchListener, Animation.AnimationListener, Runnable {

    static final float ANIMATION_DURATION = 250;

    private final View mMoveableView;
    private final float mOpenPosition;
    private final TranslateAnimationFactory mAnimationFactory;
    private final Handler mHandler;
    private float mTouchDownPosition;
    private int mActivePointerId;

    public ViewVerticalMover(View moveableView, float openPosition) {
        this(moveableView, openPosition, new TranslateAnimationFactory(), new Handler());
    }

    ViewVerticalMover(View moveableView, float openPosition, TranslateAnimationFactory animationFactory, Handler handler) {
        mMoveableView = moveableView;
        mOpenPosition = openPosition;
        mAnimationFactory = animationFactory;
        mHandler = handler;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case ACTION_DOWN:
                actionDown(event);
                break;
            case ACTION_MOVE:
                actionMove(event);
                break;
            case ACTION_POINTER_UP:
                actionPointerUp(event, event.getActionIndex());
                break;
            case ACTION_UP:
                actionUp(event);
        }
        return true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mHandler.post(this);
    }

    @Override
    public void run() {
        if (mMoveableView.getY() < mOpenPosition / 2) {
            mMoveableView.setY(0);
        } else {
            mMoveableView.setY(mOpenPosition);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void actionUp(MotionEvent event) {
        if(event.getPointerId(event.getActionIndex()) == mActivePointerId) {
            actionUp();
        }
    }

    private void actionUp() {
        float viewPosition = mMoveableView.getY();
        TranslateAnimation animation = mAnimationFactory.createWithDeltas(0, 0, 0, getToYDelta(viewPosition));
        animation.setDuration(getDurationMillis(viewPosition));
        animation.setAnimationListener(this);
        mMoveableView.startAnimation(animation);
    }

    private void actionPointerUp(MotionEvent event, int pointerIndex) {
        if (event.getPointerId(pointerIndex) ==mActivePointerId) {
            setReferencePoint(event, pointerIndex == 0 ? 1 : 0);
        }
    }

    private void setReferencePoint(MotionEvent event, int pointerIndex) {
        mTouchDownPosition = event.getY(pointerIndex);
        mActivePointerId = event.getPointerId(pointerIndex);
    }

    private void actionMove(MotionEvent event) {
        float activePointerPosition = event.getY(event.findPointerIndex(mActivePointerId));
        float newPosition = mMoveableView.getY() + activePointerPosition - mTouchDownPosition;
        mMoveableView.setY(min(max(newPosition, 0), mOpenPosition));
    }

    private void actionDown(MotionEvent event) {
        setReferencePoint(event, event.getActionIndex());
    }

    private float getToYDelta(float viewPosition) {
        if (viewPosition < mOpenPosition / 2) {
            return -viewPosition;
        } else {
            return mOpenPosition - viewPosition;
        }
    }

    private long getDurationMillis(float viewPosition) {
        if (viewPosition < mOpenPosition / 2) {
            return (long) (viewPosition / mOpenPosition * ANIMATION_DURATION);
        }
        return (long) ((mOpenPosition - viewPosition) / mOpenPosition * ANIMATION_DURATION);
    }
}