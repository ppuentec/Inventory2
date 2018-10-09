package com.example.android.bookstoreinventory_part1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**Database helper for Book Store Inventory app. manages database creation and version management */
public class BookInventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookInventoryDbHelper.class.getSimpleName();

    /** Name of the database file as a constant */
    private static final String DATABASE_NAME = "productInventory.db";

    /** Database version as a constant. If you change the database schema, you must increment the database version*/
    private static final int DATABASE_VERSION = 1;

    /** Here is the constructor that constructs a new instance of {@link BookInventoryDbHelper}.
     * @param context of the app */

    public BookInventoryDbHelper(Context context) {
        /** Because we are subclassing from another class, we call the pairing constructor via super
         * */
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Since we are subclassing SQL open helper, we need to implement onCreate and onUpgrade methods */
    @Override
    /** This is called when the database is created for the first time. Here is where the creation
     * and initial population of tables will happen*/
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the product table
        // using CREATE TABLE pets(id INTEGER PRIMARY KEY, name TEXT, weight INTEGER);
        String SQL_CREATE_PRODUCT_INVENTORY_TABLE = "CREATE TABLE " + BookInventoryContract.BookEntry.TABLE_NAME + " ("
                + BookInventoryContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookInventoryContract.BookEntry.COLUMN_PRODUCT_NAMES + " TEXT NOT NULL, "
                + BookInventoryContract.BookEntry.COLUMN_PRODUCT_PRICES + " INTEGER NOT NULL DEFAULT 0, "
                + BookInventoryContract.BookEntry.COLUMN_PRODUCTS_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookInventoryContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " INTEGER NOT NULL,"
                + BookInventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL DEFAULT 0);";

        // This method takes in an SQL statement as it's parameter and then execute the SQL statement
        // Since this is not a static method, we need to execute SQL method on the correct SQLite database class instance
        // using onCreate parameter
        db.execSQL(SQL_CREATE_PRODUCT_INVENTORY_TABLE);
    }

    /** This is called when the database needs to be upgraded */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Initially, the database is at version 1, so there's nothing to do be done here at the beginning
    }
}
