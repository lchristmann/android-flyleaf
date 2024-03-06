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

//IMPORTANT PARADIGM: NO GAPS IN ID COLUMN. GOES 1,2,3,4,5,6,... (_id AUTOINCREMENT starts with 1!, 0 is for temporary storage)
public class ToReadDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "toread.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "to_read";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "book_title";
    private static final String COLUMN_AUTHOR = "book_author";

    public ToReadDatabaseHelper(@Nullable Context context) {
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

    public void fillTheFreshTable(ArrayList<Book> toReadBookList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < toReadBookList.size()-1; i++) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_TITLE, toReadBookList.get(i).getTitle());
            cv.put(COLUMN_AUTHOR, toReadBookList.get(i).getAuthor());
            db.insert(TABLE_NAME, null, cv);
        }
    }

    public void addBookToToRead(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, book.getTitle());
        cv.put(COLUMN_AUTHOR, book.getAuthor());
        db.insert(TABLE_NAME, null, cv);
    }

    public Cursor readAllToReadData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
