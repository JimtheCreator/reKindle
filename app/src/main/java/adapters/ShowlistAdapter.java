package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.Collections;
import java.util.List;

import devydev.mirror.net.R;
import front_page.BooksActivity;
import front_page.DetailedActivity;
import front_page.HomeActivity;

import model.ForHabits;

public class ShowlistAdapter extends RecyclerView.Adapter<ShowlistAdapter.ViewHolder> {

    Context context;

    List<ForHabits> forHabitsList;

    public ShowlistAdapter(Context context, List<ForHabits> forHabitsList) {
        this.context = context;
        this.forHabitsList = forHabitsList;
    }

    @NonNull
    @Override
    public ShowlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.showlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowlistAdapter.ViewHolder holder, int position) {
        ForHabits habits = forHabitsList.get(position);

        holder.itemView.setOnClickListener(v ->{
            if (habits.getItem().equals("Reading books")) {
                context.startActivity(new Intent(context, BooksActivity.class)
                        .putExtra("title", habits.getItem()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


            }

            if (habits.getItem().equals("Start project")) {
                context.startActivity(new Intent(context, DetailedActivity.class)
                        .putExtra("habit", "PROJECT")
                        .putExtra("title", habits.getItem()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


            }

            if (habits.getItem().equals("Learn new Language")) {
                context.startActivity(new Intent(context, DetailedActivity.class)
                        .putExtra("habit", "LANG")
                        .putExtra("title", habits.getItem()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


            }

            if (habits.getItem().equals("Working out")) {
                context.startActivity(new Intent(context, DetailedActivity.class)
                        .putExtra("habit", "GYM")
                        .putExtra("title", habits.getItem()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


            }

            if (habits.getItem().equals("Waking-up time")) {

            }

            if (habits.getItem().equals("Going for a run")) {
                context.startActivity(new Intent(context, DetailedActivity.class)
                        .putExtra("habit", "RUNNING")
                        .putExtra("title", habits.getItem()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            }


            if (habits.getItem().equals("Work on Martial Arts")) {
                context.startActivity(new Intent(context, DetailedActivity.class)
                        .putExtra("habit", "MARTIALARTS")
                        .putExtra("title", habits.getItem()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


            }

            if (!habits.getItem().equals("Reading books") &&
                    !habits.getItem().equals("Working out") &&
                    !habits.getItem().equals("Waking-up time") &&
                    !habits.getItem().equals("Going for a run") &&
                    !habits.getItem().equals("Work on Martial Arts") &&
                    !habits.getItem().equals("Start project") &&
                    !habits.getItem().equals("Learn new Language")) {
                context.startActivity(new Intent(context, DetailedActivity.class).putExtra("habit", "CUSTOM")
                        .putExtra("title", habits.getItem()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


            }
        });

        if (habits.getItem().equals("Reading books")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_study_research));
            holder.titletxt.setText("Daily reading");
            holder.subT.setText("Read challenge set to complete");
        }


        if (habits.getItem().equals("Start project")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_work_history_24));
            holder.titletxt.setText("Start project");
            holder.subT.setText("On a mission to finish it");
        }

        if (habits.getItem().equals("Learn new Language")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_language_24));
            holder.titletxt.setText("Learn new Language");
            holder.subT.setText("Have to complete");
        }

        if (habits.getItem().equals("Working out")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.tried_png));
            holder.imageofitem.setImageTintList(ContextCompat.getColorStateList(context, R.color.newlyaddedcolor2));
            holder.titletxt.setText("Daily workout");
            holder.subT.setText("Workout challenge set to complete");
        }

        if (habits.getItem().equals("Waking-up time")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_alarm));
            holder.titletxt.setText("Daily Waking time");
            holder.subT.setText("Waking up challenge");
        }

        if (habits.getItem().equals("Going for a run")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_workout));
            holder.titletxt.setText("Daily Running");
            holder.imageofitem.setImageTintList(ContextCompat.getColorStateList(context, R.color.newColor));
            holder.subT.setText("Going for a run challenge");
        }

        if (habits.getItem().equals("Work on Martial Arts")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_martial_arts));
            holder.imageofitem.setImageTintList(ContextCompat.getColorStateList(context, R.color.newlyaddedcolor1));
            holder.titletxt.setText("Daily Martial arts");
            holder.subT.setText("Working on martial arts challenge");
        }

        if (!habits.getItem().equals("Reading books") &&
                !habits.getItem().equals("Working out") &&
                !habits.getItem().equals("Waking-up time") &&
                !habits.getItem().equals("Going for a run") &&
                !habits.getItem().equals("Work on Martial Arts") &&
                !habits.getItem().equals("Start project") &&
                !habits.getItem().equals("Learn new Language")) {
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_dashboard_customize_24));
            holder.titletxt.setText(habits.getItem());
            holder.subT.setText("Set to complete");
            return;
        }
    }

    @Override
    public int getItemCount() {
        return forHabitsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titletxt, subT;
        ImageView imageofitem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titletxt = itemView.findViewById(R.id.titletxt);
            subT = itemView.findViewById(R.id.subT);
            imageofitem = itemView.findViewById(R.id.imageofitem);
        }
    }
}
