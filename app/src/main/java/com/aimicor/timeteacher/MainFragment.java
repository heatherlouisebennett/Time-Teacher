package com.aimicor.timeteacher;

import com.aimicor.timeteachermodule.ViewMover;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;


public class MainFragment extends Fragment implements View.OnClickListener, Animation.AnimationListener, ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener, GestureDetector.OnGestureListener {

    private View mMinuteView;

    private View mMinuteViewPlaceHolderImg;
    private float mMinuteViewOpenPosition;
    private View mRoot;
    private float mFirstY;

    private float mLastTouchY;
    private int mActivePointerId;
    private GestureDetector mGestureDetector;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGestureDetector = new GestureDetector(container.getContext(), this);
        mRoot = inflater.inflate(R.layout.fragment_main, container, false);
        mMinuteView = mRoot.findViewById(R.id.minute_view);
        mMinuteViewPlaceHolderImg = mRoot.findViewById(R.id.minute_view_placeholder_img);
//        mMinuteView.setOnClickListener(this);
        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mRoot.setScaleX(1f);
        return mRoot;
    }

    @Override
    public void onGlobalLayout() {
        Log.i("mydebug", "orientation=" + mMinuteView.getContext().getResources().getConfiguration().orientation);
        mMinuteViewOpenPosition = mMinuteView.getRootView().findViewById(R.id.minute_view_placeholder).getY();
        ViewMover onTouchListener = new ViewMover();
        onTouchListener.onGlobalLayout(mMinuteView, mMinuteView.getRootView().findViewById(R.id.minute_view_placeholder));
        mMinuteView.setOnTouchListener(onTouchListener);
//        mMinuteView.setOnTouchListener(this);
        mRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        return mGestureDetector.onTouchEvent(event);
        return legacyOnTouch(event);
    }

    private boolean legacyOnTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                Log.w("mydebug", "ACTION_DOWN event finger=" + event.getActionIndex() + "," + event.getPointerId(event.getActionIndex()) + " Active finger=" + event.findPointerIndex(mActivePointerId) + "," + mActivePointerId);
                final int pointerIndex = event.getActionIndex();
                mLastTouchY = event.getY(pointerIndex);
                mActivePointerId = event.getPointerId(pointerIndex);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                mMinuteView.setY(mMinuteView.getY() + event.getY(event.findPointerIndex(mActivePointerId)) - mLastTouchY);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.w("mydebug", "ACTION_POINTER_UP event finger=" + event.getActionIndex() + "," + event.getPointerId(event.getActionIndex()) + " Active finger=" + event.findPointerIndex(mActivePointerId) + "," + mActivePointerId);
                int pointerIndex = event.getActionIndex();
                if (event.getPointerId(pointerIndex) == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    pointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchY = event.getY(pointerIndex);
                    mActivePointerId = event.getPointerId(pointerIndex);
                    break;
                }




            case MotionEvent.ACTION_UP:
                if(event.getPointerId(event.getActionIndex()) == mActivePointerId) {
                    Log.i("mydebug", "ACTION_UP event finger=" + event.getActionIndex() + "," + event.getPointerId(event.getActionIndex()) + " Active finger=" + event.findPointerIndex(mActivePointerId) + "," + mActivePointerId);
                    TranslateAnimation animation = getTranslateAnimation2();
                    animation.setDuration(100);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Runnable myRunnableNamed = new MyRunnable();
                            Runnable myRunnableAnon = new Runnable() {
                                @Override
                                public void run() {

                                }
                            };
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mMinuteView.getY() < mMinuteViewOpenPosition / 2) {
                                        mMinuteView.setY(0);
                                    } else {
                                        mMinuteView.setY(mMinuteViewOpenPosition);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    mMinuteView.startAnimation(animation);
                }
        }
        return true;
    }

    private TranslateAnimation getTranslateAnimation2() {
        float y = mMinuteView.getY();
        if(y < mMinuteViewOpenPosition / 2)
            return new TranslateAnimation(0, 0, 0, -y);
        return new TranslateAnimation(0, 0, 0, mMinuteViewOpenPosition - y);
    }

    @Override
    public void onClick(View v) {
        TranslateAnimation animation = getTranslateAnimation();
        animation.setDuration(250);
        animation.setAnimationListener(this);
        animation.setInterpolator(new DecelerateInterpolator(10000f));
        mMinuteView.startAnimation(animation);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (isMinuteViewClosed()) {
            mMinuteView.setY(mMinuteViewOpenPosition);
        } else {
            mMinuteView.setY(0);

        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @NonNull
    private TranslateAnimation getTranslateAnimation() {
        if (isMinuteViewClosed()) {
            return new TranslateAnimation(0, 0, 0, mMinuteViewOpenPosition);
        }
        return new TranslateAnimation(0, 0, 0, -mMinuteViewOpenPosition);
    }

    private boolean isMinuteViewClosed() {
        return mMinuteView.getY() == 0;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i("mydebug", "onFling " + velocityX + ", " + velocityY);
        return false;
    }

    private class MyRunnable implements Runnable {

        @Override
        public void run() {

        }
    }
}
