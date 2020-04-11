package com.zoportfolio.checklistproject.Tasklist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zoportfolio.checklistproject.R;
import com.zoportfolio.checklistproject.Tasklist.DataModels.UserTask;

import java.util.ArrayList;

public class TasksAdapter extends BaseAdapter {

    private static final long BASE_ID = 0x104;

    private final Context mContext;
    private final ArrayList<UserTask> mTasks;

    private TasksAdapterListener mListener;
    public interface TasksAdapterListener {
        void actionTapped();
    }

    public TasksAdapter(Context _context, ArrayList<UserTask> _tasks, TasksAdapterListener _listener) {
        mContext = _context;
        mTasks = _tasks;
        mListener = _listener;
    }

    @Override
    public int getCount() {
        if(mTasks != null) {
            return mTasks.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mTasks != null && position >= 0) {
            return mTasks.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return BASE_ID + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;
        UserTask task = (UserTask) getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.tasklist_adapter_layout, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }


        if(task != null) {
            vh.tv_task.setText(task.getTaskName());

            //Check the state of the task and set it accordingly in the image button.
            if(task.getTaskChecked()) {
                //True, set the image to checked
                vh.ib_action.setImageResource(R.drawable.ic_action_check);
            }else {
                vh.ib_action.setImageResource(R.drawable.unchecked_circle);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        final TextView tv_task;
        final ImageButton ib_action;
        private ViewHolder(View _layout) {
            tv_task = _layout.findViewById(R.id.tv_task);
            ib_action = _layout.findViewById(R.id.ib_action);
        }

    }

}
