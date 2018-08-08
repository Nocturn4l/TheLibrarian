package com.example.dovah.thelibrarian.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dovah.thelibrarian.data.BookContract.BookEntry;

/**
 * Database helper for The Librarian app. Manages database creation and version management.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "library.db";
    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the books table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + "("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_AUTHOR + " TEXT , "
                + BookEntry.COLUMN_BOOK_PRICE + " DOUBLE , "
                + BookEntry.COLUMN_BOOK_STOCK + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_BOOK_SUPPL_NAME + " TEXT, "
                + BookEntry.COLUMN_BOOK_SUPPL_PHONE + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);


    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.

    }
}
