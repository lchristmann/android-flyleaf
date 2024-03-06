package com.leanderchristmann.flyleaf.adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leanderchristmann.flyleaf.R;
import com.leanderchristmann.flyleaf.db.ToBuyDatabaseHelper;
import com.leanderchristmann.flyleaf.models.Book;
import com.leanderchristmann.flyleaf.util.ToBuyItemTouchHelperAdapter;

import java.util.ArrayList;

public class ToBuyAdapter extends RecyclerView.Adapter implements ToBuyItemTouchHelperAdapter {

    private final Context context;
    private ArrayList<Book> toBuyBooksList;
    private OnBookToBuyClickListener onBookToBuyClickListener;
    private ItemTouchHelper itemTouchHelper;

    public ToBuyAdapter(Context context, ArrayList<Book> toBuyBooksList, OnBookToBuyClickListener onBookToBuyClickListener) {
        this.context = context;
        this.toBuyBooksList = toBuyBooksList;
        this.onBookToBuyClickListener = onBookToBuyClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < toBuyBooksList.size() -1)
            return 0;
        return 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {

        private final TextView toBuyBookTitleTV;
        private final TextView toBuyBookAuthorTV;
        OnBookToBuyClickListener onBookToBuyClickListener;
        GestureDetector gestureDetector;

        public ViewHolder(@NonNull View itemView, OnBookToBuyClickListener onBookToBuyClickListener) {
            super(itemView);

            toBuyBookTitleTV = itemView.findViewById(R.id.bookTitleTV);
            toBuyBookAuthorTV = itemView.findViewById(R.id.bookAuthorTV);
            this.onBookToBuyClickListener = onBookToBuyClickListener;

            gestureDetector = new GestureDetector(itemView.getContext(), this);

            itemView.setOnTouchListener(this);
        }

        TextView getToBuyBookTitleTV() {
            return toBuyBookTitleTV;
        }

        TextView getToBuyBookAuthorTV() {
            return toBuyBookAuthorTV;
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            onBookToBuyClickListener.onBookToBuyClick(getAdapterPosition());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            itemTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return true;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    }

    public class ViewHolderPlus extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {

        private final ImageView toBuyAddBookItemImgV;
        OnBookToBuyClickListener onBookToBuyClickListener;
        GestureDetector gestureDetector;

        public ViewHolderPlus(@NonNull View itemView, OnBookToBuyClickListener onBookToBuyClickListener) {
            super(itemView);

            toBuyAddBookItemImgV = itemView.findViewById(R.id.addBookItemImgV);
            this.onBookToBuyClickListener = onBookToBuyClickListener;

            gestureDetector = new GestureDetector(itemView.getContext(), this);

            itemView.setOnTouchListener(this);
        }

        ImageView getToBuyAddBookItemImgV() {
            return toBuyAddBookItemImgV;
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            onBookToBuyClickListener.onBookToBuyClick(getAdapterPosition());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if(viewType == 0) {
            view = layoutInflater.inflate(R.layout.custom_layout_book_item, parent, false);
            return new ViewHolder(view, onBookToBuyClickListener);
        }

        view = layoutInflater.inflate(R.layout.custom_layout_book_item, parent, false);
        return new ViewHolderPlus(view, onBookToBuyClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.getToBuyBookTitleTV().setText(toBuyBooksList.get(position).getTitle());
            viewHolder.getToBuyBookAuthorTV().setText(toBuyBooksList.get(position).getAuthor());
        } else {
            ViewHolderPlus viewHolderPlus = (ViewHolderPlus) holder;
            viewHolderPlus.getToBuyAddBookItemImgV().setImageResource(R.drawable.ic_add);
        }
    }

    @Override
    public int getItemCount() {
        if (toBuyBooksList.isEmpty())
            return 0;
        return toBuyBooksList.size();
    }

    public interface OnBookToBuyClickListener{
        void onBookToBuyClick(int position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //moving in list and notifying adapter
        Book fromBook = toBuyBooksList.get(fromPosition);
        toBuyBooksList.remove(fromPosition);
        toBuyBooksList.add(toPosition, fromBook);
        this.notifyItemMoved(fromPosition, toPosition);
        //make new SQL table
        ToBuyDatabaseHelper toBuyDB = new ToBuyDatabaseHelper(context);
        toBuyDB.dropTableAndMakeNew();
        toBuyDB.fillTheFreshTable(toBuyBooksList);
    }

    @Override
    public void onItemSwiped(int position) {
        //removing in list and notifying adapter
        toBuyBooksList.remove(position);
        this.notifyItemRemoved(position);
        //make new SQL table
        ToBuyDatabaseHelper toBuyDB = new ToBuyDatabaseHelper(context);
        toBuyDB.dropTableAndMakeNew();
        toBuyDB.fillTheFreshTable(toBuyBooksList);
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }
}
