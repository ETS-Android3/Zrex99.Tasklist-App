package com.zoportfolio.checklistproject;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

//TODO: This class isnt working with the constraint layout because i am not adhering fully to accessiblity issues,
// so to make this work I will need to do more research.

public class OnSwipeTouchListener implements View.OnTouchListener {
    private GestureDetector gestureDetector;
    OnSwipeTouchListener(Context c) {
        gestureDetector = new GestureDetector(c, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //This perform click is just to comply with the warning and not anything else.
        v.performClick();
        return gestureDetector.onTouchEvent(event);
    }

    private static final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String TAG = "GestureListener.TAG";
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onClick();
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            onDoubleClick();
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onLongClick();
            super.onLongPress(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            //NOTE: This try block is checking the direction of the swipe events
            // to see which way the user is swiping and comparing the values to make sure they are actually trying to swipe.
            // Comment out further later to explain my knowledge on this and understand it better.
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if(Math.abs(diffX) > Math.abs(diffY)) {
                    if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if(diffX > 0) {
                            onSwipeRight();
                        }else {
                            onSwipeLeft();
                        }
                    }
                }
                else {
                    if(Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if(diffY > 0) {
                            onSwipeDown();
                        }else {
                            onSwipeUp();
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public void onSwipeRight() {
            Log.i(TAG, "onSwipeRight: Swiped Right");
        }

        public void onSwipeLeft() {
            Log.i(TAG, "onSwipeLeft: Swiped Left");
        }

        private void onSwipeUp() {
            Log.i(TAG, "onSwipeUp: Swiped Up");
        }

        private void onSwipeDown() {
            Log.i(TAG, "onSwipeDown: Swiped Down");
        }

        private void onClick() {
            Log.i(TAG, "onClick: Clicked");
        }

        private void onDoubleClick() {
            Log.i(TAG, "onDoubleClick: Double Clicked");
        }

        private void onLongClick() {
            Log.i(TAG, "onLongClick: Long Clicked");
        }
    }

}
