package com.example.android.todo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.todo.R;
import com.example.android.todo.data.TaskContract;

public class TaskCommentAdapter extends RecyclerView.Adapter<TaskCommentAdapter.CommentViewHolder>  {


    private Cursor mCursor;
    private Context mContext;


    public TaskCommentAdapter(Context mContext){

        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.comment_view_layout,viewGroup, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {

        mCursor.moveToPosition(i);
        int idIndex = mCursor.getColumnIndex(TaskContract.CommentsEntry._ID);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.CommentsEntry.COLUMN_COMMENTS);
        final int id = mCursor.getInt(idIndex);
        String description = mCursor.getString(descriptionIndex);
        commentViewHolder.CommentView.setText(description);
        commentViewHolder.itemView.setTag(id);




    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView CommentView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            CommentView=itemView.findViewById(R.id.postedOn);

        }
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
}
