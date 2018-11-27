package com.example.android.todo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.android.todo.data.TaskContract.TaskEntry.TABLE_NAME;

public class TaskContentProvider extends ContentProvider {


    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){


        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS + "/#", TASK_WITH_ID);
        return uriMatcher;

    }


    private TaskDbHelper mTaskDbHelper;
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTaskDbHelper = new TaskDbHelper(context);
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings,  @Nullable String s,  @Nullable String[] strings1,  @Nullable String s1) {
        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match){


            case TASKS:
                retCursor =  db.query(TABLE_NAME,
                        strings,
                       s,
                       strings1,
                        null,
                        null,
                       s1);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);


        return retCursor;
    }


    @Nullable
    @Override
    public String getType( @NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:

                return "vnd.android.cursor.dir" + "/" + TaskContract.AUTHORITY + "/" + TaskContract.PATH_TASKS;
            case TASK_WITH_ID:

                return "vnd.android.cursor.item" + "/" + TaskContract.AUTHORITY + "/" + TaskContract.PATH_TASKS;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }


    @Nullable
    @Override
    public Uri insert( @NonNull Uri uri,  @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case TASKS:

                long id = db.insert(TABLE_NAME, null, contentValues);
                if ( id > 0 ){

                    returnUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);

                }
                else {

                    throw new android.database.SQLException("Failed to insert row into " + uri);

                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public int delete( @NonNull Uri uri,  @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int tasksDeleted;
        switch (match) {

            case TASK_WITH_ID:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if (tasksDeleted != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }
        return tasksDeleted;
    }

    @Override
    public int update( @NonNull Uri uri,  @Nullable ContentValues contentValues, @Nullable String s,  @Nullable String[] strings) {
        int tasksUpdated;
        int match=sUriMatcher.match(uri);

        switch (match){
            case TASK_WITH_ID:
                String id=uri.getPathSegments().get(1);
                tasksUpdated=mTaskDbHelper.getWritableDatabase().update(TaskContract.TaskEntry.TABLE_NAME,contentValues,"_id=?",new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Not yet implemented");


        }
        if (tasksUpdated != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
            Log.v("updated",""+tasksUpdated);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return  tasksUpdated;
    }
}
