package com.example.android.bookstoreinventory_part1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * {@link BookInventoryProvider} for Product Inventory app.
 */
// Here is where the ContentProvider for the app is created as BookInventoryProvider.
// Since the BookInventoryProvider is extending from the ContentProvider class,
// here we will implement 5 methods (CRUD methods)
public class BookInventoryProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookInventoryProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the product table
     */
    private static final int PRODUCTS = 100;

    /**
     * URI matcher code for the content URI for a single product in the product table
     */
    private static final int PRODUCTS_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return fot the root URI.
     * It is common to use NO_MATCH as the input for this case.
     */

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.exmaple.android.pets/pets" will map to the
        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table
        sUriMatcher.addURI(BookInventoryContract.CONTENT_AUTHORITY,
                BookInventoryContract.PATH_PRODUCTS, PRODUCTS);

        // The content URI of the form "content://com.example.android.products/products/#" will map to the
        // integer code {@link #PRODUCTS_ID}.
        // This URI is used to provide access to ONE single row of the products table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.products/products/3" matches, but
        // "content://com.example.android.products/products" (without a number at the end) doesn't match.
        sUriMatcher.addURI(BookInventoryContract.CONTENT_AUTHORITY,
                BookInventoryContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    /**
     * Database helper object
     */
    private BookInventoryDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    // Here is created and initialized the BookInventoryHelper object to gain access to the product database
    // This is a global variable, so it can be referenced from other ContentProvider methods
    public boolean onCreate() {
        // Here is where the BookInventoryDbHelper variable is initialized
        mDbHelper = new BookInventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection,
     * selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database is used since we will not make any changes or enter new data in the database
        // Here is where we access the database using the mDbHelper variable initialized in
        // the onCreate method where we get the SQLite object from the DbHelper
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        //Figure out if the UriMatcher can match the input URI that was passed as a parameter
        // in the Cursor line as a specific code. If it is matched, we will receive an
        // integer code that will be store in the variable call match
        int match = sUriMatcher.match(uri);

        // With the match code defined, the switch command will help to decide which path to go down
        switch (match) {
            // If PRODUCTS id is matched, query is for a whole table
            case PRODUCTS:
                // For the PRODUCTS code, query the products table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the products table
                cursor = database.query(BookInventoryContract.BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            // If PRODUCTS_ID is matched, query is for a single product
            case PRODUCTS_ID:
                // For the PRODUCTS_ID code, extract out the ID from the URI
                // For an example using:
                // URI: "content://com.example.android.products/products/3" and
                // Projection: {"_id", "name"}
                // selection will be "_id=?" and
                // selectionArgs: {"3"} - selection arguments will be a String array containing
                // the actual ID of 3 in this case.

                // In this case selection and selectionArgs are pointing in a specific direction
                // For every "?" in the selection, we need to have an element in the selection arguments
                // that will fill in the "?". Since we have 1 question mark in the selection,
                // we have 1 string in the selection arguments' String array.

                selection = BookInventoryContract.BookEntry._ID + "=?";
                // Here is where the number 3 will be parse from the URI and convert into a string
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Then in the SQL database object, we will call the query method passing all the inputs
                // such as TABLE_NAME, projection, selection, selectionArgs
                // This will perform a query on the pets table where the _id equals 3 to return a
                // cursor containing that row of the table
                // The SQLite statement is: SELECT id, name FROM pets WHERE _id=3

                cursor = database.query(BookInventoryContract.BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the cursor, so we know what content URI the Cursor was created for
        // If the data at this URI changes, then we know we need to update the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.

     @Override public String getType(Uri uri) {
     return null;
     }
     */

    /**
     * Insert new data into the provider with the given ContentValues
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        // Here is where we use the URI matcher to check if there is a match
        final int match = sUriMatcher.match(uri);
        // Then the switch statement will help us to determine which case it falls into
        // In the insert method, only the PETS case are supported for insertion. Other cases
        // including no match will fall in the default case and an Exception will be thrown
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Once the PRODUCTS case is called, this will call the addProduct/insertHelper method
    // Example:
    // URI: content://com.example.android.bookstoreinventory_part1/product_inventory
    // ContentValues: name is Hobbit, quantity is 7, price is 9.99, weight is 4. The ID will
    // be auto-generated in increasing order as we insert new product into the table
    //
    // SQLite statement: INSERT INTO pets(name, price, quantity, supplierName, supplierPhone) VALUES ("Hobbit", "9.99", 7, 4, 555-555-5555)
    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(BookInventoryContract.BookEntry.COLUMN_PRODUCT_NAMES);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }
        // Check that the price is valid
        Float price = values.getAsFloat(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCT_PRICES));
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        // If the quantity is provided, check that it is greater than or equal to 0
        Integer quantity = values.getAsInteger(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCTS_QUANTITY));
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }
        // If the supplier name is not null
        int supplierName = Integer.parseInt(values.getAsString(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME)));
        if (!BookInventoryContract.BookEntry.isValidSupplier(supplierName)) {
            throw new IllegalArgumentException("Product requires a valid supplier name");
        }
        // If the supplier number is provided, check that it is greater than
        Integer supplierPhone = values.getAsInteger(BookInventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhone == null || supplierPhone <= 0) {
            throw new IllegalArgumentException("Product requires valid supplier phone number");
        }


        // No need to check supplier's name, any value is valid (including null).


        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert the new product with the given values
        long id = database.insert(BookInventoryContract.BookEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the product content URI
        // uri: content://com.example.android.bookstoreinventory_part1/products
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table, return the new URI
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }
    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCTS_ID:
                // For the PRODUCTS_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookInventoryContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the {@link BookEntry#COLUMN_PRODUCT_NAMES} key is present,
        // check that the name value is not null.
        if (values.containsKey(BookInventoryContract.BookEntry.COLUMN_PRODUCT_NAMES)) {
            String name = values.getAsString(BookInventoryContract.BookEntry.COLUMN_PRODUCT_NAMES);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        // If the {@link BookEntry#COLUMN_PRODUCT_PRICES} key is present,
        // check that the product price is valid.
        if (values.containsKey(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCT_PRICES))) {
            Float price = values.getAsFloat(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCT_PRICES));
            if (price == null || price <0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        // If the {@link BookEntry#COLUMN_PRODUCTS_QUANTITY} key is present,
        // check that the weight value is valid.
        if (values.containsKey(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCTS_QUANTITY))) {
            // Check that the weight is greater than or equal to 0 kg
            Integer quantity = values.getAsInteger(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCTS_QUANTITY));
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        // If the {@link BookEntry#COLUMN_PRODUCT_SUPPLIER_NAME} key is present,
        // check that the weight value is valid.
        if (values.containsKey(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME))) {
            // Check that the supplier name is greater than or equal to 0 kg
            int supplierName = Integer.parseInt(values.getAsString(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME)));
            if (!BookInventoryContract.BookEntry.isValidSupplier(supplierName)) {
                throw new IllegalArgumentException("Product requires valid supplier name");
            }
        }

        // If the {@link BookEntry#COLUMN_SUPPLIER_PHONE_NUMBER} key is present,
        // check that the weight value is valid.
        if (values.containsKey(BookInventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            // Check that the supplier number is greater than 0
            Integer supplierPhone = values.getAsInteger(BookInventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhone == null || supplierPhone <= 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        //return database.update(BookInventoryContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BookInventoryContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if(rowsUpdated !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.

     @Override public int delete(Uri uri, String s, String[] strings) {
     return 0;
     }
     */

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                //return database.delete(BookInventoryContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted = database.delete(BookInventoryContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                // Delete a single row given by the ID in the URI
                selection = BookInventoryContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                // return database.delete(BookInventoryContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted = database.delete(BookInventoryContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return BookInventoryContract.BookEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return BookInventoryContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
