package receivers;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import devydev.mirror.net.R;
import front_page.SecondSubActivity;
import model.Users;
import notification.HabitReminderManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import service.AppBlockingService;
import service.DelayService;
import utils.UsageStatsHelper;

public class BroadcastBootLoader extends BroadcastReceiver {

    Handler handler = new Handler();

    private static final int EVENING_REMINDER_REQUEST_CODE = 1002;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();


            if (account != null) {
                // Check if the app has the POST_NOTIFICATIONS permission
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    // Call the method to schedule reminders when the activity is created
                    HabitReminderManager.scheduleTwiceDailyReminders(context, "Your Habit Name");
                } else {
                    // Permission not granted, handle this situation (e.g., inform the user)
                    // You might want to log a message or take an alternative action
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.keepSynced(true);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        if (users.isIspaid()) {
                            boolean accessibilityServiceEnabled = isAccessibilityEnabled(context);
                            if (accessibilityServiceEnabled){
                                Intent in = new Intent(context, AppBlockingService.class);
                                ContextCompat.startForegroundService(context, in);

                                if (UsageStatsHelper.isUsageAccessPermissionGranted(context)) {
                                    // The permission is granted
                                    Intent getIntent = new Intent(context, DelayService.class);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        ContextCompat.startForegroundService(context, getIntent);
                                    } else {
                                        context.startService(getIntent);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            if (user != null){
                scheduleDailyReminder(context);
//                nightSchedule(context);
            }

        }

    }

    private boolean isAccessibilityEnabled(Context context) {
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE_NAME = context.getPackageName() + "/" + AppBlockingService.class.getName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

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


    private static void scheduleDailyReminder(Context context) {
        // Set the time to 8:00 AM
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, BroadcastBootLoader.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Schedule the notification using AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            // Use setInexactRepeating to optimize power consumption
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }


        String header = context.getResources().getString(R.string.notification_header);
        String body = context.getResources().getString(R.string.notification_body);


        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        try {
                            JSONObject jsonObject = new JSONObject();
                            JSONObject notification = new JSONObject();
                            notification.put("title", header);
                            notification.put("body", body);
                            JSONObject dataObject = new JSONObject();
                            dataObject.put("userId", users.getUserid());

                            jsonObject.put("notification", notification);
                            jsonObject.put("data", dataObject);
                            jsonObject.put("to", users.getToken());

                            MediaType JSON = MediaType.get("application/json");
                            OkHttpClient client = new OkHttpClient();

                            String url = "https://fcm.googleapis.com/fcm/send";

                            RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(requestBody)
                                    .header("Authorization", "Bearer AAAAZ1mMx5U:APA91bHg8sDA6v96PgMzhX1KTx2jEASnq6AaC28859wvyo82LiAeaK7FHb2jj1y0DMVecEwXT8AtXyA_jHgf-VTSgSeIdQxZOopnyimX5PDjpxEWi7s5_fk8lCcoBwQY1YjePcQ-rVc8")
                                    .build();

                            Log.d("Trig", "Called 2");

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.d("LOG", ""+e.getLocalizedMessage());
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    Log.d("Trig", "" + response.body());
                                }
                            });
                        }catch (Exception e){
                            Log.d("LOG", "" + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


//    private static void nightSchedule(Context context) {
//        // Set the time to 8:00 AM
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 21);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//
//        Intent intent = new Intent(context, BroadcastBootLoader.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Schedule the notification using AlarmManager
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        if (alarmManager != null) {
//            String header = context.getResources().getString(R.string.night_header);
//            String body = context.getResources().getString(R.string.night_body);
//            FirebaseDatabase.getInstance().getReference().child("Users")
//                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            Users users = snapshot.getValue(Users.class);
//                            try {
//                                JSONObject jsonObject = new JSONObject();
//                                JSONObject notification = new JSONObject();
//                                notification.put("title", header);
//                                notification.put("body", body);
//                                JSONObject dataObject = new JSONObject();
//                                dataObject.put("userId", users.getUserid());
//
//                                jsonObject.put("notification", notification);
//                                jsonObject.put("data", dataObject);
//                                jsonObject.put("to", users.getToken());
//
//                                MediaType JSON = MediaType.get("application/json");
//                                OkHttpClient client = new OkHttpClient();
//
//                                String url = "https://fcm.googleapis.com/fcm/send";
//
//                                RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
//                                Request request = new Request.Builder()
//                                        .url(url)
//                                        .post(requestBody)
//                                        .header("Authorization", "Bearer AAAAZ1mMx5U:APA91bHg8sDA6v96PgMzhX1KTx2jEASnq6AaC28859wvyo82LiAeaK7FHb2jj1y0DMVecEwXT8AtXyA_jHgf-VTSgSeIdQxZOopnyimX5PDjpxEWi7s5_fk8lCcoBwQY1YjePcQ-rVc8")
//                                        .build();
//
//                                Log.d("Trig", "Called 2");
//
//                                client.newCall(request).enqueue(new Callback() {
//                                    @Override
//                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                                        Log.d("LOG", ""+e.getLocalizedMessage());
//                                    }
//
//                                    @Override
//                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                                        Log.d("Trig", "" + response.body());
//                                    }
//                                });
//                            }catch (Exception e){
//                                Log.d("LOG", "" + e.getLocalizedMessage());
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//            // Use setInexactRepeating to optimize power consumption
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY, pendingIntent);
//        }
//    }




    static void callAPI(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();

        String url = "https://fcm.googleapis.com/fcm/send";

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization", "Bearer AAAAZ1mMx5U:APA91bHg8sDA6v96PgMzhX1KTx2jEASnq6AaC28859wvyo82LiAeaK7FHb2jj1y0DMVecEwXT8AtXyA_jHgf-VTSgSeIdQxZOopnyimX5PDjpxEWi7s5_fk8lCcoBwQY1YjePcQ-rVc8")
                .build();

        Log.d("Trig", "Called 2");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("LOG", ""+e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Trig", "" + response.body());
            }
        });
    }

}
