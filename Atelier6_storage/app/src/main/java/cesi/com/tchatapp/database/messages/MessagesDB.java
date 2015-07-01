package cesi.com.tchatapp.database.messages;

import android.provider.BaseColumns;

/**
 * Created by sca on 30/06/15.
 */
public class MessagesDB {

    /**
     * sql request to delete entries
     */
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME;

    private static final String TEXT_TYPE = " TEXT";

    private static final String LONG_TYPE = " LONG";

    private static final String BOOL_TYPE = " INT";

    private static final String COMMA_SEP = ",";

    /**
     * sql request to create Users table
     */
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + MessageEntry.TABLE_NAME + " (" +
            MessageEntry.COLUMN_MESSAGE + TEXT_TYPE + COMMA_SEP +                //column 2
            MessageEntry.COLUMN_DATE + LONG_TYPE + COMMA_SEP +              //column 3
            MessageEntry.COLUMN_USER+ LONG_TYPE + COMMA_SEP +              //column 4
            "PRIMARY KEY ("+MessageEntry.COLUMN_DATE+", "+MessageEntry.COLUMN_USER+")"+
            " )";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private MessagesDB() {
    }

    /* Inner class that defines the table contents */
    public abstract static class MessageEntry implements BaseColumns {

        public static final String TABLE_NAME = "messages";

        public static final String COLUMN_MESSAGE = "message";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_USER = "user";


    }
}
