package com.aimicor.timeteachermodule;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

public class VerticalPositionHandler implements PositionHandler {
    private final TranslateAnimationFactory mAnimationFactory;

    public VerticalPositionHandler(TranslateAnimationFactory animationFactory) {
        mAnimationFactory = animationFactory;
    }

    @Override
    public void setPos(View moveableView, float pos) {
        moveableView.setY(pos);
    }

    @Override
    public float getPos(View moveableView) {
        return moveableView.getY();
    }

    @Override
    public float getPos(MotionEvent event, int pointerIndex) {
        return event.getY(pointerIndex);
    }

    @Override
    public TranslateAnimation getAnimation(float deltaPos) {
        return mAnimationFactory.createWithDeltas(0, 0, 0, deltaPos);
    }
}