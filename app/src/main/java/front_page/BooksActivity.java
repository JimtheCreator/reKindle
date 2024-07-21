package front_page;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import adapters.BookAdapter;
import adapters.LimitJournalAdapter;
import devydev.mirror.net.R;
import model.Book;
import model.ForHabits;
import model.Users;

public class BooksActivity extends AppCompatActivity {

    ImageView statusofprogress;
    LinearLayout layer, clicked, booksLL, wholesome;
    TextInputEditText bookname, pagesread;
    EditText noOfpages;
    TextView bookno, noOfpagesread, noOfpagesleft, daysleft_to_finish, perc, statustxt;
    List<Book> booksList = new ArrayList<>();

    RelativeLayout nestpeek, add, minus, done;
    RelativeLayout thecover, movetojournal;

    List<String> stringList = new ArrayList<>();
    RecyclerView recyclerview;
    RecyclerView recyclerforbooks;
    ImageView funga;
    LimitJournalAdapter limitJournalAdapter;
    BottomSheetBehavior bottomSheetBehavior;
    RelativeLayout start_journaling;
    BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        findViewById(R.id.closepage).setOnClickListener(v -> {
            finish();
        });

        perc = findViewById(R.id.perc);
        bookno = findViewById(R.id.bookno);

        statustxt = findViewById(R.id.statustxt);
        statusofprogress = findViewById(R.id.statusofprogress);
        noOfpagesread = findViewById(R.id.noOfpagesread);
        noOfpagesleft = findViewById(R.id.noOfpagesleft);
        daysleft_to_finish = findViewById(R.id.daysleft_to_finish);
        String title = getIntent().getStringExtra("title");
        recyclerforbooks = findViewById(R.id.recyclerforbooks);
        wholesome = findViewById(R.id.wholesome);
        booksLL = findViewById(R.id.booksLL);
        done = findViewById(R.id.actuna);
        bookname = findViewById(R.id.bookname);
        pagesread = findViewById(R.id.pagesread);
        noOfpages = findViewById(R.id.noOfpages);
        add = findViewById(R.id.add);
        minus = findViewById(R.id.minus);
        funga = findViewById(R.id.funga);
        clicked = findViewById(R.id.clicked);
        nestpeek = findViewById(R.id.nestpeek);
        layer = findViewById(R.id.layer);
        movetojournal = findViewById(R.id.movetojournal);
        thecover = findViewById(R.id.thecover);
        start_journaling = findViewById(R.id.start_journaling);
        recyclerview = findViewById(R.id.recyc);
        funga = findViewById(R.id.funga);

        bottomSheetBehavior = BottomSheetBehavior.from(nestpeek);
        limitJournalAdapter = new LimitJournalAdapter(getApplicationContext(), stringList, BooksActivity.this);

        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerview.setAdapter(limitJournalAdapter);


        bookAdapter = new BookAdapter(getApplicationContext(), booksList);

        recyclerforbooks.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerforbooks.setAdapter(bookAdapter);


        bottomSheetBehavior.setDraggable(false);
        start_journaling.setOnClickListener(v -> {
            openBottomsheet();
        });

        clicked.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        funga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        noOfpages.setText("0");

        if (noOfpages.getText().toString().equals("")) {
            noOfpages.setText("0");
        }

        add.setOnClickListener(view -> {
            if (noOfpages.getText().toString().equals("")) {
                int x = 0;

                int p = x + 1;

                noOfpages.setText(String.valueOf(p));

                return;
            }

            int x = Integer.parseInt(noOfpages.getText().toString());

            int p = x + 1;

            noOfpages.setText(String.valueOf(p));
        });

        minus.setOnClickListener(view -> {
            if (!noOfpages.getText().toString().equals("")) {
                int w = Integer.parseInt(noOfpages.getText().toString());
                if (w > 0) {
                    int x = Integer.parseInt(noOfpages.getText().toString());

                    int p = x - 1;

                    noOfpages.setText(String.valueOf(p));
                }
            }
        });


        done.setOnClickListener(v -> {
            String name = bookname.getText().toString();
            String noOfpage = noOfpages.getText().toString();
            String Pagesread = pagesread.getText().toString();


            if (name.isEmpty() || noOfpage.isEmpty() || Pagesread.isEmpty()) {
                Toast.makeText(this, "Field is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            deploy(name, noOfpage, Pagesread);
        });

        movetojournal.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), JournalActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("Habit", title));
        });

        checkMethod();
        setFlags();
        displayDates();
        DisplayBook();
        an();
    }

    void displaymoreONTHETABLE(int y) {
        bookno.setText("1");
        FirebaseDatabase.getInstance().getReference().child("Books")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Book book = dataSnapshot.getValue(Book.class);
                            noOfpages.setText(book.getPagesread());

                            int value = Integer.parseInt(book.getNoofpages());

                            int x = value - Integer.parseInt(book.getPagesread());


                            ValueAnimator bx = ValueAnimator.ofInt(0, Integer.parseInt(book.getPagesread()));
                            bx.setDuration(1000);
                            bx.addUpdateListener(animation -> {
                                noOfpagesread.setText(animation.getAnimatedValue().toString());
                            });
                            bx.start();

                            ValueAnimator an = ValueAnimator.ofInt(0, x);
                            an.setDuration(1000);
                            an.addUpdateListener(animation -> {
                                noOfpagesleft.setText(animation.getAnimatedValue().toString());
                            });
                            an.start();

                            setPerc(value, Integer.parseInt(book.getPagesread()), y);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setPerc(int value, int i, int oo) {
        //if 30days = 100% what about this BD; So
        int p = (i * 100) / value;

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

        showstatus(y, oo);
    }


    void an() {
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


                long division = minus_millis / 86400000;

                BigDecimal bd = new BigDecimal(division);
                bd = bd.setScale(0, BigDecimal.ROUND_DOWN); //truncate

                int y = 30 - bd.intValue();

                ValueAnimator an = ValueAnimator.ofInt(30, y);
                an.setDuration(1000);
                an.addUpdateListener(animation -> {
                    if (y < 0) {
                        daysleft_to_finish.setText("0");
                    } else {
                        daysleft_to_finish.setText(animation.getAnimatedValue().toString());
                    }

                });
                an.start();

                displaymoreONTHETABLE(y);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void checkMethod() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Reading books");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ForHabits forHabits = snapshot.getValue(ForHabits.class);

                if (forHabits != null) {
                    if (forHabits.isIsbook()) {
                        booksLL.setVisibility(View.VISIBLE);
                        clicked.setVisibility(View.GONE);
                        wholesome.setVisibility(View.VISIBLE);
                    }

                    if (!forHabits.isIsbook()) {
                        booksLL.setVisibility(View.GONE);
                        clicked.setVisibility(View.VISIBLE);
                        wholesome.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void DisplayBook() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Books")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                booksList.clear();

                for (DataSnapshot sn : snapshot.getChildren()) {
                    Book book = sn.getValue(Book.class);
                    booksList.add(book);
                }

                bookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void deploy(String name, String noOfpage, String pagesread) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Books")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Bookname", name);
        hashMap.put("Pagesread", pagesread);
        hashMap.put("Bookid", id);
        hashMap.put("Noofpages", noOfpage);


        reference.child(id).setValue(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                HashMap<String, Object> objectHashMap = new HashMap<>();
                objectHashMap.put("Isbook", true);
                objectHashMap.put("Pagesread", pagesread);
                objectHashMap.put("Bookid", id);
                objectHashMap.put("Noofpages", noOfpage);


                FirebaseDatabase.getInstance().getReference().child("Items")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Reading books").updateChildren(objectHashMap);
            }
        });
    }

    void openBottomsheet() {
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

        custom_clicked.setOnClickListener(v -> {
            initializemethod();
            bottomSheetDialog.dismiss();
            startActivity(new Intent(getApplicationContext(), JournalActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("Habit", "Reading books"));
        });

        bottomSheetDialog.show();
    }

    void initializemethod() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Reading books");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Isjournal", true);

        reference.updateChildren(hashMap);
    }

    void setFlags() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Reading books");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ForHabits forHabits = snapshot.getValue(ForHabits.class);

                if (forHabits != null) {
                    if (forHabits.isIsjournal()) {
                        layer.setVisibility(View.VISIBLE);
                        thecover.setVisibility(View.GONE);
                        movetojournal.setVisibility(View.VISIBLE);
                    } else {
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

    void displayDates() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Journal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Reading books");

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

    void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

//    private void crushiate(){
//        DatabaseReference reference = FirebaseDatabase.getInstance()
//                .getReference().child("Items")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .child("Reading books");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ForHabits progress = snapshot.getValue(ForHabits.class);
//
//                an(progress.getCounter());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

    private void showstatus(int y, int p) {
        String xp = String.valueOf(p);

        if (!xp.isEmpty()) {
            if (p < 15) {
                if (y >= 50) {
                    statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.litt_emoji));
                    statustxt.setText("Brilliant");
                }

                if (y < 50) {
                    statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lame));
                    statustxt.setText("Awful");
                }

                return;
            }

            if (p >= 25) {
                statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.footprint));
                statustxt.setText("Starting");
                return;
            }

            if (p >= 15 && p < 25) {
                if (y < 20) {
                    statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lame));
                    statustxt.setText("Lame");
                }

                if (y >= 20) {
                    statusofprogress.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.great));
                    statustxt.setText("Great");
                }

                return;
            }
        }


    }

}