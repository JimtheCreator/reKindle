package front_page;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import adapters.WheelAdapter;
import devydev.mirror.net.R;
import model.Apps;
import model.ItemDecorator;
import model.Lethal;
import service.DelayService;
import utils.ExcludePreference;
import utils.MySharedPreferences;
import utils.UsageStatsHelper;

public class InterveneLaunchActivity extends AppCompatActivity {

    RecyclerView venerecycler;
    MyAdapter adapter;
    ViewGroup viewGroup;
    LinearLayout content;
    Handler handler = new Handler();

    List<String> blockedApps = new ArrayList<>();
    List<String> lethalApps = new ArrayList<>();
    ProgressBar loader;

    @Override
    protected void onStart() {
        super.onStart();

        if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
            // The permission is granted
            Intent intent = new Intent(this, DelayService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervene_launch);
        content = findViewById(R.id.content);
        loader = findViewById(R.id.loader);


        findViewById(R.id.pagekapush).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewGroup = findViewById(R.id.in);
        venerecycler = findViewById(R.id.venerecycler);
        venerecycler.setHasFixedSize(true);
        venerecycler.setLayoutManager(new GridLayoutManager(this, 3));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        venerecycler.addItemDecoration(new ItemDecorator(3, spacingInPixels, true));

        runExService();
    }

    private void runExService() {
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
            adapter = new MyAdapter(appList, InterveneLaunchActivity.this);
            // Background task code
            Log.d("ExecutorService", "Runnable task executed on a separate thread.");


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(viewGroup);
                    loader.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);

                    venerecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                    List<String> items = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        items.add("Item " + i);
                    }

//                    ViewPager2 recyclerView = findViewById(R.id.viewPage);
////                    WheelAdapter adapter = new WheelAdapter(items);
//                    recyclerView.setAdapter(adapter);
//                    recyclerView.setClipToPadding(false);
//                    recyclerView.setClipChildren(false);
//                    recyclerView.setOffscreenPageLimit(5);
//                    recyclerView.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
//
//                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
//                    compositePageTransformer.addTransformer(new MarginPageTransformer(8));
//                    compositePageTransformer.addTransformer((page, position) -> {
//                        float v = 1 - Math.abs(position);
//                        page.setScaleY(0.8f + v * 0.2f);
//
//                    });
//
//                    recyclerView.setPageTransformer(compositePageTransformer);
                }
            });
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private List<UsageStats> getUsageStatsList(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -5);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        return usageStatsList;
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

    private void checkstate(TextView state, ImageView checked, String packagename, RelativeLayout maincontainer,Context context, TextView appname) {
        String modifiedString = packagename.replace(".", " ");
        String foundProblem = packagename.replace(".", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Intervene")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey() != null){
                    for (DataSnapshot sn : snapshot.getChildren()) {
                        if (sn.getKey().equals(modifiedString)) {
                            state.setText("Put");
                            checked.setVisibility(View.VISIBLE);
                        }
                    }
                }else {
                    if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())){
                        Intent intent = new Intent(InterveneLaunchActivity.this, DelayService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        } else {
                            startService(intent);
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
                            maincontainer.setVisibility(View.VISIBLE);
                            appname.setTextColor(ContextCompat.getColor(context, R.color.lightTransparent));
                            maincontainer.setBackground(ContextCompat.getDrawable(context,R.drawable.borderstroke));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });


        DatabaseReference userbase = FirebaseDatabase.getInstance().getReference().child("Apps")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userbase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blockedApps.clear();
                if (snapshot != null) {
                    for (DataSnapshot sn : snapshot.getChildren()) {
                        Apps apps = sn.getValue(Apps.class);
                        if (apps.getPackagename().equals(packagename)){
                            state.setText("Blocked");
                            maincontainer.setVisibility(View.VISIBLE);
                            appname.setTextColor(ContextCompat.getColor(context, R.color.lightTransparent));
                            maincontainer.setBackground(ContextCompat.getDrawable(context,R.drawable.borderstroke));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }

    private void removeapp(String packagename, TextView state, ImageView checked) {
        checked.setVisibility(View.GONE);
        // Remove dots from the string
        String modifiedString = packagename.replace(".", " ");

        MySharedPreferences.removeItem(getApplicationContext(), modifiedString);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Intervene")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child(modifiedString).removeValue();

        state.setText("Removed");
    }

    private void uploadApptoServer(String packagename, TextView state, ImageView checked) {
        // Remove dots from the string
        checked.setVisibility(View.VISIBLE);
        String modifiedString = packagename.replace(".", " ");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Intervene")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Packagename", packagename);
        hashMap.put("Appid", id);

        reference.child(modifiedString).setValue(hashMap);
        state.setText("Put");
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
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listsofapps_layout, parent, false);
            return new MyAdapter.ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
            AppInfo appInfo = appn.get(position);

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


            checkstate(holder.state, holder.checked, appInfo.packagename, holder.maincontainer,context, holder.appname);


            String cut = appInfo.packagename.replace(".", "");


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
                } else if (holder.state.getText().toString().equals("Blocked")) {
                    Toast toast = Toast.makeText(context, "THIS APP IS CURRENTLY BLOCKED", Toast.LENGTH_SHORT);
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
                checked = itemView.findViewById(R.id.checked);
                appname = itemView.findViewById(R.id.appname);
                maincontainer = itemView.findViewById(R.id.maincontainer);
                state = itemView.findViewById(R.id.state);
            }
        }
    }

}