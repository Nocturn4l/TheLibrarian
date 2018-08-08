package com.example.dovah.thelibrarian;

import android.app.AlertDialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.app.LoaderManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dovah.thelibrarian.data.BookContract.BookEntry;

/**
 * Allows user to add a new book or edit an existing one.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the book data loader */
    private static final int EXISTING_BOOK_LOADER = 0;

    /** Content URI for the existing book (null if it's a new book) */
    private Uri mCurrentBookUri;

    /** EditText field to enter the book's name*/
    private EditText mNameEditText;

    /** EditText field to enter the book's author*/
    private EditText mAuthorEditText;

    /** EditText field to enter the book's price */
    private EditText mPriceEditText;

    /** EditText field to enter the book's stock */
    private EditText mStockEditText;

    /** EditText field to enter the book's supplier */
    private EditText mSupllierEditText;

    /** EditText field to enter the supplier phone */
    private EditText mTelephoneEditText;

    /** ImageView to order new books */
    ImageView callSupplier;

    /** Boolean flag that keeps track of whether the book has been edited (true) or not (false) */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        callSupplier=findViewById(R.id.call_supp);
        mNameEditText = findViewById(R.id.title);
        mAuthorEditText=findViewById(R.id.author);
        mPriceEditText=findViewById(R.id.price);
        mStockEditText=findViewById(R.id.quantity);
        mSupllierEditText=findViewById(R.id.supplier);
        mTelephoneEditText=findViewById(R.id.telephone);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (mCurrentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle(getString(R.string.editor_activity_title_new_book));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing book, so change app bar to say "Edit Book"
            setTitle(getString(R.string.editor_activity_title_edit_book));
            callSupplier.setVisibility(View.VISIBLE);
            callSupplier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent=new Intent(Intent.ACTION_DIAL);
                    String tel_number = (mTelephoneEditText.getText().toString());
                    Uri call=Uri.parse("tel:"+tel_number);
                    callIntent.setData(call);
                    if (callIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(callIntent);
                    }
                }
            });

            // Initialize a loader to read book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }


        ImageView minusIcon = findViewById(R.id.minus);
        minusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(mStockEditText.getText().toString().trim());
                if (quantity == 0) {
                } else {
                    quantity = quantity - 1;
                    mBookHasChanged = true;
                    mStockEditText.setText(Integer.toString(quantity));
                }
            }
        });

        ImageView plusIcon = findViewById(R.id.plus);
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(mStockEditText.getText().toString().trim());
                quantity = quantity + 1;
                mBookHasChanged = true;
                mStockEditText.setText(Integer.toString(quantity));
            }
        });


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mStockEditText.setOnTouchListener(mTouchListener);
        mSupllierEditText.setOnTouchListener(mTouchListener);
        mTelephoneEditText.setOnTouchListener(mTouchListener);

    }
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();
        String supplString=mSupllierEditText.getText().toString().trim();
        String phoneString=mTelephoneEditText.getText().toString().trim();
        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri== null &&
                (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(authorString)||TextUtils.isEmpty(supplString)||TextUtils.isEmpty(phoneString))) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, getString(R.string.editor_insert_book_with_no_info),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCurrentBookUri!=null&&(TextUtils.isEmpty(nameString) || TextUtils.isEmpty(authorString)||TextUtils.isEmpty(supplString)||TextUtils.isEmpty(phoneString))){
            Toast.makeText(this, getString(R.string.editor_update_book_with_no_info),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Double price;
        if(mPriceEditText.getText().toString().trim().length() < 1){
            price=0.0;
        } else
            { price=Double.parseDouble(mPriceEditText.getText().toString().trim());}

        int stock;
        if(mStockEditText.getText().toString().trim().length() < 1){
            stock=0;
        } else
        { stock =Integer.parseInt(mStockEditText.getText().toString().trim());}





        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_STOCK, stock);
        values.put(BookEntry.COLUMN_BOOK_SUPPL_NAME, supplString);
        values.put(BookEntry.COLUMN_BOOK_SUPPL_PHONE, phoneString);


        // Determine if this is a new or existing book by checking if mCurrentbookUri is null or not
        if (mCurrentBookUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Exit activity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save book to database
                saveBook();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_STOCK,
                BookEntry.COLUMN_BOOK_SUPPL_NAME,
                BookEntry.COLUMN_BOOK_SUPPL_PHONE,
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current book
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int stockColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_STOCK);
            int supplierColumnIndex=cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPL_NAME);
            int phoneColumnIndex=cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPL_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            Double price = cursor.getDouble(priceColumnIndex);
            int stock= cursor.getInt(stockColumnIndex);
            String supplier=cursor.getString(supplierColumnIndex);
            String phone= cursor.getString(phoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mAuthorEditText.setText(author);
            mPriceEditText.setText(String.valueOf(price));
            mStockEditText.setText(String.valueOf(stock));
            mSupllierEditText.setText(supplier);
            mTelephoneEditText.setText(phone);



        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText("");
        mStockEditText.setText("");
        mSupllierEditText.setText("");
        mTelephoneEditText.setText("");
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
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
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
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
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
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
