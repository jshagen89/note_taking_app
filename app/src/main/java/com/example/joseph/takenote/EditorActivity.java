package com.example.joseph.takenote;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private String noteFilter;
    private String oldText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // get reference to editText that user is interacting with
        editor = (EditText) findViewById(R.id.editNote);
        saveButton = (Button) findViewById(R.id.action_save_note);




        // Check to see if a new note should be created
        Intent myIntent = getIntent();
        Uri myUri = myIntent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if (myUri == null)
        {
            // Create a new note if matching id not found in db
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note_title));
        }
        else
        {
            // Get existing note from db if matching id if found
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + myUri.getLastPathSegment();
            Cursor myCursor = getContentResolver().query(myUri,DBOpenHelper.ALL_COLUMNS,
                    noteFilter,null,null);
            myCursor.moveToFirst();
            oldText = myCursor.getString(myCursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));

            // Place note text in the editor and place cursor at end of text string
            editor.setText(oldText);
            editor.requestFocus();
        }
    }

    // Creates Options menu in the Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show item if user is editing an existing note
        if (action.equals(Intent.ACTION_EDIT))
        {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.menu_new_note, menu);
        }

        return true;
    }

    // Handles ActionBar Item Clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finishedEditing();
                break;
            case R.id.action_delete_note:
                deleteNote();
                break;
            case R.id.action_save_note:
                saveNote();
                break;

        }
        return true;
    }

    // Called when user is finished typing their note
    private void finishedEditing()
    {
        String newNoteText = editor.getText().toString().trim();

        switch (action)
        {
            case Intent.ACTION_INSERT:
                if (newNoteText.length() == 0)
                {
                    setResult(RESULT_CANCELED);
                }
                else
                {
                    insertNote(newNoteText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (oldText.length() == 0)
                {
                    deleteNote();
                }
                else if (oldText.equals(newNoteText))
                {
                    setResult(RESULT_CANCELED);
                }
                else
                {
                    updateNote(newNoteText);
                }
        }

        // Returns to parent activity
        finish();
    }

    // Update note in db if user edits a note
    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT,noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values,noteFilter,null);
        Toast.makeText(this, R.string.successful_note_update,Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    // Delete note currently being edited and return to main activity
    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI,noteFilter,null);
        Toast.makeText(this, R.string.note_deleted,Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    // Inserts a new note into the db
    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT,noteText);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    // Called if user presses the save button while editing a note
    public void saveNote () {
        finishedEditing();
    }
}
