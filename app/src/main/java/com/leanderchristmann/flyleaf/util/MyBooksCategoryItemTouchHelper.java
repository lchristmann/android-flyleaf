package com.leanderchristmann.flyleaf.util;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leanderchristmann.flyleaf.R;
import com.leanderchristmann.flyleaf.adapters.MyBooksAdapter;

public class MyBooksCategoryItemTouchHelper extends ItemTouchHelper.Callback {

    private final MyBooksCategoryItemTouchHelperAdapter myBooksCategoryItemTouchHelperAdapter;

    public MyBooksCategoryItemTouchHelper(MyBooksCategoryItemTouchHelperAdapter myBooksCategoryItemTouchHelperAdapter) {
        this.myBooksCategoryItemTouchHelperAdapter = myBooksCategoryItemTouchHelperAdapter;
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

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.drag_highlighting));
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        //cast the generic viewholder to specific viewHolder methods can be applied
        MyBooksAdapter.ViewHolder holder  = (MyBooksAdapter.ViewHolder) viewHolder;

        //if category is expanded make undraggable
        if(holder.isCategoryExpanded())
            dragFlags = 0;

        //if category is not empty make unswipeable
        if(!holder.isCategoryEmpty())
            swipeFlags = 0;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        myBooksCategoryItemTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        myBooksCategoryItemTouchHelperAdapter.onItemSwiped(viewHolder.getAdapterPosition());
    }
}
