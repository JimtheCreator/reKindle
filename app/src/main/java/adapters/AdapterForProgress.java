package adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import devydev.mirror.net.R;
import model.Book;
import model.ForHabits;
import model.Users;

public class AdapterForProgress extends RecyclerView.Adapter<AdapterForProgress.ViewHolder> {

    List<ForHabits> progressList;
    Context context;

    public AdapterForProgress(List<ForHabits> progressList, Context context) {
        this.progressList = progressList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterForProgress.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.showing_progress,parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterForProgress.ViewHolder holder, int position) {
        ForHabits progress = progressList.get(position);

        if (progress.getItem().equals("Reading books")) {
            holder.mainsetup.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.displayone));
            holder.nnn.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
            an(holder.nnn, holder.mainsetup);

            return;
        }

        int pp = Integer.parseInt(progress.getCounter());

        //if 30days = 100% what about this BD; So
        int p = (pp*100) / 30;

        BigDecimal bigDecimal = new BigDecimal(p);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN); //truncate

//                DecimalFormat twoDForm = new DecimalFormat("#.##");
//
//                Toast.makeText(HomeActivity.this,""+  Double.valueOf(twoDForm.format(division)), Toast.LENGTH_SHORT).show();

        holder.mainsetup.setMax(10000);
        ObjectAnimator animation = ObjectAnimator.ofInt(holder.mainsetup, "progress",
                holder.mainsetup.getProgress(), bigDecimal.intValue() * 100);
        animation.setDuration(3000);
        animation.setAutoCancel(false);
        animation.setInterpolator( new DecelerateInterpolator());
        animation.start();

        int y = bigDecimal.intValue();

        String conversion = "" + y + "%";

        holder.nnn.setText(conversion);

        if (progress.getItem().equals("Working out")) {
            holder.mainsetup.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.displaythree));
            holder.nnn.setTextColor(ContextCompat.getColor(context, R.color.blue));
        }

        if (progress.getItem().equals("Waking-up time")) {
            holder.mainsetup.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.displayfour));
            holder.nnn.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        if (progress.getItem().equals("Going for a run")) {
            holder.mainsetup.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.displayfive));
            holder.nnn.setTextColor(ContextCompat.getColor(context, R.color.newColor));
        }


        if (progress.getItem().equals("Work on Martial Arts")) {
            holder.mainsetup.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.displaysix));
            holder.nnn.setTextColor(ContextCompat.getColor(context, R.color.newlyaddedcolor1));

        }

        if (!progress.getItem().equals("Reading books") &&
                !progress.getItem().equals("Working out") &&
                !progress.getItem().equals("Waking-up time") &&
                !progress.getItem().equals("Going for a run") &&
                !progress.getItem().equals("Work on Martial Arts")) {
            holder.mainsetup.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.displaytwo));
            holder.nnn.setTextColor(ContextCompat.getColor(context,R.color.purpleembrace));
            return;
        }
    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ProgressBar mainsetup;
        TextView nnn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mainsetup = itemView.findViewById(R.id.mainsetup);
            nnn = itemView.findViewById(R.id.nnn);
        }
    }


    void an (TextView nnn, ProgressBar mainsetup){
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

                displaymoreONTHETABLE(y, nnn, mainsetup);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    void displaymoreONTHETABLE(int y, TextView nnn, ProgressBar mainsetup) {
        FirebaseDatabase.getInstance().getReference().child("Books")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Book book = dataSnapshot.getValue(Book.class);

                            int value = Integer.parseInt(book.getNoofpages());

                            setPerc(value, Integer.parseInt(book.getPagesread()), nnn, mainsetup);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setPerc(int value, int i, TextView nnn, ProgressBar mainsetup) {
        //if 30days = 100% what about this BD; So
        int p = (i * 100) / value;

        BigDecimal bigDecimal = new BigDecimal(p);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN); //truncate


        int y = bigDecimal.intValue();

        BigDecimal bb = new BigDecimal(p);
        bb = bb.setScale(0, BigDecimal.ROUND_DOWN); //truncate
        mainsetup.setMax(10000);
        ObjectAnimator animation = ObjectAnimator.ofInt(mainsetup, "progress",
                mainsetup.getProgress(), bb.intValue() * 100);
        animation.setDuration(3000);
        animation.setAutoCancel(false);
        animation.setInterpolator( new DecelerateInterpolator());
        animation.start();

        ValueAnimator animator = ValueAnimator.ofInt(0, y);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                nnn.setText(animation.getAnimatedValue().toString() + "%");
            }
        });

        animator.start();
    }

}
