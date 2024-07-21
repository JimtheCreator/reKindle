package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import devydev.mirror.net.R;

public class WheelAdapter extends RecyclerView.Adapter<WheelAdapter.WheelViewHolder> {

    private List<String> items;

    public WheelAdapter(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public WheelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wheel, parent, false);
        return new WheelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WheelViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class WheelViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public WheelViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_item);
        }

        public void bind(String item) {
            textView.setText(item);
        }
    }
}


