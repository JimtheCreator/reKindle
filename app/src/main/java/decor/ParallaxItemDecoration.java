package decor;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ParallaxItemDecoration extends RecyclerView.ItemDecoration {

    private final int spacing;
    private final int bottomSpacing;

    public ParallaxItemDecoration(int spacing, int bottomSpacing) {
        this.spacing = spacing;
        this.bottomSpacing = bottomSpacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        int spanCount = 1; // default span count if not using GridLayoutManager

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }

        int column = position % spanCount;

        // Calculate spacing for left and right
        outRect.left = column * spacing / spanCount;
        outRect.right = spacing - (column + 1) * spacing / spanCount;

        // Calculate spacing for top
        if (position < spanCount) {
            // For the first row, adjust the top spacing
            outRect.top = spacing;
        }

        // Adjust bottom spacing
        outRect.bottom = bottomSpacing;
    }
}





