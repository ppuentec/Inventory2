package com.example.android.bookstoreinventory_part1;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.bookstoreinventory_part1.data.BookInventoryContract;

/** Displays list of products that were entered and stored in the app */
// Here is the public activity declaration
public class ProductInventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks <Cursor> {

    FloatingActionButton fab;

    private static final int PRODUCT_LOADER = 0;

    // Here is where we define the adapter for the ListView
    ProductCursorAdapter mCursorAdapter;

    ListView productListView;

    TextView emptyView;

    @Override
    //Here is where it created the ProductInventoryActivity activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_inventory);

        // Setup FAB using a ClickListener that uses an Intent to open ProductEditorActivity
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductInventoryActivity.this, ProductEditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the product data
        ListView productListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of product data in the Cursor
        // There is no product data yet (until the loader finishes) so pass in null for the cursor
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        // Here is where a new activity will start up when the user clicks on the chosen item from the ListView

        // Setup item click listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> adapterView, View view, int position, long id) {
                // adapterView: is a ListView
                // view:        represents a particular view for the item
                // position:    represents the position of the item in the ListView
                // id:          id of the item where we clicked on. Since we will be generating the URI for
                // the product in order to pass along as an intent extra

                // Create a new intent to go to {@Link ProductEditorActivity}
                Intent intent = new Intent(ProductInventoryActivity.this, ProductEditorActivity.class);

                // Form the content URI that represents the specific product that was clicked on,
                // by appending the "id" (passed as input to this method) onto the {@link BookEntry#CONTENT_URI}
                // For example, the URI would be "content://com.example.android.bookstoreinventory_part1/products/2"
                // if the pet with ID 2 was clicked on
                Uri currentProductUri = ContentUris.withAppendedId(BookInventoryContract.BookEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentProductUri);

                // Launch the {@link ProductEditorActivity} to display the data for the current product
                // Remember that receiving the message intent is the ProductEditorActivity
                startActivity(intent);
            }
        });

        // Kick off the loader using this
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    private void insertProduct(){
        // Here is created a ContentValues object
        ContentValues values = new ContentValues();
        // Here is where the key value pairs are store for the new product
        values.put(BookInventoryContract.BookEntry.COLUMN_PRODUCT_NAMES, "The Hobbit");
        values.put(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCT_PRICES), "9.99");
        values.put(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCTS_QUANTITY), "7");
        values.put(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME), BookInventoryContract.BookEntry.SUPPLIER_3);
        values.put(BookInventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "5555555555");
        // Here is where the information about the new product is inserted in the database
        // This is completed by using the insert method and using the SQLite db object
        // In this case the first parameter is the name of the table, then null and ContentValues object
        // db.insert(BookEntry.TABLE_NAME, null, values);

        // In order to capture errors, we can capture the value that is returned by the insert
        // method in a variable called newRodId and then use Log to display a message

        //long newRowId = db.insert(BookInventoryContract.BookEntry.TABLE_NAME, null, values);
        //Log.v("CatalogActivity", "New row ID " + newRowId);

        // Insert a new row for Hobbit into the provider using the ContentResolver.
        // Use the {@link BookEntry#CONTENT_URI} to indicate that we want to insert
        // into the products database table.
        // Receive the new content URI that will allow us to access Hobbit's data in the future.
        Uri newUri = getContentResolver().insert(BookInventoryContract.BookEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all products in the database.
     */
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(BookInventoryContract.BookEntry.CONTENT_URI, null, null);
        Log.v("Product Inventory", rowsDeleted + " rows deleted from product database");
    }

    @Override
    //Here where the option menu in the app bar
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_product_inventory.xml file.
        // This adds menu items to the app bar.
        // Here we inflate a menu with resources by referring to its resource id from the menu_product_inventory.xml
        // then the option will be displayed in the Options menu after being created by the ProductInventoryActivity
        getMenuInflater().inflate(R.menu.menu_product_inventory, menu);
        return true;
    }

    @Override
    //Here is where the set up happens after the user selects an option
    // This method provides the behavior/selection made by the user
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        // the switch statement uses the ID to handle the different cases for the different menu items
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Here is where both methods are called once the user click on action_insert_dummy_data
                insertProduct();
                //displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader <Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about
        // To make the app perform better and more responsive by using a loader, we will use a smaller
        // projection (3 items: ID, Name and quantity). ID is always needed by the cursor that we are
        // going to pass to any cursorAdapter
        String [] projection = {
                BookInventoryContract.BookEntry._ID,
                BookInventoryContract.BookEntry.COLUMN_PRODUCT_NAMES,
                BookInventoryContract.BookEntry.COLUMN_PRODUCT_PRICES,
                String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCTS_QUANTITY)};

        // This loader will execute the ContentProvider's query method on a background thread
        // Parameters here are very similar with the one used for creating in the main thread
        return new android.content.CursorLoader(
                this,                                   // Parent activity context
                BookInventoryContract.BookEntry.CONTENT_URI,    // Provider content URI to query
                projection,                                     // Columns to include in the resulting Cursor
                null,                                  // No selection clause
                null,                               // No selection arguments
                null);                                 // Default sort order
    }

    @Override
    public void onLoadFinished(Loader <Cursor> loader, Cursor data) {
        // Update {@Link ProductCursorAdapter} with this new cursor containing updated product data
        // This method receives the cursor with the new product data and pass into my CursorAdapter using
        // the swapCursor method
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader <Cursor> loader) {
        // This callback is called when the data needs to be deleted, this iw why the method passes
        // null as the cursor here
        mCursorAdapter.swapCursor(null);
    }
}
