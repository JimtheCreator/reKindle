package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import devydev.mirror.net.R;
import model.Journal;

public class MiniJournalAdapter extends RecyclerView.Adapter<MiniJournalAdapter.ViewHolder> {

    int limit = 3;
    List<Journal> journalList;
    Context context;

    public MiniJournalAdapter(List<Journal> journalList, Context context) {
        this.journalList = journalList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.displayjournals, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Journal journal = journalList.get(position);
        holder.eventName.setText(journal.getWrittentxt());
    }

    @Override
    public int getItemCount() {
        if (journalList.size() > limit) {
            return limit;
        } else {
            return journalList.size();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView eventName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventName = itemView.findViewById(R.id.eventName);
        }
    }
}
