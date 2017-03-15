package com.aimicor.timeteachermodule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ViewVerticalMoverTest {

    private ViewVerticalMover subject;

    @Mock
    private View mockMoveableView;

    @Mock
    private TranslateAnimationFactory mockAnimationFactory;

    @Mock
    private Handler mockHandler;

    private static final float openPosition = 100;
    private static final float mFullAnimationDuration = ViewVerticalMover.ANIMATION_DURATION;

    @Before
    public void setup() {
        initMocks(this);
        subject = new ViewVerticalMover(mockMoveableView, openPosition, mockAnimationFactory, mockHandler);
    }

    @Test
    public void on_touch_returns_true() {
        assertThat(subject.onTouch(mockMoveableView, motionEvent(ACTION_DOWN, 30f, 0, 0))).isTrue();
        assertThat(subject.onTouch(mockMoveableView, motionEvent(ACTION_MOVE, 30f, 0, 0))).isTrue();

        when(mockAnimationFactory.createWithDeltas(eq(0f), eq(0f), eq(0f), anyFloat())).thenReturn(mock(TranslateAnimation.class));
        assertThat(subject.onTouch(mockMoveableView, motionEvent(ACTION_UP, 30f, 0, 0))).isTrue();
    }

    @Test
    public void can_move_view_down() {
        subject.onTouch(mockMoveableView, motionEvent(ACTION_DOWN, 10f, 0, 0));
        subject.onTouch(mockMoveableView, motionEvent(ACTION_MOVE, 20f, 0, 0));

        verify(mockMoveableView).setY(10f);
    }

    @Test
    public void can_move_view_up() {
        when(mockMoveableView.getY()).thenReturn(30f);

        subject.onTouch(mockMoveableView, motionEvent(ACTION_DOWN, 30f, 0, 0));
        subject.onTouch(mockMoveableView, motionEvent(ACTION_MOVE, 20f, 0, 0));

        verify(mockMoveableView).setY(20f);

    }

    @Test
    public void cant_move_above_closed_position() {
        when(mockMoveableView.getY()).thenReturn(30f);

        subject.onTouch(mockMoveableView, motionEvent(ACTION_DOWN, 50f, 0, 0));
        subject.onTouch(mockMoveableView, motionEvent(ACTION_MOVE, 0f, 0, 0));

        verify(mockMoveableView).setY(0);
    }

    @Test
    public void cant_move_below_open_position() {
        when(mockMoveableView.getY()).thenReturn(80f);

        subject.onTouch(mockMoveableView, motionEvent(ACTION_DOWN, 10f, 0, 0));
        subject.onTouch(mockMoveableView, motionEvent(ACTION_MOVE, 40f, 0, 0));

        verify(mockMoveableView).setY(openPosition);
    }

    @Test
    public void view_animates_back_to_closed_if_less_than_half_way_open() {
        TranslateAnimation animation = mock(TranslateAnimation.class);
        when(mockAnimationFactory.createWithDeltas(0f, 0f, 0f, -30f)).thenReturn(animation);
        when(mockMoveableView.getY()).thenReturn(30f);

        subject.onTouch(mockMoveableView, motionEvent(ACTION_UP, 30f, 0, 0));

        verify(mockMoveableView).startAnimation(animation);
        verify(animation).setDuration((long) (30f / openPosition * mFullAnimationDuration));
        verify(animation).setAnimationListener(subject);
    }

    @Test
    public void view_animates_to_open_if_more_than_half_way_open() {
        TranslateAnimation animation = mock(TranslateAnimation.class);
        when(mockAnimationFactory.createWithDeltas(0, 0, 0, 30f)).thenReturn(animation);
        when(mockMoveableView.getY()).thenReturn(70f);

        subject.onTouch(mockMoveableView, motionEvent(ACTION_UP, 70f, 0, 0));

        verify(mockMoveableView).startAnimation(animation);
        verify(animation).setDuration((long) (30f / openPosition * mFullAnimationDuration));
        verify(animation).setAnimationListener(subject);
    }

    @Test
    public void view_stays_at_closed_when_animation_ends() {
        when(mockMoveableView.getY()).thenReturn(30f);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);

        subject.onAnimationEnd(null);

        verify(mockHandler).post(captor.capture());
        captor.getValue().run();
        verify(mockMoveableView).setY(0);
    }

    @Test
    public void view_stays_at_open_when_animation_ends() {
        when(mockMoveableView.getY()).thenReturn(60f);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);

        subject.onAnimationEnd(null);

        verify(mockHandler).post(captor.capture());
        captor.getValue().run();
        verify(mockMoveableView).setY(openPosition);
    }

    @Test
    public void remaining_second_finger_becomes_new_point_of_reference() {
        newReferenceTest(10f, 20f, 40f, 20f);
        newReferenceTest(15f, 30f, 45f, 15f);
        newReferenceTest(20f, 10f, 50f, 40f);
        newReferenceTest(5f, 95f, 50f, 0f);
    }

    private void newReferenceTest(float firstfingerposition, float secondfingerposition, float moveposition, float finalposition) {
        MotionEvent event = motionEvent(ACTION_POINTER_UP, firstfingerposition, 0, 0);
        when(event.getY(1)).thenReturn(secondfingerposition);
        when(event.getPointerId(1)).thenReturn(1);

        subject.onTouch(mockMoveableView, motionEvent(ACTION_DOWN, firstfingerposition, 0, 0));
        subject.onTouch(mockMoveableView, event);
        subject.onTouch(mockMoveableView, motionEvent(ACTION_MOVE, moveposition, 0, 1));

        verify(mockMoveableView).setY(finalposition);
    }


    @Test
    public void remaining_first_finger_not_affected_by_second_finger() {
        when(mockAnimationFactory.createWithDeltas(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(mock(TranslateAnimation.class));
        subject.onTouch(mockMoveableView, motionEvent(ACTION_DOWN, 10f, 0, 0));
        subject.onTouch(mockMoveableView, motionEvent(ACTION_UP, 70f, 1, 1));

        verify(mockMoveableView, never()).setY(anyFloat());
        verify(mockMoveableView, never()).startAnimation(any(TranslateAnimation.class));
    }

    @NonNull
    private MotionEvent motionEvent(int action, float value, int pointerIndex, int pointerId) {
        MotionEvent event = mock(MotionEvent.class);
        when(event.getActionIndex()).thenReturn(pointerIndex);
        when(event.getPointerId(pointerIndex)).thenReturn(pointerId);
        when(event.findPointerIndex(pointerId)).thenReturn(pointerIndex);
        when(event.getActionMasked()).thenReturn(action);
        when(event.getY(pointerIndex)).thenReturn(value);
        return event;
    }
}