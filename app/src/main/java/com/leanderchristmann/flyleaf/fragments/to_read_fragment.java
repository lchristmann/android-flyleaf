package com.leanderchristmann.flyleaf.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.leanderchristmann.flyleaf.R;
import com.leanderchristmann.flyleaf.adapters.ToReadAdapter;
import com.leanderchristmann.flyleaf.db.ToReadDatabaseHelper;
import com.leanderchristmann.flyleaf.models.Book;
import com.leanderchristmann.flyleaf.models.CustomTFSpan;
import com.leanderchristmann.flyleaf.util.ToReadItemTouchHelper;

import java.util.ArrayList;

public class to_read_fragment extends Fragment implements ToReadAdapter.OnBookToReadClickListener {

    //layout elements
    private RecyclerView toReadRecyclerView;

    //vars
    ArrayList<Book> toReadBooksList;
    private ToReadAdapter toReadAdapter;
    ToReadDatabaseHelper toReadDB;

    public to_read_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_read_fragment, container, false);

        //initialize RecyclerView and DatabaseHelper
        toReadRecyclerView = view.findViewById(R.id.toReadRecyclerView);
        toReadDB = new ToReadDatabaseHelper(getContext());

        //initialize ArrayList and store data from database int it
        toReadBooksList = new ArrayList<>();
        storeToReadDbDataInArray();
        //add last position to Array for the + to show | NOTE: CARE THAT LAST ELEMENT IS THIS WHEN WRITING TO DB!
        toReadBooksList.add(new Book("addBookItemOnLastPosition", "addBookItemOnLastPosition"));

        initToReadRecyclerView();

        return view;
    }

    private void initToReadRecyclerView(){
        //LayoutManager and Adapter
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        toReadRecyclerView.setLayoutManager(layoutManager);

        toReadAdapter = new ToReadAdapter(getContext(), toReadBooksList, this);

        //the itemTouchHelper
        ItemTouchHelper.Callback callback = new ToReadItemTouchHelper(toReadAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        toReadAdapter.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(toReadRecyclerView);

        toReadRecyclerView.setAdapter(toReadAdapter);
    }

    private void storeToReadDbDataInArray() {
        Cursor cursor = toReadDB.readAllToReadData();
        if(cursor.getCount() == 0)
            return;
        while(cursor.moveToNext()) {
            toReadBooksList.add(new Book(cursor.getString(1), cursor.getString(2)));
        }
    }

    @Override
    public void onBookToReadClick(int position) {
        if(position == toReadBooksList.size()-1)
            showAddBookToToReadListDialog();
    }

    private void showAddBookToToReadListDialog() {

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

                //STEP 2: add the book to toReadBooksList
                String bookTitleInput = bookTitleEditText.getText().toString();
                String bookAuthorInput = bookAuthorEditText.getText().toString();

                if(bookTitleInput.isEmpty() || bookTitleInput.trim().length() == 0)
                    return;

                Book bookToAdd = new Book(bookTitleInput, bookAuthorInput);
                toReadBooksList.add(toReadBooksList.size()-1, bookToAdd);
                toReadAdapter.notifyItemInserted(toReadBooksList.size()-1);

                //put the book into the database
                toReadDB.addBookToToRead(bookToAdd);
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