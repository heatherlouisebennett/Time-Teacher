package com.aimicor.timeteachermodule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ViewVerticalMoverTest {

    @InjectMocks
    private ViewMover subject;

    @Mock
    private View mockMoveableView;

    @Mock
    private TranslateAnimationFactory mockAnimationFactory;

    @Mock
    private Handler mockHandler;

    private static final float openPosition = 100;
    private static final float mFullAnimationDuration = ViewMover.ANIMATION_DURATION;

    private PositionMocker mPositionMocker;

    @Before
    public void setup() {
        initMocks(this);
        View mockEndPointView = mock(View.class);
        when(mockEndPointView.getY()).thenReturn(openPosition);
        subject.onGlobalLayout(mockMoveableView, mockEndPointView);
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
        moveTest(0f, 10f, 20f, 10f, new VerticalPositionMocker());
    }

    @Test
    public void can_move_view_up() {
        moveTest(30f, 30f, 20f, 20f, new VerticalPositionMocker());
    }

    @Test
    public void cant_move_above_top_position() {
        moveTest(30f, 50f, 0f, 0, new VerticalPositionMocker());
    }

    @Test
    public void cant_move_below_bottom_position() {
        moveTest(80f, 10f, 40f, openPosition, new VerticalPositionMocker());
    }

    @Test
    public void view_animates_back_to_top_if_less_than_half_way_down() {
        animationTest(30f, -30f, 30f / openPosition * mFullAnimationDuration, new VerticalPositionMocker());
    }

    @Test
    public void view_animates_to_bottom_if_more_than_half_way_down() {
        animationTest(70f, 30f, 30f / openPosition * mFullAnimationDuration, new VerticalPositionMocker());
    }

    @Test
    public void view_stays_at_top_when_animation_ends() {
        finalPositionTest(30f, (float) 0, new VerticalPositionMocker());
    }

    @Test
    public void view_stays_at_bottom_when_animation_ends() {
        finalPositionTest(60f, openPosition, new VerticalPositionMocker());
    }

    @Test // TODO split between moving up and moving down, set initial position
    public void remaining_second_finger_becomes_new_point_of_reference() {
        mPositionMocker = new VerticalPositionMocker();
        newReferenceTest(10f, 20f, 40f, 20f);
        newReferenceTest(15f, 30f, 45f, 15f);
        newReferenceTest(20f, 10f, 50f, 40f);
        newReferenceTest(5f, 95f, 50f, 0f);
    }

    @Test
    public void remaining_first_finger_not_affected_by_second_finger() {
        when(mockAnimationFactory.createWithDeltas(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(mock(TranslateAnimation.class));
        subject.onTouch(mockMoveableView, motionEvent(ACTION_DOWN, 10f, 0, 0));
        subject.onTouch(mockMoveableView, motionEvent(ACTION_UP, 70f, 1, 1));

        verify(mockMoveableView, never()).setY(anyFloat());
        verify(mockMoveableView, never()).startAnimation(any(TranslateAnimation.class));
    }

    private void finalPositionTest(float initialViewPos, float finalViewPos, PositionMocker positionMocker) {
        positionMocker.mockPos(initialViewPos);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);

        subject.onAnimationEnd(null);

        verify(mockHandler).post(captor.capture());
        captor.getValue().run();
        positionMocker.verifyPos(finalViewPos);
        reset(mockHandler);
    }

    private void animationTest(float actionUpPos, float expectedAnimationChange, float expectedAnimationDuration, PositionMocker positionMocker) {
        TranslateAnimation animation = mock(TranslateAnimation.class);
        positionMocker.mockAnimationFactory(expectedAnimationChange, animation);
        positionMocker.mockPos(actionUpPos);

        subject.onTouch(mockMoveableView, motionEvent(ACTION_UP, actionUpPos, 0, 0));

        verify(mockMoveableView).startAnimation(animation);
        verify(animation).setDuration((long) expectedAnimationDuration);
        verify(animation).setAnimationListener(subject);
    }


    private void moveTest(float initialpos, float actionDownPos, float movePos, float finalPos, PositionMocker positionMocker) {
        positionMocker.mockPos(initialpos);

        subject.onTouch(mockMoveableView, motionEvent(ACTION_DOWN, actionDownPos, 0, 0));
        subject.onTouch(mockMoveableView, motionEvent(ACTION_MOVE, movePos, 0, 0));

        positionMocker.verifyPos(finalPos);
    }

    private void newReferenceTest(float firstfingerposition, float secondfingerposition, float moveposition, float finalposition) {
        MotionEvent actionPointerUp = motionEvent(ACTION_POINTER_UP, firstfingerposition, 0, 0);
        when(actionPointerUp.getY(1)).thenReturn(secondfingerposition);
        when(actionPointerUp.getPointerId(1)).thenReturn(1);

        subject.onTouch(mockMoveableView, motionEvent(ACTION_DOWN, firstfingerposition, 0, 0));
        subject.onTouch(mockMoveableView, actionPointerUp);
        subject.onTouch(mockMoveableView, motionEvent(ACTION_MOVE, moveposition, 0, 1));

        mPositionMocker.verifyPos(finalposition);
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

    private interface PositionMocker {
        void verifyPos(float finalViewPos);
        void mockPos(float initialViewPos);
        void mockAnimationFactory(float expectedAnimationChange, TranslateAnimation animation);
    }

    private class VerticalPositionMocker implements PositionMocker{

        public void verifyPos(float finalViewPos) {
            verify(mockMoveableView).setY(finalViewPos);
        }

        public void mockPos(float initialViewPos) {
            when(mockMoveableView.getY()).thenReturn(initialViewPos);
        }

        public void mockAnimationFactory(float expectedAnimationChange, TranslateAnimation animation) {
            when(mockAnimationFactory.createWithDeltas(0f, 0f, 0f, expectedAnimationChange)).thenReturn(animation);
        }
    }

    private class HorizontalPositionMocker implements PositionMocker{

        public void verifyPos(float finalViewPos) {
            verify(mockMoveableView).setX(finalViewPos);
        }

        public void mockPos(float initialViewPos) {
            when(mockMoveableView.getX()).thenReturn(initialViewPos);
        }

        public void mockAnimationFactory(float expectedAnimationChange, TranslateAnimation animation) {
            when(mockAnimationFactory.createWithDeltas(0f, 0f, expectedAnimationChange, 0f)).thenReturn(animation);
        }
    }
}
