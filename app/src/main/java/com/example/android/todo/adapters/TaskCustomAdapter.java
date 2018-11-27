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
    private  int isChecked;






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
       int checkedtaskIndex=mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED);
        final int id = mCursor.getInt(idIndex);


        String description = mCursor.getString(descriptionIndex);
        holder.itemView.setTag(id);
        holder.taskDescriptionView.setText(description);


        Cursor mcursor = getData( position+1);
        if (mcursor != null && mcursor.getCount()!=0 ) {
            mcursor.moveToFirst();

           isChecked = mcursor.getInt(mcursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED));

               if (isChecked == 1)
                   holder.mCompleteTaskCheck.setChecked(true);
               else
                   holder.mCompleteTaskCheck.setChecked(false);


        }



        holder.mCompleteTaskCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Cursor mcursor = getData(position + 1);
                if(mcursor!=null && mcursor.getCount()!=0) {
                    mcursor.moveToFirst();

                    isChecked = mcursor.getInt(mcursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED));

                }

                if(isChecked==0)
                {

                    boolean isupdated= update(position+1,1);
                    if(isupdated) {
                        Cursor mcursor1 = getData(position + 1);
                        if (mcursor1 != null && mcursor1.getCount() != 0) {
                            mcursor1.moveToFirst();
                           isChecked= mcursor1.getInt(mcursor1.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED));

                        }
                        Toast.makeText(mContext,"Marked as Completed",Toast.LENGTH_SHORT).show();

                    }

                }else {

                    boolean isupdated = update(position + 1, 0);
                    if(isupdated){
                        Cursor mcursor1=getData(position+1);
                        if(mcursor1!=null && mcursor1.getCount()!=0) {
                            mcursor1.moveToFirst();
                           isChecked = mcursor1.getInt(mcursor1.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED));

                        }








                    }

                }



            }
        });












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

     public Cursor getData(int id){
         String stringId=Integer.toString(id);
         Uri uri=TaskContract.TaskEntry.CONTENT_URI;
         Cursor res=mContext.getContentResolver().query(uri,null,"_id=?",new String[]{stringId},null);
         return  res;

     }

     public boolean update(Integer id,int val){

         String stringId=Integer.toString(id);
         Uri uri=TaskContract.TaskEntry.CONTENT_URI;
         uri=uri.buildUpon().appendPath(stringId).build();
         ContentValues values=new ContentValues();
         values.put(TaskContract.TaskEntry.COLUMN_ISCOMPLETED,val);

         mContext.getContentResolver().update(uri,values,null,null);

         return true;


     }


}
