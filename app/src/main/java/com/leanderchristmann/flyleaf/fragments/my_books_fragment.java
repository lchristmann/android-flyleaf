package com.leanderchristmann.flyleaf.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leanderchristmann.flyleaf.R;
import com.leanderchristmann.flyleaf.adapters.MyBooksAdapter;
import com.leanderchristmann.flyleaf.models.Book;
import com.leanderchristmann.flyleaf.models.Category;
import com.leanderchristmann.flyleaf.models.CustomTFSpan;
import com.leanderchristmann.flyleaf.util.MyBooksCategoryItemTouchHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class my_books_fragment extends Fragment{

    //layout elements
    private RecyclerView myBooksRecyclerView;
    private FloatingActionButton addCategoryButton;

    //vars
    ArrayList<Category> categoriesList = new ArrayList<>();
    private MyBooksAdapter myBooksAdapter;

    //utils
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public my_books_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_books_fragment, container, false);

        myBooksRecyclerView = view.findViewById(R.id.myBooksRecyclerView);
        addCategoryButton = view.findViewById(R.id.floatingActionButton);

        //categoriesList is READ from SharedPrefs, if exists
        sharedPreferences = this.getActivity().getSharedPreferences("com.leanderchristmann.flyleaf", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.contains("categoriesList")) {
            String serializedList = sharedPreferences.getString("categoriesList", null);
            Type type = new TypeToken<List<Category>>(){}.getType();
            categoriesList = gson.fromJson(serializedList, type);
            //sets all categories to not expanded when app when this fragment is created
            for (int i = 0; i < categoriesList.size(); i++) {
                categoriesList.get(i).setExpanded(false);
            }
        }

        initMyBooksRecyclerView();

        //onClickListener for FloatingACtionButton
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCategoryDialog();
            }
        });

        return view;
    }

    private void initMyBooksRecyclerView(){
        //create a LayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        myBooksRecyclerView.setLayoutManager(layoutManager);

        myBooksAdapter = new MyBooksAdapter(getContext(), categoriesList);

        //the itemTouchHelper(swipeDeleting, moving Items by drag&drop while highlighting)
        ItemTouchHelper.Callback callback = new MyBooksCategoryItemTouchHelper(myBooksAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        myBooksAdapter.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(myBooksRecyclerView);

        myBooksRecyclerView.setAdapter(myBooksAdapter);
    }

    private void showAddCategoryDialog() {

        //STEP 1: show Alert Dialog for user to input category name or cancel
        //START START START ALERT DIALOG START START START ALERT DIALOG START START START ALERT DIALOG
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //set the title to right font
        String alertDialogTitle = "New Category";
        Typeface tf = ResourcesCompat.getFont(getContext(), R.font.montserrat);
        CustomTFSpan tfSpan = new CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(alertDialogTitle);
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(spannableString);

        //set positive and negative button text font
        String positiveButtonText = getResources().getString(R.string.ok);
        String negativeButtonText = getResources().getString(R.string.cancel);
        SpannableString spannableStringPositive = new SpannableString(positiveButtonText);
        SpannableString spannableStringNegative = new SpannableString(negativeButtonText);
        spannableStringPositive.setSpan(tfSpan, 0, spannableStringPositive.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringNegative.setSpan(tfSpan, 0, spannableStringNegative.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //set the text input layout
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.category_title_dialog, (ViewGroup) getView(), false);
        //declare and init the editText
        final EditText titleEditText = (EditText) viewInflated.findViewById(R.id.categoryTitleEditText);
        builder.setView(viewInflated);

        //set up the buttons
        builder.setPositiveButton(spannableStringPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //STEP 2: create the Category in categoriesList and put it in SharedPrefs
                String newCategoryName = titleEditText.getText().toString();

                if(newCategoryName.isEmpty())
                    return;

                if(containsListName(categoriesList, newCategoryName)) {
                    Toast.makeText(getContext(), "Category already exists!", Toast.LENGTH_LONG).show();
                    return;
                }

                //ADD titleInput to CategoryList
                Category categoryToAdd = new Category(newCategoryName);
                categoriesList.add(categoryToAdd);

                //put updated categoriesList in SharedPrefs
                String categoriesListToJson = gson.toJson(categoriesList);
                editor.putString("categoriesList", categoriesListToJson).apply();

                //create varCatBooksList (a space in sharedpreferences with that list)
                ArrayList<Book> emptyNewCategoryList = new ArrayList<>();
                String emptyNewCategoryListJson = gson.toJson(emptyNewCategoryList);
                editor.putString(newCategoryName, emptyNewCategoryListJson).apply();

                //notifyAdapterSetChanged (did work without, but to be sure...)
                myBooksAdapter.notifyItemInserted(categoriesList.size()-1);
                myBooksAdapter.notifyItemRangeChanged(categoriesList.size()-1, categoriesList.size());
            }
        });
        builder.setNegativeButton(spannableStringNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
        //END END END ALERT DIALOG END END END ALERT DIALOG END END END ALERT DIALOG END END END
    }

    private static boolean containsListName(ArrayList<Category> categoriesList, String listName){
        for(Category category : categoriesList) {
            if(category != null && category.getListName().equals(listName))
                return true;
        }
        return false;
    }
}