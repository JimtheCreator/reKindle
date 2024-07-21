package front_page;

import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import devydev.mirror.net.R;
import model.Lethal;
import space.SpacebetweenItems;
import utils.ExcludePreference;
import utils.MySharedPreferences;

public class AppSparedActivity extends AppCompatActivity {

    TextView time;
    RelativeLayout delete, color_back;
    RecyclerView itemlist;

    String hours, minutes, items, spareid;
    List<String> lethalApps = new ArrayList<>();
    ArrayList<String> receivedList;
    TimeAdapter TimeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_app_spared);

        Intent intent = getIntent();

        minutes = intent.getStringExtra("minutes");
        hours = intent.getStringExtra("hours");
        spareid = intent.getStringExtra("spareid");
        String colorString = intent.getStringExtra("color");
        receivedList = getIntent().getStringArrayListExtra("key");


        delete = findViewById(R.id.delete);


        // Get the list of installed apps
        List<UsageStats> usageStatsList = getUsageStatsList(this);
        List<AppInfo> appInfoList = new ArrayList<>();
        Set<String> Add = new HashSet<>();

        for (UsageStats usageStats : usageStatsList) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(usageStats.getPackageName(), 0);
                // Filter out system apps
//                String dotremoved = usageStats.getPackageName().replace(".","");

                boolean isUserApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
                boolean isUpdatedSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
                if ((isUserApp || isUpdatedSystemApp)
                        && !usageStats.getPackageName().equals(getPackageName())
                        && !Add.contains(usageStats.getPackageName())
                        && receivedList.contains(packageInfo.applicationInfo.packageName)) {
                    String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                    Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
                    appInfoList.add(new AppInfo(appName, usageStats.getPackageName(), appIcon));
                    Add.add(usageStats.getPackageName());

                    TimeAdapter = new TimeAdapter(appInfoList, AppSparedActivity.this);
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        itemlist = findViewById(R.id.itemlist);
        time = findViewById(R.id.time);
        color_back = findViewById(R.id.color_back);

        Log.d("POST", "" + receivedList);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Convert the string back to an integer
        int colorInt = Integer.parseInt(colorString);

        ifmethod(colorInt);


        // The permission is already granted, proceed with your logic
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // Example: Submitting a Runnable task
        executorService.submit(() -> {
            runOnUiThread(() -> {
                SpacebetweenItems spacebetweenItems = new SpacebetweenItems(30);
                itemlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                if (receivedList.size() > 1) {
                    itemlist.addItemDecoration(spacebetweenItems);
                }

                itemlist.setAdapter(TimeAdapter);
                TimeAdapter.notifyDataSetChanged();
            });
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdialog();
            }
        });
    }

    private void openAdialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        AlertDialog.Builder builder = new AlertDialog.Builder(AppSparedActivity.this);
        View view = LayoutInflater.from(AppSparedActivity.this).inflate(R.layout.warn,
                viewGroup, false);

        Button deny = view.findViewById(R.id.cancel_action);
        Button delete_action = view.findViewById(R.id.delete_action);
        TextView textDisclaimer = view.findViewById(R.id.textDisclaimer);

        builder.setCancelable(true);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();


        deny.setOnClickListener(view1 -> {
            alertDialog.dismiss();
        });

        delete_action.setOnClickListener(view12 -> {
            alertDialog.dismiss();
            deleteapps();
        });
    }

    private void deleteapps() {
        ProgressDialog pd = new ProgressDialog(AppSparedActivity.this);
        pd.show();

        pd.setCancelable(false);
        pd.setContentView(R.layout.delete_progress);

        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        for (String id : receivedList) {
            String mode = id.replace(".", "");
            FirebaseDatabase.getInstance().getReference().child("Spare Time")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(spareid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseDatabase.getInstance().getReference().child("Lethal")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mode).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    pd.dismiss();
                                                    ExcludePreference.removeItem(getApplicationContext(), mode);
                                                    finish();
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }

    }

    private void ifmethod(int colorInt) {
        if (minutes != null && !minutes.isEmpty()) {
            int x = Integer.parseInt(minutes);
            int y = Integer.parseInt(hours);
            // Continue with your logic
            if (hours.equals("0")) {
                if (x <= 9) {
                    time.setText(hours + "0:0" + minutes + "h");
                } else {
                    time.setText(hours + "0:" + minutes + "h");
                }
            } else if (y <= 9) {
                if (x <= 9) {
                    time.setText("0" + hours + ":0" + minutes + "h");
                } else {
                    time.setText("0" + hours + ":" + minutes + "h");
                }
            } else {
                if (x <= 9) {
                    time.setText(hours + ":0" + minutes + "h");
                } else {
                    time.setText(hours + ":" + minutes + "h");
                }
            }

            color_back.setBackgroundColor(colorInt);
        } else {
            // Handle the case where s is null or empty
        }
    }


    private List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -5);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        return usageStatsList;
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

    private static class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {
        List<AppInfo> appn;
        Context context;

        public TimeAdapter(List<AppInfo> appn, Context context) {
            this.appn = appn;
            this.context = context;
        }


        @NonNull
        @Override
        public TimeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.time_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TimeAdapter.ViewHolder holder, int position) {
            AppInfo appInfo = appn.get(position);
            holder.logo.setImageDrawable(appInfo.icon);
            holder.appname.setText(appInfo.name);
        }

        @Override
        public int getItemCount() {
            return appn.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView logo;
            TextView appname;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                logo = itemView.findViewById(R.id.logo);
                appname = itemView.findViewById(R.id.appName);
            }
        }
    }

}