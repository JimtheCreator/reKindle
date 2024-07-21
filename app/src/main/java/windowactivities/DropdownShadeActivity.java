package windowactivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import devydev.mirror.net.R;

public class DropdownShadeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropdown_shade);


        RelativeLayout closeapp = findViewById(R.id.closeapp);
        RelativeLayout thepage = findViewById(R.id.thepage);

        // Create an animation set
        AnimationSet animationSet = new AnimationSet(true);

        // Create a scale animation to scale the view from its original height to 0
        ScaleAnimation moveUp = new ScaleAnimation(
                1f,  // Start X
                1f,  // End X
                1f,  // Start Y
                0f,  // End Y
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 1f
        );

        moveUp.setDuration(4000); // Duration in milliseconds
        moveUp.setFillAfter(true); // If true, the animation will apply its transformation after it ends

        // Add the first animation to the set
        animationSet.addAnimation(moveUp);

        // Add an animation listener to the moveUp animation
        moveUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //DO NOTHING
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Set the view's height to 0 when the moveDown animation ends
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) thepage.getLayoutParams();
                params.height = 0;
                thepage.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });

        thepage.startAnimation(animationSet);

        closeapp.setOnClickListener(v -> {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        onDestroy();
    }
}