package service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import devydev.mirror.net.R;
import floating_windows.EasyWindow;
import floating_windows.WaterLikeWindow;
import floating_windows.Window;
import model.Apps;
import model.Lethal;
import utils.ExcludePreference;
import utils.MySharedPreferences;
import windowactivities.DropdownShadeActivity;
import windowactivities.WaterActivity;
import windowactivities.WindowActivity;

public class AppBlockingService extends AccessibilityService {
    private static final int NOTIFICATION_ID = 123;
    private static final String CHANNEL_ID = "block";
    private static final CharSequence CHANNEL_NAME = "Blocked Apps";

    private static final long CHECK_INTERVAL = 1000; // 5 seconds

    private Handler wow = new Handler(Looper.getMainLooper());
    private boolean isServiceRunning = true;

    boolean isTRIGGERED= false;
    private static final String CHANNEL_DESCRIPTION = "Checking blocked apps";
    List<String> blockedApps = new ArrayList<>();

    List<String> lethalApps = new ArrayList<>();

    private long startTime;
    Runnable ru;

    private static String removeDots(String input) {
        // Replace dots with an empty string
        return input.replace(".", "");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName().toString();

            long timeSpentOnWhatsApp =getAppUsageTime(this, packageName);
            long minutes = (timeSpentOnWhatsApp / (1000 * 60)) % 60;
            long hours = (timeSpentOnWhatsApp / (1000 * 60 * 60)) % 24;
            long seconds = (timeSpentOnWhatsApp / (1000));


            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String formattedTime = dateFormat.format(getAppUsageTime(this, packageName));


            if (getUsageTimeForApp(getApplicationContext(), packageName) > 360000){

            }

            // Check if the app is locked
            if (isBlockedApp(packageName)) {
                // Show a blocking window
                showBlockingWindow();
            }

            long endTime = System.currentTimeMillis();
            long usageTime = endTime - startTime;


            packageName = event.getPackageName().toString();
            startTime = System.currentTimeMillis();

            String cut = packageName.replace(".", "");


            if (madeLethal(cut)) {
                update(packageName, startTime);
            }


        }
        else {
            Log.d("EXECUTE", "REMOVED");
        }


    }

    @Override
    public void onInterrupt() {
        // Handle interrupt, if needed
    }

    private void showBlockingWindow() {
        // Start the WindowActivity

        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);

        Log.d("HELLO", "CALLED");
        Intent intent = new Intent(this, WindowActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        showForegroundNotification();
        // Initialize GestureDetector in onServiceConnected
    }

    private void showForegroundNotification() {
        // Create Notification Channel on devices running Android 8.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Build the notification using NotificationCompat
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Blocked Apps")
                .setContentText("Tap to configure or disable.")
                .setSmallIcon(R.drawable.transparent_logo) // Replace with your notification icon
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)) // Replace with your app's launcher icon
                .setAutoCancel(false);

        // Use FLAG_MUTABLE if the PendingIntent needs to be mutable
        int flags = PendingIntent.FLAG_IMMUTABLE;

        // Intent for the accessibility settings
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);
        builder.setContentIntent(pendingIntent);

        // Build the notification
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;


        // Start the service in the foreground
        startForeground(NOTIFICATION_ID, notification);
    }

    private void tryOne(String packageName, long startTime) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String string = packageName.replace(".", "");

        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Lethal")
                    .child(user.getUid())
                    .child(string);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot != null) {
                        Lethal spareTime = snapshot.getValue(Lethal.class);

                        if (spareTime != null) {
                            long hours = Integer.parseInt(spareTime.getHours());
                            long millyhours = hours * 3600000;
                            long mins = Integer.parseInt(spareTime.getMinutes());
                            long millyMins = mins * 60000;

                            long total = millyhours + millyMins;

//                            if (spareTime.isIslimited()) {
//                                Log.d("CALLED", "CALLED 1");
//                                WaterLikeWindow lethalWindow = new WaterLikeWindow(getApplicationContext());
//                                lethalWindow.open();
//                            } else {
//                                Log.d("CALLED", "TRIGGERED");
//                                hh.postDelayed(() -> updateData(packageName, hh), total);
//                            }

                            // Convert the time to a human-readable format
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                            String formattedTime = dateFormat.format(getAppUsageTime(getApplicationContext(), packageName));


                            Log.d("CURRENT TIME", ""+formattedTime);

                            if (total <= getAppUsageTime(getApplicationContext(), packageName)){
                                Log.d("EVEN", "CALLED");
                                Intent intent = new Intent(getApplicationContext(), DropdownShadeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

//                            if (spareTime.isIslimited()) {
//                                Log.d("CALLED", "CALLED 1");
//                                WaterLikeWindow lethalWindow = new WaterLikeWindow(getApplicationContext());
//                                lethalWindow.open();
//                            } else {
//                                Log.d("CALLED", "TRIGGERED");
//                                hh.postDelayed(() -> updateData(packageName, hh), total);
//                            }
                        }

                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });
        } else {
            Log.e("TAG_meee", "User not signed in");
        }
    }

    private void updateData(String packageName, Handler hh) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar1 = Calendar.getInstance();
        final String todaysdate = dateFormat.format(calendar1.getTime());
        try {
            Log.d("REPEATING", "CALL TWO");
            Date d = dateFormat.parse(todaysdate);
            long milliseconds = d.getTime();

            String modifiedString = removeDots(packageName);

            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            stringObjectHashMap.put("Islimited", true);
            stringObjectHashMap.put("Lastchecked", milliseconds);

            FirebaseDatabase.getInstance().getReference().child("Lethal")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(modifiedString)
                    .updateChildren(stringObjectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast toast = Toast.makeText(AppBlockingService.this, "", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            hh.removeCallbacksAndMessages(null);
                        }
                    });

//            Handler run = new Handler();
//            run.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    FirebaseDatabase.getInstance().getReference().child("Lethal")
//                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                            .child(modifiedString)
//                            .updateChildren(stringObjectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    hh.removeCallbacksAndMessages(null);
//                                    WaterLikeWindow lethalWindow = new WaterLikeWindow(getApplicationContext());
//                                    lethalWindow.open();
//                                    Toast.makeText(AppBlockingService.this, "Time's up!!!", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//            }, 15000);


        } catch (Exception e) {
            Log.d("LOGGED", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    private void update(String packageName, long startTime) {
        String bb = packageName.replace(".", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Lethal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(bb);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Lethal users = snapshot.getValue(Lethal.class);
                if (users != null) {
                    try {
                        long x = users.getLastchecked();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Calendar calendar1 = Calendar.getInstance();
                        final String todaysdate = dateFormat.format(calendar1.getTime());
                        Date d = dateFormat.parse(todaysdate);
                        long milliseconds = d.getTime();

                        if (milliseconds > x) {
                            Log.d("TIME", "SIKE");
                            setStatus2(milliseconds, users.getPackage());
                        } else {
                            tryOne(packageName, startTime);
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setStatus2(long milliseconds, String packageName) {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("Islimited", false);
        stringObjectHashMap.put("Lastchecked", milliseconds);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Lethal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(packageName);

        if (reference.getKey() != null) {
            reference.updateChildren(stringObjectHashMap);
//            reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    // Iterate over all children and update them with the HashMap
//                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//                        // Update each child with the HashMap
//                        childSnapshot.getRef().updateChildren(stringObjectHashMap);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle errors here
//                }
//            });

        }

    }

    private boolean isBlockedApp(String packageName) {
        // Add logic to determine if the app is blocked
        // You might have a list of blocked apps stored in SharedPreferences
        // For simplicity, let's consider blocking a hypothetical app called "BlockedApp"
        // Implement logic to check if the app is in the blocked list

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Apps")
                    .child(user.getUid());

            reference.keepSynced(true);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    blockedApps.clear();
                    if (snapshot != null) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            Apps apps = sn.getValue(Apps.class);
                            blockedApps.add(apps.getPackagename());

                            // Update the SharedPreferences after adding all items
                            MySharedPreferences.saveStringArray(getApplicationContext(), blockedApps);
                        }


                        // Now, you can check if the current app is blocked
//                        if (blockedApps.contains(packageName)) {
//                            // Show a blocking window
//                            showBlockingWindow();
//                            Window window = new Window(getApplicationContext());
//                            window.close();
//                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });
        } else {
            Log.e("TAG_meee", "User not signed in");
        }

        return blockedApps.contains(packageName);
    }

    private boolean madeLethal(String packageName) {
        // Add logic to determine if the app is blocked
        // You might have a list of blocked apps stored in SharedPreferences
        // For simplicity, let's consider blocking a hypothetical app called "BlockedApp"
        // Implement logic to check if the app is in the blocked list

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Lethal")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.keepSynced(true);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lethalApps.clear();
                    Log.d("LAUNCH", "FALSE");
                    if (snapshot != null) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            Lethal apps = sn.getValue(Lethal.class);
                            lethalApps.add(apps.getPackage());
                            Log.d("LAUNCH", apps.getPackage());

                            ExcludePreference.saveStringArray(getApplicationContext(), lethalApps);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });
        } else {
            Log.e("TAG_meee", "User not signed in");
        }

        return lethalApps.contains(packageName);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());

        // Use FLAG_IMMUTABLE for immutable PendingIntent
        int flags = PendingIntent.FLAG_IMMUTABLE;

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1,
                restartService, flags);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), pendingIntent);

        super.onTaskRemoved(rootIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up any resources used by the service
    }


    private long getUsageTimeForApp(Context context, String packageName){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        for (UsageStats usageStats : usageStatsList) {
            if (usageStats.getPackageName().equals(packageName)) {
                return usageStats.getTotalTimeInForeground();
            }
        }

        return 0;
    }


    private long getUsageTime(Context context, String packagename){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        for (UsageStats usageStats : usageStatsList) {
            if (usageStats.getPackageName().equals(packagename)) {
                return usageStats.getTotalTimeInForeground();
            }
        }

        return 0;
    }

    private long getAppUsageTime(Context context, String packageName) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        for (UsageStats usageStats : usageStatsList) {
            if (usageStats.getPackageName().equals(packageName)) {
                return usageStats.getTotalTimeInForeground();
            }
        }

        return 0;
    }


}
