package adapters;

import android.content.Context;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.math.BigDecimal;
import java.util.List;

import devydev.mirror.net.R;
import model.Book;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    Context context;
    List<Book> bookList;

    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.bookname.setText(book.getBookname());
        holder.book_name.setText(book.getBookname());
        holder.currentpage.setText("Page "+book.getPagesread());

        int x = Integer.parseInt(book.getNoofpages());

        int y = x/30;

        BigDecimal bigDecimal = new BigDecimal(y);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN);

        holder.moredetail.setText(book.getBookname() + " has " + book.getNoofpages() + " pages. Reading " + bigDecimal + " pages everyday will help you complete it in 30 days. One day at a time. Happy Reading :)");
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookname, moredetail;
//        RelativeLayout done_forD, minus, add;


        TextView currentpage, book_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookname = itemView.findViewById(R.id.bookname);
            book_name = itemView.findViewById(R.id.viee);
            moredetail = itemView.findViewById(R.id.moredetail);
//            done_forD = itemView.findViewById(R.id.done_forD);

//            minus = itemView.findViewById(R.id.minus);
//            add = itemView.findViewById(R.id.add);
            currentpage = itemView.findViewById(R.id.currentpage);

        }
    }
}
