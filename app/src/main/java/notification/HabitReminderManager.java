package notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.core.app.NotificationCompat;

import receivers.HabitReminderReceiver;

public class HabitReminderManager {
    private static final int MORNING_REMINDER_REQUEST_CODE = 1001;
    private static final int EVENING_REMINDER_REQUEST_CODE = 1002;

    public static void scheduleTwiceDailyReminders(Context context, String habitName) {
        // Schedule the morning reminder at 9:00 AM
        scheduleDailyReminder(context, habitName, 0, 9, MORNING_REMINDER_REQUEST_CODE);

        // Schedule the evening reminder at 8:00 PM
        scheduleDailyReminder(context, habitName, 23, 50, EVENING_REMINDER_REQUEST_CODE);
    }

    private static void scheduleDailyReminder(Context context, String habitName, int hourOfDay, int minute, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Create an Intent to launch the BroadcastReceiver
        Intent intent = new Intent(context, HabitReminderReceiver.class);
        intent.putExtra("habitName", habitName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm to start at the specified time
        // Repeat the alarm every day
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTriggerTime(hourOfDay, minute), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private static long getTriggerTime(int hourOfDay, int minute) {
        // Set the alarm to trigger at the specified time
        long now = System.currentTimeMillis();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(java.util.Calendar.MINUTE, minute);
        calendar.set(java.util.Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= now) {
            // If the specified time has already passed, schedule it for the next day
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
        }

        return calendar.getTimeInMillis();
    }
}

