package com.example.dovah.thelibrarian.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.dovah.thelibrarian";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.dovah.thelibrarian/books( is a valid path for
     * looking at books data. content://com.example.dovah.thelibrarian/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_BOOKS = "books";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {
    }

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {
        /**
         * The content URI to access the books data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * Name of database table for books
         */
        public final static String TABLE_NAME = "books";

        /**
         * Unique ID number for the book (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Title of the book.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_NAME = "name";

        /**
         * Author of the book.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_AUTHOR = "author";

        /**
         * Price of the book.
         * <p>
         * Type: FLOAT
         */
        public final static String COLUMN_BOOK_PRICE = "price";

        /**
         * Stock of books.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_BOOK_STOCK = "stock";

        /**
         * Supplier of the book.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_SUPPL_NAME = "supplier";

        /**
         * Supplier contact.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_SUPPL_PHONE = "phone";


    }

}
