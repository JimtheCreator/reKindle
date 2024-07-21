package front_page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
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
import java.util.List;
import java.util.Locale;

import adapters.HomeEventAdapter;
import devydev.mirror.net.R;
import model.ForHabits;
import model.Journal;
import space.SpacebetweenItems;

public class JournalActivity extends AppCompatActivity {

    ViewGroup viewGroup;
    ImageView down;
    BottomSheetBehavior bottomSheetBehavior;
    RecyclerView journal_recyclerview;
    EditText box2, box3;
    TextView setjournal;
    List<Journal> journalList = new ArrayList<>();
    List<String> stringList = new ArrayList<>();
    HomeEventAdapter homeEventAdapter;
    RelativeLayout ttpp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        viewGroup = findViewById(R.id.box);
        down = findViewById(R.id.down);
        findViewById(R.id.backpressed).setOnClickListener(view -> finish());
        SpacebetweenItems spa = new SpacebetweenItems(35);
        setjournal = findViewById(R.id.setjournal);
        View box = findViewById(R.id.box);
        box2 = findViewById(R.id.box2);
        box3 = findViewById(R.id.box3);
        ttpp = findViewById(R.id.ttpp);
        homeEventAdapter = new HomeEventAdapter(getApplicationContext(), stringList, JournalActivity.this);
        journal_recyclerview = findViewById(R.id.journal_recyclerview);
        journal_recyclerview.setHasFixedSize(true);
        journal_recyclerview.addItemDecoration(spa);
        journal_recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        journal_recyclerview.setAdapter(homeEventAdapter);

        String habit = getIntent().getStringExtra("Habit");
        bottomSheetBehavior = BottomSheetBehavior.from(box);

        bottomSheetBehavior.setPeekHeight(160);

        bottomSheetBehavior.setDraggable(false);

        box2.setOnClickListener(view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                closeKeyboard();
            }
        });


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    TransitionManager.beginDelayedTransition(viewGroup);
                    bottomSheetBehavior.setDraggable(false);
                    ttpp.setVisibility(View.GONE);
                    box3.setVisibility(View.GONE);
                    box2.setVisibility(View.VISIBLE);
                    closeKeyboard();
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    TransitionManager.beginDelayedTransition(viewGroup);
                    bottomSheetBehavior.setDraggable(true);
                    ttpp.setVisibility(View.VISIBLE);
                    box2.setVisibility(View.GONE);
                    box3.setVisibility(View.VISIBLE);
                    openKeyboard();
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        setjournal.setOnClickListener(v -> {
            closeKeyboard();
            if (box3.getText().toString().isEmpty()) {
                Toast.makeText(this, "Field is empty", Toast.LENGTH_SHORT).show();
                return;
            }


            saveEvents(box3.getText().toString(), habit);

        });

        displayDates(habit);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void openKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    private String getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    private String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    private void saveEvents(String written, String habit) {
        String currentDate = getCurrentDate();
        String currentMonth = getCurrentMonth();
        String currentDay = getCurrentDay();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Journal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(habit)
                .child(currentDay + " " + currentMonth);

        String pushID = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("Journalid", pushID);
        hashMap.put("Day", currentDay);
        hashMap.put("Month", currentMonth);
        hashMap.put("Habitname", habit);
        hashMap.put("Writtentxt", written);
        hashMap.put("Date", currentDate);
        hashMap.put("Timeinmillis", System.currentTimeMillis());

        reference.child(pushID).setValue(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                box3.setText("");
            }
        }).addOnFailureListener(e -> {
            final AlertDialog.Builder alert;
            alert = new AlertDialog.Builder(getApplicationContext());
            alert.setCancelable(true);
            alert.setTitle("Error");
            alert.setMessage(e.getMessage());
            alert.setPositiveButton("Ok", (dialogInterface, i) -> {

            });

            alert.create().show();
        });


    }

    void displayDates(String habit) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Journal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(habit);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stringList.clear();

                for (DataSnapshot sn : snapshot.getChildren()) {
                    stringList.add(sn.getKey());

                }


//                displayJournals(habit);

                Collections.reverse(stringList);
                homeEventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (BottomSheetBehavior.STATE_EXPANDED == bottomSheetBehavior.getState()) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            closeKeyboard();
        } else {
            super.onBackPressed();
        }
    }
}