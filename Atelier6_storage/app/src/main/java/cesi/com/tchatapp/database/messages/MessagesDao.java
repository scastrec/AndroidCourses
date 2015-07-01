package cesi.com.tchatapp.database.messages;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import cesi.com.tchatapp.database.DatabaseHelper;
import cesi.com.tchatapp.model.Message;

/**
 * Created by sca on 30/06/15.
 */
public class MessagesDao {

    /**
     * database access
     */
    private DatabaseHelper mDbHelper = null;

    public MessagesDao(final Context context) {
        mDbHelper = new DatabaseHelper(context);
    }

    /**
     * Write a new message.
     */
    public void writeMessage(final SQLiteDatabase db, final Message msg) {

        // Create a new map of values, where column names are the keys
        final ContentValues values = new ContentValues();
        values.put(MessagesDB.MessageEntry.COLUMN_MESSAGE, msg.getMsg());
        values.put(MessagesDB.MessageEntry.COLUMN_USER, msg.getUsername());
        values.put(MessagesDB.MessageEntry.COLUMN_DATE, msg.getDate());

        db.insert(MessagesDB.MessageEntry.TABLE_NAME, null, values);
    }

    public void writeMessages(List<Message> messages) {
        // Gets the data repository in write mode
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        for (Message msg: messages) {
            // insert
            writeMessage(db, msg);
        }
        db.close();

    }

    /**
     * retrieve all registered messages.
     */
    public List<Message> readMessages() {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        final String[] projection = {
                MessagesDB.MessageEntry.COLUMN_MESSAGE,
                MessagesDB.MessageEntry.COLUMN_USER,
                MessagesDB.MessageEntry.COLUMN_DATE,};


        final Cursor c = db.query(MessagesDB.MessageEntry.TABLE_NAME,       // The table to query
                projection,                               // The columns to return
                null,                                    // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                  // don't group the rows
                null,                                 // don't filter by row groups
                null                            // The sort order
        );
        final List<Message> messages = cursorToMessages(c);
        c.close();
        db.close();
        return messages;
    }

    /**
     * Transform cursor to messages
     *
     * @param cursor
     * @return
     */
    private List<Message> cursorToMessages(Cursor cursor) {
        final List<Message> msgs = new LinkedList<Message>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            msgs.add(cursorToMessage(cursor));
            cursor.moveToNext();
        }
        return msgs;
    }

    /**
     * Transform to Message
     *
     * @param cursor
     * @return
     */
    private Message cursorToMessage(Cursor cursor) {
        return new Message(
                cursor.getString(cursor.getColumnIndex(MessagesDB.MessageEntry.COLUMN_USER)),
                cursor.getString(cursor.getColumnIndex(MessagesDB.MessageEntry.COLUMN_MESSAGE)),
                cursor.getLong(cursor.getColumnIndex(MessagesDB.MessageEntry.COLUMN_DATE)));
    }
}
