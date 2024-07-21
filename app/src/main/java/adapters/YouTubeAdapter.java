package adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import devydev.mirror.net.R;
import model.YouTube;

public class YouTubeAdapter extends RecyclerView.Adapter<YouTubeAdapter.ViewHolder> {

    Context context;
    List<YouTube> youTubeList;

    public YouTubeAdapter(Context context, List<YouTube> youTubeList) {
        this.context = context;
        this.youTubeList = youTubeList;
    }

    @NonNull
    @Override
    public YouTubeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_onyt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YouTubeAdapter.ViewHolder holder, int position) {
        YouTube youTube = youTubeList.get(position);

        holder.name.setText(youTube.getName());

        Uri profile = Uri.parse(youTube.getProfilepic());
        Uri path = Uri.parse(youTube.getYoutubelink());

        Glide.with(context).load(profile).into(holder.profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(path);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return youTubeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircularImageView profile;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
        }
    }
}
