package windowactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import devydev.mirror.net.R;
import service.DelayService;

public class WaterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_water);

        TextView ask = findViewById(R.id.ask);
        RelativeLayout closeapp = findViewById(R.id.closeapp);
        LinearLayout page = findViewById(R.id.page);
        RelativeLayout thepage = findViewById(R.id.thepage);
        RelativeLayout closewindow = findViewById(R.id.closewindow);
        View calli = findViewById(R.id.yourWaterViewId);

        List<String> encouragementList = createEncouragementList();

        // Shuffle the list to ensure randomness
        Collections.shuffle(encouragementList);

        // Generate a random index
        Random random = new Random();
        int randomIndex = random.nextInt(encouragementList.size());

        // Display the randomly selected encouragement text
        String randomEncouragement = encouragementList.get(randomIndex);

        ask.setText(randomEncouragement);

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

                thepage.setVisibility(View.VISIBLE);
                page.setVisibility(View.GONE);
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
                Animation.RELATIVE_TO_SELF, 1f);

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
//            DelayService.isTRIGGERED = false;
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });

        closewindow.setOnClickListener(v -> {
            // Inside your onClick method for the "Continue" button
//            DelayService.isTRIGGERED = true;
            DelayService.continueInstagram = true;
            finish();
        });
    }

    private static List<String> createEncouragementList() {
        List<String> encouragementList = new ArrayList<>();

        encouragementList.add("Is it worth it mate. Think about it...");
        encouragementList.add("It's time to close the app, having second thoughts...");
        encouragementList.add("Pause and reflect on your choices...");
        encouragementList.add("Challenge the pursuit of instant pleasure...");
        encouragementList.add("What truly brings you here?");
        encouragementList.add("Are you genuinely happy, or just seeking a quick fix?");
        encouragementList.add("Redirect your energy toward meaningful pursuits...");
        encouragementList.add("Savor delayed gratification for lasting satisfaction...");
        encouragementList.add("Consider the consequences of chasing instant pleasure...");
        encouragementList.add("Find happiness in the ordinary and the meaningful...");
        encouragementList.add("Break this routine of seeking immediate pleasure...");
        encouragementList.add("Break the cycle to reveal a more authentic self...");
        encouragementList.add("Reflect on deeper needs instead of quick fixes...");
        encouragementList.add("Align actions with broader life goals...");
        encouragementList.add("Reassess priorities and pursue what truly matters...");
        encouragementList.add("We help you say 'no' to instant cravings...");
        encouragementList.add("Pause, breathe, and think about this...");
        encouragementList.add("Is this app helping you...");

        return encouragementList;
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
    }
}