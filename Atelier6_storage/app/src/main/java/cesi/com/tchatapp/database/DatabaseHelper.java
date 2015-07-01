package cesi.com.tchatapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cesi.com.tchatapp.database.messages.MessagesDB;

/**
 * Created by sca on 30/06/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "tchatApp.db";

    public DatabaseHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(MessagesDB.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int i, final int i2) {
        //DO NOTHING for now
    }
}