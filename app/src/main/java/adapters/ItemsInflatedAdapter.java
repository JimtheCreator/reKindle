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

public class ItemsInflatedAdapter extends RecyclerView.Adapter<ItemsInflatedAdapter.ViewHolder> {

    List<String> itemsClassList;
    Context context;

    public ItemsInflatedAdapter(List<String> itemsClassList, Context context) {
        this.itemsClassList = itemsClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemsInflatedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lists, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsInflatedAdapter.ViewHolder holder, int position) {

        holder.itemput.setText(itemsClassList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return itemsClassList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView itemput;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemput = itemView.findViewById(R.id.itemput);
        }
    }
}
