package com.leanderchristmann.flyleaf.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leanderchristmann.flyleaf.R;
import com.leanderchristmann.flyleaf.models.Book;
import com.leanderchristmann.flyleaf.models.Category;
import com.leanderchristmann.flyleaf.models.CustomTFSpan;
import com.leanderchristmann.flyleaf.util.MyBooksCategoryItemTouchHelperAdapter;
import com.leanderchristmann.flyleaf.util.VarCatItemTouchHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyBooksAdapter extends RecyclerView.Adapter<MyBooksAdapter.ViewHolder> implements MyBooksCategoryItemTouchHelperAdapter, VarCatAdapter.OnVarCatClickListener {

    //declare everything needed to fill the layout
    private final Context context;
    private ArrayList<Category> categoriesList;
    private ArrayList<Book> varCatBooksList;
    private String currentVarCatName;
    private ItemTouchHelper itemTouchHelper;
    private ViewGroup parent;
    private VarCatAdapter varCatAdapter;
    //TODO test
    private RecyclerView globalExpandedCategoryRecyclerView;

    public MyBooksAdapter(Context context, ArrayList<Category> categoriesList) {
        this.context = context;
        this.categoriesList = categoriesList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {
        private final TextView categoryItemTitleTextView;
        ConstraintLayout expandedCategoryLayout;
        RecyclerView expandedCategoryRecyclerView;
        GestureDetector gestureDetector;
        boolean categoryExpanded; //needed to make category unmovable when expanded and making the varCatRecyclerView (the inner one) able to receive the long press and drag gesture
        boolean categoryEmpty; //needed to make category unswipeable when not empty | determined with sharedprefs lits of that var category & works bcs SharedPrefs list is kept always up to date on item add, move or remove

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryItemTitleTextView = itemView.findViewById(R.id.categoryItemTitleTextView);
            expandedCategoryLayout = itemView.findViewById(R.id.expandedCategoryLayout);
            expandedCategoryRecyclerView = itemView.findViewById(R.id.expandedCategoryRecyclerView);
            //TODO test
            globalExpandedCategoryRecyclerView = itemView.findViewById(R.id.expandedCategoryRecyclerView);
            this.categoryExpanded = false;

            gestureDetector = new GestureDetector(itemView.getContext(), this);

            itemView.setOnTouchListener(this);
        }

        TextView getCategoryItemTitleTextView() {
            return categoryItemTitleTextView;
        }

        ConstraintLayout getExpandedCategoryLayout() { //todo explayout
            return expandedCategoryLayout;
        }

        //getter method to use in MyBooksItemTouchHelper to make movable based on that (expanded true->not draggable)
        public boolean isCategoryExpanded() {
            return categoryExpanded;
        }

        //setter method to set in onBindViewHolder every time category gets expanded or collapsed
        public void setCategoryExpanded(boolean categoryEmpty) {
            this.categoryExpanded = categoryEmpty;
        }

        public boolean isCategoryEmpty() {
            return categoryEmpty;
        }

        public void setCategoryEmpty(boolean categoryEmpty) {
            this.categoryEmpty = categoryEmpty;
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
            Category category = categoriesList.get(getAdapterPosition());
            category.setExpanded(!category.isExpanded());
            notifyItemChanged(getAdapterPosition());
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

    @NonNull
    @Override
    public MyBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //save parent globally so I can inflate new book dialog on that parent at the very bottom of this code
        this.parent = parent;

        //Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_layout_my_books_category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyBooksAdapter.ViewHolder holder, int position) {
        holder.getCategoryItemTitleTextView().setText(categoriesList.get(position).getListName());

        //handling the expandable part of the item
        boolean isExpanded = categoriesList.get(position).isExpanded();
        if (isExpanded) {
            currentVarCatName = categoriesList.get(position).getListName();
            //make SharedPrefs connection to get varCat data
            SharedPreferences sharedPreferences = context.getSharedPreferences("com.leanderchristmann.flyleaf", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            //READ arraylist of this category
            String jsonListFromSharedPrefs = sharedPreferences.getString(currentVarCatName, null);
            Type type = new TypeToken<List<Book>>(){}.getType();
            varCatBooksList = gson.fromJson(jsonListFromSharedPrefs, type);

            varCatBooksList.add(new Book("addIconOnLastBookListPosition", "addIconOnLAstBookListPosition"));

            //creating and setting layout manager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            holder.expandedCategoryRecyclerView.setLayoutManager(layoutManager);


            varCatAdapter = new VarCatAdapter(context, varCatBooksList, currentVarCatName, this);

            ItemTouchHelper.Callback callback = new VarCatItemTouchHelper(varCatAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            varCatAdapter.setItemTouchHelper(itemTouchHelper);
            itemTouchHelper.attachToRecyclerView(holder.expandedCategoryRecyclerView);

            holder.expandedCategoryRecyclerView.setAdapter(varCatAdapter);
        }

        //setting isEmpty property
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.leanderchristmann.flyleaf", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String serializedVarCatList = sharedPreferences.getString(categoriesList.get(position).getListName(), null);
        Type type = new TypeToken<List<Book>>(){}.getType();
        ArrayList<Book> varCatArrayList = gson.fromJson(serializedVarCatList, type);
        holder.setCategoryEmpty(varCatArrayList.isEmpty());

        //setting the ViewHolder expanded property
        if(isExpanded) {
            holder.setCategoryExpanded(true);
        } else if (!isExpanded) {
            holder.setCategoryExpanded(false);
        }

        holder.getExpandedCategoryLayout().setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onVarCatClick(int position) {
        if (position == varCatBooksList.size() - 1)
            showAddToVarCatListDialog();
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }   //POSSIBLE ERROR fixed by if(categoriesList.isEmpty) return 0;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Category fromCategory = categoriesList.get(fromPosition);
        categoriesList.remove(fromPosition);
        categoriesList.add(toPosition, fromCategory);
        this.notifyItemMoved(fromPosition, toPosition);
        //update categoriesList in SharedPreferences
        updateCategoriesListInSharedPrefs();
    }

    @Override
    public void onItemSwiped(int position) {
        String categoryName = categoriesList.get(position).getListName();
        categoriesList.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, categoriesList.size());
        //update categoriesList in SharedPreferences AND remove the list of the specific category from SharedPrefs
        updateCategoriesListInSharedPrefs(categoryName);
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    //method updateCategoriesListInSharedPrefs is overloaded: can be called with no or one parameter, and does more when called with a parameter
    private void updateCategoriesListInSharedPrefs() {
        //make SharedPrefs connection, make Gson object, convert the list to jsonString, put it in SharedPrefs
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.leanderchristmann.flyleaf", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String updatedListToJson = gson.toJson(categoriesList);
        editor.putString("categoriesList", updatedListToJson).apply();
    }

    private void updateCategoriesListInSharedPrefs(String categoryName) {
        //make SharedPrefs connection, make Gson object, convert the list to jsonString, put it in SharedPrefs
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.leanderchristmann.flyleaf", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String updatedListToJson = gson.toJson(categoriesList);
        editor.putString("categoriesList", updatedListToJson).apply();

        //remove the specific category list
        editor.remove(categoryName).apply();
    }

    private void showAddToVarCatListDialog() {
        //STEP 1: show Dialog with input & ok/cancel
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //set the title in montserrat font
        String alertDialogTitle = "New Book";
        Typeface tf = ResourcesCompat.getFont(context, R.font.montserrat);
        CustomTFSpan tfSpan = new CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(alertDialogTitle);
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(spannableString);

        //set positive and negative button in montserrat font
        String positiveButtonText = context.getResources().getString(R.string.ok);
        String negativeButtonText = context.getResources().getString(R.string.cancel);
        SpannableString spannableStringPositive = new SpannableString(positiveButtonText);
        SpannableString spannableStringNegative = new SpannableString(negativeButtonText);
        spannableStringPositive.setSpan(tfSpan, 0, spannableStringPositive.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringNegative.setSpan(tfSpan, 0, spannableStringNegative.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //set the text input layout
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.new_book_dialog, (ViewGroup) parent, false);

        builder.setView(viewInflated);

        //declare and init the editTexts
        EditText bookTitleEditText = viewInflated.findViewById(R.id.bookTitleEditText);
        EditText bookAuthorEditText = viewInflated.findViewById(R.id.bookAuthorEditText);

        //set up the buttons
        builder.setPositiveButton(spannableStringPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //STEP 2: add the book to toReadBooksList
                String bookTitleInput = bookTitleEditText.getText().toString();
                String bookAuthorInput = bookAuthorEditText.getText().toString();

                if (bookTitleInput.isEmpty() || bookTitleInput.trim().length() == 0)
                    return;

                Book bookToAdd = new Book(bookTitleInput, bookAuthorInput);

                varCatBooksList.add((varCatBooksList.size() - 1), bookToAdd);
                Log.d("INFO LS: ", "THIS IS THE LIST AFTER ADDING IN FRONTEND");
                for (int j = 0; j < varCatBooksList.size(); j++) {
                    Log.d("LS FRONTEND", varCatBooksList.get(j).getTitle() + " " + varCatBooksList.get(j).getAuthor());
                }

                varCatAdapter.notifyItemInserted(varCatBooksList.size()-1);
                varCatAdapter.notifyItemRangeChanged(varCatBooksList.size()-1, varCatBooksList.size());
                Log.d("LS AFTER ADD: ", String.valueOf(varCatBooksList.size()));

                //update the varCatList in SharedPreferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("com.leanderchristmann.flyleaf", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                ArrayList<Book> varCatListWithoutLastAddItem = new ArrayList<>();
                for (int j = 0; j < varCatBooksList.size() - 1; j++) {
                    varCatListWithoutLastAddItem.add(new Book(varCatBooksList.get(j).getTitle(), varCatBooksList.get(j).getAuthor()));
                }
                Log.d("LS BACKEND", "THIS IS THE LITS ADDED TO SHAREDPREFS");
                for (int j = 0; j < varCatListWithoutLastAddItem.size(); j++) {
                    Log.d("LS BACKEND", varCatListWithoutLastAddItem.get(j).getTitle() + " " + varCatListWithoutLastAddItem.get(j).getAuthor());
                }
                String updatedVarCatList = gson.toJson(varCatListWithoutLastAddItem);
                editor.putString(currentVarCatName, updatedVarCatList).apply();
            }
        });
        builder.setNegativeButton(spannableStringNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }
}
