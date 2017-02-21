package com.aimicor.timeteachermodule;

import android.view.animation.TranslateAnimation;

class MoveAnimation extends TranslateAnimation {

    private final float mFromXDelta;
    private final float mToXDelta;
    private final float mFromYDelta;
    private final float mToYDelta;

    MoveAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        super(fromXDelta, toXDelta, fromYDelta, toYDelta);
        mFromXDelta = fromXDelta;
        mToXDelta = toXDelta;
        mFromYDelta = fromYDelta;
        mToYDelta = toYDelta;
    }

    float getFromXDelta() {
        return mFromXDelta;
    }

    float getToXDelta() {
        return mToXDelta;
    }

    float getFromYDelta() {
        return mFromYDelta;
    }

    float getToYDelta(){
        return mToYDelta;
    }
}
