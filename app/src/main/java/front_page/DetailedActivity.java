package front_page;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import adapters.HomeEventAdapter;
import adapters.LimitJournalAdapter;
import devydev.mirror.net.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import model.ForHabits;
import model.Users;

public class DetailedActivity extends AppCompatActivity {

    ImageView symbol, statusofprogress;
    LinearLayout layer;
    RelativeLayout thecover, movetojournal;
    List<String> stringList = new ArrayList<>();
    TextView symbolTxt, perc, dayscompleted, dayskipped, daysleft, statustxt;
    RelativeLayout start_journaling;
    View viewborder;
    RecyclerView recyclerview;
    LimitJournalAdapter limitJournalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        String habits = getIntent().getStringExtra("habit");
        String title = getIntent().getStringExtra("title");

        findViewById(R.id.closepage).setOnClickListener(v -> {
            finish();
        });


        layer = findViewById(R.id.layer);
        movetojournal = findViewById(R.id.movetojournal);
        recyclerview = findViewById(R.id.recyc);
        viewborder = findViewById(R.id.viewborder);
        thecover = findViewById(R.id.thecover);
        start_journaling = findViewById(R.id.start_journaling);
        statustxt = findViewById(R.id.statustxt);
        statusofprogress = findViewById(R.id.statusofprogress);
        dayscompleted = findViewById(R.id.dayscompleted);
        dayskipped = findViewById(R.id.dayskipped);
        daysleft = findViewById(R.id.daysleft);
        perc = findViewById(R.id.perc);
        symbol = findViewById(R.id.symbol);
        symbolTxt = findViewById(R.id.symbolTxt);


        limitJournalAdapter = new LimitJournalAdapter(getApplicationContext(), stringList, DetailedActivity.this);
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerview.setAdapter(limitJournalAdapter);


        start_journaling.setOnClickListener(v ->{
            openBottomsheet(title);
        });

        movetojournal.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), JournalActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("Habit", title));
        });

        ifstatements(habits, title);

        crushiate(title);

        displayDates(title);
        setFlags(title);
    }

    void setFlags(String title) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(title);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ForHabits forHabits = snapshot.getValue(ForHabits.class);

               if (forHabits != null){
                   if (forHabits.isIsjournal()){
                       layer.setVisibility(View.VISIBLE);
                       thecover.setVisibility(View.GONE);
                       movetojournal.setVisibility(View.VISIBLE);
                   }else {
                       layer.setVisibility(View.GONE);
                       thecover.setVisibility(View.VISIBLE);
                       movetojournal.setVisibility(View.GONE);
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void displayDates(String title) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Journal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(title);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stringList.clear();

                for (DataSnapshot sn : snapshot.getChildren()) {
                    stringList.add(sn.getKey());

                }

                Collections.reverse(stringList);
                limitJournalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void openBottomsheet(String title) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.bottomsheetTheme);

        View view = LayoutInflater.from(this).inflate(R.layout.for_notes,
                (ViewGroup) findViewById(R.id.ones));

        bottomSheetDialog.setCancelable(true);

        bottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.darkTheme));
        }

        RelativeLayout custom_clicked = view.findViewById(R.id.custom_clicked);

        custom_clicked.setOnClickListener(v ->{
            initializemethod(title);
            bottomSheetDialog.dismiss();
            startActivity(new Intent(getApplicationContext(), JournalActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("Habit", title));
        });

        bottomSheetDialog.show();
    }

    private void initializemethod(String title) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(title);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Isjournal", true);

        reference.updateChildren(hashMap);
    }

    private void showstatus(int y, int minus) {
        if (y<15){
            if (minus <= 3){
                statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.litt_emoji));
                statustxt.setText("Brilliant");
            }

            if (minus > 3){
                statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lame));
                statustxt.setText("Awful");
            }

            return;
        }

        if (y>=25){
            statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.footprint));
            statustxt.setText("Starting");
            return;
        }

        if (y>=15 && y<25){
            if (minus > 3){
                statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lame));
                statustxt.setText("Lame");
            }

            if (minus <= 3){
                statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.great));
                statustxt.setText("Great");
            }
            return;
        }

    }

    private void ifstatements(String habits, String title) {
        if (habits.equals("MARTIALARTS")) {
            symbol.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_martial_arts));
            symbol.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.newlyaddedcolor1));
            symbolTxt.setText("Martial arts");


            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child("Items")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(title);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ForHabits progress = snapshot.getValue(ForHabits.class);

                    int pp = Integer.parseInt(progress.getCounter());

                    //if 30days = 100% what about this BD; So
                    int p = (pp * 100) / 30;

                    BigDecimal bigDecimal = new BigDecimal(p);
                    bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN); //truncate


                    int y = bigDecimal.intValue();

                    ValueAnimator animator = ValueAnimator.ofInt(0, y);
                    animator.setDuration(1000);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            perc.setText(animation.getAnimatedValue().toString() + "%");
                        }
                    });
                    animator.start();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (habits.equals("GYM")) {
            symbol.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tried_png));
            symbol.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.newlyaddedcolor2));
            symbolTxt.setText("Working out");

            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child("Items")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(title);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ForHabits progress = snapshot.getValue(ForHabits.class);

                    int pp = Integer.parseInt(progress.getCounter());

                    //if 30days = 100% what about this BD; So
                    int p = (pp * 100) / 30;

                    BigDecimal bigDecimal = new BigDecimal(p);
                    bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN); //truncate


                    int y = bigDecimal.intValue();

                    ValueAnimator animator = ValueAnimator.ofInt(0, y);
                    animator.setDuration(1000);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            perc.setText(animation.getAnimatedValue().toString() + "%");
                        }
                    });
                    animator.start();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (habits.equals("RUNNING")) {
            symbol.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_workout));
            symbol.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.newColor));
            symbolTxt.setText("Daily Running");

            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child("Items")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(title);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ForHabits progress = snapshot.getValue(ForHabits.class);

                    int pp = Integer.parseInt(progress.getCounter());

                    //if 30days = 100% what about this BD; So
                    int p = (pp * 100) / 30;

                    BigDecimal bigDecimal = new BigDecimal(p);
                    bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN); //truncate


                    int y = bigDecimal.intValue();

                    ValueAnimator animator = ValueAnimator.ofInt(0, y);
                    animator.setDuration(1000);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            perc.setText(animation.getAnimatedValue().toString() + "%");
                        }
                    });
                    animator.start();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        if (habits.equals("CUSTOM")) {
            symbol.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_dashboard_customize_24));
            symbolTxt.setText(title);


            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child("Items")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(title);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ForHabits progress = snapshot.getValue(ForHabits.class);

                    int pp = Integer.parseInt(progress.getCounter());

                    //if 30days = 100% what about this BD; So
                    int p = (pp * 100) / 30;

                    BigDecimal bigDecimal = new BigDecimal(p);
                    bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN); //truncate


                    int y = bigDecimal.intValue();

//                    String conversion = "" + y + "%";
//
//                    perc.setText(conversion);


                    ValueAnimator animator = ValueAnimator.ofInt(0, y);
                    animator.setDuration(1000);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            perc.setText(animation.getAnimatedValue().toString() + "%");
                        }
                    });
                    animator.start();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        if (habits.equals("PROJECT")) {
            symbol.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_work_history_24));
            symbolTxt.setText(title);


            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child("Items")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(title);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ForHabits progress = snapshot.getValue(ForHabits.class);

                    int pp = Integer.parseInt(progress.getCounter());

                    //if 30days = 100% what about this BD; So
                    int p = (pp * 100) / 30;

                    BigDecimal bigDecimal = new BigDecimal(p);
                    bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN); //truncate


                    int y = bigDecimal.intValue();

//                    String conversion = "" + y + "%";
//
//                    perc.setText(conversion);


                    ValueAnimator animator = ValueAnimator.ofInt(0, y);
                    animator.setDuration(1000);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            perc.setText(animation.getAnimatedValue().toString() + "%");
                        }
                    });
                    animator.start();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (habits.equals("LANG")) {
            symbol.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_language_24));
            symbolTxt.setText(title);


            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child("Items")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(title);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ForHabits progress = snapshot.getValue(ForHabits.class);

                    int pp = Integer.parseInt(progress.getCounter());

                    //if 30days = 100% what about this BD; So
                    int p = (pp * 100) / 30;

                    BigDecimal bigDecimal = new BigDecimal(p);
                    bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN); //truncate


                    int y = bigDecimal.intValue();

//                    String conversion = "" + y + "%";
//
//                    perc.setText(conversion);


                    ValueAnimator animator = ValueAnimator.ofInt(0, y);
                    animator.setDuration(1000);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            perc.setText(animation.getAnimatedValue().toString() + "%");
                        }
                    });
                    animator.start();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void crushiate(String title){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(title);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ForHabits progress = snapshot.getValue(ForHabits.class);

                ValueAnimator animator = ValueAnimator.ofInt(0, Integer.parseInt(progress.getCounter()));
                animator.setDuration(1000);
                animator.addUpdateListener(animation -> {
                    dayscompleted.setText(animation.getAnimatedValue().toString());
                });
                animator.start();
                an(progress.getCounter());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void an (String s){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Calendar calendar1 = Calendar.getInstance();

                long x = Long.parseLong(users.getTimemillis());

                long minus_millis = ((calendar1.getTimeInMillis() + 86400000) - x);

                //millisec into a day
                // if 1day = 24hours & 1hour = 60minutes & 1 min = 60sec =


                long division = minus_millis/86400000;

                BigDecimal bd = new BigDecimal(division);
                bd = bd.setScale(0, BigDecimal.ROUND_DOWN); //truncate

                int y = 30 - bd.intValue();

                int ab = Integer.parseInt(s);

                int minus = bd.intValue() - ab;

                ValueAnimator animator = ValueAnimator.ofInt(0, minus);
                animator.setDuration(1000);
                animator.addUpdateListener(animation -> {
                    dayskipped.setText(animation.getAnimatedValue().toString());
                });

                animator.start();


                ValueAnimator an = ValueAnimator.ofInt(30, y);
                an.setDuration(1000);
                an.addUpdateListener(animation -> {
                    if (y<0){
                        daysleft.setText("0");
                    }else {
                        daysleft.setText(animation.getAnimatedValue().toString());
                    }
                });
                an.start();

                showstatus(y, minus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}