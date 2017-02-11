package com.aimicor.timeteacher;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;


public class MainFragment extends Fragment implements View.OnClickListener, Animation.AnimationListener, ViewTreeObserver.OnGlobalLayoutListener {

    private View mMinuteView;

    private View mMinuteViewPlaceHolderImg;
    private float mMinuteViewClosedPosition;
    private float mMinuteViewOpenPosition;
    private View mRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_main, container, false);
        mMinuteView = mRoot.findViewById(R.id.minute_view);
        mMinuteViewPlaceHolderImg = mRoot.findViewById(R.id.minute_view_placeholder_img);
        mMinuteView.setOnClickListener(this);
        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(this);
        return mRoot;
    }

    @Override
    public void onGlobalLayout() {
        mMinuteViewOpenPosition = mMinuteView.getRootView().findViewById(R.id.minute_view_placeholder).getY();
        mMinuteViewClosedPosition = mMinuteView.getY();
        mRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onClick(View v) {
        TranslateAnimation animation;
        if (isMinuteViewClosed()) {
            animation = new TranslateAnimation(
                    0, 0, mMinuteViewClosedPosition, mMinuteViewOpenPosition);
        } else {
            animation = new TranslateAnimation(
                    0, 0, mMinuteViewClosedPosition, -mMinuteViewOpenPosition);
        }
        animation.setDuration(250);
        animation.setAnimationListener(this);
        mMinuteView.startAnimation(animation);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(isMinuteViewClosed()) {
            mMinuteView.setY(mMinuteViewOpenPosition);
        } else {
            mMinuteView.setY(mMinuteViewClosedPosition);

        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private boolean isMinuteViewClosed() {
        return mMinuteView.getY() == mMinuteViewClosedPosition;
    }
}
