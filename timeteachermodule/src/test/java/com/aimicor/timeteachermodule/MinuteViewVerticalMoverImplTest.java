package com.aimicor.timeteachermodule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MinuteViewVerticalMoverImplTest {

    private MinuteViewVerticalMoverImpl subject;

    private View mockMinuteView;

    private static final float openPosition = 100;
    private static final float mFullAnimationDuration = 250f;

    @Before
    public void setup() {
        mockMinuteView = mock(View.class);
        subject = new MinuteViewVerticalMoverImpl(mockMinuteView, openPosition);
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
        ArgumentCaptor<MoveAnimation> animationCaptor = ArgumentCaptor.forClass(MoveAnimation.class);

        subject.onTouch(mockMinuteView, motionEvent(ACTION_UP, 30f));

        verify(mockMinuteView).startAnimation(animationCaptor.capture());
        MoveAnimation moveAnimation = animationCaptor.getValue();
        assertThat(onlyToYDeltaSet(moveAnimation, -30f)).isTrue();
        assertThat(moveAnimation.getDuration()).isEqualTo(30f / openPosition * mFullAnimationDuration);
    }

    @NonNull
    private MotionEvent motionEvent(int action, float value) {
        MotionEvent event = mock(MotionEvent.class);
        when(event.getAction()).thenReturn(action);
        when(event.getY()).thenReturn(value);
        return event;
    }

    private boolean onlyToYDeltaSet(MoveAnimation moveAnimation, float toYDelta) {
        return moveAnimation.getFromXDelta() == 0
                && moveAnimation.getToXDelta() == 0
                && moveAnimation.getFromYDelta() == 0
                && moveAnimation.getToYDelta() == toYDelta;
    }
}