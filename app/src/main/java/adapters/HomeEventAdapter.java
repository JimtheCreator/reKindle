package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import devydev.mirror.net.R;
import model.Journal;
import space.SpacebetweenItems;

public class HomeEventAdapter extends RecyclerView.Adapter<HomeEventAdapter.ViewHolder> {

    Context context;

    List<String> stringList;

    Activity activity;

    RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public HomeEventAdapter(Context context, List<String> stringList, Activity activity) {
        this.context = context;
        this.stringList = stringList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String habit = activity.getIntent().getStringExtra("Habit"); //TICK
//        String hallostring = "hallo";
//        String asubstring = hallostring.substring(0, 1);

        List<Journal> journalList = new ArrayList<>();

        holder.day_for_event.setText(stringList.get(position));

        SpacebetweenItems spacebetweenItems = new SpacebetweenItems(20);

        NestedRecyclerviewAdapter nestedRecyclerviewAdapter = new NestedRecyclerviewAdapter(journalList, holder.recyclerview.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.recyclerview.getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setInitialPrefetchItemCount(journalList.size());
        holder.recyclerview.setLayoutManager(linearLayoutManager);
        holder.recyclerview.setAdapter(nestedRecyclerviewAdapter);
        holder.recyclerview.setRecycledViewPool(viewPool);
        holder.recyclerview.addItemDecoration(spacebetweenItems);


        displayJournals(habit, stringList.get(position), nestedRecyclerviewAdapter, journalList);
    }

    @Override
    public int getItemCount() {
//        if (eventList.size()>limit){
//            return limit;
//        }else {
//            return eventList.size();
//        }

//        return stringList.size() + journalList.size();

        return stringList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView day_for_event;
        RecyclerView recyclerview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

//            date_for_event = itemView.findViewById(R.id.date_for_event);
            day_for_event = itemView.findViewById(R.id.day_number);
            recyclerview = itemView.findViewById(R.id.recyclerview);
//            month = itemView.findViewById(R.id.month);

        }
    }


    void displayJournals(String habit, String snapshot, NestedRecyclerviewAdapter homeEventAdapter, List<Journal> journalList) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Journal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(habit)
                .child(snapshot);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                journalList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Journal journal = dataSnapshot.getValue(Journal.class);
                    journalList.add(journal);
                }


                Collections.reverse(journalList);
                homeEventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
