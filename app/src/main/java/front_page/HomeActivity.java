package front_page;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import adapters.AdapterForProgress;
import adapters.DisplayProgress;
import adapters.ItemsofHabitAdapter;
import adapters.ShowlistAdapter;
import adapters.YouTubeAdapter;
import devydev.mirror.net.MainActivity;
import devydev.mirror.net.R;
import interface_package.ConfettiDisplay;
import me.tankery.lib.circularseekbar.CircularSeekBar;
import model.Book;
import model.ForHabits;
import model.ItemDecorator;
import model.Users;
import model.YouTube;
import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import service.AppBlockingService;
import service.DelayService;
import space.SpacebetweenItems;
import subscription.SubscriptionPageActivity;
import utils.UsageStatsHelper;

public class HomeActivity extends AppCompatActivity implements ConfettiDisplay {

    private static final int REQUEST_POST_NOTIFICATIONS = 123;
    int ppTotal = 0;
    ViewGroup viewGroup, vv2;
    AlertDialog alertDialog;
    LinearLayout profile_page, signout, open_source;
    RelativeLayout tiktok, instagram, contact_us, rateus, premium, termsofservice, privacy_policy, shareapp, telegram;
    TextView displayed_texts, tag_member;
    ShapeableImageView loadYT;
    TextView state_txtx;
    List<String> stringList = new ArrayList<>();
    boolean signed;
    CircularSeekBar seekBar;
    List<String> snapshots = new ArrayList<>();
    KonfettiView konfettiView;
    TextView top, countdown, seektext, statustxt, Day, Hours, Minutes, Seconds;
    List<String> array = new ArrayList<>();
    List<String> stringArrayList = new ArrayList<>();
    ImageView statusofprogress, tagicon;
    RelativeLayout bottom, killerswitch, timebar, hold, openYoutube, launchpad;
    View view;
    LinearLayout list;
    CountDownTimer countDownTimer, countme;
    RecyclerView habits_recyclerview, habits_tomorrow_recyclerview, habits_tomorrow_recyclerview_two;
    CircularProgressIndicator progress1;
    ItemsofHabitAdapter itemsofHabitAdapter; //Displaying habits in a circle with progressbar
    RelativeLayout custom_clicked;
    List<ForHabits> forHabitsList = new ArrayList<>();
    RecyclerView exhibitrecyclerview;
    BottomSheetBehavior bottomSheetBehavior, profilebehavior;
    DisplayProgress displayProgress; //Displaying habits with numbers below
    List<ForHabits> fo = new ArrayList<>();
    double in;
    List<ForHabits> getForHabitsList = new ArrayList<>();
    LinearLayout thetomorrow, time_holder, goodbye, lottie_layer_name;
    AdapterForProgress adapterForProgress; //Not in use at the moment
    List<ForHabits> getGetForHabitsList = new ArrayList<>();
    ShowlistAdapter showlistAdapter; //For displaying habits in a boxstyle
    TextView ff;
    boolean isSoundPlaying;
    NestedScrollView nest;
    ConstraintLayout xxxp;
    RelativeLayout button;
    private Shape.DrawableShape drawableShape;

    // Create a method that takes an ArrayList of strings and returns the sum of the percentages of each value compared to 30 days
    public static int calculateSum(List<String> list) {
        int totalValue = 0; // Initialize the sum of values

        // Loop through the list and add the values
        for (String s : list) {
            int value = Integer.parseInt(s);
            totalValue += value;
        }

        // Return the total sum
        return totalValue;
    }

    private static List<String> removeDots(List<String> list) {
        List<String> resultList = new ArrayList<>();

        for (String item : list) {
            // Replace dots with an empty string
            resultList.add(item.replace(".", ""));
        }

        return resultList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                if (!users.isIspaid()) {
                    tag_member.setText("Free");
                    tagicon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.starts));
                    premium.setVisibility(View.VISIBLE);
                } else {
                    tag_member.setText("Premium");
                    premium.setVisibility(View.GONE);
                    tagicon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.diamond));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                if (!users.isIspaid()) {
                    if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
                        Intent intent = new Intent(HomeActivity.this, DelayService.class);

                        // Stop the service
                        stopService(intent);
                    }

                    tag_member.setText("FREE");
                    tagicon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.starts));
                    premium.setVisibility(View.VISIBLE);
                } else {
                    premium.setVisibility(View.GONE);
                    tag_member.setText("PREMIUM");
                    tagicon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.diamond));
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    databaseReference.keepSynced(true);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users subscription = snapshot.getValue(Users.class);
                            if (!subscription.getPackage().equals("")) {
                                comparenreset(Long.parseLong(subscription.getTimepaid()), subscription.getPackage());
                            } else {
                                openBottomsheet();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void comparenreset(long timepaid, String plan) {
        if (plan.equals("Monthly")) {
            // Calculate the difference between the current time and the provided timestamp
            long x = System.currentTimeMillis();
            long diffMillis = x - timepaid;

            // Convert 31 days to milliseconds
            long thirtyOneDaysMillis = TimeUnit.DAYS.toMillis(33);

            // Check if 31 days have passed since the provided timestamp
            if (diffMillis >= thirtyOneDaysMillis) {
                autorestartSubscription();
            } else {
                // If 31 days have passed, calculate the time for the next toast
                long nextToastMillis = diffMillis % thirtyOneDaysMillis;
                long delayMillis = thirtyOneDaysMillis - nextToastMillis;

                // Create a new CountDownTimer
                new CountDownTimer(delayMillis, 1000) {
                    public void onTick(long millisUntilFinished) {
                        // This method is called every tick (in this case, every second)

                        long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                    }

                    public void onFinish() {
                        // This method is called when the countdown is finished
                        autorestartSubscription();
                    }
                }.start();
            }
        } else {
            // Calculate the difference between the current time and the provided timestamp
            long x = System.currentTimeMillis();
            long diffMillis = x - timepaid;

            // Convert 31 days to milliseconds
            long thirtyOneDaysMillis = TimeUnit.DAYS.toMillis(367);

            // Check if 31 days have passed since the provided timestamp
            if (diffMillis >= thirtyOneDaysMillis) {
                autorestartSubscription();
            } else {
                // If 31 days have passed, calculate the time for the next toast
                long nextToastMillis = diffMillis % thirtyOneDaysMillis;
                long delayMillis = thirtyOneDaysMillis - nextToastMillis;

                // Create a new CountDownTimer
                new CountDownTimer(delayMillis, 1000) {
                    public void onTick(long millisUntilFinished) {
                        // This method is called every tick (in this case, every second)

                        long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                    }

                    public void onFinish() {
                        // This method is called when the countdown is finished
                        autorestartSubscription();
                    }
                }.start();
            }
        }

    }

    private void autorestartSubscription() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Ispaid", false);
        hashMap.put("Timepaid", "");
        hashMap.put("Package", "");
        hashMap.put("Expiring", "");

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(hashMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        openBottomsheet();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Admin")
                                .child("Mapped Vision");

                        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    }
                }).addOnFailureListener(e -> {

                });

    }

    private void openBottomsheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.bottomsheetTheme);

        View view = LayoutInflater.from(this).inflate(R.layout.renew_subscription,
                (ViewGroup) findViewById(R.id.charge));

        bottomSheetDialog.setCancelable(true);

        bottomSheetDialog.setContentView(view);

        Window window = getWindow();
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.darkTheme));

        RelativeLayout renew = view.findViewById(R.id.renew);

        RelativeLayout cancel = view.findViewById(R.id.cancel);

        renew.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(getApplicationContext(), SecondSubActivity.class));

        });

        cancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });


        bottomSheetDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                if (!users.isIssigned()) {
                    signFirst(users.isIspaid());
                }

                if (users.isIspaid()) {
                    // Check if the app has the POST_NOTIFICATIONS permission
                    if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // Request the permission
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
                        }
                    }

//                    if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
//                        Intent intent = new Intent(HomeActivity.this, DelayService.class);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            startForegroundService(intent);
//                        } else {
//                            startService(intent);
//                        }
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_hurt);
        drawableShape = new Shape.DrawableShape(drawable, true, true);

        // Initialize SharedPreferences
        SharedPreferences.Editor pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
        pref.putString("State", "Logged_In");
        pref.apply();

        boolean accessibilityServiceEnabled = isAccessibilityEnabled();

        if (accessibilityServiceEnabled) {
            Intent in = new Intent(this, AppBlockingService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Call startForegroundService
                startForegroundService(in);
            } else {
                // Use the old method for older API levels
                startService(in);
            }
        }

        open_source = findViewById(R.id.open_source);
        tagicon = findViewById(R.id.tagicon);
        tag_member = findViewById(R.id.tag_member);
        itemsofHabitAdapter = new ItemsofHabitAdapter(forHabitsList, getApplicationContext(), HomeActivity.this, this);
        displayProgress = new DisplayProgress(getApplicationContext(), fo);
        profile_page = findViewById(R.id.profile_page);
        displayed_texts = findViewById(R.id.displayed_texts);
        button = findViewById(R.id.button);
        launchpad = findViewById(R.id.launchpad);
        loadYT = findViewById(R.id.loadYT);
        telegram = findViewById(R.id.telegram);
        shareapp = findViewById(R.id.shareapp);
//        x = findViewById(R.id.x);
        premium = findViewById(R.id.premium);
        tiktok = findViewById(R.id.tiktok);
        instagram = findViewById(R.id.instagram);
        contact_us = findViewById(R.id.contact_us);
        rateus = findViewById(R.id.rateus);
        termsofservice = findViewById(R.id.termsofservice);
        privacy_policy = findViewById(R.id.privacy_policy);

        openYoutube = findViewById(R.id.openYoutube);
        hold = findViewById(R.id.hold);
        xxxp = findViewById(R.id.xxxp);
        konfettiView = findViewById(R.id.konfettiView);
        signout = findViewById(R.id.signout);
        goodbye = findViewById(R.id.goodbye);
        time_holder = findViewById(R.id.time_holder);
        lottie_layer_name = findViewById(R.id.lottie_layer_name);
        state_txtx = findViewById(R.id.state_txtx);
        timebar = findViewById(R.id.timebar);

        Day = findViewById(R.id.days);
        Hours = findViewById(R.id.hours);
        Minutes = findViewById(R.id.minutes);
        Seconds = findViewById(R.id.seconds);
        killerswitch = findViewById(R.id.lskdde);
        statusofprogress = findViewById(R.id.statusofprogress);
        statustxt = findViewById(R.id.statustxt);
        seektext = findViewById(R.id.seektext);
        seekBar = findViewById(R.id.seekBar);
        vv2 = findViewById(R.id.viewwww);
        top = findViewById(R.id.top);
        bottom = findViewById(R.id.bottom);
        thetomorrow = findViewById(R.id.thetomorrow);
        countdown = findViewById(R.id.contdown);
        habits_tomorrow_recyclerview_two = findViewById(R.id.habits_tomorrow_recyclerview_two);
        habits_tomorrow_recyclerview_two.setLayoutManager(new GridLayoutManager(this, 3));
        habits_tomorrow_recyclerview_two.setAdapter(displayProgress);

        habits_tomorrow_recyclerview = findViewById(R.id.habits_tomorrow_recyclerview);
        habits_tomorrow_recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        habits_tomorrow_recyclerview.setAdapter(displayProgress);

        adapterForProgress = new AdapterForProgress(getGetForHabitsList, getApplicationContext());
        SpacebetweenItems spacebetweenItems = new SpacebetweenItems(50);

        viewGroup = findViewById(R.id.list);
        view = findViewById(R.id.vvvii);
        showlistAdapter = new ShowlistAdapter(getApplicationContext(), getForHabitsList);
        exhibitrecyclerview = findViewById(R.id.exhibitrecyclerview);

        exhibitrecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        exhibitrecyclerview.setAdapter(showlistAdapter);
        exhibitrecyclerview.addItemDecoration(spacebetweenItems);
        custom_clicked = findViewById(R.id.custom_clicked);

        progress1 = findViewById(R.id.progress1);
        nest = findViewById(R.id.nest);
        itemsofHabitAdapter = new ItemsofHabitAdapter(forHabitsList, getApplicationContext(), HomeActivity.this, this);

        habits_recyclerview = findViewById(R.id.habits_recyclerview);

        ff = findViewById(R.id.ff);

        // Set the layout manager for the RecyclerView
        habits_recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        habits_recyclerview.setAdapter(itemsofHabitAdapter);

        bottomSheetBehavior = BottomSheetBehavior.from(nest);
        profilebehavior = BottomSheetBehavior.from(profile_page);

        findViewById(R.id.closedrawer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilebehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        custom_clicked.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        profilebehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                profilebehavior.setDraggable(true);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        launchpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SecondSubActivity.class));

            }
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            boolean visible;

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    TransitionManager.beginDelayedTransition(viewGroup);
                    custom_clicked.setVisibility(View.VISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    TransitionManager.beginDelayedTransition(viewGroup);
//                    view.setVisibility(View.GONE);
                    custom_clicked.setVisibility(View.GONE);
//                    timebar.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        seekBar.setOnTouchListener((view, motionEvent) -> true);

        killerswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), KillerSwitchActivity.class));

            }
        });

        button.setOnClickListener(v -> {
            profilebehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        Glide.with(getApplicationContext())
                .load("https://firebasestorage.googleapis.com/v0/b/mirror-8a40e.appspot.com/o/oig_another.jpg?alt=media&token=a227816d-5296-4a92-81cc-f8fd9906e2ff")
                .into(loadYT);


        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String privacyurl = getString(R.string.privacy_policy);
                gotoURL(privacyurl);
            }
        });

        termsofservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoURL(getString(R.string.terms_of_service));
            }
        });


        open_source.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), OpenSourceActivity.class));

        });


        openYoutube.setOnClickListener(v -> {
            openChannelsbottomsheet();
        });

        signout.setOnClickListener(v -> {
            openAdialog();
        });

        tiktok.setOnClickListener(v -> {
            openTikTok();
        });

        telegram.setOnClickListener(v -> {
            openTelegram();
        });

//        x.setOnClickListener(v -> {
//            openX();
//        });

        instagram.setOnClickListener(v -> {
            openInstagram();
        });

        rateus.setOnClickListener(v -> {
            Toast toast = Toast.makeText(this, "We're not yet on the Google PlayStore", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });

        contact_us.setOnClickListener(v -> {
            contactUs();
        });


        shareapp.setOnClickListener(v -> {
            shareProgress();
        });


        displayItems();
        displayItems2();
        theMethod();
        diffMe();
        theMethofhfhd();
        checktomorrow();
        progress();
        setStringArrayList();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    // Get new Instance ID token
                    String token = task.getResult();
                    Log.d("TOKEN", "Refreshed token: " + token);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    HashMap<String, Object> objectHashMap = new HashMap<>();
                    objectHashMap.put("Token", token);

                    reference.updateChildren(objectHashMap);
                } else {
                    Log.d("TOKEN", "Failed to refresh token", task.getException());
                }
            }
        });
    }

    private void openBlockDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.blockuser, (ViewGroup) findViewById(R.id.block));

//        ImageView image = (ImageView) layout.findViewById(R.id.image);
//        image.setImageResource(R.drawable.android); // replace with your own image resource
//
        Button text = (Button) layout.findViewById(R.id.discard_alert);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setCancelable(false) // This makes the AlertDialog non-dismissable
                .create();

        // Perform null check on the window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        alertDialog.show();
    }

    private void gotoURL(String privacyurl) {
        Uri uri = Uri.parse(privacyurl);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The user granted the POST_NOTIFICATIONS permission
            } else {
                // The user denied the permission. Handle this case appropriately.
                Toast toast = Toast.makeText(this, "You'll not get the full experience", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        }
    }

    private void openTelegram() {
        Uri uri = Uri.parse("https://t.me/tryrekindle");

        Intent launchIntent = new Intent(Intent.ACTION_VIEW, uri);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchIntent.setPackage("org.telegram.messenger");

        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/tryrekindle")));
        }
    }

    private int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private void openChannelsbottomsheet() {
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View view = inflater.inflate(R.layout.list_of_youtubers, null);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomeActivity.this,
                R.style.bottomsheetTheme);

        List<YouTube> youTubeList = new ArrayList<>();
        YouTubeAdapter youTubeAdapter = new YouTubeAdapter(HomeActivity.this, youTubeList);
        RecyclerView channels_recyclerview = view.findViewById(R.id.channels_recyclerview);

        channels_recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        channels_recyclerview.addItemDecoration(new ItemDecorator(3, spacingInPixels, true));
        channels_recyclerview.setAdapter(youTubeAdapter);

        readYTData(youTubeList, youTubeAdapter);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.create();
        bottomSheetDialog.show();
    }

    private void readYTData(List<YouTube> youTubeList, YouTubeAdapter youTubeAdapter) {
        FirebaseDatabase.getInstance().getReference().child("YouTube Media").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                youTubeList.clear();
                for (DataSnapshot sn : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot : sn.getChildren()) {
                        YouTube youTube = dataSnapshot.getValue(YouTube.class);

                        youTubeList.add(youTube);
                    }
                }

                youTubeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void progress() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sn : snapshot.getChildren()) {
                    ForHabits progress = sn.getValue(ForHabits.class);
                    assert progress != null;

                    if (!"Reading books".equals(progress.getItem())) {
                        // Process the other nodes
                        int pp = Integer.parseInt(progress.getCounter());
                        int p = (int) ((double) pp / 30 * 100);
                        BigDecimal bigDecimal = new BigDecimal(p);
                        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN);

                        int y = bigDecimal.intValue();

                        // Add the desired value to stringList
                        stringList.clear();
                        stringList.add(String.valueOf(y));
                    }

                    if ("Reading books".equals(progress.getItem())) {
                        array.add(String.valueOf(progress.getCounter()));
                        snapshots.add(sn.getKey());

                        gettingActualAvg();

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Books");

                        if (reference1 != null) {
                            if (reference1.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                gettingActualAvg();
                            } else {
                                setPerc(0, 0);
                            }
                        } else {
                            setPerc(0, 0);
                        }

                        an();
                        return;
                    }


                    // Process the other nodes
                    int pp = Integer.parseInt(progress.getCounter());
                    int p = (int) ((double) pp / 30 * 100);
                    BigDecimal bigDecimal = new BigDecimal(p);
                    bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN);

                    int y = bigDecimal.intValue();

                    // Add the desired value to stringList
                    stringList.clear();
                    stringList.add(String.valueOf(y));

                    array.add(String.valueOf(progress.getCounter()));

                    snapshots.add(sn.getKey());

                    an();

                    stringList.add(String.valueOf(y));
                    gettingAverage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled as needed
            }
        });

    }

    private void setPerc(int value, int i) {
        if (stringList != null) {
            if (i == 0 && value == 0) {
                seektext.setText(String.format("%d%%", 0));
                seekBar.setProgress(0);
                return;
            }

            int xy = (i * 100) / value;

            // Add sum and xy together
            int result = calculateSum(stringList) + xy;

            Log.d("Log", "" + calculateSum(stringList));
            Log.d("Another Log", "" + stringList);

            // Calculate k using the total
            int k = result / 3;

            seektext.setText(String.format("%d%%", k));
            seekBar.setProgress(k);

            runnable(1000);
        }

    }

    private void runnable(int i) {

    }

    private void gettingActualAvg() {
        FirebaseDatabase.getInstance().getReference().child("Books")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Book book = dataSnapshot.getValue(Book.class);

                            int value = Integer.parseInt(book.getNoofpages());

                            setPerc(value, Integer.parseInt(book.getPagesread()));
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    void gettingAverage() {
        for (String numberString : stringList) {
            if (numberString != null) {
                int x = Integer.parseInt(numberString);

                int sum = 0;

                int p = sum += x;

                int k = p / 3;


                if (k == 0) {
                    seektext.setText(String.format("%d%%", 0));
                } else {
                    seektext.setText(String.format("%d%%", k));
                }
                seekBar.setProgress(k);

            }
        }
    }

    private void an() {
        for (String x : array) {
            int oo = Integer.parseInt(x);

            int sum = 0;

            int p = sum += oo;

            int k = p / 3;

            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference().child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users users = snapshot.getValue(Users.class);
                    Calendar calendar1 = Calendar.getInstance();

                    long x = Long.parseLong(users.getTimemillis());

                    long minus_millis = ((calendar1.getTimeInMillis() + 86400000) - x);

                    //millisec into a day
                    // if 1day = 24hours & 1hour = 60minutes & 1 min = 60sec =


                    long division = minus_millis / 86400000;

                    BigDecimal bd = new BigDecimal(division);
                    bd = bd.setScale(0, BigDecimal.ROUND_DOWN); //truncate

                    int y = 30 - bd.intValue();

                    int minus = bd.intValue() - k;

                    showstate(y, minus);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void showstate(int y, int minus) {

        if (y < 15) {
            if (minus <= 3) {
                statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.litt_emoji));
                statustxt.setText("Brilliant");
            }

            if (minus > 3) {
                statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lame));
                statustxt.setText("Awful");
            }

            return;
        }

        if (y >= 25) {
            statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.walk));
            statustxt.setText("Starting");
            return;
        }

        if (y >= 15 && y < 25) {
            if (minus > 3) {
                statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lame));
                statustxt.setText("Lame");
            }

            if (minus <= 3) {
                statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.great));
                statustxt.setText("Great");
            }
            return;
        }
    }

    private void checktomorrow() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                if (users != null) {
                    long x = Long.parseLong(users.getTimemillis());

                    if (x > System.currentTimeMillis()) {
//                        top.setVisibility(View.GONE);
                        bottom.setVisibility(View.GONE);
                        countdown.setVisibility(View.VISIBLE);
                        thetomorrow.setVisibility(View.VISIBLE);
                        timebar.setVisibility(View.GONE);
                        long minus = x - System.currentTimeMillis();
                        countdownClock(minus);
                        return;
                    }

                    if (x <= System.currentTimeMillis()) {
                        timebar.setVisibility(View.VISIBLE);
//                        top.setVisibility(View.VISIBLE);
                        bottom.setVisibility(View.VISIBLE);
                        countdown.setVisibility(View.GONE);
                        thetomorrow.setVisibility(View.GONE);
                        return;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countdownClock(long lo) {
        countDownTimer = new CountDownTimer(lo, 1000) {
            @Override
            public void onTick(long l) {
                updateTimer(l);
            }

            @Override
            public void onFinish() {
                TransitionManager.beginDelayedTransition(vv2);
                Toast toast = Toast.makeText(getApplicationContext(), "It's now time to make a difference", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                top.setVisibility(View.VISIBLE);
                bottom.setVisibility(View.VISIBLE);
                countdown.setVisibility(View.GONE);
                thetomorrow.setVisibility(View.GONE);
            }
        }.start();
    }

    private void updateTimer(long l) {
        int hours = (int) l / 3600000;
        int minutes = (int) ((l / 1000) % 3600) / 60;
        int seconds = (int) (l / 1000) % 60;

        String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        countdown.setText(time);
    }

    void setStringArrayList() {
        String[] quotes = {
                "Strength comes from enduring the pain of self-development, not from avoiding it.",
                "In the forge of self-discovery, endure the heat, for that's where true strength is forged.",
                "The road to self-improvement is paved with challenges; don't turn back, press on.",
                "Endurance transforms obstacles into stepping stones on the path to self-growth.",
                "Don't give up when the journey gets tough; that's where your character is built.",
                "Embrace the pain of self-development, for it is the sculptor of your greatest self.",
                "Resilience is born in the crucible of challenges; let it shape your destiny.",
                "Each setback is a setup for a comeback; endure the process of self-betterment.",
                "The pain of self-discovery is temporary; the strength gained is everlasting.",
                "A river cuts through rock not because of its power, but its persistence; persist in self-development.",
                "Enduring the discomfort of growth is the price we pay for becoming who we are meant to be.",
                "Don't fear the pain; fear the stagnation that comes from avoiding it.",
                "Like a diamond, your true self emerges under pressure; endure and shine.",
                "The seed must break its shell to become a mighty tree; endure the breaking for the growth.",
                "In the cocoon of self-transformation, endure the struggle; soon, wings of resilience will emerge.",
                "Strength is not the absence of pain but the courage to endure and transform it.",
                "Every challenge you face is an opportunity to strengthen the warrior within; embrace it.",
                "The journey of self-development is a marathon, not a sprint; endure and savor every step.",
                "When the storm of adversity hits, be the sturdy oak that bends but doesn't break.",
                "Endurance is the bridge between your dreams and their fulfillment; cross it with determination.",
                "Life's challenges are not roadblocks but stepping stones on the path to your best self.",
                "Perseverance turns stumbling blocks into stepping stones on the journey of self-discovery.",
                "Courage is not the absence of fear, but the triumph over it through relentless self-improvement.",
                "In the crucible of challenge, your character is refined; endure and emerge stronger.",
                "The caterpillar endures the cocoon to become a butterfly; embrace your transformation.",
                "Great oaks grow strong in contrary winds; face your challenges with unwavering determination.",
                "The pain of self-growth is a testament to your commitment to becoming the best version of yourself.",
                "Adversity is the forge where the sword of resilience is tempered; endure and become invincible.",
                "Endurance is not just the ability to bear a hard thing, but to turn it into glory through self-development.",
                "Don't be disheartened by the storm; it's watering the seeds of your future success.",
                "Rivers do not carve mountains in a day; endure the constant flow of self-improvement.",
                "In the tapestry of life, challenges are the threads that weave the story of your strength.",
                "Endurance is the key that unlocks the door to the undiscovered potential within you.",
                "The pain of today is the strength of tomorrow; endure and let resilience be your legacy.",
                "Like a phoenix rising from the ashes, embrace the pain, for it heralds your rebirth.",
                "Challenges are the weights you lift to strengthen the muscles of your character.",
                "In the symphony of self-growth, endure the dissonance, for harmony awaits on the other side.",
                "The journey may be uphill, but the view from the summit is worth every step; persevere.",
                "In the laboratory of self-discovery, endure the experiments; they reveal the formula for greatness.",
                "Every stumbling block is a stepping stone in disguise; keep moving forward with faith.",
                "The pain of discipline is temporary; the regret of giving up lasts forever.",
                "To become a masterpiece, endure the chisel of self-improvement; greatness is a work in progress.",
                "Like a star shining in the darkest night, your resilience illuminates the path to success.",
                "Through the storms of self-development, become the captain who steers their own ship.",
                "Endurance is the compass that guides you through the uncharted waters of personal growth.",
                "Rise above the challenges like a kite in the storm; adversity is the wind that lifts you higher.",
                "Enduring the pain of self-discovery is not a burden but a privilege; it signifies your evolution.",
                "Let the echoes of perseverance drown out the whispers of doubt; you are stronger than you think.",
                "In the marathon of self-mastery, endurance is the fuel that propels you towards the finish line.",
                "Sweat is the currency of success; spend it generously on the journey of self-development.",
                "When the winds of change blow, be the sturdy tree that bends but doesn't break; adapt and endure.",
                "In the school of life, challenges are the exams that test your readiness for success; study hard and endure.",
                "The seeds of success lie in the soil of hard work and endure the storms of challenges; cultivate them wisely.",
                "Endurance is the bridge that connects your present struggles to the future triumphs of self-discovery.",
                "The scars of perseverance are the tattoos of resilience; wear them proudly on the canvas of your journey.",
                "The symphony of success is composed of the melodies of endurance and the harmonies of self-discipline.",
                "To reach the heights of your potential, endure the climb; the summit rewards the persistent.",
                "In the tapestry of dreams, endurance is the thread that weaves ambition into reality.",
                "The fire of determination burns brightest in the crucible of challenges; endure and let it ignite your path.",
                "Endurance is the compass that guides you through the storms and leads you to the shores of self-fulfillment.",
                "The caterpillar endures the darkness of the cocoon to emerge as the butterfly of self-transformation; so can you.",
                "When life throws challenges, catch them with the mitt of resilience and throw back the ball of perseverance.",
                "To master the art of self-development, endure the brushstrokes of challenges; your canvas will be a masterpiece.",
                "In the gallery of life, endurance is the frame that elevates the portrait of your achievements.",
                "Endurance is not just a virtue; it's the secret sauce that adds flavor to the recipe of success.",
                "Like a phoenix rising from the ashes, let every setback be the fuel that propels you to new heights of self-achievement.",
                "The path of self-discovery is not a sprint but a marathon; pace yourself, endure, and celebrate each step.",
                "Amidst the storm, be the lighthouse that stands tall; endure, guide, and shine your light on the seas of challenges.",
                "In the garden of self-growth, endure the rains of adversity; for every storm makes the flowers of resilience bloom.",
                "Endurance is the silent force that",
                "Endurance is not just surviving; it's thriving in the face of adversity and emerging victorious.",
                "Every challenge is an opportunity for growth; embrace them and let them shape your journey.",
                "Success is not given; it's earned through the sweat and perseverance of enduring challenges.",
                "Like a phoenix, let every trial be the ashes from which you rise stronger and more resilient.",
                "Endurance is the compass that keeps you on course when the storms of life try to pull you off track.",
                "In the tapestry of resilience, every thread of endurance weaves a story of triumph over adversity.",
                "The journey to greatness is not a straight line; it's the zigzag of enduring setbacks and pushing forward.",
                "The pain of self-improvement is a temporary discomfort; the reward is everlasting strength.",
                "Like a tree standing tall against the wind, your endurance roots you deeply in the soil of self-discovery.",
                "Adversity introduces you to yourself; endure it, learn from it, and emerge wiser and stronger.",
                "Success is the reflection of endurance in the mirror of challenges; keep enduring, keep reflecting.",
                "The marathon of life is won not by the swift but by those who endure through every mile.",
                "In the dance of self-mastery, endurance is the partner that leads you gracefully to success.",
                "Endurance is the armor that shields you from the arrows of doubt on the battlefield of self-growth.",
                "The gem of your potential is polished through the grindstone of endurance; let it shine.",
                "Like a phoenix reborn from the ashes, let each challenge be the birthplace of a stronger you.",
                "In the symphony of success, endurance plays the note that resonates through the entire composition.",
                "Endurance is the engine that propels you through the twists and turns of the self-development journey.",
                "The pain you feel today is the strength you'll wield tomorrow; endure it with purpose.",
                "Like a puzzle coming together, each moment of endurance completes the picture of your success.",
                "Endurance is the melody that harmonizes with the rhythm of perseverance in the song of achievement.",
                "Success is not found in comfort; it's discovered through the discomfort of endurance.",
                "The marathon of self-betterment is fueled by the endurance that turns each step into progress.",
                "Like a flower pushing through concrete, endure the challenges and blossom into your full potential.",
                "In the grand theater of self-discovery, endurance is the spotlight that illuminates your journey.",
                "Success is not just reaching the destination; it's savoring the journey of endurance and growth.",
                "Endurance is the compass that points true north when the storms of uncertainty surround you.",
                "The symphony of achievement has a recurring theme of endurance; let it be the melody of your story.",
                "Like a river carving through rock, your endurance shapes the landscape of your own destiny.",
                "In the garden of self-mastery, endurance is the constant gardener that nurtures success to bloom.",
                "Endurance is the fuel that keeps the fire of determination burning bright on the path to greatness.",
                "Success is not a sprint to the finish; it's the enduring marathon of consistent effort and resilience.",
                "Like a phoenix rising from the ashes of challenges, let your endurance be the flame that lights the way.",
                "In the grand tapestry of life, endurance is the thread that weaves the fabric of your legacy.",
                "Endurance is the key that unlocks the door to the hidden chambers of your untapped potential.",
                "The journey of self-development is a story of endurance written on the pages of your experiences.",
                "Like a mountain standing firm against the elements, endure the storms and stand tall in your growth.",
                "In the grand mosaic of success, endurance is the vibrant color that brings the picture to life.",
                "Endurance is the compass that navigates the labyrinth of challenges, guiding you to triumph.",
                "Success is not the absence of failure but the triumph over it; endure and triumph.",
                "Like a phoenix, rise from the ashes of setbacks; your endurance is the wings that carry you higher.",
                "In the marathon of self-fulfillment, endurance is the pace that takes you across the finish line.",
                "Endurance is the bridge that spans the gap between where you are now and where you aspire to be.",
                "The journey may be tough, but your endurance is the engine that propels you through every challenge.",
                "Like a sculptor chiseling a masterpiece, endure the process and reveal the work of art that is you.",
                "In the grand gallery of self-achievement, endurance is the masterpiece that captures the viewer's gaze.",
                "Endurance is the key that unlocks the door to the palace of self-discovery; keep turning it.",
                "Success is not found in the absence of challenges but in the endurance to overcome them.",
                "Like a phoenix emerging from the ashes of adversity, let your endurance be the flame that lights the path to success."
        };

        Random random = new Random();
        int index = random.nextInt(quotes.length);

        displayed_texts.setText(quotes[index]);

    }

    private void theMethod() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                forHabitsList.clear();
                for (DataSnapshot sn : snapshot.getChildren()) {
                    ForHabits forHabits = sn.getValue(ForHabits.class);

                    forHabitsList.add(forHabits);
                }

                itemsofHabitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void theMethofhfhd() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fo.clear();
                for (DataSnapshot sn : snapshot.getChildren()) {
                    ForHabits forHabits = sn.getValue(ForHabits.class);
                    fo.add(forHabits);
                }

                displayProgress.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void diffMe() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getGetForHabitsList.clear();
                for (DataSnapshot sn : snapshot.getChildren()) {
                    ForHabits forHabits = sn.getValue(ForHabits.class);

                    getGetForHabitsList.add(forHabits);
                }

                adapterForProgress.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayItems() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getKey() != null) {
                    Users users = snapshot.getValue(Users.class);


                    // Initialize SharedPreferences
                    SharedPreferences.Editor preference = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
                    preference.putBoolean("Switch", users.isIskillerswitch());
                    preference.apply();


                    Calendar calendar1 = Calendar.getInstance();

                    long x = Long.parseLong(users.getTimemillis());

                    long minus_millis = ((calendar1.getTimeInMillis() + 86400000) - x);

                    //millisec into a day
                    // if 1day = 24hours & 1hour = 60minutes & 1 min = 60sec =


                    long division = minus_millis / 86400000;

                    BigDecimal bd = new BigDecimal(division);
                    bd = bd.setScale(0, BigDecimal.ROUND_DOWN); //truncate

                    //if 30days = 100% what about this BD; So

                    int p = (bd.intValue() * 100) / 30;

                    BigDecimal bigDecimal = new BigDecimal(p);
                    bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN); //truncate

                    ff.setText("" + bd);


//                DecimalFormat twoDForm = new DecimalFormat("#.##");
//
//                Toast.makeText(HomeActivity.this,""+  Double.valueOf(twoDForm.format(division)), Toast.LENGTH_SHORT).show();

                    progress1.setMax(10000);
                    ObjectAnimator animation = ObjectAnimator.ofInt(progress1, "progress",
                            progress1.getProgress(), bigDecimal.intValue() * 100);
                    animation.setDuration(3000);
                    animation.setAutoCancel(true);
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();

                    // Create a Calendar object with the start time in milliseconds
                    Calendar startDate = Calendar.getInstance();
                    startDate.setTimeInMillis(x + 86400000);

                    // Add 30 days to the startDate using the add method
                    Calendar endDate = (Calendar) startDate.clone();
                    endDate.add(Calendar.DATE, 30);

                    // Get the end time in milliseconds using the getTimeInMillis method
                    long endTime = endDate.getTimeInMillis() - 86400000;

                    long minus = endTime - System.currentTimeMillis();

                    startCountdown(minus);

                    if (users.isIskillerswitch()) {
                        state_txtx.setText(R.string.on);
                        state_txtx.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.lightGreen));
                    } else {
                        state_txtx.setText(R.string.off);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startCountdown(long endTimeInMillis) {
        countme = new CountDownTimer(endTimeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String time = String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toDays(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - (TimeUnit.MILLISECONDS.toDays(millisUntilFinished) * 24),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) * 60),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) * 60));

                        final String[] daystosec = time.split(":");

                        updateCountdownText(daystosec);
                    }
                });
            }

            public void onFinish() {
                setVisibility();
                parade();
            }
        }.start();
    }

    public void explode() {
        EmitterConfig emitterConfig = new Emitter(100L, TimeUnit.MILLISECONDS).max(100);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .spread(360)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 30f)
                        .position(new Position.Relative(0.5, 0.3))
                        .build()
        );
    }

    public void parade() {
        EmitterConfig emitterConfig = new Emitter(3, TimeUnit.SECONDS).perSecond(90);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .angle(Angle.RIGHT - 45)
                        .spread(Spread.SMALL)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(10f, 30f)
                        .position(new Position.Relative(0.0, 0.5))
                        .build(),
                new PartyFactory(emitterConfig)
                        .angle(Angle.LEFT + 45)
                        .spread(Spread.SMALL)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(10f, 30f)
                        .position(new Position.Relative(1.0, 0.5))
                        .build()
        );
    }

    public void rain() {
        EmitterConfig emitterConfig = new Emitter(5, TimeUnit.SECONDS).perSecond(100);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .angle(Angle.BOTTOM)
                        .spread(Spread.ROUND)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 15f)
                        .position(new Position.Relative(0.0, 0.0).between(new Position.Relative(1.0, 0.0)))
                        .build()
        );
    }

    private void setVisibility() {
        timebar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.invicibleColor));
        time_holder.setVisibility(View.GONE);
        goodbye.setVisibility(View.VISIBLE);
        lottie_layer_name.setVisibility(View.VISIBLE);
        bottom.setVisibility(View.VISIBLE);
        xxxp.setVisibility(View.GONE);
        hold.setVisibility(View.GONE);
    }

    private void updateCountdownText(String[] i) {
        Day.setText(i[0]);
        Hours.setText(i[1]);
        Minutes.setText(i[2]);
        Seconds.setText(i[3]);
    }

    private void displayItems2() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getForHabitsList.clear();
                for (DataSnapshot sn : snapshot.getChildren()) {
                    ForHabits forHabits = sn.getValue(ForHabits.class);

                    getForHabitsList.add(forHabits);
                }

                showlistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (BottomSheetBehavior.STATE_EXPANDED == bottomSheetBehavior.getState() || BottomSheetBehavior.STATE_EXPANDED == profilebehavior.getState()) {
            nest.smoothScrollTo(0, 0);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            profilebehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    private boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE_NAME = getPackageName() + "/" + AppBlockingService.class.getName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString(settingValue);
                while (colonSplitter.hasNext()) {
                    String accessibilityService = colonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE_NAME)) {
                        return true;
                    }
                }
            }
        } else {
            Log.d(TAG, "Accessibility service disabled");
        }
        return false;
    }

    @Override
    public void onDestroy() {
//        itemsofHabitAdapter.onDestroy();
        // Stop the sound if it's playing

        super.onDestroy();
    }

    private void signOut() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser.getUid() != null) {
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(HomeActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

            googleSignInClient.signOut().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Create an intent to identify the service to be stopped
                    if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
                        Intent intent = new Intent(this, DelayService.class);
                        // Stop the service
                        stopService(intent);
                    }

                    FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Create an Intent for your Accessibility Service
                                Intent serviceIntent = new Intent(HomeActivity.this, AppBlockingService.class);
                                // Stop the Accessibility Service
                                stopService(serviceIntent);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                auth.signOut();
                                finish();
                            }
                        }
                    });


                }
            });
        }

        try {
            // Create an intent to identify the service to be stopped
            if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
                Intent intent = new Intent(this, DelayService.class);
                // Stop the service
                stopService(intent);
            }
            auth.signOut();
            stopService(new Intent(getApplicationContext(), AppBlockingService.class));
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } catch (Exception e) {
            Log.e("TAG", "Error: ", e);
        }
    }

    private void shareProgress() {
        //Add in a url link that helps people share the app
        String uri = "https://play.google.com/store/apps/details?id=com.jimthedeveloper.purypal";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/link");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Get reKINDLE");
        intent.putExtra(Intent.EXTRA_TEXT, "Awaken from Slumber" + "\n" + "\n" + "Ever wondered how you will ever get " +
                "control of yourself?. Every time you want to do something productive you get distracted and end up procrastinating your work." +
                " reKindle got you covered. reKindle will help you get back the control you want." + "\n" + "\n" + "Your life is precious and we are not here for a long time, so ditch your phone and do something productive. " + "\n" + "\n" +
                "You can get it for free at" + "\n" + uri);

        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private void contactUs() {
        // Example data
        String emailSubject = "FEEDBACK";
        String[] emailReceiver = {"mappedvision@gmail.com"};

        // Create an Intent with the ACTION_SENDTO action
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        // Set the data for the intent (specify the "mailto" scheme)
        emailIntent.setData(Uri.parse("mailto:"));

        // Set the email subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);

        // Set the email recipients
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailReceiver);

        // Verify if there's an email app to handle the intent
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            // Start the activity
            startActivity(emailIntent);
        } else {
            Toast toast = Toast.makeText(HomeActivity.this, "Kindly install an email app and try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void rateUs() {
        //Set the url for the app that heads directly to ratings
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.jimthedeveloper.purypal");

        Intent launchIntent = new Intent(Intent.ACTION_VIEW, uri);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.jimthedeveloper.purypal")));
        }
    }

    private void signFirst(boolean ispaid) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.ThickTranslucentDialog);
        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.sign_alertbar,
                viewGroup, false);

        RelativeLayout signcheck = view.findViewById(R.id.signcheck);
        RelativeLayout clear = view.findViewById(R.id.clear);
        RelativeLayout donesigning = view.findViewById(R.id.donesigning);
        LinearLayout downloading = view.findViewById(R.id.downloading);
        ViewGroup disappear = view.findViewById(R.id.disappear);
        TextView showing = view.findViewById(R.id.showing);
        ProgressBar progress = view.findViewById(R.id.progress);
        SignaturePad signpad = view.findViewById(R.id.signpad);

        builder.setCancelable(false);
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        signpad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                signcheck.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSigned() {
                signcheck.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClear() {
                signcheck.setVisibility(View.GONE);
            }
        });


        clear.setOnClickListener(view1 -> {
            signpad.clear();
        });

        donesigning.setOnClickListener(view12 -> {
            if (signpad.isEmpty()) {
                Toast toast = Toast.makeText(this, "SIGN TO PROCEED", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


            signcheck.setVisibility(View.GONE);
            downloading.setVisibility(View.VISIBLE);
            uploadSignature(signpad, alertDialog, ispaid, progress, showing, disappear);
        });
    }

    private void uploadSignature(SignaturePad signpad, AlertDialog alertDialog, boolean ispaid, ProgressBar progress, TextView showing, ViewGroup disappear) {
        byte[] data = new byte[0];

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Signatures");

        Bitmap bitmap = signpad.getSignatureBitmap();


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = storageReference.putBytes(data);
        storageReference.putBytes(data);


        Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                TransitionManager.beginDelayedTransition(disappear);
                progress.setVisibility(View.GONE);
                showing.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGreen));
                showing.setText("Done!");

//                alertDialog.dismiss();
                Uri downLoadUri = task.getResult();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Signature", downLoadUri.toString());
                hashMap.put("Issigned", true);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Initialize SharedPreferences
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PREF", Context.MODE_PRIVATE);
                            SharedPreferences.Editor pref = sharedPreferences.edit();
                            pref.putBoolean("Signed", true);
                            pref.commit();

                            if (!ispaid) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(getApplicationContext(), SubscriptionPageActivity.class));
                                        finish();
                                    }
                                }, 1000);
                            }

                        }
                    }
                });
            }
        });
    }

    private void openAdialog() {
        boolean accessibility = isAccessibilityEnabled();
        ViewGroup viewGroup = findViewById(android.R.id.content);
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.grant_permission_layout,
                viewGroup, false);

        Button deny = view.findViewById(R.id.cancel_alert);
        Button allow = view.findViewById(R.id.discard_alert);
        TextView textDisclaimer = view.findViewById(R.id.textDisclaimer);

        builder.setCancelable(true);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();


        if (accessibility) {
            deny.setText("CANCEL");
            allow.setText("SWITCH-OFF");

            textDisclaimer.setText("Before you go, kindly switch off the Accessibility Service.");

            deny.setOnClickListener(view1 -> {
                alertDialog.dismiss();
            });

            allow.setOnClickListener(view12 -> {
                alertDialog.dismiss();
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            });

            return;
        }

        deny.setText("CANCEL");
        allow.setText("SIGN-OUT");

        textDisclaimer.setText("It's sad you're leaving us. Are you sure you want to sign-out");

        deny.setOnClickListener(view1 -> {
            alertDialog.dismiss();
        });

        allow.setOnClickListener(view12 -> {
            alertDialog.dismiss();
            signOut();
        });
    }

    private void openInstagram() {
        Uri uri = Uri.parse("https://www.instagram.com/rekindle.app/");

        Intent launchIntent = new Intent(Intent.ACTION_VIEW, uri);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchIntent.setPackage("com.instagram.android");

        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/rekindle.app/")));
        }
    }

    private void openTikTok() {


        Uri uri = Uri.parse("https://www.tiktok.com/@rekindle.app");

        Intent launchIntent = new Intent(Intent.ACTION_VIEW, uri);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchIntent.setPackage("com.zhiliaoapp.musically");

        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@rekindle.app")));
        }
    }

    private void openX() {
        Uri uri = Uri.parse("https://x.com/devy_the_dev");

        Intent launchIntent = new Intent(Intent.ACTION_VIEW, uri);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchIntent.setPackage("com.twitter.android");

        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://x.com/devy_the_dev")));
        }
    }

}



