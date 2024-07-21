package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import devydev.mirror.net.R;
import front_page.AppSparedActivity;
import model.SpareTime;

public class CollectionList extends RecyclerView.Adapter<CollectionList.ViewHolder> {

    Context context;
    List<SpareTime> spareTimeList;
    private String[] data;
    private Random random = new Random();


    public CollectionList(Context context, List<SpareTime> spareTimeList) {
        this.context = context;
        this.spareTimeList = spareTimeList;
    }

    @NonNull
    @Override
    public CollectionList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.carousel, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CollectionList.ViewHolder holder, int position) {
        SpareTime spareTime = spareTimeList.get(position);
        holder.collname.setText(spareTime.getName());

//        holder.appLevel.setText(spareTime.getLevel());

        int x = Integer.parseInt(spareTime.getMinutes());
        int y = Integer.parseInt(spareTime.getHours());


        // Generate a color based on the position
        float[] hsv = new float[3];
        hsv[0] = (position * 35) % 361; // Hue
        hsv[1] = 0.5f; // Saturation
        hsv[2] = 0.8f; // Value

        // Set the background color of the item
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.drawable_layout);
        assert drawable != null;
        drawable.setColor(Color.HSVToColor(hsv));
        holder.color.setBackground(drawable);

        int colorInt = Color.HSVToColor(hsv);
        String colorString = Integer.toString(colorInt);

        // Split the string by commas and get the size of the resulting array
        String[] items = spareTime.getPackage().split(", ");
        int size = items.length;

        ArrayList<String> list = new ArrayList<>(Arrays.asList(spareTime.getPackage().split(",\\s*")));

        if (size == 1) {
            holder.noOfApps.setText("" + size + " App");
        } else {
            holder.noOfApps.setText("" + size + " Apps");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, AppSparedActivity.class)
                        .putExtra("hours", String.valueOf(y))
                        .putExtra("minutes", String.valueOf(x))
                        .putExtra("spareid", spareTime.getPushid())
                        .putExtra("itemsapps", items)
                        .putStringArrayListExtra("key", list)
                        .putExtra("color", colorString)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public int getItemCount() {
        return spareTimeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView collname, noOfApps;
        //        TextView timeset, appLevel;
        RelativeLayout color;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            color = itemView.findViewById(R.id.color);
            collname = itemView.findViewById(R.id.collname);
//            timeset = itemView.findViewById(R.id.timeset);
            noOfApps = itemView.findViewById(R.id.noOfApps);
//            appLevel = itemView.findViewById(R.id.appLevel);
//            collname = itemView.findViewById(R.id.collname);
        }
    }
}
