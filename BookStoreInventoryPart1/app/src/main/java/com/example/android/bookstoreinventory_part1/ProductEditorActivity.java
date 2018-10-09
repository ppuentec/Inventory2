package com.example.android.bookstoreinventory_part1;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.telecom.PhoneAccount;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.bookstoreinventory_part1.data.BookInventoryContract;

import static com.example.android.bookstoreinventory_part1.data.BookInventoryContract.BookEntry;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class ProductEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the product data loader */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /** Content URI for the existing pet (null if it's a new pet) */
    private Uri mCurrentProductUri;

    /** EditText field to enter the product name */
    private EditText mProductNameEditText;

    /** EditText field to enter the product price */
    private EditText mProductPriceEditText;

    /** EditText field to enter the product quantity */
    private EditText mProductQuantityEditText;

    /** EditText field to enter the Product Supplier Name */
    private Spinner mSupplierNameSpinner;

    /**
     * Book supplier. In my case, there are 5 possible contracted suppliers and
     * one option for non-contracted supplier:
     * 0 for non-contracted supplier, 1-5 different contracted suppliers
     */
    private int mSupplierName = BookEntry.SUPPLIER_UNKNOWN;

    /** Boolean flag that keeps track of whether the product has been edited (true) or not (false) */
    private boolean mProductHasChanged = false;

    /** EditText field to enter the Product Supplier Phone Number */
    private EditText mSupplierPhoneNumberEditText;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mProductHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    //Here is where all the edit text variables are connected to their views
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_editor);

        // Examine the intent that was used to launch this activity in order to figure out
        // if we are creating a new product or editing an existing one

        // Here is where we get the intent
        Intent intent = getIntent();
        // Here is where we get the data from the intent, which is the URI that was attached to it
        mCurrentProductUri = intent.getData();

        // Remember that our app has two options here the first one is when the user clicked on
        // an item form the ListView to edit a product and the second one is when the use clicks
        // on the FAB to add a new product

        // If the intent DOES NOT contain a product content URI, then we know that we are creating a new product
        if (mCurrentProductUri == null) {
            // This is a new product, so change the app bar to say "add a product"
            setTitle(R.string.editor_activity_title_new_product);

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();

        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(R.string.editor_activity_title_edit_product);

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mProductNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mProductPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mProductQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);
        //Here is where the Spinner begins
        mSupplierNameSpinner = (Spinner) findViewById(R.id.spinner_supplier);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameSpinner.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the supplier of the product
     */
    private void setupSpinner() {
        // Here is where is created an ArrayAdapter to populate the spinner with options.
        // The list with options are taken from the String array available in the resource file
        ArrayAdapter supplierNameSpinnerAdapter = ArrayAdapter.createFromResource(this,
                //Here is where the layout is specified and the drop down style of the spinner
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        supplierNameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSupplierNameSpinner.setAdapter(supplierNameSpinnerAdapter);

        // Set the integer mSelected to the constant values
        // Here is also where is created an instance of the abstract adapter variable class. This will
        // require to define a default option as onNothingSelected
        mSupplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                //Here is where the user's selection is stored and set using a mVariable
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("Supplier 1")) {
                        mSupplierName = BookEntry.SUPPLIER_1; // Supplier # 1
                    } else if (selection.equals("Supplier 2")) {
                        mSupplierName = BookEntry.SUPPLIER_2; // Supplier # 2
                    }else if (selection.equals("Supplier 3")) {
                        mSupplierName = BookEntry.SUPPLIER_3; // Supplier # 3
                    }else if (selection.equals("Supplier 4")) {
                        mSupplierName = BookEntry.SUPPLIER_4; // Supplier # 4
                    }else if (selection.equals("Supplier 5")) {
                        mSupplierName = BookEntry.SUPPLIER_5; // Supplier # 5
                    }else {
                        mSupplierName = BookEntry.SUPPLIER_UNKNOWN; // Unknown supplier
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplierName = BookEntry.SUPPLIER_UNKNOWN; // Unknown
            }
        });
    }

    /** Get user input from editor and save product information into database*/
    // Here is where the information entered by the user will be saved and inputted into de db
    // We will get data entered by user from the EditText field. We will do that using the ID
    private void saveProduct() {
        // trim helps to eliminate blank space entered by user
        // The users information will be stored in the nameString variable
        String productNameString = mProductNameEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String productQuantityString = mProductQuantityEditText.getText().toString().trim();
        // Since weight is a number, we will convert string into integer using parseInt
        //int productQuantity = Integer.parseInt(productQuantityString);
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(productPriceString) &&
                TextUtils.isEmpty(productQuantityString) && TextUtils.isEmpty(supplierPhoneNumberString)
                && mSupplierName == BookEntry.SUPPLIER_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        //BookInventoryDbHelper mDbHelper = new BookInventoryDbHelper((this));
        // Gets the database in write more
        //SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        // Here is created a ContentValues object
        ContentValues values = new ContentValues();
        // Here is where the key value pairs are store for the new book/product
        values.put(BookEntry.COLUMN_PRODUCT_NAMES, productNameString);
        //values.put(BookEntry.COLUMN_PRODUCT_PRICES, productPriceString);
        //values.put(BookEntry.COLUMN_PRODUCTS_QUANTITY, productQuantityString);
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, mSupplierName);
        //values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

        //long newRowId = db.insert(BookInventoryContract.BookEntry.TABLE_NAME, null, values);
        int price = 0;
        if (!TextUtils.isEmpty(productPriceString)) {
            price = Integer.parseInt(productPriceString);
        }
        values.put(BookEntry.COLUMN_PRODUCT_PRICES, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(productQuantityString)) {
            quantity = Integer.parseInt(productQuantityString);
        }
        values.put(BookEntry.COLUMN_PRODUCTS_QUANTITY, quantity);

        int supplierPhone = 0;
        if (!TextUtils.isEmpty(supplierPhoneNumberString)) {
            supplierPhone = Integer.parseInt(supplierPhoneNumberString);
        }
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

        // Determine if this is a new or existing product by checking if mCurrentProductUri is null or not
        if(mCurrentProductUri == null){
            // This is a NEW product, so insert a new product into the provider,
            // returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if(newUri == null){
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentProductUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentProductUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_product_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    // Here is where a method is defined to set up what action happens when a user selects
    // something from the menu (app bar overflow menu)
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                // Here is where the save button was clicked (action_save) and
                // this will trigger insertPet method in the ProductEditorActivity class
                // which will insert the pet information in the db
                saveProduct();
                // Exit activity
                // then the editor will close and it will return to previous activity (ProductInventoryActivity)
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    // Navigate back to parent activity (ProductInventoryActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                    }
                };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAMES,
                BookEntry.COLUMN_PRODUCT_PRICES,
                BookEntry.COLUMN_PRODUCTS_QUANTITY,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader <Cursor> loader, Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAMES);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICES);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCTS_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            float price = Float.parseFloat(cursor.getString(priceColumnIndex));
            int quantity = cursor.getInt(quantityColumnIndex);
            int supplierName = cursor.getInt(supplierNameColumnIndex);
            String supplierNumber = cursor.getString(supplierNumberColumnIndex);

            // Update the views on the screen with the values from the database
            mProductNameEditText.setText(name);
            mProductPriceEditText.setText(Float.toString(price));
            mProductQuantityEditText.setText(Integer.toString(quantity));
            mSupplierPhoneNumberEditText.setText(supplierNumber);

            // SupplierName is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown supplier, 1 is SUPPLIER_1, 2 is SUPPLIER_2,
            // 3 is SUPPLIER_3, 4 is SUPPLIER_4, 5 is SUPPLIER_5).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (supplierName) {
                case BookEntry.SUPPLIER_1:
                    mSupplierNameSpinner.setSelection(1);
                    break;
                case BookEntry.SUPPLIER_2:
                    mSupplierNameSpinner.setSelection(2);
                    break;
                case BookEntry.SUPPLIER_3:
                    mSupplierNameSpinner.setSelection(3);
                    break;
                case BookEntry.SUPPLIER_4:
                    mSupplierNameSpinner.setSelection(4);
                    break;
                case BookEntry.SUPPLIER_5:
                    mSupplierNameSpinner.setSelection(5);
                    break;
                default:
                    mSupplierNameSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader <Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mSupplierNameSpinner.setSelection(0); // Select "Unknown" supplier
        mSupplierPhoneNumberEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Perform the deletion of the product from the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

}
