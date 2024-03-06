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
import com.leanderchristmann.flyleaf.db.ToReadDatabaseHelper;
import com.leanderchristmann.flyleaf.models.Book;
import com.leanderchristmann.flyleaf.util.ToReadItemTouchHelperAdapter;

import java.util.ArrayList;

public class ToReadAdapter extends RecyclerView.Adapter implements ToReadItemTouchHelperAdapter {

    private final Context context;
    private ArrayList<Book> toReadBooksList;
    private OnBookToReadClickListener onBookToReadClickListener;
    private ItemTouchHelper itemTouchHelper;

    public ToReadAdapter(Context context, ArrayList<Book> toReadBooksList, OnBookToReadClickListener onBookToReadClickListener){
        this.context = context;
        this.toReadBooksList = toReadBooksList;
        this.onBookToReadClickListener = onBookToReadClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < toReadBooksList.size()-1)
            return 0;
        return 1;
    }

    //ViewHolder for books
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {
        private final TextView toReadBookTitleTV;
        private final TextView toReadBookAuthorTV;
        OnBookToReadClickListener onBookToReadClickListener;
        GestureDetector gestureDetector;

        //constructor for ViewHolder
        ViewHolder(@NonNull View itemView, OnBookToReadClickListener onBookToReadClickListener) {
            super(itemView);

            toReadBookTitleTV = itemView.findViewById(R.id.bookTitleTV);
            toReadBookAuthorTV = itemView.findViewById(R.id.bookAuthorTV);
            this.onBookToReadClickListener = onBookToReadClickListener;

            gestureDetector = new GestureDetector(itemView.getContext(), this);

            itemView.setOnTouchListener(this);
        }

        TextView getToReadBookTitleTV() {
            return toReadBookTitleTV;
        }

        TextView getToReadBookAuthorTV() {
            return toReadBookAuthorTV;
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
            onBookToReadClickListener.onBookToReadClick(getAdapterPosition());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
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

    //ViewHolder for PlusIcon
    public class ViewHolderPlus extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {

        private final ImageView toReadAddBookItemImgV;
        OnBookToReadClickListener onBookToReadClickListener;
        GestureDetector gestureDetector;

        public ViewHolderPlus(@NonNull View itemView, OnBookToReadClickListener onBookToReadClickListener) {
            super(itemView);

            toReadAddBookItemImgV = itemView.findViewById(R.id.addBookItemImgV);
            this.onBookToReadClickListener = onBookToReadClickListener;

            gestureDetector = new GestureDetector(itemView.getContext(), this);

            itemView.setOnTouchListener(this);
        }

        ImageView getToReadAddBookItemImgV() {
            return toReadAddBookItemImgV;
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
            onBookToReadClickListener.onBookToReadClick(getAdapterPosition());
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
            return new ViewHolder(view, onBookToReadClickListener);
        }

        view = layoutInflater.inflate(R.layout.custom_layout_book_item, parent, false);
        return new ViewHolderPlus(view, onBookToReadClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.getToReadBookTitleTV().setText(toReadBooksList.get(position).getTitle());
            viewHolder.getToReadBookAuthorTV().setText(toReadBooksList.get(position).getAuthor());
        } else {
            ViewHolderPlus viewHolderPlus = (ViewHolderPlus) holder;
            viewHolderPlus.getToReadAddBookItemImgV().setImageResource(R.drawable.ic_add);
        }
    }

    @Override
    public int getItemCount() {
        if (toReadBooksList.isEmpty())
            return 0;
        return toReadBooksList.size();
    }

    //best practice way for recyclerrview onClickListener: interface -> implement it in activity(to_read_fragment)
    public interface OnBookToReadClickListener{
        void onBookToReadClick(int position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //moving in list and notifying adapter
        Book fromBook = toReadBooksList.get(fromPosition);
        toReadBooksList.remove(fromPosition);
        toReadBooksList.add(toPosition, fromBook);
        this.notifyItemMoved(fromPosition, toPosition);
        //make new SQL table
        ToReadDatabaseHelper toReadDB = new ToReadDatabaseHelper(context);
        toReadDB.dropTableAndMakeNew();
        toReadDB.fillTheFreshTable(toReadBooksList);
    }

    @Override
    public void onItemSwiped(int position) {
        //removing in list and notifying adapter
        toReadBooksList.remove(position);
        this.notifyItemRemoved(position);
        //make new SQL table
        ToReadDatabaseHelper toReadDB = new ToReadDatabaseHelper(context);
        toReadDB.dropTableAndMakeNew();
        toReadDB.fillTheFreshTable(toReadBooksList);
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }
}
//way to make last item not touchable: have separate ViewHolder for the "+" and set makeMovementFlags to 0 if viewholder is instance of the "+"ViewHolder
//https://www.youtube.com/watch?v=VtnLpHUu2U0&ab_channel=yoursTRULY -> https://stackoverflow.com/questions/30713121/disable-swipe-for-position-in-recyclerview-using-itemtouchhelper-simplecallback