package com.leanderchristmann.flyleaf.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.leanderchristmann.flyleaf.R;
import com.leanderchristmann.flyleaf.adapters.ToBuyAdapter;
import com.leanderchristmann.flyleaf.db.ToBuyDatabaseHelper;
import com.leanderchristmann.flyleaf.models.Book;
import com.leanderchristmann.flyleaf.models.CustomTFSpan;
import com.leanderchristmann.flyleaf.util.ToBuyItemTouchHelper;

import java.util.ArrayList;

public class to_buy_fragment extends Fragment implements ToBuyAdapter.OnBookToBuyClickListener {

    //layout elements
    private RecyclerView toBuyRecyclerView;

    //vars
    ArrayList<Book> toBuyBooksList;
    private ToBuyAdapter toBuyAdapter;
    ToBuyDatabaseHelper toBuyDB;

    public to_buy_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_buy_fragment, container, false);

        //intialize RecyclerView and Database Helper
        toBuyRecyclerView = view.findViewById(R.id.toBuyRecyclerView);
        toBuyDB = new ToBuyDatabaseHelper(getContext());

        //initialize ArrayList and store data from database in it
        toBuyBooksList = new ArrayList<>();
        storeToBuyDbDataInArray();

        //add last position to Array for the + to show | NOTE: CARE THAT LAST ELEMENT IS THIS WHEN WRITING TO DB!
        toBuyBooksList.add(new Book("addBookItemOnLastPosition", "addBookItemOnLastPosition"));

        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        //layoutManager and Adapter
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        toBuyRecyclerView.setLayoutManager(layoutManager);

        toBuyAdapter = new ToBuyAdapter(getContext(), toBuyBooksList, this);

        //the itemTouchHelper
        ItemTouchHelper.Callback callback = new ToBuyItemTouchHelper(toBuyAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        toBuyAdapter.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(toBuyRecyclerView);

        toBuyRecyclerView.setAdapter(toBuyAdapter);
    }

    private void storeToBuyDbDataInArray() {
        Cursor cursor = toBuyDB.readAllToBuyData();
        if(cursor.getCount() == 0)
            return;
        while(cursor.moveToNext()) {
            toBuyBooksList.add(new Book(cursor.getString(1), cursor.getString(2)));
        }
    }

    @Override
    public void onBookToBuyClick(int position) {
        if (position == toBuyBooksList.size()-1)
            showAddBookToToBuyListDialog();
    }

    private void showAddBookToToBuyListDialog() {
        //STEP 1: show Dialog with input & ok/cancel
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //set the title in montserrat font
        String alertDialogTitle = "New Book";
        Typeface tf = ResourcesCompat.getFont(getContext(), R.font.montserrat);
        CustomTFSpan tfSpan = new CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(alertDialogTitle);
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(spannableString);

        //set positive and negative button in montserrat font
        String positiveButtonText = getResources().getString(R.string.ok);
        String negativeButtonText = getResources().getString(R.string.cancel);
        SpannableString spannableStringPositive = new SpannableString(positiveButtonText);
        SpannableString spannableStringNegative = new SpannableString(negativeButtonText);
        spannableStringPositive.setSpan(tfSpan, 0, spannableStringPositive.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringNegative.setSpan(tfSpan, 0, spannableStringNegative.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //set the text input layout
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.new_book_dialog, (ViewGroup) getView(), false);

        builder.setView(viewInflated);

        //declare and init the editTexts
        EditText bookTitleEditText = viewInflated.findViewById(R.id.bookTitleEditText);
        EditText bookAuthorEditText = viewInflated.findViewById(R.id.bookAuthorEditText);

        //set up the buttons
        builder.setPositiveButton(spannableStringPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //STEP 2: add the book to toBuyBooksList
                String bookTitleInput = bookTitleEditText.getText().toString();
                String bookAuthorInput = bookAuthorEditText.getText().toString();

                if(bookTitleInput.isEmpty() || bookTitleInput.trim().length() == 0)
                    return;

                Book bookToAdd = new Book(bookTitleInput, bookAuthorInput);
                toBuyBooksList.add(toBuyBooksList.size()-1, bookToAdd);
                toBuyAdapter.notifyItemInserted(toBuyBooksList.size()-1);

                //put the book into the database
                toBuyDB.addBookToToBuy(bookToAdd);
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