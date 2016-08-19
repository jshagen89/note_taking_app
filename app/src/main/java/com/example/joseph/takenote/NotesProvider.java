package com.example.joseph.takenote;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class NotesProvider extends ContentProvider
{

    private static final String AUTHORITY = "com.example.joseph.takenote.notesprovider";
    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    // Initialize Available URI Actions
    private static final UriMatcher myUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        myUriMatcher.addURI(AUTHORITY,BASE_PATH,NOTES);
        myUriMatcher.addURI(AUTHORITY,BASE_PATH + "/#",NOTES_ID);
    }
    public static final String CONTENT_ITEM_TYPE = "Note";

    private SQLiteDatabase db;

    // Creates the SQLite Database
    @Override
    public boolean onCreate() {
        DBOpenHelper myDBHelper = new DBOpenHelper(getContext());
        db = myDBHelper.getReadableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {

        // Only return one row from db if user selects an existing note
        if (myUriMatcher.match(uri) == NOTES_ID)
        {
            s = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        return db.query(DBOpenHelper.TABLE_NOTES,DBOpenHelper.ALL_COLUMNS,s,null,null,null,
                DBOpenHelper.NOTE_CREATED + " DESC");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = db.insert(DBOpenHelper.TABLE_NOTES,null,contentValues);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return db.delete(DBOpenHelper.TABLE_NOTES,s,strings);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return db.update(DBOpenHelper.TABLE_NOTES,contentValues,s,strings);
    }
}
