package service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import devydev.mirror.net.R;
import floating_windows.TouchOverlayView;
import floating_windows.WaterLikeWindow;
import model.Intervene;
import notification.HabitReminderManager;
import windowactivities.WaterActivity;

public class DelayService extends Service {
    private static final int CHECK_INTERVAL = 1000; // Check every second
    private static final String CHANNEL_ID = "intercept";
    public static boolean continueInstagram = false;
    Long lastEventTime;
    String name;
    Handler hh = new Handler();

    Handler handler = new Handler();
    //    public static boolean isTRIGGERED = false;
    List<String> interveneApps = new ArrayList<>();
    private HashMap<String, Handler> resetHandlers = new HashMap<>();
    private UsageStatsManager usageStatsManager;
    private HashMap<String, Long> lastBackgroundEvent = new HashMap<>();

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private HashMap<String, Long> lastForegroundEvent = new HashMap<>();

    WindowManager.LayoutParams params;


    @Override
    public void onCreate() {
        super.onCreate();
        usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, DelayService.class);
        alarmIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel();
        Log.d("STARTED", "EXCELLENCE");
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Intercept Launch";
            String description = "This helps intercept app launch";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Checking AppLaunch")
                .setContentText("Currently checking opened apps...")
                .setSmallIcon(R.drawable.transparent_logo)
                .build();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = createNotification();
        startForeground(1, notification);



        // Start a background thread for continuous monitoring
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(CHECK_INTERVAL);
                        Log.d("LOGGING", "" + continueInstagram);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            // Check if the app has the POST_NOTIFICATIONS permission
                            if (ContextCompat.checkSelfPermission(DelayService.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                // Call the method to schedule reminders when the activity is created
                                HabitReminderManager.scheduleTwiceDailyReminders(DelayService.this, "Your Habit Name");
                            } else {
                                // Permission not granted, handle this situation (e.g., inform the user)
                                // You might want to log a message or take an alternative action
                            }
                        }else {
                            HabitReminderManager.scheduleTwiceDailyReminders(DelayService.this, "Your Habit Name");
                        }

                        isInterveneApps();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        DelayService.continueInstagram = false;
                    }
                }
            }
        }).start();

        return START_STICKY;
    }


    private void checkForegroundApp() {
        UsageEvents usageEvents = usageStatsManager.queryEvents(System.currentTimeMillis() - 1000, System.currentTimeMillis());
        UsageEvents.Event event = new UsageEvents.Event();
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            Log.d("INNIT", "EXCELLENCE");
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                if (event.getPackageName().equals("com.instagram.android")) {
                    Log.d("INNIT", "START");
                    // The locked app is launched, show the lock screen
                    Intent lockIntent = new Intent(this, WaterActivity.class);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(lockIntent);
                }
            }
        }
    }

    private void scheduleNextCheck() {
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + CHECK_INTERVAL, alarmIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hh.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
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

    private boolean isAppInForeground(String packageName) {
        // If Instagram is in the background, reset the continueInstagram flag after a delay

        if (!DelayService.continueInstagram) {
            // If Instagram is in the background, reset the continueInstagram flag
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

            if (usageStatsManager != null) {
                long currentTime = System.currentTimeMillis();
//            UsageEvents usageEvents = usageStatsManager.queryEvents(currentTime - 1000 * 60, currentTime + 1000 * 60);

                UsageEvents usageEvents = usageStatsManager.queryEvents(currentTime - 1000 * 5, currentTime);

                while (usageEvents.hasNextEvent()) {
                    UsageEvents.Event event = new UsageEvents.Event();
                    usageEvents.getNextEvent(event);

                    if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND
                            && packageName.equals(event.getPackageName())
                            && !shouldIgnoreEvent(packageName, event.getEventType())) {
                        return true;
                    } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND
                            && packageName.equals(event.getPackageName())) {
                        shouldIgnoreEvent(packageName, event.getEventType());
                    }

                }
            }
        }


        return false;
    }


    private boolean shouldIgnoreEvent(String packageName, int eventType) {
        long currentTime = System.currentTimeMillis();
        if (eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {

            lastEventTime = lastForegroundEvent.get(packageName);
            if (lastEventTime != null && currentTime - lastEventTime < 900000) {
                // Ignore this event
                return true;
            }

            // Update the time of the last event
            lastForegroundEvent.put(packageName, currentTime);
        } else if (eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
            if (lastEventTime != null && currentTime - lastEventTime < 900000) {
                // Ignore this event
                return true;
            }

            // Reset the time of the last foreground event
            lastForegroundEvent.remove(packageName);
            lastBackgroundEvent.put(packageName, currentTime);
        }
        return false;
    }


    private void isInterveneApps() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Intervene")
                    .child(user.getUid());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    interveneApps.clear();
                    if (snapshot != null) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            Intervene apps = sn.getValue(Intervene.class);
                            interveneApps.add(apps.getPackagename());


                            if (isAppInForeground(apps.getPackagename())) {
                                // Prompt your screen lock window here
                                // ...
                                Log.d("LOGGING", "" + continueInstagram);
                                hh.postDelayed(() -> {
                                    hh.removeCallbacksAndMessages(null);
                                    // The locked app is launched, show the lock screen
                                    Intent lockIntent = new Intent(DelayService.this, WaterActivity.class);
                                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(lockIntent);

                                }, 1000);

                                // Exit the thread when the condition is met
                                //return;
                            } else {
                                WaterLikeWindow waterLikeWindow = new WaterLikeWindow(DelayService.this);
                                waterLikeWindow.close();

                                if (DelayService.continueInstagram) {
//                                    Log.d("DEBUGGING SAIII", "" + DelayService.isTRIGGERED);
                                    handler.postDelayed(() -> {
//                                        DelayService.isTRIGGERED = false;
                                        DelayService.continueInstagram = false;
                                    }, 10000); // 1 min delay
                                }

                            }


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

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}
