package com.example.joseph.takenote;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter myCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Query the notes database
        myCursorAdapter = new NotesCursorAdapter(this,null,0);

        // Display the notes from the database in the list view
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(myCursorAdapter);

        // Add click listener to each list item to determine if user selects existing note
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Intent editorIntent = new Intent(MainActivity.this,EditorActivity.class);
                Uri noteIDUri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                editorIntent.putExtra(NotesProvider.CONTENT_ITEM_TYPE,noteIDUri);
                startActivityForResult(editorIntent,EDITOR_REQUEST_CODE);
            }
        });

        getLoaderManager().initLoader(0,null,this);
    }

    // Inserts provided note text into the database
    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT,noteText);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
    }

    // Creates Options menu in the Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    // Handles ActionBar Item Clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_create_sample:
                InsertSampleNotes();
                break;
            case R.id.action_delete_all:
                DeleteAllNotes();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void InsertSampleNotes() {
        insertNote("Simple Note Test");
        insertNote("Multi-line\nNote");
        insertNote("This is an example of a very long note that exceeds the width of the screen" +
                "of the device on which I have loaded this app!");

        restartLoader();
    }

    private void DeleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            // Delete all notes from the database
                            getContentResolver().delete(NotesProvider.CONTENT_URI,null,null);
                            restartLoader();

                            // Display message to user confirming that notes were deleted
                            Toast.makeText(MainActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    // Reloads data from database each time data is added or deleted
    private void restartLoader() {
        getLoaderManager().restartLoader(0,null,this);
    }

    // Creates Loader and specifies where the data is coming from
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,NotesProvider.CONTENT_URI,null,null,null,null);
    }

    // Pass the returned data to the cursorAdapter when data is finished loading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        myCursorAdapter.swapCursor(data);
    }

    // Reset cursorAdapter data
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myCursorAdapter.swapCursor(null);
    }

    // Opens the editor to create a new note
    public void openEditorForNewNote(View view) {
        Intent myIntent = new Intent(this,EditorActivity.class);
        startActivityForResult(myIntent,EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK)
        {
            restartLoader();
        }
    }
}
