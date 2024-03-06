package com.leanderchristmann.flyleaf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.leanderchristmann.flyleaf.models.Book;

import java.util.ArrayList;

public class ToBuyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "tobuy.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "to_buy";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "book_title";
    private static final String COLUMN_AUTHOR = "book_author";

    public ToBuyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_AUTHOR + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void dropTableAndMakeNew(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void fillTheFreshTable(ArrayList<Book> toBuyBookList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < toBuyBookList.size()-1; i++) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_TITLE, toBuyBookList.get(i).getTitle());
            cv.put(COLUMN_AUTHOR, toBuyBookList.get(i).getAuthor());
            db.insert(TABLE_NAME, null, cv);
        }
    }

    public void addBookToToBuy(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, book.getTitle());
        cv.put(COLUMN_AUTHOR, book.getAuthor());
        db.insert(TABLE_NAME, null, cv);
    }

    public Cursor readAllToBuyData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
