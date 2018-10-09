package com.example.android.bookstoreinventory_part1.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**API Contract for the Book Store Inventory app*/

    public final class BookInventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookInventoryContract() {
    }


     /**
     * The "Content authority" is a name for the entire Content Provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreinventory_part1";
     /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
     /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.bookstoreinventory_part1/product_inventory/ is a valid path for
     * looking at product data. content://com.example.android.bookstoreinventory_part1/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PRODUCTS = "Products";

     /**
     * Inner class that defines constant values for the Book Inventory database table.
     * Each entry in the table represents a book
     */

    public static final class BookEntry implements BaseColumns {

         /** The content URI to access the product data in the provider */
         public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

         /**
          * The MIME type of the {@link #CONTENT_URI} for a list of products.
          */
         public static final String CONTENT_LIST_TYPE =
                 ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
         /**
          * The MIME type of the {@link #CONTENT_URI} for a single product.
          */
         public static final String CONTENT_ITEM_TYPE =
                 ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * Name of database table for books/products
         */
        public final static String TABLE_NAME = "product_inventory";

        /**
         * Unique ID number for the books/products (only for use in the database table) - Type: INTEGER
         */

        // The String below describe the type of names of the variables, they do not refer to the its
        // attributes or values stored in the columns
        // The ID comes from the BaseColumns class
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the products - Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAMES = "product_name";

        /**
         * Price of products - Type: FLOAT
         */
        public static final String COLUMN_PRODUCT_PRICES = "product_price";

        /**
         * Quantity of products - Type: INTEGER
         */
        public final static String COLUMN_PRODUCTS_QUANTITY = "product_quantity";
        /**
         * Name of product suppliers
         *
         * The only possible values are {@link #SUPPLIER_1}, {@link #SUPPLIER_2}, {@link #SUPPLIER_3}
         * {@link #SUPPLIER_4}, {@link #SUPPLIER_5} or {@link #SUPPLIER_UNKNOWN}
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";

        /**
         * Supplier Phone Number - Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        /**
         * Possible values for the supplier of products.
         */
        // Here is where the constants are assigned to 5 suppliers. There is an option for unknown supplier as well
        public static final int SUPPLIER_1 = 1;
        public static final int SUPPLIER_2 = 2;
        public static final int SUPPLIER_3 = 3;
        public static final int SUPPLIER_4 = 4;
        public static final int SUPPLIER_5 = 5;
        public static final int SUPPLIER_UNKNOWN = 0;

         /**
          * Returns whether or not the given gender is {@link #SUPPLIER_UNKNOWN}, {@link #SUPPLIER_1},
          * or {@link #SUPPLIER_2}, {@link #SUPPLIER_3}, {@link #SUPPLIER_4} or {@link #SUPPLIER_5}.
          */
         public static boolean isValidSupplier(int supplierName) {
             if (supplierName == SUPPLIER_UNKNOWN || supplierName == SUPPLIER_1 || supplierName == SUPPLIER_2
                     || supplierName == SUPPLIER_3 || supplierName == SUPPLIER_4  || supplierName == SUPPLIER_5 ) {
                 return true;
             }
             return false;
         }

    }
}