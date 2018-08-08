package com.example.dovah.thelibrarian;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dovah.thelibrarian.data.BookContract.BookEntry;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
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
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView authorTextView = view.findViewById(R.id.author);
        TextView priceTextView = view.findViewById(R.id.price);
        final TextView stockTextView = view.findViewById(R.id.quantity);


        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int stockColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_STOCK);


        // Read the book attributes from the Cursor for the current book
        String bookName = cursor.getString(nameColumnIndex);
        String bookAuthor = cursor.getString(authorColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        final int stock = cursor.getInt(stockColumnIndex);


        // Update the TextViews with the attributes for the current book
        nameTextView.setText(bookName);
        authorTextView.setText(bookAuthor);
        priceTextView.setText(String.valueOf(price) + " €");
        stockTextView.setText(String.valueOf(stock) + " pc");

        final Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI,
                cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry._ID)));

        //Set a click listener on Sale button
        ImageView saleButton = view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int remaining = stock - 1;
                if (remaining < 0) {
                    Toast.makeText(v.getContext(), v.getContext().getString(R.string.no_book), Toast.LENGTH_LONG).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_STOCK, remaining);
                    context.getContentResolver().update(uri, values, null, null);
                    stockTextView.setText(String.valueOf(remaining));
                }
            }
        });

    }
}
