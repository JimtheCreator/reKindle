package utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class MyApplication extends Application {

    private static boolean isAppInForeground = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register ActivityLifecycleCallbacks to track app's lifecycle
        registerActivityLifecycleCallbacks(new AppLifecycleCallbacks());
    }

    public static boolean isAppInForeground() {
        return isAppInForeground;
    }

    private static class AppLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            // Activity created
        }

        @Override
        public void onActivityStarted(Activity activity) {
            // Activity started
            isAppInForeground = true;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            // Activity resumed
            isAppInForeground = true;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            // Activity paused
            isAppInForeground = false;
        }

        @Override
        public void onActivityStopped(Activity activity) {
            // Activity stopped
            isAppInForeground = false;
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            // Activity save instance state
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            // Activity destroyed
        }
    }
}

