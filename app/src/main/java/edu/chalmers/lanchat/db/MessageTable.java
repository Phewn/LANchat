package edu.chalmers.lanchat.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Contract class for the table of messages. Mostly exists just to define some constant
 * names and ids.
 */
public class MessageTable {

    // Database table
    public static final String TABLE_MESSAGE = "message";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_POPULARITY = "popularity";
    public static final String COLUMN_COLOR = "color";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_MESSAGE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_POPULARITY + " integer not null, "
            + COLUMN_COLOR + " integer not null, "
            + COLUMN_MESSAGE + " text not null "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(MessageTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        onCreate(database);
    }
}
