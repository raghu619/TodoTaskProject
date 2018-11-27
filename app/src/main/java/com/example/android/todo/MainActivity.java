package com.example.android.todo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.todo.adapters.TaskCustomAdapter;
import com.example.android.todo.data.CommentsDbHelper;
import com.example.android.todo.data.TaskContract;

public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> ,TaskCustomAdapter.OnItemClickListener {

    private TaskCustomAdapter mAdapter;
    RecyclerView mRecyclerView;

    private ImageView mImageView;
    private static final int TASK_LOADER_ID = 0;
    private Cursor mCursor;
    final LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks=this;




    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerViewTasks);
        mImageView=findViewById(R.id.add_task_view);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new TaskCustomAdapter(this);
        mAdapter.SetOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   openDialog();


            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int id= (int) viewHolder.itemView.getTag();
                String stringId=Integer.toString(id);
                Uri uri=TaskContract.TaskEntry.CONTENT_URI;
                uri=uri.buildUpon().appendPath(stringId).build();
                getContentResolver().delete(uri,null,null);
                CommentsDbHelper commentsDbHelper=new CommentsDbHelper(getApplicationContext());
                commentsDbHelper.getWritableDatabase().delete(TaskContract.CommentsEntry.TABLE_NAME,"commentid=?",new String[]{stringId});
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID,null, MainActivity.this);



            }
        }).attachToRecyclerView(mRecyclerView);


        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

    }


    private void openDialog(){


        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.task_input_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);
        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String input = userInputDialogEditText.getText().toString();
                        if (input.length() == 0) {
                            return;
                        }
                        int mDefaultCompletedValue=0;
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input);
                        contentValues.put(TaskContract.TaskEntry.COLUMN_ISCOMPLETED,mDefaultCompletedValue);
                        Uri uri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);
                        if(uri != null) {
                            Toast.makeText(getBaseContext(), "Task is SucessFully Added", Toast.LENGTH_LONG).show();
                        }
                        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, cursorLoaderCallbacks);

                        dialogInterface.dismiss();

                    }
                })
             .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                 }
             });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {


        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {

                    deliverResult(mTaskData);
                } else {

                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                try {

                    return getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {

                    //  Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };


    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mCursor=cursor;

        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    @Override
    public void OnItemClick(View view, int position) {

        mCursor.moveToPosition(position);
        int idIndex = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        final int id = mCursor.getInt(idIndex);
        String description = mCursor.getString(descriptionIndex);



        Intent DetailActivityIntent = new Intent(getBaseContext(),TaskDetailActivity.class);



        Bundle mSendingArguments=new Bundle();

        mSendingArguments.putString("title",description);
        mSendingArguments.putInt("id",id);
        DetailActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DetailActivityIntent.putExtras( mSendingArguments);
        startActivity(DetailActivityIntent);

    }
}
