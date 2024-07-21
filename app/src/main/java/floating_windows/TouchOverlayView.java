package floating_windows;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//public class TouchOverlayView extends View implements GestureDetector.OnGestureListener {
//
//    private GestureDetector gestureDetector;
//
//    public TouchOverlayView(Context context) {
//        super(context);
//        init(context);
//    }
//
//    public TouchOverlayView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }
//
//    public TouchOverlayView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        init(context);
//    }
//
//    private void init(Context context) {
//        gestureDetector = new GestureDetector(context, this);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
//    }
//
////    @Override
////    public boolean onDown(MotionEvent e) {
////        // Always return true to indicate that the down event was consumed
////        return true;
////    }
//
//    /**
//     * Notified when a tap occurs with the down {@link MotionEvent}
//     * that triggered it. This will be triggered immediately for
//     * every down event. All other events should be preceded by this.
//     *
//     * @param e The down motion event.
//     */
//    @Override
//    public boolean onDown(@NonNull MotionEvent e) {
//        return false;
//    }
//
//    /**
//     * The user has performed a down {@link MotionEvent} and not performed
//     * a move or up yet. This event is commonly used to provide visual
//     * feedback to the user to let them know that their action has been
//     * recognized i.e. highlight an element.
//     *
//     * @param e The down motion event
//     */
//    @Override
//    public void onShowPress(@NonNull MotionEvent e) {
//
//    }
//
//    /**
//     * Notified when a tap occurs with the up {@link MotionEvent}
//     * that triggered it.
//     *
//     * @param e The up motion event that completed the first tap
//     * @return true if the event is consumed, else false
//     */
//    @Override
//    public boolean onSingleTapUp(@NonNull MotionEvent e) {
//        return false;
//    }
//
//    /**
//     * Notified when a scroll occurs with the initial on down {@link MotionEvent} and the
//     * current move {@link MotionEvent}. The distance in x and y is also supplied for
//     * convenience.
//     *
//     * @param e1        The first down motion event that started the scrolling. A {@code null} event
//     *                  indicates an incomplete event stream or error state.
//     * @param e2        The move motion event that triggered the current onScroll.
//     * @param distanceX The distance along the X axis that has been scrolled since the last
//     *                  call to onScroll. This is NOT the distance between {@code e1}
//     *                  and {@code e2}.
//     * @param distanceY The distance along the Y axis that has been scrolled since the last
//     *                  call to onScroll. This is NOT the distance between {@code e1}
//     *                  and {@code e2}.
//     * @return true if the event is consumed, else false
//     */
//    @Override
//    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
//        return false;
//    }
//
//    /**
//     * Notified when a long press occurs with the initial on down {@link MotionEvent}
//     * that trigged it.
//     *
//     * @param e The initial on down motion event that started the longpress.
//     */
//    @Override
//    public void onLongPress(@NonNull MotionEvent e) {
//
//    }
//
//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        Log.d("DETECT", "FLING " + e2);
//        int screenHeight = getResources().getDisplayMetrics().heightPixels;
//
//        if (e1.getY() > 0.8 * screenHeight && velocityY > 0) {
//            showToast("Close to Down Swipe Detected!");
//        }
//        return true;
//    }
//
////    @Override
////    public void onLongPress(MotionEvent e) {
////        // Handle long press gesture
////        showToast("Long Press Detected!");
////    }
////
////    @Override
////    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
////        // Handle scroll gesture
////        showToast("Scroll Detected!");
////        return true;
////    }
////
////    @Override
////    public void onShowPress(MotionEvent e) {
////        // Called when the user makes a tap gesture but doesn't release the finger
////    }
////
////    @Override
////    public boolean onSingleTapUp(MotionEvent e) {
////        // Called when the user releases a tap gesture
////        showToast("Single Tap Up Detected!");
////        return true;
////    }
//
//    private void showToast(String message) {
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//    }
//}
//
public class TouchOverlayView extends View {
    private GestureDetector gestureDetector;

    public TouchOverlayView(Context context) {
        super(context);
        // Initialize GestureDetector in the constructor
        gestureDetector = new GestureDetector(context, new MyGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Delegate touch events to GestureDetector
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    // Implement other methods from GestureDetector.OnGestureListener if needed

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            // Always return true to indicate that the down event was consumed
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Handle fling gesture here
            Log.d("DETECT", "FLING " + e2);
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            if (e1.getY() > 0.8 * screenHeight && velocityY > 0) {
                showToast();
            }
            return true;
        }

        // Implement other gesture methods as needed
    }

    public void showToast() {
        Log.d("DETECT", "FLING");
        Toast toast = Toast.makeText(getContext(), "SCROOOOO", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}
