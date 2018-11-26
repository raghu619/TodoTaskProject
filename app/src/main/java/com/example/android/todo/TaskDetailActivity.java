package com.example.android.todo;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.todo.adapters.TaskCommentAdapter;
import com.example.android.todo.data.CommentsDbHelper;
import com.example.android.todo.data.TaskContract;
import com.example.android.todo.reminderservice.AlarmReceiver;
import com.example.android.todo.reminderservice.LocalData;
import com.example.android.todo.reminderservice.NotificationScheduler;

import java.util.Calendar;

public class TaskDetailActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    private Bundle mBundle;

    private TextView mTextView;
    private TextView mTimePickerView;
    RecyclerView mRecyclerView;
    private CommentsDbHelper mCommentsDbHelper;
    private Context mContext;
    private int CommentId;
    private Cursor mCursor;
    LocalData localData;
    private TaskCommentAdapter mAdapter;
    private static final int TASK_LOADER_ID = 1;
    private static final int DAILY_REMINDER_REQUEST_CODE=2;
    private int mHour,mMinute;
    private  String mtitle;
    final LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        localData = new LocalData(getApplicationContext());
        mTextView=findViewById(R.id.addComment);
        mTimePickerView=findViewById(R.id.timepickerview);

        mHour = localData.get_hour();
        mMinute = localData.get_min();



        mCommentsDbHelper=new CommentsDbHelper(mContext);
        mBundle=getIntent().getExtras();
        mContext=this;


         mtitle=mBundle.getString("title");
        CommentId=mBundle.getInt("id");
        setTitle(mtitle);

        mRecyclerView = findViewById(R.id.recyclerCommentViewTasks);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new TaskCommentAdapter(this);
        mRecyclerView.setAdapter(mAdapter);


        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        mTimePickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c=Calendar.getInstance();

                TimePickerDialog timePickerDialog=new TimePickerDialog(TaskDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
//                        String status="AM";
//                        if(i>11){
//                            status="PM";
//                        }
//                   }
//
//                        int hour=timePicker.getHour();
//                        int minutes=timePicker.getMinute();
//
//                        setAlaram(hour,minutes,status);

                        localData.set_hour(i);
                        localData.set_min(i1);
                        localData.setTask(mtitle);


                        NotificationScheduler.setReminder(TaskDetailActivity.this, AlarmReceiver.class, localData.get_hour(), localData.get_min());






                    }
                },mHour,mMinute,false);

                timePickerDialog.show();
            }
        });

        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }


    private void openDialog(){


        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.comment_input_dialog, null);
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

                        ContentValues contentValues = new ContentValues();

                        contentValues.put(TaskContract.CommentsEntry.COLUMN_COMMENTS, input);
                        contentValues.put(TaskContract.CommentsEntry.COLUMN_COMMENT_ID,CommentId);

                       long value= mCommentsDbHelper.getWritableDatabase().insert(TaskContract.CommentsEntry.TABLE_NAME,null,contentValues);
                        if(value != 0) {
                            Toast.makeText(getBaseContext(), "Added Comment", Toast.LENGTH_LONG).show();

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

     final  Cursor mTaskData = null;
        mCommentsDbHelper=new CommentsDbHelper(mContext);


        return new AsyncTaskLoader<Cursor>(this) {
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

                Cursor mCursor=mCommentsDbHelper.getReadableDatabase().query(TaskContract.CommentsEntry.TABLE_NAME,null,"commentid=?",new String[]{String.valueOf(CommentId)},null,null,null);

                return mCursor;
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

//    public void setAlaram(Context context,Class<?> cls,int mHour,int mMinute,String status){
//
////        Intent myIntent=new Intent(this,NotifyService.class);
////        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
////        PendingIntent pendingIntent=PendingIntent.getService(this,0,myIntent,0);
////        Calendar calendar=Calendar.getInstance();
////        calendar.set(Calendar.SECOND,0);
////        calendar.set(Calendar.MINUTE,mMinute);
////        calendar.set(Calendar.HOUR,mHour);
////
////
////        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),1000*60*60*24,pendingIntent);
////
////          Toast.makeText(TaskDetailActivity.this,"Remineder start",Toast.LENGTH_LONG).show();
//
//
//        Calendar calendar = Calendar.getInstance();
//        Calendar setcalendar = Calendar.getInstance();
//        setcalendar.set(Calendar.HOUR_OF_DAY, mHour);
//        setcalendar.set(Calendar.MINUTE, mMinute);
//        setcalendar.set(Calendar.SECOND, 0);
//        if(setcalendar.before(calendar))
//            setcalendar.add(Calendar.DATE,1);
//        ComponentName receiver = new ComponentName(context, cls);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
//
//        Intent intent1 = new Intent(context, cls);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//                DAILY_REMINDER_REQUEST_CODE, intent1,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, pendingIntent);
//
//    }
}
