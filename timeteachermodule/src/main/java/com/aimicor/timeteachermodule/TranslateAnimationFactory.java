package com.aimicor.timeteachermodule;

import android.view.animation.TranslateAnimation;

class TranslateAnimationFactory {

    TranslateAnimation createWithDeltas(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        return new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
    }
}
