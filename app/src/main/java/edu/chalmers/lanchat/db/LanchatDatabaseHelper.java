package edu.chalmers.lanchat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LanchatDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lanchat.db";
    private static final int DATABASE_VERSION = 1;

    public LanchatDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        ClientTable.onCreate(database);
        MessageTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        ClientTable.onUpgrade(database, oldVersion, newVersion);
        MessageTable.onUpgrade(database, oldVersion, newVersion);
    }
}

