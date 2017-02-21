package com.aimicor.timeteachermodule;

import org.junit.Before;
import org.junit.Test;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MinuteViewVerticalMoverImplTest {

    private MinuteViewVerticalMoverImpl subject;

    private View mockMinuteView;
    private TranslateAnimationFactory mockAnimationFactory;

    private static final float openPosition = 100;
    private static final float mFullAnimationDuration = MinuteViewVerticalMoverImpl.ANIMATION_DURATION;

    @Before
    public void setup() {
        mockMinuteView = mock(View.class);
        mockAnimationFactory = mock(TranslateAnimationFactory.class);
        subject = new MinuteViewVerticalMoverImpl(mockMinuteView, openPosition, mockAnimationFactory);
    }

    @Test
    public void on_touch_returns_true() {
        assertThat(subject.onTouch(mockMinuteView, motionEvent(ACTION_DOWN, 30f))).isTrue();
        assertThat(subject.onTouch(mockMinuteView, motionEvent(ACTION_MOVE, 30f))).isTrue();

        when(mockAnimationFactory.createWithDeltas(eq(0f), eq(0f), eq(0f), anyFloat())).thenReturn(mock(TranslateAnimation.class));
        assertThat(subject.onTouch(mockMinuteView, motionEvent(ACTION_UP, 30f))).isTrue();
    }

    @Test
    public void can_move_view_down() {
        subject.onTouch(mockMinuteView, motionEvent(ACTION_DOWN, 10f));
        subject.onTouch(mockMinuteView, motionEvent(ACTION_MOVE, 20f));

        verify(mockMinuteView).setY(10f);
    }

    @Test
    public void can_move_view_up() {
        when(mockMinuteView.getY()).thenReturn(30f);

        subject.onTouch(mockMinuteView, motionEvent(ACTION_DOWN, 30f));
        subject.onTouch(mockMinuteView, motionEvent(ACTION_MOVE, 20f));

        verify(mockMinuteView).setY(20f);

    }

    @Test
    public void cant_move_above_closed_position() {
        when(mockMinuteView.getY()).thenReturn(30f);

        subject.onTouch(mockMinuteView, motionEvent(ACTION_DOWN, 50f));
        subject.onTouch(mockMinuteView, motionEvent(ACTION_MOVE, 0f));

        verify(mockMinuteView).setY(0);
    }

    @Test
    public void cant_move_below_open_position() {
        when(mockMinuteView.getY()).thenReturn(80f);

        subject.onTouch(mockMinuteView, motionEvent(ACTION_DOWN, 10f));
        subject.onTouch(mockMinuteView, motionEvent(ACTION_MOVE, 40f));

        verify(mockMinuteView).setY(openPosition);
    }

    @Test
    public void view_animates_back_to_closed_if_less_than_half_way_open() {
        TranslateAnimation animation = mock(TranslateAnimation.class);
        when(mockAnimationFactory.createWithDeltas(0f, 0f, 0f, -30f)).thenReturn(animation);
        when(mockMinuteView.getY()).thenReturn(30f);

        subject.onTouch(mockMinuteView, motionEvent(ACTION_UP, 30f));

        verify(mockMinuteView).startAnimation(animation);
        verify(animation).setDuration((long) (30f / openPosition * mFullAnimationDuration));
        verify(animation).setAnimationListener(subject);
    }

    @Test
    public void view_animates_to_open_if_more_than_half_way_open() {
        TranslateAnimation animation = mock(TranslateAnimation.class);
        when(mockAnimationFactory.createWithDeltas(0, 0, 0, 30f)).thenReturn(animation);
        when(mockMinuteView.getY()).thenReturn(70f);

        subject.onTouch(mockMinuteView, motionEvent(ACTION_UP, 70f));

        verify(mockMinuteView).startAnimation(animation);
        verify(animation).setDuration((long) (30f / openPosition * mFullAnimationDuration));
        verify(animation).setAnimationListener(subject);
    }

    @Test
    public void view_stays_at_closed_when_animation_ends(){
        when(mockMinuteView.getY()).thenReturn(30f);
        subject.onAnimationEnd(null);
        verify(mockMinuteView).setY(0);
    }

    @Test
    public void view_stays_at_open_when_animation_ends(){
        when(mockMinuteView.getY()).thenReturn(60f);
        subject.onAnimationEnd(null);
        verify(mockMinuteView).setY(openPosition);
    }

    @NonNull
    private MotionEvent motionEvent(int action, float value) {
        MotionEvent event = mock(MotionEvent.class);
        when(event.getAction()).thenReturn(action);
        when(event.getY()).thenReturn(value);
        return event;
    }
}