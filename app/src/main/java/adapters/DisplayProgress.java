package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import devydev.mirror.net.R;
import model.ForHabits;

public class DisplayProgress extends RecyclerView.Adapter<DisplayProgress.ViewHolder> {

    Context context;
    List<ForHabits> forHabitsList;

    public DisplayProgress(Context context, List<ForHabits> forHabitsList) {
        this.context = context;
        this.forHabitsList = forHabitsList;
    }

    @NonNull
    @Override
    public DisplayProgress.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.state_stateblank, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayProgress.ViewHolder holder, int position) {
        ForHabits progress = forHabitsList.get(position);

        holder.howmany.setText("" + progress.getCounter() +"/30");

        holder.itemView.setOnClickListener(view -> {
            try {
                String string_date = "02-November-2023";
                SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
                Date d = f.parse(string_date);
                long milliseconds = d.getTime();

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        if (progress.getItem().equals("Reading books")) {
            holder.mainsetup.setBackground(ContextCompat.getDrawable(context, R.drawable.colorone));
            holder.mainsetup2.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke1));
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_study_research));
            holder.imageofitem.setImageTintList(ContextCompat.getColorStateList(context, R.color.black));
        }

        if (progress.getItem().equals("Working out")) {
            holder.mainsetup.setBackground(ContextCompat.getDrawable(context, R.drawable.colortwo));
            holder.mainsetup2.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke2));
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.tried_png));
            holder.imageofitem.setImageTintList(ContextCompat.getColorStateList(context, R.color.black));

        }

        if (progress.getItem().equals("Waking-up time")) {
            holder.mainsetup.setBackground(ContextCompat.getDrawable(context, R.drawable.coloursix));
            holder.mainsetup2.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke6));
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_alarm));
            holder.imageofitem.setImageTintList(ContextCompat.getColorStateList(context, R.color.black));

        }

        if (progress.getItem().equals("Going for a run")) {
            holder.mainsetup.setBackground(ContextCompat.getDrawable(context, R.drawable.colorthree));
            holder.mainsetup2.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke3));
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_workout));
            holder.imageofitem.setImageTintList(ContextCompat.getColorStateList(context, R.color.black));

        }


        if (progress.getItem().equals("Work on Martial Arts")) {
            holder.mainsetup.setBackground(ContextCompat.getDrawable(context, R.drawable.colorfour));
            holder.mainsetup2.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke4));
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_martial_arts));
        }

        if (!progress.getItem().equals("Reading books") &&
                !progress.getItem().equals("Working out") &&
                !progress.getItem().equals("Waking-up time") &&
                !progress.getItem().equals("Going for a run") &&
                !progress.getItem().equals("Work on Martial Arts")) {
            holder.mainsetup.setBackground(ContextCompat.getDrawable(context, R.drawable.colourfive));
            holder.imageofitem.setImageTintList(ContextCompat.getColorStateList(context, R.color.black));
            holder.mainsetup2.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke5));
            holder.imageofitem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_dashboard_customize_24));

            return;
        }
    }

    @Override
    public int getItemCount() {
        return forHabitsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout mainsetup, mainsetup2;
        TextView howmany;
        ImageView imageofitem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mainsetup = itemView.findViewById(R.id.mainsetup);
            mainsetup2 = itemView.findViewById(R.id.mainsetup2);
            howmany = itemView.findViewById(R.id.howmany);
            imageofitem = itemView.findViewById(R.id.imageofitem);
        }
    }
}
