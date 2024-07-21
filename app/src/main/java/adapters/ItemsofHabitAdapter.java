package adapters;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.os.Handler;
import android.os.Looper;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import devydev.mirror.net.R;
import front_page.BooksActivity;
import interface_package.ConfettiDisplay;
import me.tankery.lib.circularseekbar.CircularSeekBar;
import model.ForHabits;
import model.Users;
import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ItemsofHabitAdapter extends RecyclerView.Adapter<ItemsofHabitAdapter.ViewHolder> {

    List<ForHabits> forHabitsList;
    Context context;
    // Declare a Handler
    private Handler handler;
    ConfettiDisplay confettiDisplay;
    boolean isAnimating = false;
    Activity activity;
//    SoundPool soundPool;

    public ItemsofHabitAdapter(List<ForHabits> forHabitsList, Context context, Activity activity, ConfettiDisplay confettiDisplay) {
        this.forHabitsList = forHabitsList;
        this.context = context;
        this.activity = activity;
        this.confettiDisplay = confettiDisplay;
    }


    @NonNull
    @Override
    public ItemsofHabitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.habit_items_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsofHabitAdapter.ViewHolder holder, int position) {
        ForHabits habits = forHabitsList.get(position);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        handler = new Handler(Looper.getMainLooper());

//        soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build();
//        int sound1 = soundPool.load(context, R.raw.complete_sound, 1);

        checkforstatus(holder.imageofitem, holder.flipview, holder.progress1, habits.getUserid(), habits.getItem());

        renewal2(habits.getItem());
        holder.progress1.setMax(10000);
        ObjectAnimator animation = ObjectAnimator.ofInt(holder.progress1, "progress", holder.progress1.getProgress(), 10 * 100);
        animation.setDuration(1500);
        animation.setAutoCancel(true);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        holder.imageofitem.setOnClickListener(v -> {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                holder.flipview.flipTheView();
            }, 1500);
            holder.flipview.flipTheView();
        });

        holder.touch.setOnClickListener(v -> {
//            holder.flipview.flipTheView();
            holder.touch.setEnabled(false);
            if (habits != null) {
                if (habits.getItem().equals("Reading books")) {
                    if (habits.getBookid() != null) {
                        setPageRead(habits.getPagesread(), habits.getNoofpages(), holder.progress1, habits.getItem(), habits.getCounter(), habits.getBookid());
                    } else {
                        context.startActivity(new Intent(context, BooksActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(context, "Add a book", Toast.LENGTH_SHORT).show();
                    }

                    return;
                }

                renewal(v, holder.progress1, habits.getItem());
                renewaltwo(habits.getItem(), habits.getCounter());
            }
        });


        if (habits.getItem().equals("Start project")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_work_history_24));
        }

        if (habits.getItem().equals("Learn new Language")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_language_24));
        }

        if (habits.getItem().equals("Reading books")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_study_research));
        }

        if (habits.getItem().equals("Working out")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.tried_png));
            holder.imageofitem.setImageTintList(ContextCompat.getColorStateList(context, R.color.newlyaddedcolor2));

        }

        if (habits.getItem().equals("Waking-up time")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_alarm));
        }

        if (habits.getItem().equals("Going for a run")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_workout));
            holder.imageofitem.setImageTintList(ContextCompat.getColorStateList(context, R.color.newColor));
        }


        if (habits.getItem().equals("Work on Martial Arts")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_martial_arts));
            holder.imageofitem.setImageTintList(ContextCompat.getColorStateList(context, R.color.newlyaddedcolor1));

        }

        if (!habits.getItem().equals("Reading books") && !habits.getItem().equals("Working out") && !habits.getItem().equals("Waking-up time") && !habits.getItem().equals("Going for a run") && !habits.getItem().equals("Work on Martial Arts")&&
                !habits.getItem().equals("Start project") &&
                !habits.getItem().equals("Learn new Language")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_dashboard_customize_24));
            return;
        }

    }

    private void throwConfetti(KonfettiView konfettiView) {
        final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_hurt);
        Shape.DrawableShape drawableShape = new Shape.DrawableShape(drawable, true, true);

        EmitterConfig emitterConfig = new Emitter(5, TimeUnit.SECONDS).perSecond(100);
        konfettiView.start(new PartyFactory(emitterConfig).angle(Angle.BOTTOM).spread(Spread.ROUND).shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape)).colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def)).setSpeedBetween(0f, 15f).position(new Position.Relative(0.0, 0.0).between(new Position.Relative(1.0, 0.0))).build());
    }

    private void setPageRead(String pagesread, String noofpages, ProgressBar progress1, String item, String counter, String bookID) {
        int x = Integer.parseInt(noofpages);

        int y = x / 30;

        // Declare a boolean flag to indicate the animation state


        BigDecimal bigDecimal = new BigDecimal(y);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN);

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.seekfirst_layout, null);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.bottomsheetTheme);

        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);

        // Adjust window flags to position the BottomSheetDialog above the navigation bar
        Window window = bottomSheetDialog.getWindow();

        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            window.setAttributes(layoutParams);
        }

        TextView fortoday = view.findViewById(R.id.fortoday);
        TextView head = view.findViewById(R.id.head);
        TextView comeback = view.findViewById(R.id.comeback);
        ConstraintLayout constraintone = view.findViewById(R.id.constrainone);
        RelativeLayout doneclick = view.findViewById(R.id.doneclick);
        ViewGroup view_g = view.findViewById(R.id.view_g);
        TextView daily_goal = view.findViewById(R.id.daily_goal);
        TextView state_clicked = view.findViewById(R.id.state_clicked);
        ImageView complete_img = view.findViewById(R.id.complete_img);
        LinearLayout holdermama = view.findViewById(R.id.holdermama);
        RelativeLayout cancel_sheet = view.findViewById(R.id.cancel_sheet);
        RelativeLayout bookcheck = view.findViewById(R.id.bookcheck);
        TextView seektext = view.findViewById(R.id.seektext);
        CircularSeekBar seekbar = view.findViewById(R.id.seekbar);

        if (counter.equals("0")) {
            seekbar.setMax(Integer.parseInt(noofpages));
            seektext.setText(pagesread);
            seekbar.setProgress(Float.parseFloat(pagesread));

            daily_goal.setText("Daily goal: " + y + " pages");
            cancel_sheet.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
            });

            doneclick.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                throwtoserver(seektext.getText().toString(), bookID);
                forseek(item, 0, seektext.getText().toString());
                renewalReplica(progress1, item);
            });

            bookcheck.setOnClickListener(view1 -> {
                int v = Integer.parseInt(seektext.getText().toString());
                int p = Integer.parseInt(pagesread);

                if (v < p) {
                    Toast toast = Toast.makeText(context, "You cannot move backwards", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                if (v == p) {
                    Toast toast = Toast.makeText(context, "Cancel if there's no progress", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }


                int abc = Integer.parseInt(seektext.getText().toString());
                int nn = Integer.parseInt(noofpages);

                // Create the ObjectAnimator for the progress property
                seekbar.setDisablePointer(true);
                seekbar.setPointerStrokeWidth(30);
                ValueAnimator animator = ValueAnimator.ofInt(abc, nn);
                animator.setDuration(1650);
                // Update the SeekBar progress during animation
                animator.setInterpolator(new AccelerateDecelerateInterpolator());

                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        // Animation started, disable the SeekBar
                        seekbar.setFocusable(false);
                        seekbar.setClickable(false);
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Animation ended, re-enable the SeekBar
                        seekbar.setFocusable(true);
                        seekbar.setClickable(true);
                        isAnimating = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // Animation canceled, re-enable the SeekBar
                        seekbar.setFocusable(true);
                        seekbar.setClickable(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        // Animation repeated, do nothing
                    }
                });

                animator.addUpdateListener(valueAnimator -> {
                    int animatedValue = (int) valueAnimator.getAnimatedValue();
                    seekbar.setProgress(animatedValue);
                });

                // Start the animation
                animator.start();

                holdermama.setVisibility(View.GONE);
                doneclick.setVisibility(View.VISIBLE);
                bookcheck.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(view_g);
                cancel_sheet.setVisibility(View.GONE);
                complete_img.setVisibility(View.VISIBLE);
                head.setVisibility(View.GONE);
                daily_goal.setVisibility(View.GONE);
                fortoday.setVisibility(View.VISIBLE);
                comeback.setVisibility(View.VISIBLE);
            });

            seekbar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
                @Override
                public void onProgressChanged(@Nullable CircularSeekBar circularSeekBar, float v, boolean b) {
                    // Only update the text if the animation is not running
                    if (!isAnimating) {
                        BigDecimal bd = new BigDecimal (v);
                        bd = bd.setScale (0, BigDecimal.ROUND_UP);
                        seektext.setText ("" + bd);
                    }
                }

                @Override
                public void onStopTrackingTouch(@Nullable CircularSeekBar circularSeekBar) {

                }

                @Override
                public void onStartTrackingTouch(@Nullable CircularSeekBar circularSeekBar) {

                }
            });
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.create();
            bottomSheetDialog.show();

            return;
        }

        seekbar.setMax(Integer.parseInt(noofpages));
        seektext.setText(pagesread);
        seekbar.setProgress(Float.parseFloat(pagesread));


        daily_goal.setText("Daily goal: " + y + " pages");
        cancel_sheet.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        doneclick.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            throwtoserver(seektext.getText().toString(), bookID);
            forseek(item, 0, seektext.getText().toString());
            renewalReplica(progress1, item);
        });

        bookcheck.setOnClickListener(view1 -> {
            int v = Integer.parseInt(seektext.getText().toString());
            int p = Integer.parseInt(pagesread);

            if (v < p) {
                Toast toast = Toast.makeText(context, "You cannot move backwards", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (v == p) {
                Toast toast = Toast.makeText(context, "Cancel if there's no progress", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            int abc = Integer.parseInt(seektext.getText().toString());
            int nn = Integer.parseInt(noofpages);

            // Create the ObjectAnimator for the progress property
            seekbar.setDisablePointer(true);
            seekbar.setPointerStrokeWidth(30);
            ValueAnimator animator = ValueAnimator.ofInt(abc, nn);
            animator.setDuration(1650);
            // Update the SeekBar progress during animation
            animator.setInterpolator(new AccelerateDecelerateInterpolator());

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    // Animation started, disable the SeekBar
                    seekbar.setFocusable(false);
                    seekbar.setClickable(false);
                    isAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // Animation ended, re-enable the SeekBar
                    seekbar.setFocusable(true);
                    seekbar.setClickable(true);
                    isAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // Animation canceled, re-enable the SeekBar
                    seekbar.setFocusable(true);
                    seekbar.setClickable(true);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    // Animation repeated, do nothing
                }
            });

            animator.addUpdateListener(valueAnimator -> {
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                seekbar.setProgress(animatedValue);
            });

            // Start the animation
            animator.start();

            holdermama.setVisibility(View.GONE);
            doneclick.setVisibility(View.VISIBLE);
            bookcheck.setVisibility(View.GONE);
            TransitionManager.beginDelayedTransition(view_g);
            cancel_sheet.setVisibility(View.GONE);
            complete_img.setVisibility(View.VISIBLE);
            head.setVisibility(View.GONE);
            daily_goal.setVisibility(View.GONE);
            fortoday.setVisibility(View.VISIBLE);
            comeback.setVisibility(View.VISIBLE);
        });

        seekbar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(@Nullable CircularSeekBar circularSeekBar, float v, boolean b) {
                // Only update the text if the animation is not running
                if (!isAnimating) {
                    BigDecimal bd = new BigDecimal (v);
                    bd = bd.setScale (0, BigDecimal.ROUND_UP);
                    seektext.setText ("" + bd);
                }
            }

            @Override
            public void onStopTrackingTouch(@Nullable CircularSeekBar circularSeekBar) {

            }

            @Override
            public void onStartTrackingTouch(@Nullable CircularSeekBar circularSeekBar) {

            }
        });


        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.create();
        bottomSheetDialog.show();
    }

    private void renewalReplica(ProgressBar progress1, String item) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
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

                setStatusReplica(bd, progress1, item);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setStatusReplica(BigDecimal bd, ProgressBar progress1, String item) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(item);
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("Item", item);
        objectHashMap.put("Checked", true);
        objectHashMap.put("Timestamp", "" + bd.intValue());

        reference.updateChildren(objectHashMap);

        ObjectAnimator an = ObjectAnimator.ofInt(progress1, "progress", progress1.getProgress(), 100 * 100);
        an.setDuration(1500);
        an.setAutoCancel(true);
        an.setInterpolator(new DecelerateInterpolator());
        an.start();

        confettiDisplay.explode();
    }

    private void throwtoserver(String string, String bookID) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Pagesread", string);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Books").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(bookID);

        reference.updateChildren(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> objectHashMap = new HashMap<>();
                objectHashMap.put("Pagesread", string);

                FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Reading books").updateChildren(objectHashMap);
                Log.d("DISPLAY TEXT", string);
            }
        });
    }

    private void forseek(String item, int counter, String string) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar1 = Calendar.getInstance();
        final String todaysdate = dateFormat.format(calendar1.getTime());
        try {
            Date d = dateFormat.parse(todaysdate);
            long milliseconds = d.getTime();


            int result = counter + 1;

            String p = String.valueOf(result);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(item);

            HashMap<String, Object> objectHashMap = new HashMap<>();
            objectHashMap.put("Counter", "" + p);
            objectHashMap.put("Lastchecked", "" + milliseconds);

            reference.updateChildren(objectHashMap);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void renewal(View v, ProgressBar progress1, String item) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
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

                setStatus(v, bd, progress1, item);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void renewaltwo(String item, String counter) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar1 = Calendar.getInstance();
        final String todaysdate = dateFormat.format(calendar1.getTime());
        try {
            Date d = dateFormat.parse(todaysdate);
            long milliseconds = d.getTime();


            int result = Integer.parseInt(counter) + 1;

            String p = String.valueOf(result);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Items")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(item);

            HashMap<String, Object> objectHashMap = new HashMap<>();
            objectHashMap.put("Counter", "" + p);
            objectHashMap.put("Lastchecked", "" + milliseconds);

            reference.updateChildren(objectHashMap);

//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Record")
//                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(item);
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("Past",  String.valueOf(milliseconds));
//
//            databaseReference.updateChildren(hashMap);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    private void renewal2(String item) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Items")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(item);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ForHabits users = snapshot.getValue(ForHabits.class);
                try {
                    long x = Long.parseLong(users.getLastchecked());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar1 = Calendar.getInstance();
                    final String todaysdate = dateFormat.format(calendar1.getTime());
                    Date d = dateFormat.parse(todaysdate);
                    long milliseconds = d.getTime();

                    if (milliseconds > x) {
                        setStatus2(item);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkforstatus(ImageView imageofitem, EasyFlipView flipview, ProgressBar progress1, String userid, String item) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Items").child(userid).child(item);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ForHabits status = snapshot.getValue(ForHabits.class);

                if (status.isChecked()) {
                    progress1.setMax(10000);
                    ObjectAnimator animation = ObjectAnimator.ofInt(progress1, "progress", progress1.getProgress(), 100 * 100);
                    animation.setDuration(1500);
                    animation.setAutoCancel(true);
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();

                    flipview.setEnabled(false);
                    imageofitem.setEnabled(false);
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setStatus(View v, BigDecimal bd, ProgressBar progress1, String item) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(item);
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("Item", item);
        objectHashMap.put("Checked", true);
        objectHashMap.put("Timestamp", "" + bd.intValue());

        reference.updateChildren(objectHashMap);

        ObjectAnimator an = ObjectAnimator.ofInt(progress1, "progress", progress1.getProgress(), 100 * 100);
        an.setDuration(1500);
        an.setAutoCancel(true);
        an.setInterpolator(new DecelerateInterpolator());
        an.start();

        confettiDisplay.explode();
    }

//    private void sendNotification(String s) {
//        String header = context.getResources().getString(R.string.notification_header);
//        String body = context.getResources().getString(R.string.notification_body);
//
//        FirebaseDatabase.getInstance().getReference().child("Users")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Users users = snapshot.getValue(Users.class);
//
//                        try {
//                            JSONObject jsonObject = new JSONObject();
//                            JSONObject notification = new JSONObject();
//                            notification.put("title", header);
//                            notification.put("body", body);
//                            JSONObject dataObject = new JSONObject();
//                            dataObject.put("userId", users.getUserid());
//
//                            jsonObject.put("notification", notification);
//                            jsonObject.put("data", dataObject);
//                            jsonObject.put("to", users.getToken());
//
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    callAPI(jsonObject);
//                                    try {
//                                        Log.d("Trig", "Called 1 " + users.getToken() + " " + jsonObject.getString("to"));
//                                    } catch (JSONException e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                }
//                            }, 60000);
//
//                        }catch (Exception e){
//                            Log.d("LOG", "" + e.getLocalizedMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//    void callAPI(JSONObject jsonObject){
//        MediaType JSON = MediaType.get("application/json");
//        OkHttpClient client = new OkHttpClient();
//
//        String url = "https://fcm.googleapis.com/fcm/send";
//
//        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .header("Authorization", "Bearer AAAAZ1mMx5U:APA91bHg8sDA6v96PgMzhX1KTx2jEASnq6AaC28859wvyo82LiAeaK7FHb2jj1y0DMVecEwXT8AtXyA_jHgf-VTSgSeIdQxZOopnyimX5PDjpxEWi7s5_fk8lCcoBwQY1YjePcQ-rVc8")
//                .build();
//
//        Log.d("Trig", "Called 2");
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Log.d("LOG", ""+e.getLocalizedMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                Log.d("Trig", "" + response.body());
//            }
//        });
//    }

    // In your adapter class
//    public void onDestroy () {
//        soundPool.release();
//        soundPool = null;
//    }


    private void setStatus2(String item) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Items").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(item);
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("Item", item);
        objectHashMap.put("Checked", false);

        reference.updateChildren(objectHashMap);
    }

    private static String removeDots(String input) {
        // Replace dots with an empty string
        return input.replace(".", "");
    }
    @Override
    public int getItemCount() {
        return forHabitsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        EasyFlipView flipview;
        ImageView imageofitem;
        ProgressBar progress1;
        RelativeLayout touch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            touch = itemView.findViewById(R.id.touch);
            imageofitem = itemView.findViewById(R.id.imageofitem);
            flipview = itemView.findViewById(R.id.flipview);
            progress1 = itemView.findViewById(R.id.progress1);
        }
    }
}
