package com.example.android.todo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "tasksDb.db";

    private static final int VERSION = 1;
    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE ="CREATE TABLE "+ TaskContract.TaskEntry.TABLE_NAME+" ("+
                TaskContract.TaskEntry._ID +" INTEGER PRIMARY KEY , "+
                TaskContract.TaskEntry.COLUMN_DESCRIPTION +" TEXT NOT NULL,"+
                TaskContract.TaskEntry.COLUMN_ISCOMPLETED +" INTEGER NOT NULL);";


        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME);

    }
}
