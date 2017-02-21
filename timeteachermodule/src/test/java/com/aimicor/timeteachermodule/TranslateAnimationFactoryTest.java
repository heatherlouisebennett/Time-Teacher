package com.aimicor.timeteachermodule;

import org.junit.Test;

import android.view.animation.TranslateAnimation;

import static org.assertj.core.api.Assertions.assertThat;

public class TranslateAnimationFactoryTest {

    @Test
    public void can_create_animation_object() {
        assertThat(new TranslateAnimationFactory().createWithDeltas(0, 0, 0, 0)).isInstanceOf(TranslateAnimation.class);
    }
}