package front_page;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import decor.ParallaxItemDecoration;
import devydev.mirror.net.R;
import model.ItemDecorator;
import model.Lethal;
import model.Users;
import service.AppBlockingService;
import service.DelayService;
import utils.MySharedPreferences;
import utils.UsageStatsHelper;

public class KillerSwitchActivity extends AppCompatActivity {

    private static final String LOGTAG = "LOG 1";
    private static final int REQUEST_CODE_SYSTEM_ALERT_WINDOW = 123;
    SwitchCompat killerswitchtoggle;
    Handler handler;
    RecyclerView sparetimerecyclerview;
    LinearLayout idhead;
    RelativeLayout spare;
    boolean checked;
    boolean isAccess;


    LinearLayout notice, vene;
    MyAdapter adapter;
    RecyclerView listView;
    SpareAdapter spareAdapter;
    LinearLayout loading_apps, warning_layout, lay_over, getWarning_layout, accessibility, battery, useraccess;
    ViewGroup viewGroup;
    TextView theText;
    private final ActivityResultLauncher<Intent> requestOverlayPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (Settings.canDrawOverlays(KillerSwitchActivity.this)) {
                                // The permission has been granted, proceed with your logic
                                TransitionManager.beginDelayedTransition(viewGroup);
                                lay_over.setVisibility(View.GONE);
                                bt1.setVisibility(View.GONE);
                            } else {
                                // The user denied the permission. Handle accordingly.
                                // You might want to inform the user about the necessity of the permission.
                                TransitionManager.beginDelayedTransition(viewGroup);
                                theText.setVisibility(View.VISIBLE);
                                getWarning_layout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
    View bt0, bt1, bt2, bt3, bt4;
    NestedScrollView nested;
    RelativeLayout displayoverapps;
    private boolean isScrolling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_killer_switch);

        SharedPreferences pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        checked = pref.getBoolean("Switch", false);

        findViewById(R.id.closepage).setOnClickListener(view -> finish());

//        SharedPreferences sha = getSharedPreferences("PREF", Context.MODE_PRIVATE);
//        UsageAccess = sha.getBoolean("UsageAccess", false);

        vene = findViewById(R.id.vene);
        spare = findViewById(R.id.spare);
        idhead = findViewById(R.id.idhead);
        sparetimerecyclerview = findViewById(R.id.sparetimerecyclerview);
        bt0 = findViewById(R.id.bt0);
        battery = findViewById(R.id.battery);
        killerswitchtoggle = findViewById(R.id.killerswitchtoggle);
        notice = findViewById(R.id.notice);
        useraccess = findViewById(R.id.useraccess);
        theText = findViewById(R.id.theText);
        getWarning_layout = findViewById(R.id.kska);
        lay_over = findViewById(R.id.lay_over);
        warning_layout = findViewById(R.id.warning_layout);
        loading_apps = findViewById(R.id.loading_apps);
        accessibility = findViewById(R.id.accessibility);
        displayoverapps = findViewById(R.id.displayoverapps);
        nested = findViewById(R.id.nested);
        viewGroup = findViewById(R.id.viewGroup);

        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.oopp);
        bt4 = findViewById(R.id.bt1);

        nested.setFillViewport(true);

        // Get the list view
        listView = findViewById(R.id.listview);
        listView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        listView.addItemDecoration(spacebetweenItems);
        listView.setLayoutManager(new GridLayoutManager(this, 3));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        listView.addItemDecoration(new ItemDecorator(3, spacingInPixels, true));

        // Add ParallaxItemDecoration with a parallax factor (adjust as needed)
        int parallaxFactor = getResources().getDimensionPixelOffset(R.dimen.parallax_factor);
        sparetimerecyclerview.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        sparetimerecyclerview.addItemDecoration(new ParallaxItemDecoration(parallaxFactor, 17));
        // Now, you can control the scroll behavior

        sparetimerecyclerview.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return true; // Consume the touch event
            }
        });

        warning_layout.setOnClickListener(v -> {
            checkingManufacturer();
        });

        displayoverapps.setOnClickListener(v -> {
            checkAndRequestOverlayPermission();
        });


        boolean accessibilityServiceEnabled = isAccessibilityEnabled();

        puthingstonull(accessibilityServiceEnabled);

        // Initialize SharedPreferences
        SharedPreferences.Editor preference = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
        preference.putBoolean("Access", isAccessibilityEnabled());
        preference.apply();


        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        isAccess = sharedPreferences.getBoolean("Access", false);

        accessibility.setOnClickListener(v -> {
            //Set Accessibility
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });

        useraccess.setOnClickListener(v -> {
            requestUsageStatsPermission();
        });

        battery.setOnClickListener(v -> {
            // When you need to start the battery optimization settings activity
            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivity(intent);
        });

        killerswitchtoggle.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users users = snapshot.getValue(Users.class);
                    if (!users.isIspaid()) {
                        startActivity(new Intent(getApplicationContext(), SecondSubActivity.class));
                        killerswitchtoggle.setChecked(false);
                    } else {
                        if (killerswitchtoggle.isEnabled()) {
                            if (killerswitchtoggle.isChecked()) {
                                setState();
                            } else {
                                removeState();
                            }
                            return;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });


        notice.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(KillerSwitchActivity.this);
            View view = inflater.inflate(R.layout.coming_soon, null);

            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(KillerSwitchActivity.this,
                    R.style.bottomsheetTheme);

            RelativeLayout closedialog = view.findViewById(R.id.closedialog);

            closedialog.setOnClickListener(v1 -> bottomSheetDialog.dismiss());

            bottomSheetDialog.setCanceledOnTouchOutside(true);
            bottomSheetDialog.setCancelable(true);

            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.create();
            bottomSheetDialog.show();
        });

        checkSmartphone();

        killerswitchtoggle.setChecked(checked);
        interveneMethod();
    }

    private void interveneMethod(){
        RelativeLayout whichmore = findViewById(R.id.whichmore);
        whichmore.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), InterveneLaunchActivity.class)));
    }

    private void puthingstonull(boolean accessibilityServiceEnabled) {
        if (killerswitchtoggle.isChecked()) {
            Log.d("Pulled trigger", "SHoot");
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm != null
                    && pm.isIgnoringBatteryOptimizations(getPackageName())
                    && accessibilityServiceEnabled
                    && Settings.canDrawOverlays(this)
                    && UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
                loading_apps.setVisibility(View.VISIBLE);
                killerswitchtoggle.setEnabled(false);
                theText.setVisibility(View.GONE);
                getWarning_layout.setVisibility(View.GONE);
                // The permission is already granted, proceed with your logic
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                // Example: Submitting a Runnable task
                executorService.submit(() -> {
                    // Get the list of installed apps
//                    List<AppInfo> installedApps = getInstalledApps(KillerSwitchActivity.this);

                    List<UsageStats> usageStatsList = getUsageStatsList(this);
                    List<AppInfo> appList = new ArrayList<>();
                    Set<String> addedPackages = new HashSet<>();
                    for (UsageStats usageStats : usageStatsList) {
                        try {
                            PackageInfo packageInfo = getPackageManager().getPackageInfo(usageStats.getPackageName(), 0);
                            boolean isUserApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
                            boolean isUpdatedSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
                            if ((isUserApp || isUpdatedSystemApp)
                                    && !usageStats.getPackageName().equals(getPackageName())
                                    && !addedPackages.contains(usageStats.getPackageName())) {
                                String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                                Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
                                appList.add(new AppInfo(appName, usageStats.getPackageName(), appIcon));
                                addedPackages.add(usageStats.getPackageName());
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }


                    // Create a custom adapter
                    adapter = new MyAdapter(appList, KillerSwitchActivity.this);
                    // Background task code
                    Log.d("ExecutorService", "Runnable task executed on a separate thread.");

                    spareAdapter = new SpareAdapter(appList, KillerSwitchActivity.this);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TransitionManager.beginDelayedTransition(viewGroup);
                            loading_apps.setVisibility(View.GONE);
                            killerswitchtoggle.setEnabled(true);
                            listView.setVisibility(View.VISIBLE);
                            vene.setVisibility(View.VISIBLE);
                            spare.setVisibility(View.VISIBLE);
                            idhead.setVisibility(View.VISIBLE);
                            listView.setAdapter(adapter);
                            sparetimerecyclerview.setAdapter(spareAdapter);
                            // Start automatic scrolling
                            startAutoScroll();
                            adapter.notifyDataSetChanged();
                            spareAdapter.notifyDataSetChanged();
                        }
                    });
                });
            } else {
                TransitionManager.beginDelayedTransition(viewGroup);
                theText.setVisibility(View.VISIBLE);
                getWarning_layout.setVisibility(View.VISIBLE);
            }

            return;
        }

        spare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SpareTimeActivity.class));
            }
        });


        idhead.setVisibility(View.GONE);
        TransitionManager.beginDelayedTransition(viewGroup);
        listView.setVisibility(View.GONE);
        theText.setVisibility(View.GONE);
        getWarning_layout.setVisibility(View.GONE);
    }

    private void removeState() {
        RecyclerView listView = findViewById(R.id.listview);
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("Iskillerswitch", false);
        objectHashMap.put("Iscollection", false);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.updateChildren(objectHashMap);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Apps").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (databaseReference != null) {
            databaseReference.removeValue();
        }


        DatabaseReference fire = FirebaseDatabase.getInstance().getReference().child("Lethal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference lit = FirebaseDatabase.getInstance().getReference().child("Spare Time")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (fire != null) {
            fire.removeValue();
        }

        if (lit != null) {
            lit.removeValue();
        }

        // Initialize SharedPreferences
        SharedPreferences.Editor preference = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
        preference.putBoolean("Switch", false);
        preference.apply();

        TransitionManager.beginDelayedTransition(viewGroup);
        listView.setVisibility(View.GONE);
        theText.setVisibility(View.GONE);
        idhead.setVisibility(View.GONE);
        getWarning_layout.setVisibility(View.GONE);
        spare.setVisibility(View.GONE);
        vene.setVisibility(View.GONE);
        // Call the removeStringArray method from MySharedPreferences
        MySharedPreferences.removeStringArray(getApplicationContext());
        // Create an intent to identify the service to be stopped

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor pref = sharedPreferences.edit();
        pref.putBoolean("UsageAccess", false);
        pref.commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext(), DelayService.class);
                    // Stop the service
                    stopService(intent);
                }
            }
        }, 6000);

    }

    private void setState() {
        // Initialize SharedPreferences
//        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PREF", Context.MODE_PRIVATE);
//        SharedPreferences.Editor pref = sharedPreferences.edit();
//        pref.putBoolean("UsageAccess", true);
//        pref.commit();


        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("Iskillerswitch", true);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.updateChildren(objectHashMap);

        // Initialize SharedPreferences
        SharedPreferences.Editor preference = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
        preference.putBoolean("Switch", true);
        preference.apply();

        onResume();

//        if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())){
//            Intent intent = new Intent(this, DelayService.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(intent);
//            } else {
//                startService(intent);
//            }
//        }
    }

    private void checkAndRequestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));

        requestOverlayPermissionLauncher.launch(intent);
    }

    private void checkSmartphone() {
        try {
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                warning_layout.setVisibility(View.VISIBLE);
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                warning_layout.setVisibility(View.VISIBLE);
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                warning_layout.setVisibility(View.VISIBLE);
            } else if ("huawei".equalsIgnoreCase(manufacturer)) {
                warning_layout.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void checkingManufacturer() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("huawei".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                this.startActivity(intent);
            } else {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        } catch (Exception e) {
            e.getStackTrace();
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
    protected void onResume() {
        super.onResume();
//        handler.postDelayed(sliderRunnable, 3000);
        boolean back = isAccessibilityEnabled();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        if (!killerswitchtoggle.isChecked()) {
            TransitionManager.beginDelayedTransition(viewGroup);
            theText.setVisibility(View.GONE);
            getWarning_layout.setVisibility(View.GONE);
            return;
        }

        // Initialize SharedPreferences
        SharedPreferences.Editor preference = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
        preference.putBoolean("Access", isAccessibilityEnabled());
        preference.apply();


        if (pm != null
                && pm.isIgnoringBatteryOptimizations(getPackageName())
                && back
                && Settings.canDrawOverlays(this)
                && UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
            loading_apps.setVisibility(View.VISIBLE);
            theText.setVisibility(View.GONE);
            getWarning_layout.setVisibility(View.GONE);
            killerswitchtoggle.setEnabled(false);
            // The permission is already granted, proceed with your logic
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            // Example: Submitting a Runnable task
            executorService.submit(() -> {
                // Get the list of installed apps
                List<UsageStats> usageStatsList = getUsageStatsList(this);
                List<AppInfo> appList = new ArrayList<>();
                Set<String> addedPackages = new HashSet<>();
                for (UsageStats usageStats : usageStatsList) {
                    try {
                        PackageInfo packageInfo = getPackageManager().getPackageInfo(usageStats.getPackageName(), 0);
                        boolean isUserApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
                        boolean isUpdatedSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
                        if ((isUserApp || isUpdatedSystemApp) && !usageStats.getPackageName().equals(getPackageName()) && !addedPackages.contains(usageStats.getPackageName())) {
                            String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                            Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
                            appList.add(new AppInfo(appName, usageStats.getPackageName(), appIcon));
                            addedPackages.add(usageStats.getPackageName());
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }


                // Create a custom adapter
                adapter = new MyAdapter(appList, KillerSwitchActivity.this);
                // Background task code
                Log.d("ExecutorService", "Runnable task executed on a separate thread.");

                spareAdapter = new SpareAdapter(appList, KillerSwitchActivity.this);
                // Background task code
                Log.d("ExecutorService", "Runnable task executed on a separate thread.");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TransitionManager.beginDelayedTransition(viewGroup);
                        loading_apps.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        vene.setVisibility(View.VISIBLE);
                        killerswitchtoggle.setEnabled(true);
                        sparetimerecyclerview.setAdapter(spareAdapter);
                        // Start automatic scrolling
                        startAutoScroll();
                        spare.setVisibility(View.VISIBLE);
                        idhead.setVisibility(View.VISIBLE);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        spareAdapter.notifyDataSetChanged();
                    }
                });
            });
        }

        if (pm != null && pm.isIgnoringBatteryOptimizations(getPackageName())) {
            TransitionManager.beginDelayedTransition(viewGroup);
            battery.setVisibility(View.GONE);
            bt0.setVisibility(View.GONE);
        }

        else {
            TransitionManager.beginDelayedTransition(viewGroup);
            theText.setVisibility(View.VISIBLE);
            getWarning_layout.setVisibility(View.VISIBLE);
        }

        if (back) {
            TransitionManager.beginDelayedTransition(viewGroup);
            accessibility.setVisibility(View.GONE);
            bt3.setVisibility(View.GONE);
        }
        else {
            TransitionManager.beginDelayedTransition(viewGroup);
            theText.setVisibility(View.VISIBLE);
            getWarning_layout.setVisibility(View.VISIBLE);
        }

        if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
            // The permission is granted
            TransitionManager.beginDelayedTransition(viewGroup);
            useraccess.setVisibility(View.GONE);
            bt2.setVisibility(View.GONE);
        } else {
            TransitionManager.beginDelayedTransition(viewGroup);
            theText.setVisibility(View.VISIBLE);
            getWarning_layout.setVisibility(View.VISIBLE);
        }

//        if (UsageAccess){
//            if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
//                // The permission is granted
//                TransitionManager.beginDelayedTransition(viewGroup);
//                useraccess.setVisibility(View.GONE);
//                bt2.setVisibility(View.GONE);
//            }
//            else {
//                TransitionManager.beginDelayedTransition(viewGroup);
//                theText.setVisibility(View.VISIBLE);
//                getWarning_layout.setVisibility(View.VISIBLE);
//            }
//        }
    }

    private void requestUsageStatsPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

//    private List<AppInfo> getInstalledApps(Context context) {
//        List<AppInfo> installedApps = new ArrayList<>();
//
//        PackageManager packageManager = context.getPackageManager();
//        Intent intent = new Intent(Intent.ACTION_MAIN, null);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
////        for (PackageInfo packageInfo : packages) {
////            // Filter out system apps
////            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
////                    &&(packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
////                    && !packageInfo.applicationInfo.packageName.equals(context.getPackageName())) {
////                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
////                Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
////                String packagename = packageInfo.applicationInfo.packageName;
////                installedApps.add(new AppInfo(appName, packagename, appIcon));
////            }
////        }
//
//
////        for (PackageInfo packageInfo : packages) {
////            // Check if the app is an updated system app
////            boolean isUpdatedSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
////            if (isUpdatedSystemApp) {
////                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
////                Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
////                String packageName = packageInfo.applicationInfo.packageName;
////                installedApps.add(new AppInfo(appName, packageName, appIcon));
////            }
////        }
//
//
//        for (PackageInfo packageInfo : packages) {
//            // Check if the app is not the current app
//            if (!packageInfo.applicationInfo.packageName.equals(context.getPackageName())) {
//                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
//                Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
//                String packageName = packageInfo.applicationInfo.packageName;
//                installedApps.add(new AppInfo(appName, packageName, appIcon));
//            }
//        }
//
//        return installedApps;
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -5);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        return usageStatsList;
    }


    private void checkstate(TextView state, ImageView checked, String packagename, RelativeLayout maincontainer, Context context, TextView appname) {
        String modifiedString = packagename.replace(".", " ");
        String foundProblem = packagename.replace(".", "");
        DatabaseReference exel = FirebaseDatabase.getInstance().getReference().child("Intervene")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        exel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey() != null){
                    for (DataSnapshot sn : snapshot.getChildren()) {
                        if (sn.getKey().equals(modifiedString)) {
                            state.setText("Intervene");
                            appname.setTextColor(ContextCompat.getColor(context, R.color.lightTransparent));
                            maincontainer.setBackground(ContextCompat.getDrawable(context,R.drawable.borderstroke));
                            maincontainer.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Lethal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    for (DataSnapshot sn : snapshot.getChildren()) {
                        Lethal apps = sn.getValue(Lethal.class);
                        assert apps != null;
                        if (apps.getPackage().equals(foundProblem)){
                            state.setText("Spare");
                            appname.setTextColor(ContextCompat.getColor(context, R.color.lightTransparent));
                            maincontainer.setBackground(ContextCompat.getDrawable(context,R.drawable.borderstroke));
                            maincontainer.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });




        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Apps")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sn : snapshot.getChildren()) {
                    if (sn.getKey().equals(modifiedString)) {
                        state.setText("Put");
                        checked.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeapp(String packagename, TextView state, ImageView checked) {
        checked.setVisibility(View.GONE);
        // Remove dots from the string
        String modifiedString = packagename.replace(".", " ");

        MySharedPreferences.removeItem(getApplicationContext(), modifiedString);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Apps")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child(modifiedString).removeValue();

        state.setText("Removed");
    }

    private void uploadApptoServer(String packagename, TextView state, ImageView checked) {
        // Remove dots from the string
        checked.setVisibility(View.VISIBLE);
        String modifiedString = packagename.replace(".", " ");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Apps")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Packagename", packagename);
        hashMap.put("Appid", id);

        reference.child(modifiedString).setValue(hashMap);
        state.setText("Put");
    }

    private void startAutoScroll() {
        final int scrollSpeed = 9; // Adjust the speed as needed (smaller values for slower motion)

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isScrolling) {
                    int visibleItemCount = sparetimerecyclerview.getLayoutManager().getChildCount();
                    int totalItemCount = sparetimerecyclerview.getLayoutManager().getItemCount();
                    int pastVisibleItems = ((GridLayoutManager) sparetimerecyclerview.getLayoutManager()).findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        // Reached the end of the list, reset to the beginning
                        sparetimerecyclerview.scrollToPosition(0);
                    } else {
                        // Scroll to the next item
                        sparetimerecyclerview.smoothScrollBy(scrollSpeed, 0);
                    }
                }

                // Schedule the next scroll
                handler.postDelayed(this, scrollSpeed);
            }
        }, scrollSpeed);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isScrolling = true; // Stop scrolling when the activity is destroyed
    }

    private static class AppInfo {
        String name, packagename;
        Drawable icon;

        AppInfo(String name, String packagename, Drawable icon) {
            this.name = name;
            this.packagename = packagename;
            this.icon = icon;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        List<AppInfo> appn;
        Context context;

        // Constructor to initialize the adapter with data

        public MyAdapter(List<AppInfo> appn, Context context) {
            this.appn = appn;
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listsofapps_layout, parent, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AppInfo appInfo = appn.get(position);

            holder.maincontainer.setBackgroundColor(ContextCompat.getColor(context,R.color.invicibleColor));

            //holder.textView.setText(appInfo.name);
            holder.appIcon.setImageDrawable(appInfo.icon);
            String textToDisplay = appInfo.name;

            // Set the maximum length to 5 characters
            int maxLength = 7;

            // Check if the text exceeds the maximum length
            if (textToDisplay.length() > maxLength) {
                // Truncate the text and append ellipsis
                String truncatedText = textToDisplay.substring(0, maxLength) + "...";
                holder.appname.setText(truncatedText);
            } else {
                // Display the original text if it doesn't exceed the maximum length
                holder.appname.setText(textToDisplay);
            }

            checkstate(holder.state, holder.checked, appInfo.packagename, holder.maincontainer, context, holder.appname);

            holder.itemView.setOnClickListener(v -> {
                Log.d("hold",holder.state.getText().toString());
                if (holder.state.getText().equals("Remove")) {
                    uploadApptoServer(appInfo.packagename, holder.state, holder.checked);
                } else if (holder.state.getText().equals("Put")) {
                    removeapp(appInfo.packagename, holder.state, holder.checked);
                }

                if (holder.state.getText().toString().equals("Spare")) {
                    Toast toast = Toast.makeText(context, "THIS APP IS ON SPARE TIME", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                } else if (holder.state.getText().toString().equals("Intervene")) {
                    Toast toast = Toast.makeText(context, "THIS APP IS ON INTERVENED LAUNCH", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return appn.size();
        }

        // Provide a reference to the views for each data item
        class ViewHolder extends RecyclerView.ViewHolder {
            //TextView textView;
            ImageView appIcon, checked;
            TextView appname, state;
            RelativeLayout maincontainer;

            ViewHolder(View itemView) {
                super(itemView);
//                textView = itemView.findViewById(R.id.appName);
                appIcon = itemView.findViewById(R.id.appIcon);
                maincontainer = itemView.findViewById(R.id.maincontainer);
                checked = itemView.findViewById(R.id.checked);
                appname = itemView.findViewById(R.id.appname);
                state = itemView.findViewById(R.id.state);
            }
        }
    }

    private class SpareAdapter extends RecyclerView.Adapter<SpareAdapter.ViewHolder> {
        List<AppInfo> appn;
        Context context;

        public SpareAdapter(List<KillerSwitchActivity.AppInfo> appn, Context context) {
            this.appn = appn;
            this.context = context;
        }


        @NonNull
        @Override
        public SpareAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.spare_apps, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SpareAdapter.ViewHolder holder, int position) {
            AppInfo appInfo = appn.get(position);
            holder.spareappIcon.setImageDrawable(appInfo.icon);
        }

        @Override
        public int getItemCount() {
            return appn.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView spareappIcon;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                spareappIcon = itemView.findViewById(R.id.spareappIcon);
            }
        }
    }

    private class CustomGridLayoutManager extends GridLayoutManager {
        private boolean isScrollEnabled = true;

        public CustomGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount, GridLayoutManager.HORIZONTAL, false);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled && super.canScrollVertically();
        }

        @Override
        public boolean canScrollHorizontally() {
            return isScrollEnabled && super.canScrollHorizontally();
        }
    }

}