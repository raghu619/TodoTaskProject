package com.example.android.todo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CommentsDbHelper  extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "commentsDb.db";
    private static final int VERSION = 1;

    public CommentsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {



        final String CREATE_TABLE ="CREATE TABLE "+ TaskContract.CommentsEntry.TABLE_NAME+" ("+
                TaskContract.CommentsEntry._ID +" INTEGER PRIMARY KEY , "+
                TaskContract.CommentsEntry.COLUMN_COMMENTS +" TEXT NOT NULL,"+
                TaskContract.CommentsEntry.COLUMN_COMMENT_ID+" INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TaskContract.CommentsEntry.TABLE_NAME);


    }
}
