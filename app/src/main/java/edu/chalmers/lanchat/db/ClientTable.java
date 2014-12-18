package edu.chalmers.lanchat.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ClientTable {

    // Database table
    public static final String TABLE_CLIENT = "client";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IP = "ip";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CLIENT
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_IP + " text unique not null "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ClientTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENT);
        onCreate(database);
    }
}
