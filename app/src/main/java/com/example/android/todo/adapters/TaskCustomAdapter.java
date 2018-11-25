package com.example.android.todo.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todo.R;
import com.example.android.todo.data.TaskContract;
import com.example.android.todo.data.TaskDbHelper;

public class TaskCustomAdapter extends RecyclerView.Adapter<TaskCustomAdapter.TaskViewHolder>  {

private  static final String LOG_TAG=TaskCustomAdapter.class.getSimpleName();
    private Cursor mCursor;
    private Context mContext;






  OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {

        void OnItemClick(View view, int position);

    }


    public TaskCustomAdapter(Context mContext) {
        this.mContext = mContext;


    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_item_layout, parent, false);

        return new TaskViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final  TaskViewHolder holder, final int position) {


        mCursor.moveToPosition(position);
        int idIndex = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
      //  int checkedtaskIndex=mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED);
        final int id = mCursor.getInt(idIndex);

        String description = mCursor.getString(descriptionIndex);
        holder.itemView.setTag(id);
        holder.taskDescriptionView.setText(description);

        holder.mCompleteTaskCheck.setOnCheckedChangeListener(null);





     //   Log.v(LOG_TAG,""+description+"  "+ischeckTask+" "+checkedtaskIndex);






    }



        public Cursor swapCursor(Cursor c) {

        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView taskDescriptionView;
        CheckBox mCompleteTaskCheck;




        public TaskViewHolder(View itemView) {
            super(itemView);

            taskDescriptionView = (TextView) itemView.findViewById(R.id.taskDescription);
            mCompleteTaskCheck=itemView.findViewById(R.id.task_checkbox);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);




        }


        @Override
        public void onClick(View view) {

            if (mItemClickListener != null) {
                mItemClickListener.OnItemClick(view, getAdapterPosition());
            }

        }
    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}
