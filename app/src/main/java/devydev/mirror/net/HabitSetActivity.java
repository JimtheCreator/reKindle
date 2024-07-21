package devydev.mirror.net;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import adapters.ItemsInflatedAdapter;
import front_page.HomeActivity;
import model.Productstate;

public class HabitSetActivity extends AppCompatActivity {
    LinearLayout pickedLayout;
    RecyclerView itemHolder;
    ProgressDialog pd;
    String state;


    TextView done_txt;
    RadioButton radio1, radio2, radio4, radio6, radioproject, radiolanguage;

//    RadioButton radio5;

    RelativeLayout tap1, tap2, tap4, tap6, learn, project, time_layout;
    //    RelativeLayout tap5;
    List<String> itemsClassList;
    ItemsInflatedAdapter itemsInflatedAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        state = pref.getString("State", "none");

        if (state.equals("Logged_In")) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_set);



        SharedPreferences pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        state = pref.getString("State", "none");

        project = findViewById(R.id.project);
        learn = findViewById(R.id.learn);
        radioproject = findViewById(R.id.radioproject);
        radiolanguage = findViewById(R.id.radiolanguage);
        done_txt = findViewById(R.id.done_txt);
        radio1 = findViewById(R.id.radio1);
        radio2 = findViewById(R.id.radio2);
//        radio3 = findViewById(R.id.radio3);
        radio4 = findViewById(R.id.radio4);
//        radio5 = findViewById(R.id.radio5);
//        time_layout = findViewById(R.id.time_layout);
        radio6 = findViewById(R.id.radio6);
        findViewById(R.id.custom_clicked).setOnClickListener(v -> {
            openDialog();
        });
        tap1 = findViewById(R.id.tap1);
        tap2 = findViewById(R.id.tap2);
//        tap3 = findViewById(R.id.tap3);
//        actualtime = findViewById(R.id.actualtime);
//        set_time = findViewById(R.id.set_time);
        tap4 = findViewById(R.id.tap4);
//        tap5 = findViewById(R.id.tap5);
        tap6 = findViewById(R.id.tap6);

        itemHolder = findViewById(R.id.itemHolder);
        pickedLayout = findViewById(R.id.pickedLayout);

        itemsClassList = new ArrayList<>();
        itemsInflatedAdapter = new ItemsInflatedAdapter(itemsClassList, getApplicationContext());
        itemHolder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        itemHolder.setAdapter(itemsInflatedAdapter);


        justclicked();

        done_txt.setOnClickListener(v -> {
            if (itemsClassList.size() < 3) {
                Toast.makeText(this, "Kindly select more items", Toast.LENGTH_SHORT).show();
                return;
            }

//            if (radio3.isChecked()) {
//                String x = actualtime.getText().toString();
//
//                if (x.equals("Set time")) {
//                    Toast.makeText(this, "Kindly set time", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }

            pd = new ProgressDialog(HabitSetActivity.this);
            pd.show();

            pd.setCancelable(false);
            pd.setContentView(R.layout.progress_bar);

            pd.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                pd.dismiss();
//                openNextDialog(actualtime.getText().toString());
                openNextDialog();
            }, 7500);
            //TOBECONTINUED

        });

        if (state.equals("Fresh")) {

        }
    }

    private void openNextDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.DialogStyle);

        View view = LayoutInflater.from(this).inflate(R.layout.onestepmorelayout,
                (ViewGroup) findViewById(R.id.ones));

        NumberPicker numberPicker = view.findViewById(R.id.numberPicker);
        RelativeLayout btnpressed = view.findViewById(R.id.btnpressed);
        TextView stateeee = view.findViewById(R.id.stateeee);

        stateeee.setText("Today");

        Productstate.iniatializeLang();
        numberPicker.setMaxValue(Productstate.getLanguageArrayList().size() - 1);
        numberPicker.setMinValue(0);
        numberPicker.setDisplayedValues(Productstate.state());

        numberPicker.setOnValueChangedListener((num, i, i1) -> {
            stateeee.setText(Productstate.getLanguageArrayList().get(i1).getName());
//            if (Productstate.getLanguageArrayList().get(i1).getName().equals("Not Launched")) {
//                justTxt.setVisibility(View.VISIBLE);
//            }
//
//            if (Productstate.getLanguageArrayList().get(i1).getName().equals("Launched")) {
//                justTxt.setVisibility(View.GONE);
//            }
        });

        bottomSheetDialog.setCancelable(false);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();


        btnpressed.setOnClickListener(l -> {
            bottomSheetDialog.dismiss();
            ProgressDialog pdg = new ProgressDialog(HabitSetActivity.this);
            pdg.show();

            pdg.setCancelable(false);
            pdg.setContentView(R.layout.second_progress);

            pdg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


            try {
                registerOfficially(stateeee.getText().toString(), pdg);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private void registerOfficially(String toString, ProgressDialog pd) throws ParseException {
        //Tomorrow date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        long tomorrow = calendar.getTimeInMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String tomorrowAsString = dateFormat.format(tomorrow);
        Date date = dateFormat.parse(tomorrowAsString);
        long time = date.getTime();

        //Today date
        Calendar calendar1 = Calendar.getInstance();
        final String todaysdate = dateFormat.format(calendar1.getTime());
        Date d = dateFormat.parse(todaysdate);
        long milliseconds = d.getTime();


        if (toString.equals("Today")) {
            for (String st : itemsClassList) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Counter", "0");
                hashMap.put("Item", st);
                hashMap.put("Userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("Checked", false);
                hashMap.put("Timestamp", "0");
                hashMap.put("Lastchecked", "0");
                hashMap.put("Isjournal", false);
                hashMap.put("Isbook", false);

                databaseReference.child(st).setValue(hashMap);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                HashMap<String, Object> objectHashMap = new HashMap<>();
                objectHashMap.put("Tostart", toString);
                objectHashMap.put("Ishabit", true);
                objectHashMap.put("Whencreated", todaysdate);

                objectHashMap.put("Timemillis", "" + milliseconds);

                reference.updateChildren(objectHashMap);



            }

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                pd.dismiss();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();

            }, 7500);
            return;
        }

        if (toString.equals("Tomorrow")) {
            for (String st : itemsClassList) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Counter", "0");
                hashMap.put("Item", st);
                hashMap.put("Userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("Checked", false);
                hashMap.put("Timestamp", "0");
                hashMap.put("Lastchecked", "0");
                hashMap.put("Isjournal", false);
                hashMap.put("Isbook", false);

                databaseReference.child(st).setValue(hashMap);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                HashMap<String, Object> objectHashMap = new HashMap<>();
                objectHashMap.put("Tostart", toString);
                objectHashMap.put("Ishabit", true);
                objectHashMap.put("Whencreated", tomorrowAsString);

                objectHashMap.put("Timemillis", "" + time);

                reference.updateChildren(objectHashMap);
            }


            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                pd.dismiss();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                SharedPreferences.Editor pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
                pref.putBoolean("Ispaid", false);
                pref.apply();

//                SharedPreferences.Editor editor = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
//                editor.putBoolean("Issigned", false);
//                editor.apply();

                finish();

            }, 7500);

            return;
        }

    }


    private void justclicked() {
        learn.setOnClickListener(view -> {
            if (radiolanguage.isChecked()) {
                if (itemsClassList.size() == 1) {
                    itemsClassList.remove("Learn new Language");
                    pickedLayout.setVisibility(View.GONE);
                    radiolanguage.setChecked(false);
                    return;
                }
                radiolanguage.setChecked(false);
                itemsClassList.remove("Learn new Language");
                itemsInflatedAdapter.notifyDataSetChanged();

                return;
            }

            if (itemsClassList.size() > 2) {
                Toast toast = Toast.makeText(this, "Max capacity reached", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

                return;
            }

            pickedLayout.setVisibility(View.VISIBLE);
            radiolanguage.setChecked(true);
            itemsClassList.add("Learn new Language");
            itemsInflatedAdapter.notifyDataSetChanged();
        });



        project.setOnClickListener(view -> {
            if (radioproject.isChecked()) {
                if (itemsClassList.size() == 1) {
                    itemsClassList.remove("Start project");
                    pickedLayout.setVisibility(View.GONE);
                    radioproject.setChecked(false);
                    return;
                }

                radioproject.setChecked(false);
                itemsClassList.remove("Start project");
                itemsInflatedAdapter.notifyDataSetChanged();

                return;
            }

            if (itemsClassList.size() > 2) {
                Toast toast = Toast.makeText(this, "Max capacity reached", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

                return;
            }

            pickedLayout.setVisibility(View.VISIBLE);
            radioproject.setChecked(true);
            itemsClassList.add("Start project");
            itemsInflatedAdapter.notifyDataSetChanged();
        });

        tap1.setOnClickListener(view -> {
            if (radio1.isChecked()) {
                if (itemsClassList.size() == 1) {
                    itemsClassList.remove("Reading books");
                    pickedLayout.setVisibility(View.GONE);
                    radio1.setChecked(false);
                    return;
                }
                radio1.setChecked(false);
                itemsClassList.remove("Reading books");
                itemsInflatedAdapter.notifyDataSetChanged();

                return;
            }

            if (itemsClassList.size() > 2) {
                Toast toast = Toast.makeText(this, "Max capacity reached", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

                return;
            }

            pickedLayout.setVisibility(View.VISIBLE);
            radio1.setChecked(true);
            itemsClassList.add("Reading books");
            itemsInflatedAdapter.notifyDataSetChanged();
        });

        tap2.setOnClickListener(view -> {
            if (radio2.isChecked()) {
                if (itemsClassList.size() == 1) {
                    TransitionManager.beginDelayedTransition(pickedLayout);
                    pickedLayout.setVisibility(View.GONE);
                    itemsClassList.remove("Working out");
                    radio2.setChecked(false);
                    return;
                }
                radio2.setChecked(false);
                itemsClassList.remove("Working out");
                itemsInflatedAdapter.notifyDataSetChanged();
                return;
            }

            if (itemsClassList.size() > 2) {
                Toast toast = Toast.makeText(this, "Max capacity reached", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }

            pickedLayout.setVisibility(View.VISIBLE);
            radio2.setChecked(true);
            itemsClassList.add("Working out");
            itemsInflatedAdapter.notifyDataSetChanged();
        });

//        tap3.setOnClickListener(view -> {
//            if (radio3.isChecked()) {
//                if (itemsClassList.size() == 1) {
//                    TransitionManager.beginDelayedTransition(pickedLayout);
//                    pickedLayout.setVisibility(View.GONE);
//                    itemsClassList.remove("Waking-up time");
//                    actualtime.setText("Set time");
//                    radio3.setChecked(false);
//                    TransitionManager.beginDelayedTransition(tap3);
//                    time_layout.setVisibility(View.GONE);
//                    return;
//                }
//
//                actualtime.setText("Set time");
//                radio3.setChecked(false);
//                itemsClassList.remove("Waking-up time");
//                itemsInflatedAdapter.notifyDataSetChanged();
//
//                TransitionManager.beginDelayedTransition(tap3);
//                time_layout.setVisibility(View.GONE);
//                return;
//            }
//
//            if (itemsClassList.size() > 2) {
//                Toast.makeText(this, "Max capacity reached", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            pickedLayout.setVisibility(View.VISIBLE);
////            radio3.setChecked(true);
//            itemsClassList.add("Waking-up time");
//            itemsInflatedAdapter.notifyDataSetChanged();
//            TransitionManager.beginDelayedTransition(tap3);
//            time_layout.setVisibility(View.VISIBLE);
//        });

        tap4.setOnClickListener(view -> {
            if (radio4.isChecked()) {
                if (itemsClassList.size() == 1) {
                    TransitionManager.beginDelayedTransition(pickedLayout);
                    pickedLayout.setVisibility(View.GONE);
                    itemsClassList.remove("Going for a run");
                    radio4.setChecked(false);
                    return;
                }

                radio4.setChecked(false);
                itemsClassList.remove("Going for a run");
                itemsInflatedAdapter.notifyDataSetChanged();
                return;
            }

            if (itemsClassList.size() > 2) {
                Toast toast = Toast.makeText(this, "Max capacity reached", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }

            pickedLayout.setVisibility(View.VISIBLE);
            radio4.setChecked(true);
            itemsClassList.add("Going for a run");
            itemsInflatedAdapter.notifyDataSetChanged();
        });

//        tap5.setOnClickListener(view -> {
//            if (radio5.isChecked()){
//                if (itemsClassList.size()==1){
//                    TransitionManager.beginDelayedTransition(pickedLayout);
//                    pickedLayout.setVisibility(View.GONE);
//                    itemsClassList.remove("Hydrate daily");
//                    radio5.setChecked(false);
//                    return;
//                }
//
//                radio5.setChecked(false);
//                itemsClassList.remove("Hydrate daily");
//                itemsInflatedAdapter.notifyDataSetChanged();
//                return;
//            }
//
//            if (itemsClassList.size() >2){
//                Toast.makeText(this, "Max capacity reached", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            pickedLayout.setVisibility(View.VISIBLE);
//            radio5.setChecked(true);
//            itemsClassList.add("Hydrate daily");
//            itemsInflatedAdapter.notifyDataSetChanged();
//        });

        tap6.setOnClickListener(view -> {
            if (radio6.isChecked()) {
                if (itemsClassList.size() == 1) {
                    TransitionManager.beginDelayedTransition(pickedLayout);
                    pickedLayout.setVisibility(View.GONE);
                    itemsClassList.remove("Work on Martial Arts");
                    radio6.setChecked(false);
                    return;
                }

                radio6.setChecked(false);
                itemsClassList.remove("Work on Martial Arts");
                itemsInflatedAdapter.notifyDataSetChanged();
                return;
            }

            if (itemsClassList.size() > 2) {
                Toast toast = Toast.makeText(this, "Max capacity reached", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }

            pickedLayout.setVisibility(View.VISIBLE);
            radio6.setChecked(true);
            itemsClassList.add("Work on Martial Arts");
            itemsInflatedAdapter.notifyDataSetChanged();
        });

        if (itemsClassList.size() == 0) {
            pickedLayout.setVisibility(View.GONE);
            return;
        }

    }

    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(this, "You can't go back now", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    private void openDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.DialogStyle);

        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_dialog, (ViewGroup) findViewById(R.id.newid));

        RelativeLayout new_item = view.findViewById(R.id.new_item);
        TextInputEditText textInputEditText = view.findViewById(R.id.initial_pass);

        new_item.setOnClickListener(v1 -> {
            bottomSheetDialog.dismiss();

            if (itemsClassList.size() > 2) {
                Toast toast = Toast.makeText(this, "Max capacity reached", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }

            pickedLayout.setVisibility(View.VISIBLE);
            itemsClassList.add(textInputEditText.getText().toString());
            itemsInflatedAdapter.notifyDataSetChanged();

        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
}