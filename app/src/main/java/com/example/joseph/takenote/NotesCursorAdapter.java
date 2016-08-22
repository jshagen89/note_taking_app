package com.example.joseph.takenote;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotesCursorAdapter extends CursorAdapter{

    public NotesCursorAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.note_list_item,parent,false);
    }

    private String formatDateCreated(String date)
    {
        String[] timeStampParts = date.split(" ");
        String[] dateParts = timeStampParts[0].split("-");
        String[] timeParts = timeStampParts[1].split(":");

        // Need to convert from military time to regular time here
        
        return (dateParts[1] + "-" + dateParts[2] + "-" + dateParts[0] + "  " + timeParts[0] + ":" + timeParts[1]);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // get text of note from database
        String noteText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
        String created_date = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_CREATED));
        String formattedDate = formatDateCreated(created_date);

        // determine if the note text contains a line feed character
        int position = noteText.indexOf(10);
        if (position != -1)
        {
            noteText = noteText.substring(0,position) + "...";
        }

        // Set the text of the view with the note text
        TextView noteTV = (TextView) view.findViewById(R.id.tvNote);
        noteTV.setText(noteText);

        // Set the text of the date created field
        TextView dateTV = (TextView) view.findViewById(R.id.date_created);
        dateTV.setText(formattedDate);
    }
}
