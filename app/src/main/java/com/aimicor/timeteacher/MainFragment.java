package com.aimicor.timeteacher;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;


public class MainFragment extends Fragment implements View.OnClickListener, Animation.AnimationListener, ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener {

    private View mMinuteView;

    private View mMinuteViewPlaceHolderImg;
    private float mMinuteViewOpenPosition;
    private View mRoot;
    private float mFirstY;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_main, container, false);
        mMinuteView = mRoot.findViewById(R.id.minute_view);
        mMinuteViewPlaceHolderImg = mRoot.findViewById(R.id.minute_view_placeholder_img);
//        mMinuteView.setOnClickListener(this);
        mMinuteView.setOnTouchListener(this);
        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(this);
        return mRoot;
    }

    @Override
    public void onGlobalLayout() {
        mMinuteViewOpenPosition = mMinuteView.getRootView().findViewById(R.id.minute_view_placeholder).getY();
        mRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mFirstY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float newY = mMinuteView.getY() + event.getY() - mFirstY;
                if (newY >= 0 && newY <= mMinuteViewOpenPosition) {
                    mMinuteView.setY(newY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mMinuteView.getY() < mMinuteViewOpenPosition / 2) {
                    mMinuteView.setY(0);
                } else {
                    mMinuteView.setY(mMinuteViewOpenPosition);
                }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        TranslateAnimation animation = getTranslateAnimation();
        animation.setDuration(250);
        animation.setAnimationListener(this);
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
}
