package com.example.android.bookstoreinventory_part1;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreinventory_part1.data.BookInventoryContract;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */

public class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        // Find individual views that we want to modify in the list_item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        // Extract properties from cursor
        // Find the columns of product attributes that we are interested in, in other words the product data
        // from the current row in the cursor using the corresponding column index/name
        int nameColumnIndex = cursor.getColumnIndex(BookInventoryContract.BookEntry.COLUMN_PRODUCT_NAMES);
        int priceColumnIndex = cursor.getColumnIndex(String.valueOf(BookInventoryContract.BookEntry.COLUMN_PRODUCT_PRICES));
        int quantityColumnIndex = cursor.getColumnIndex(BookInventoryContract.BookEntry.COLUMN_PRODUCTS_QUANTITY);

        // Read the product attributes from the Cursor for the current product
        // Here is read the values for the specific column indices
        String name = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);

        // If the product price is empty string or null, then use some default text
        // that says "Unknown price", so the TextView isn't blank.
        if (TextUtils.isEmpty(price)) {
            price = context.getString(R.string.unknown_price);
        }

        // Populate fields with extracted properties
        // Update the TextViews with the attributes for the current product
        nameTextView.setText(name);
        summaryTextView.setText(price);
        summaryTextView.setText(quantity);
    }
}
