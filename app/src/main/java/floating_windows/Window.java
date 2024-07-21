package floating_windows;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import devydev.mirror.net.R;

public class Window {

    // declaring required variables
    private final Context context;
    TextView toptitle;
    RelativeLayout clickkk;
    TextView short_descr;
    ImageView savedpic;

    Random r;
    private final View mView;
    private final Handler handler = new Handler();
    Random random;
    private WindowManager.LayoutParams mParams;
    private final WindowManager mWindowManager;
    private final LayoutInflater layoutInflater;

    public Window(Context context) {
        this.context = context;

        SharedPreferences preferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String theId = preferences.getString("Thisid", "none");

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
        mView = layoutInflater.inflate(R.layout.window_layout, null);


        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        savedpic = mView.findViewById(R.id.savedpic);
        clickkk = mView.findViewById(R.id.click_relative);
        short_descr = mView.findViewById(R.id.short_descr);
        toptitle =  mView.findViewById(R.id.toptitle);


        setdesctext(short_descr, toptitle);

        // Set OnClickListener for the RelativeLayout
        clickkk.setOnClickListener(v -> {
            // Handle click event
            close();
        });

//        if (clickkk != null) {
//            // Set OnClickListener for the RelativeLayout
//            clickkk.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Handle click event
//                    close();
//                }
//            });
//        } else {
//            // Log an error and show a toast if the RelativeLayout is not found
//            Log.e("Button", "Button not found with ID clickkk in the layout");
//            Toast toast = Toast.makeText(context, "Click again", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0,0);
//            toast.show();
//        }

        // Define the position of the
        // window within the screen
        mParams.gravity = Gravity.FILL_VERTICAL;

//        mParams.gravity = Gravity.START | Gravity.TOP;
        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
    }

    private void setdesctext(TextView shortDescr, TextView toptitle) {
        String[] intrusiveNotes = {
                "Embrace discomfort; that's where growth happens.",
                "Your future self will thank you for pushing through challenges today.",
                "Don't fear the struggle; it's shaping the best version of you.",
                "Each setback is a setup for a remarkable comeback.",
                "The path to success is paved with resilience, not comfort.",
                "In discomfort, you find your true strength.",
                "Every challenge is a stepping stone; keep climbing.",
                "Endure the pain; it's the forge of your inner strength.",
                "Break through limitations; your potential is boundless.",
                "Sweat today, shine tomorrow.",
                "Be the sculptor of your destiny; endure the chisel of challenges.",
                "Storms make trees take deeper roots; embrace your storms.",
                "Face challenges head-on; they are stepping stones to success.",
                "The struggle is real, but so is your capacity to overcome.",
                "Strength lies in pushing boundaries, not staying within them.",
                "Transform obstacles into opportunities; it's your superpower.",
                "Discomfort is the currency for self-improvement; spend it wisely.",
                "Bend like a reed in the wind, but never break.",
                "Challenge accepted; your resilience will surprise you.",
                "In discomfort, find your comfort zone of growth.",
                "Grit is the secret sauce; sprinkle it liberally on your journey.",
                "Each challenge is a hidden gift, unwrap it with perseverance.",
                "Shatter your limits; you're more capable than you think.",
                "A diamond is a chunk of coal that did well under pressure; shine bright.",
                "Obstacles are detours, not dead ends; keep moving forward.",
                "Adopt a growth mindset; every setback is a setup for a comeback.",
                "Face challenges like a warrior, not a worrier.",
                "Your potential is like a muscle; it grows stronger with resistance.",
                "Rise above the noise; your resilience speaks louder.",
                "Let your challenges be the wind beneath your wings of success.",
                "Break the mold; ordinary is overrated, embrace the extraordinary.",
                "Embrace the discomfort of uncertainty; that's where innovation begins.",
                "Life is a series of choices; choose resilience, choose growth.",
                "Your story is not defined by the chapter of challenges; it's authored by triumphs.",
                "Don't tiptoe through life; stomp through it with resilience.",
                "Dance with discomfort; it's a partner that leads to greatness.",
                "Turn obstacles into stepping stones and stumbling blocks into building blocks.",
                "Don't dodge difficulty; confront it and let it build your character.",
                "Strive for progress, not perfection; imperfection is the canvas of growth.",
                "Every effort you put in today is a deposit in the bank of your future success.",
                "Unleash your inner warrior; challenges are battles waiting to be won.",
                "Fall seven times, stand up eight; resilience is your bounce-back factor.",
                "See challenges as puzzles; each piece you solve gets you closer to the big picture.",
                "Conquer your doubts; your strength is greater than you think.",
                "You're not defined by your challenges; you're defined by how you overcome them.",
                "Life's best lessons are often hidden in challenges; be a diligent student.",
                "Don't let setbacks break you; let them make you unbreakable.",
                "Your journey is a canvas; paint it with strokes of resilience and shades of growth.",
                "Celebrate small victories; they are the building blocks of lasting success.",
                "Every step forward, no matter how small, is a victory on your path to greatness.",
                "Dare to dream; resilience turns dreams into reality.",
                "Bounce back from setbacks like a rubber ball; the higher you fall, the higher you rise.",
                "Your strength is a muscle; exercise it daily with challenges.",
                "You blocked it, why comeback. Don't be a d..."
        };



        random = new Random();
        int index = random.nextInt(intrusiveNotes.length);
        shortDescr.setText(intrusiveNotes[index]);


        String[] additionalTitles = {
                "Embrace the Unknown Journey",
                "Courageous Pursuit of Dreams",
                "Rise Above Every Hurdle",
                "Fearless Pursuit of Excellence",
                "Dare to Be Exceptional",
                "Triumph Through Perseverance",
                "Break Free from the Ordinary",
                "Courage Sparks Transformation",
                "Navigate Challenges Boldly",
                "Courage Amidst Chaos",
                "Leap into Action",
                "Fear Defying Triumphs",
                "Persist, Prevail, Prosper",
                "Unleash Inner Power",
                "Courage Over Comfort",
                "Boldly Tackle Challenges",
                "Triumph Over Adversity",
                "Rise Above Obstacles",
                "Face Fears Courageously",
                "Breakthrough, Transform, Triumph",
                "Bold Actions Triumph",
                "Defy Comfort Zones",
                "Triumph Over Trials",
                "Courage Ignites Triumph",
                "Breakthrough to Victory",
                "Embrace Bold Aspirations",
                "Unleash Inner Warrior",
                "Defy Limits Daily",
                "Triumph Through Trials",
                "Courageous Resilience Wins",
                "Bold Moves Triumph",
                "Conquer Challenges Boldly",
                "Overcome, Grow, Triumph",
                "Dare to Dream",
                "Endure, Overcome, Achieve",
                "Champion Your Dreams",
                "Boldly Face Challenges",
                "Fearless Growth Journey",
                "Conquer Every Obstacle",
                "Triumph in Boldness",
                "Overcome, Adapt, Achieve",
                "Bold Steps Forward",
                "Unleash Bold Potential",
                "Conquer, Thrive, Triumph",
                "Embrace the Challenge",
                "Dare to Be",
                "Triumph Over Setbacks",
                "Boldly Pursue Dreams",
                "Defy All Limits",
                "Triumph Over Fear",
                "Bold Moves Pay",
                "Dare Greatly Now",
                "Conquer Every Fear",
                "Suck it up"
        };



        r = new Random();
        int i = r.nextInt(additionalTitles.length);
        toptitle.setText(additionalTitles[i]);

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
            Log.d("DEBUG", "close method called");
            ViewParent parent = mView.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup parentViewGroup = (ViewGroup) parent;
                parentViewGroup.removeAllViews();
            }

            // remove the view from the window
            ((WindowManager) context.getSystemService(WINDOW_SERVICE)).removeView(mView);
            // invalidate the view
            mView.invalidate();
        } catch (Exception e) {
            Log.e("Error2", e.toString());
        }
    }

}