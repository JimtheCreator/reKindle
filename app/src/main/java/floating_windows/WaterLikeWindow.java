package floating_windows;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import devydev.mirror.net.R;
import front_page.HomeActivity;

public class WaterLikeWindow {

    // declaring required variables
    private final Context context;
    private final View mView;
    private final Handler handler = new Handler();
    private WindowManager.LayoutParams mParams;
    private final WindowManager mWindowManager;
    private final LayoutInflater layoutInflater;


    public WaterLikeWindow(Context context) {
        this.context = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather
                    // than filling the screen
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                    // Display it on top of other application windows
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    // Don't let it grab the input focus
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // Make the underlying application window visible
                    // through any transparent parts
                    PixelFormat.TRANSLUCENT);
        }

        // getting a LayoutInflater
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflating the view with the custom layout we created
        mView = layoutInflater.inflate(R.layout.waterlikewindow_layout, null);


        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        RelativeLayout closeapp = mView.findViewById(R.id.closeapp);
        LinearLayout page = mView.findViewById(R.id.page);
        RelativeLayout thepage = mView.findViewById(R.id.thepage);
        RelativeLayout closewindow = mView.findViewById(R.id.closewindow);
        View calli = mView.findViewById(R.id.yourWaterViewId);

        // Create an animation set
        AnimationSet animationSet = new AnimationSet(true);

        // Create a scale animation to scale the view from 0 to its original height
        ScaleAnimation moveUp = new ScaleAnimation(
                1f,  // Start X
                1f,  // End X
                0f,  // Start Y
                1f,  // End Y
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 1f
        );
        moveUp.setDuration(5000); // Duration in milliseconds
        moveUp.setFillAfter(true); // If true, the animation will apply its transformation after it ends
        // Add the first animation to the set
        animationSet.addAnimation(moveUp);

        // Add an animation listener to the moveDown animation
        moveUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //DO NOTHING
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Now, set the visibility
                // Create a Handler to delay the visibility change
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Set the view's visibility after a delay
//                        thepage.setVisibility(View.VISIBLE);
//                    }
//                }, 100); // Delay in milliseconds


                page.setVisibility(View.GONE);
                thepage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });

        // Create a scale animation to scale the view from its original height back to 0
        ScaleAnimation moveDown = new ScaleAnimation(
                1f,  // Start X
                1f,  // End X
                1f,  // Start Y
                0f,  // End Y
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 1f
        );
        moveDown.setDuration(8000); // Duration in milliseconds
        moveDown.setFillAfter(true); // If true, the animation will apply its transformation after it ends
        moveDown.setStartOffset(5500); // Start this animation after the first one ends

        // Add an animation listener to the moveDown animation
        moveDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //DO NOTHING
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Set the view's height to 0 when the moveDown animation ends
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) calli.getLayoutParams();
                params.height = 0;
                calli.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });

        // Add the second animation to the set
        animationSet.addAnimation(moveDown);

        // Start the animation set
        calli.startAnimation(animationSet);

        closeapp.setOnClickListener(v -> {
            close();
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(homeIntent);
        });

        closewindow.setOnClickListener(v -> close());

        // Define the position of the
        // window within the screen
        mParams.gravity = Gravity.FILL_VERTICAL;

//        mParams.gravity = Gravity.START | Gravity.TOP;
        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
    }

    public void open() {
        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.getWindowToken() == null) {
                if (mView.getParent() == null) {
                    mWindowManager.addView(mView, mParams);
                }
            }
        } catch (Exception e) {
            Log.d("Error1", e.toString());
        }
    }

    public void close() {
        try {
            // Remove the view from the window
            mWindowManager.removeView(mView);
            // Invalidate the view
            mView.invalidate();
        } catch (Exception e) {
            Log.e("Error", "Error closing window: " + e.toString());
        }
    }




}
