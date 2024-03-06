package com.leanderchristmann.flyleaf.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leanderchristmann.flyleaf.R;
import com.leanderchristmann.flyleaf.adapters.ToReadAdapter;

public class ToReadItemTouchHelper extends ItemTouchHelper.Callback {

    private final ToReadItemTouchHelperAdapter toReadItemTouchHelperAdapter;

    public ToReadItemTouchHelper(ToReadItemTouchHelperAdapter toReadItemTouchHelperAdapter) {
        this.toReadItemTouchHelperAdapter = toReadItemTouchHelperAdapter;
    }

    //disable that, because we want to handle the drag inside of the ViewHolder of the RecyclerView
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    //return true, because I want to be able to swipe the views
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    //when I let go of dragged item, clear view is going to be called   | colors is set back to normal
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.white));
    }

    //change the color to a lighter grey when dragged
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.drag_highlighting));
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ToReadAdapter.ViewHolderPlus) return 0;
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        if (target.getItemViewType() == 1) return false;
        toReadItemTouchHelperAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        toReadItemTouchHelperAdapter.onItemSwiped(viewHolder.getAdapterPosition());
    }
}
