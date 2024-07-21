package receivers;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import devydev.mirror.net.R;
import front_page.HomeActivity;

public class HabitReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the app has the POST_NOTIFICATIONS permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with sending the notification
            sendNotification(context, intent);
        } else {
            // Permission not granted, handle this situation (e.g., inform the user)
            // You might want to log a message or take an alternative action
        }

    }

    private void sendNotification(Context context, Intent intent) {
        String habitName = intent.getStringExtra("3 Habits");

        // Create an intent to launch the app when the notification is clicked
        Intent resultIntent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        Notification notification = new NotificationCompat.Builder(context, "habit")
                .setContentTitle("Habit Reminder")
                .setContentText("Don't forget to work on your " + habitName + ".")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        // Display the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}

