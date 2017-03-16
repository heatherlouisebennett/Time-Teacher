package com.aimicor.timeteachermodule;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

public interface PositionHandler {

    void setPos(View moveableView, float pos);

    float getPos(View moveableView);

    float getPos(MotionEvent event, int pointerIndex);

    TranslateAnimation getAnimation(float deltaPos);
}