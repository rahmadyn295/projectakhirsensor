package com.hp.projekakhirsensor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "step_counter.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STEP_TABLE = "CREATE TABLE " + DatabaseContract.StepEntry.TABLE_NAME + " (" +
                DatabaseContract.StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseContract.StepEntry.COLUMN_STEP_COUNT + " INTEGER)";
        db.execSQL(CREATE_STEP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.StepEntry.TABLE_NAME);
        onCreate(db);
    }
}
