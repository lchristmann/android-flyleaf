package com.leanderchristmann.flyleaf.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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

import com.google.gson.Gson;
import com.leanderchristmann.flyleaf.R;
import com.leanderchristmann.flyleaf.db.ToReadDatabaseHelper;
import com.leanderchristmann.flyleaf.models.Book;
import com.leanderchristmann.flyleaf.util.VarCatItemTouchHelperAdapter;

import java.util.ArrayList;

public class VarCatAdapter extends RecyclerView.Adapter implements VarCatItemTouchHelperAdapter {

    private final Context context;
    private ArrayList<Book> varCatBooksList;
    private String currentCategoryName;
    private OnVarCatClickListener onVarCatClickListener;
    private ItemTouchHelper itemTouchHelper;

    public VarCatAdapter(Context context, ArrayList<Book> varCatBooksList, String currentCategoryName, OnVarCatClickListener onVarCatClickListener) {
        this.context = context;
        this.varCatBooksList = varCatBooksList;
        this.onVarCatClickListener = onVarCatClickListener;
        this.currentCategoryName = currentCategoryName;
    }
    //later private ItemTouchHelper itemTouchHelper;

    @Override
    public int getItemViewType(int position) {
        if (position < varCatBooksList.size()-1)
            return 0;
        return 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {
        private final TextView varCatBookTitleTV;
        private final TextView varCatBookAuthorTV;
        OnVarCatClickListener onVarCatClickListener;
        GestureDetector gestureDetector;

        public ViewHolder(@NonNull View itemView, OnVarCatClickListener onVarCatClickListener) {
            super(itemView);

            varCatBookTitleTV = itemView.findViewById(R.id.smallBookTitleTV);
            varCatBookAuthorTV = itemView.findViewById(R.id.smallBookAuthorTV);
            this.onVarCatClickListener = onVarCatClickListener;

            gestureDetector = new GestureDetector(itemView.getContext(), this);

            itemView.setOnTouchListener(this);
        }

        TextView getVarCatBookTitleTV() {
            return varCatBookTitleTV;
        }

        TextView getVarCatBookAuthorTV() {
            return varCatBookAuthorTV;
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
            onVarCatClickListener.onVarCatClick(getAdapterPosition());
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

        private final ImageView varCatAddBookItemImgV;
        OnVarCatClickListener onVarCatClickListener;
        GestureDetector gestureDetector;

        public ViewHolderPlus(@NonNull View itemView, OnVarCatClickListener onVarCatClickListener) {
            super(itemView);

            varCatAddBookItemImgV = itemView.findViewById(R.id.smallAddBookItemImgV);
            this.onVarCatClickListener = onVarCatClickListener;

            gestureDetector = new GestureDetector(itemView.getContext(), this);

            itemView.setOnTouchListener(this);
        }

        ImageView getVarCatAddBookItemImgV() {
            return varCatAddBookItemImgV;
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
            onVarCatClickListener.onVarCatClick(getAdapterPosition());
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
            view = layoutInflater.inflate(R.layout.custom_layout_small_book_item, parent, false);
            return new ViewHolder(view, onVarCatClickListener);
        }

        view = layoutInflater.inflate(R.layout.custom_layout_small_book_item, parent, false);
        return new ViewHolderPlus(view, onVarCatClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.getVarCatBookTitleTV().setText(varCatBooksList.get(position).getTitle());
            viewHolder.getVarCatBookAuthorTV().setText(varCatBooksList.get(position).getAuthor());
        } else {
            ViewHolderPlus viewHolderPlus = (ViewHolderPlus) holder;
            viewHolderPlus.getVarCatAddBookItemImgV().setImageResource(R.drawable.ic_add);
        }
    }

    @Override
    public int getItemCount() {
        if (varCatBooksList.isEmpty())
            return 0;
        return varCatBooksList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //moving in list and notifying adapter
        Book fromBook = varCatBooksList.get(fromPosition);
        varCatBooksList.remove(fromPosition);
        varCatBooksList.add(toPosition, fromBook);
        this.notifyItemMoved(fromPosition, toPosition);

        //update list in SharedPrefs list as well
        updateVarCatListInSharedPrefs();
    }

    @Override
    public void onItemSwiped(int position) {
        //removing in list and notifying adapter
        Log.d("LS B4 REMOVE: ", String.valueOf(varCatBooksList.size()));
        for (int i = 0; i < varCatBooksList.size(); i++) {
            Log.d("LS B4 REMOVE", varCatBooksList.get(i).getTitle() + " " + varCatBooksList.get(i).getAuthor());
        }
        varCatBooksList.remove(position);
        this.notifyItemRemoved(position);
        //notifyItemRangeChanged(position, varCatBooksList.size());
        Log.d("LS AFTER REMOVE: ", String.valueOf(varCatBooksList.size()));     //TODO RESULT: book adding doesnt go to varCatBookList! thats causes the issue. find where book added to varCat
        for (int i = 0; i < varCatBooksList.size(); i++) {
            Log.d("LS AFTER REMOVE", varCatBooksList.get(i).getTitle() + " " + varCatBooksList.get(i).getAuthor());
        }

        //update list in SharedPrefs as well
        updateVarCatListInSharedPrefs();
    }

    private void updateVarCatListInSharedPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.leanderchristmann.flyleaf", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        ArrayList<Book> varCatListWithoutLastAddItem = new ArrayList<>();
        for (int i = 0; i < varCatBooksList.size()-1; i++) {
            varCatListWithoutLastAddItem.add(varCatBooksList.get(i));
        }
        String updatedJsonList = gson.toJson(varCatListWithoutLastAddItem);
        editor.putString(currentCategoryName, updatedJsonList).apply();
    }

    public interface OnVarCatClickListener{
        void onVarCatClick(int position);
    }
    
    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    /*public void setVarCatBooksList(ArrayList<Book> varCatBooksList){ thought this could remove the issue but didnt. just an idea, thats how updating possible
        this.varCatBooksList = varCatBooksList;
    }*/
}
