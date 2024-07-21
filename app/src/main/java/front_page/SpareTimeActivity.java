package front_page;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import adapters.CollectionList;
import decor.ParallaxItemDecoration;
import devydev.mirror.net.R;
import model.ItemDecorator;
import model.SpareTime;
import model.Users;
import space.SpacebetweenItems;
import utils.ExcludePreference;
import utils.FortheApps;
import utils.MySharedPreferences;

public class SpareTimeActivity extends AppCompatActivity {

    BottomSheetBehavior bottomSheetBehavior;
    // Retrieve ArrayList from SharedPreferences
    EditText collectionname;

    LinearLayout linearholder, drawerofapps;
    CollectionList collectionList;
    ProgressBar progresscircle;
    NestedScrollView nnest;
    boolean isVisible = false;
    ImageView arrowup, arrowdown;
    AppCollectionAdapter appCollectionAdapter;
    RecyclerView sparetimerecyclerview, collectionlist, selectappslist;
    SpareAdapter spareAdapter;
//    int hours = 0;
//    int mins = 30;
    TextView donetext;
    RelativeLayout clicked_collection, minimize, done_setup, progress, shut;
    Handler handler;

    Handler getHandler = new Handler();
    ViewGroup collection_set;
    List<SpareTime> spareTimeList = new ArrayList<>();
    private boolean isScrolling = false;

    private static List<String> removeDots(List<String> list) {
        List<String> resultList = new ArrayList<>();

        for (String item : list) {
            // Replace dots with an empty string
            resultList.add(item.replace(".", ""));
        }

        return resultList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_spare_time);

        findViewById(R.id.closedrawer).setOnClickListener(v -> finish());

        collectionList = new CollectionList(getApplicationContext(), spareTimeList);
        shut = findViewById(R.id.shut);
//        easy = findViewById(R.id.easy);
//        lethal = findViewById(R.id.lethal);
        arrowup = findViewById(R.id.arrowup);
        arrowdown = findViewById(R.id.arrowdown);
        nnest = findViewById(R.id.nnest);
        progress = findViewById(R.id.progress);
        done_setup = findViewById(R.id.done_setup);
        collectionname = findViewById(R.id.collectionname);
        clicked_collection = findViewById(R.id.clicked_collection);
        progresscircle = findViewById(R.id.progresscircle);
        collectionlist = findViewById(R.id.collectionlist);
        collection_set = findViewById(R.id.collection_set);
        drawerofapps = findViewById(R.id.drawerofapps);
        linearholder = findViewById(R.id.linearholder);
        donetext = findViewById(R.id.donetext);
        minimize = findViewById(R.id.minimize);
        selectappslist = findViewById(R.id.selectappslist);

        SpacebetweenItems spacebetweenItems = new SpacebetweenItems(25);

        selectappslist.setLayoutManager(new LinearLayoutManager(this));
        selectappslist.addItemDecoration(spacebetweenItems);


        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.modify);

        collectionlist.setLayoutManager(new GridLayoutManager(this, 2));
        collectionlist.addItemDecoration(new ItemDecorator(2, spacingInPixels, true));

        // Get the list of installed apps
        List<UsageStats> usageStatsList = getUsageStatsList(this);
        List<AppInfo> installedApps = new ArrayList<>();
        Set<String> addedPackages = new HashSet<>();


        List<AppInfo> appInfoList = new ArrayList<>();
        List<String> retrievedArrayList = MySharedPreferences.getStringArray(getApplicationContext());
        List<String> alreadyListed = ExcludePreference.getStringArray(getApplicationContext());
        Set<String> Add = new HashSet<>();

        for (UsageStats usageStats : usageStatsList) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(usageStats.getPackageName(), 0);
                // Filter out system apps
                boolean isUserApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
                boolean isUpdatedSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;

                if ((isUserApp || isUpdatedSystemApp)
                        && !usageStats.getPackageName().equals(getPackageName())
                        && !addedPackages.contains(usageStats.getPackageName())) {


                    String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                    Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
                    installedApps.add(new AppInfo(appName, usageStats.getPackageName(), appIcon));
                    addedPackages.add(usageStats.getPackageName());
                }

            }

            catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }


        for (UsageStats usageStats : usageStatsList) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(usageStats.getPackageName(), 0);
                // Filter out system apps
                String dotremoved = usageStats.getPackageName().replace(".","");
                boolean isUserApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
                boolean isUpdatedSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
                if ((isUserApp || isUpdatedSystemApp)
                        && !usageStats.getPackageName().equals(getPackageName())
                        && !Add.contains(usageStats.getPackageName())
                        && !retrievedArrayList.contains(packageInfo.applicationInfo.packageName)
                        && !alreadyListed.contains(dotremoved)) {
                    String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                    Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
                    appInfoList.add(new AppInfo(appName, usageStats.getPackageName(), appIcon));
                    Add.add(usageStats.getPackageName());
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        spareAdapter = new SpareAdapter(installedApps, SpareTimeActivity.this);
        appCollectionAdapter = new AppCollectionAdapter(appInfoList, SpareTimeActivity.this);
        int parallaxFactor = getResources().getDimensionPixelOffset(R.dimen.parallax_factor);
        sparetimerecyclerview = findViewById(R.id.madesimple);

        sparetimerecyclerview.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        sparetimerecyclerview.addItemDecoration(new ParallaxItemDecoration(parallaxFactor, 17));
        sparetimerecyclerview.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return true; // Consume the touch event
            }
        });


        bottomSheetBehavior = BottomSheetBehavior.from(collection_set);

        // The permission is already granted, proceed with your logic
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // Example: Submitting a Runnable task
        executorService.submit(() -> {
            runOnUiThread(() -> {
                inflateData();
                if (BottomSheetBehavior.STATE_EXPANDED == bottomSheetBehavior.getState()) {
                    selectappslist.setAdapter(appCollectionAdapter);
                    appCollectionAdapter.notifyDataSetChanged();
                }
                sparetimerecyclerview.setAdapter(spareAdapter);
                // Start automatic scrolling
                startAutoScroll();
                spareAdapter.notifyDataSetChanged();


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        if (users != null) {
                            if (users.isIscollection()) {
                                linearholder.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                collectionlist.setAdapter(collectionList);
            });
        });

        forThesheet();

//        nnest.setOnClickListener(v->{
//            closeKeyboard();
//        });
    }

    private void forThesheet() {
        shut.setOnClickListener(v -> {
            collectionname.setText("");
            closeKeyboard();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            boolean visible;

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    FortheApps.removeStringArray(getApplicationContext());
                    bottomSheetBehavior.setDraggable(false);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    // Example: Submitting a Runnable task
                    executorService.submit(() -> {
                        runOnUiThread(() -> {
                            Log.d("Apps", "List of apps: " + MySharedPreferences.getStringArray(getApplicationContext()));
                            progress.setVisibility(View.GONE);
                            nnest.setVisibility(View.VISIBLE);
                            done_setup.setVisibility(View.VISIBLE);
                            selectappslist.setAdapter(appCollectionAdapter);
                            appCollectionAdapter.notifyDataSetChanged();
                        });
                    });
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        NumberPicker hoursPicker = findViewById(R.id.hours_picker);
        NumberPicker minutesPicker = findViewById(R.id.minutes_picker);

        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(23);
        minutesPicker.setMaxValue(59);

        minutesPicker.setMinValue(30);

        getHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hoursPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                    if (newVal == 0) {
                        minutesPicker.setMinValue(30);
                    } else {
                        minutesPicker.setMinValue(0);
                    }

                    getHandler.postDelayed(this, 1);
                });

            }
        }, 1);



        // Set the initial minimum value for minutePicker based on the initial value of hourPicker
//        if (hoursPicker.getValue() == 0) {
//            minutesPicker.setMinValue(30);
//        } else {
//            minutesPicker.setMinValue(0);
//        }
//
//        // Adjust the minute picker when the hour picker value changes
//        hoursPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
//            if (newVal == 0) {
//                minutesPicker.setMinValue(30);
//            } else {
//                minutesPicker.setMinValue(0);
//            }
//        });


//        minutesPicker.setMinValue(30);
//        minutesPicker.setMaxValue(59);

        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVisible) {
                    isVisible = true;
                    arrowup.setVisibility(View.GONE);
                    arrowdown.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(collection_set);
                    selectappslist.setVisibility(View.GONE);
                } else if (isVisible) {
                    isVisible = false;
                    arrowup.setVisibility(View.VISIBLE);
                    arrowdown.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(collection_set);
                    selectappslist.setVisibility(View.VISIBLE);
                }
            }
        });

        clicked_collection.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        hoursPicker.setOnValueChangedListener((num, i, i1) -> {
//            hours = i1;
        });

        minutesPicker.setOnValueChangedListener((num, i, i1) -> {
//            mins = i1;
        });

//        lethal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                state = "Lethal";
//                lethal.setBackground(ContextCompat.getDrawable(SpareTimeActivity.this, R.drawable.stateselected));
//                easy.setBackground(ContextCompat.getDrawable(SpareTimeActivity.this, R.drawable.fade_white));
//            }
//        });
//
//        easy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                state = "Easy";
//                easy.setBackground(ContextCompat.getDrawable(SpareTimeActivity.this, R.drawable.stateselected));
//                lethal.setBackground(ContextCompat.getDrawable(SpareTimeActivity.this, R.drawable.fade_white));
//            }
//        });


        done_setup.setOnClickListener(v -> {
            String string = collectionname.getText().toString();

            if (string.isEmpty()) {
                Toast toast = Toast.makeText(SpareTimeActivity.this, "Give your collection a name", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

//            if (state.isEmpty()) {
//                Toast toast = Toast.makeText(SpareTimeActivity.this, "Select level", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//                return;
//            }

//            if (mins <= 5 && hours < 1) {
//                Toast toast = Toast.makeText(SpareTimeActivity.this, "Time set is low", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//                return;
//            }


            List<String> stringArray = FortheApps.getStringArray(getApplicationContext());

            if (stringArray.isEmpty()) {
                Toast toast = Toast.makeText(SpareTimeActivity.this, "Select at least one app", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }


            uploadAppdata(string, stringArray, hoursPicker.getValue(), minutesPicker.getValue());
        });
    }

    private void inflateData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Spare Time")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                spareTimeList.clear();
                for (DataSnapshot sn : snapshot.getChildren()) {
                    SpareTime sp = sn.getValue(SpareTime.class);
                    if (sp != null) {
                        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                        exec.scheduleAtFixedRate(() -> {
                            // Your task here
                            spareTimeList.add(sp);
                        }, 0, 1, TimeUnit.SECONDS);
                    }
                }

                Collections.reverse(spareTimeList);
                collectionList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadAppdata(String string, List<String> nu, int value, int minutesPickerValue) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Spare Time")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String pushID = reference.push().getKey();

        done_setup.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.all_rounded));
        donetext.setVisibility(View.GONE);
        progresscircle.setVisibility(View.VISIBLE);

        StringBuilder resultStringBuilder = new StringBuilder();
        for (int i = 0; i < nu.size(); i++) {
            resultStringBuilder.append(nu.get(i));
            if (i < nu.size() - 1) {
                resultStringBuilder.append(", ");
            }
        }

        String resultString = resultStringBuilder.toString();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Package", resultString);
        hashMap.put("Name", string);
        hashMap.put("Level", "Lethal");
        hashMap.put("Pushid", pushID);
        hashMap.put("Hours", String.valueOf(value));
        hashMap.put("Minutes", String.valueOf(minutesPickerValue));

        assert pushID != null;

        reference.child(pushID).setValue(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                closeKeyboard();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                FortheApps.removeStringArray(SpareTimeActivity.this);

                HashMap<String, Object> objectHashMap = new HashMap<>();
                objectHashMap.put("Iscollection", true);

                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(objectHashMap);

                // Remove dots from each string in the array
                List<String> modifiedList = removeDots(nu);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Calendar calendar1 = Calendar.getInstance();
                final String todaysdate = dateFormat.format(calendar1.getTime());
                try {
                    Date d = dateFormat.parse(todaysdate);
                    long milliseconds = d.getTime();

                    // Display the modified list
                    for (String item : modifiedList) {
                        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
                        stringObjectHashMap.put("Islimited", false);
                        stringObjectHashMap.put("Package", item);
                        stringObjectHashMap.put("Lastchecked", milliseconds);
                        stringObjectHashMap.put("Hours", String.valueOf(value));
                        stringObjectHashMap.put("Minutes", String.valueOf(minutesPickerValue));

                        FirebaseDatabase.getInstance().getReference().child("Lethal")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(item)
                                .setValue(stringObjectHashMap);
                    }
                } catch (Exception e) {
                    Log.d("Logged", e.getLocalizedMessage());
                }


                done_setup.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_background));
                donetext.setVisibility(View.VISIBLE);
                progresscircle.setVisibility(View.GONE);
                collectionname.setText("");
            }
        }).addOnFailureListener(e -> {
            Toast toast = Toast.makeText(SpareTimeActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });

//        if (state.equals("Lethal")) {
//            reference.child(pushID).setValue(hashMap).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    closeKeyboard();
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    FortheApps.removeStringArray(SpareTimeActivity.this);
//
//                    HashMap<String, Object> objectHashMap = new HashMap<>();
//                    objectHashMap.put("Iscollection", true);
//
//                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                            .updateChildren(objectHashMap);
//
//                    // Remove dots from each string in the array
//                    List<String> modifiedList = removeDots(nu);
//
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//                    Calendar calendar1 = Calendar.getInstance();
//                    final String todaysdate = dateFormat.format(calendar1.getTime());
//                    try {
//                        Date d = dateFormat.parse(todaysdate);
//                        long milliseconds = d.getTime();
//
//                        // Display the modified list
//                        for (String item : modifiedList) {
//                            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
//                            stringObjectHashMap.put("Islimited", false);
//                            stringObjectHashMap.put("Package", item);
//                            stringObjectHashMap.put("Lastchecked", milliseconds);
//                            stringObjectHashMap.put("Hours", String.valueOf(hours));
//                            stringObjectHashMap.put("Minutes", String.valueOf(mins));
//
//                            FirebaseDatabase.getInstance().getReference().child("Lethal")
//                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                    .child(item)
//                                    .setValue(stringObjectHashMap);
//                        }
//                    } catch (Exception e) {
//                        Log.d("Logged", e.getLocalizedMessage());
//                    }
//
//
//                    done_setup.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_background));
//                    donetext.setVisibility(View.VISIBLE);
//                    progresscircle.setVisibility(View.GONE);
//                    collectionname.setText("");
//                }
//            }).addOnFailureListener(e -> {
//                Toast toast = Toast.makeText(SpareTimeActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//            });
//        }
//
//        else {
//            reference.child(pushID).setValue(hashMap).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    closeKeyboard();
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    FortheApps.removeStringArray(SpareTimeActivity.this);
//
//                    HashMap<String, Object> objectHashMap = new HashMap<>();
//                    objectHashMap.put("Iscollection", true);
//
//                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                            .updateChildren(objectHashMap);
//
//                    done_setup.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_background));
//                    donetext.setVisibility(View.VISIBLE);
//                    progresscircle.setVisibility(View.GONE);
//                    collectionname.setText("");
//                }
//            }).addOnFailureListener(e -> {
//                Toast toast = Toast.makeText(SpareTimeActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//            });
//        }

    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    @Override
    public void onBackPressed() {
        if (BottomSheetBehavior.STATE_EXPANDED == bottomSheetBehavior.getState()) {
            getHandler.removeCallbacksAndMessages(null);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            handler.removeCallbacksAndMessages(null);
            getHandler.removeCallbacksAndMessages(null);
            super.onBackPressed();
        }
    }

//    private List<AppInfo> getInstalledApps(Context context) {
//        List<AppInfo> installedApps = new ArrayList<>();
//
//        PackageManager packageManager = context.getPackageManager();
//        Intent intent = new Intent(Intent.ACTION_MAIN, null);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
//        for (PackageInfo packageInfo : packages) {
//            // Filter out system apps
//            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
//                    && !packageInfo.applicationInfo.packageName.equals(context.getPackageName())) {
//                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
//                Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
//                String packagename = packageInfo.applicationInfo.packageName;
//                installedApps.add(new AppInfo(appName, packagename, appIcon));
//            }
//        }
//
//        return installedApps;
//    }


    private List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -5);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        return usageStatsList;
    }


    private List<AppInfo> simplified(Context context) {
        List<AppInfo> installedApps = new ArrayList<>();

        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<PackageInfo> packages = packageManager.getInstalledPackages(0);

        List<String> retrievedArrayList = MySharedPreferences.getStringArray(getApplicationContext());

        List<String> alreadyListed = ExcludePreference.getStringArray(getApplicationContext());


        for (PackageInfo packageInfo : packages) {
            // Filter out system apps
            String dotremoved = packageInfo.applicationInfo.packageName.replace(".","");

            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                    && !packageInfo.applicationInfo.packageName.equals(context.getPackageName())
                    && !retrievedArrayList.contains(packageInfo.applicationInfo.packageName)
                    && !alreadyListed.contains(dotremoved)) {
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
                String packagename = packageInfo.applicationInfo.packageName;
                installedApps.add(new AppInfo(appName, packagename, appIcon));
            }
        }

        return installedApps;
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

    private static class SpareAdapter extends RecyclerView.Adapter<SpareAdapter.ViewHolder> {
        List<AppInfo> appn;
        Context context;

        public SpareAdapter(List<AppInfo> appn, Context context) {
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

    private static class AppCollectionAdapter extends RecyclerView.Adapter<AppCollectionAdapter.ViewHolder> {
        List<AppInfo> appn;
        Context context;

        public AppCollectionAdapter(List<AppInfo> appn, Context context) {
            this.appn = appn;
            this.context = context;
        }


        @NonNull
        @Override
        public AppCollectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.vertical_applistsing, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppCollectionAdapter.ViewHolder holder, int position) {
            AppInfo appInfo = appn.get(position);
            holder.logo.setImageDrawable(appInfo.icon);
            holder.appname.setText(appInfo.name);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> stringArrayList = FortheApps.getStringArray(context);
                    if (holder.noError.getText().toString().equals("HOME")) {
                        holder.noError.setText("AWAY");
                        if (!FortheApps.getStringArray(context).contains(appInfo.packagename)) {
                            FortheApps.addItem(context, appInfo.packagename);
                        }

                        holder.check.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.green_check_circle_24));
                    } else if (holder.noError.getText().toString().equals("AWAY")) {
                        holder.noError.setText("HOME");
                        FortheApps.removeItem(context, appInfo.packagename);
                        holder.check.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_radio_button_unchecked_24));
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return appn.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView logo, check;
            TextView appname, noError;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                logo = itemView.findViewById(R.id.logo);
                check = itemView.findViewById(R.id.check);
                noError = itemView.findViewById(R.id.noError);
                appname = itemView.findViewById(R.id.appName);
            }
        }
    }



}