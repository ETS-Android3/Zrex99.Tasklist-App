package com.zoportfolio.checklistproject.Tasklist.Adapters;

import android.content.Context;
import android.util.Log;
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

    private static final String TAG = "TasksAdapter.TAG";

    private static final long BASE_ID = 0x104;

    private final Context mContext;
    private final ArrayList<UserTask> mTasks;

    private TasksAdapterListener mListener;
    public interface TasksAdapterListener {
        void actionTapped(UserTask userTask, int position);
        void taskTapped(UserTask userTask, int position);
        void addTaskTapped();

    }

    public TasksAdapter(Context _context, ArrayList<UserTask> _tasks, TasksAdapterListener _listener) {
        mContext = _context;
        mTasks = _tasks;
        mListener = _listener;
    }

    @Override
    public int getCount() {
        if(mTasks != null) {
            //Adding 1 for the last row
            return mTasks.size() + 1;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder vh;

        int totalTask = getCount() - 1;
        int viewType = getItemViewType(position);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Check for the last row.
        //TODO: Need to check the logic of this if statement
        // Only want this last row to create if the position is the last position AND is not bigger than 5.
        if(position >= totalTask && position <= 6) {
            if(convertView == null) {
                if(viewType == 0) {
                    //Last row
                    convertView = layoutInflater.inflate(R.layout.tasklist_adapter_add_task_layout, parent, false);
                }
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            }else {
                vh = (ViewHolder) convertView.getTag();
            }

            //Set the click listeners to the views, and interface back to the fragment.
            vh.ib_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.addTaskTapped();
                }
            });
            vh.tv_task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.addTaskTapped();
                }
            });

        }else {
            //Get the task and fill the row with the regular information.
            final UserTask task = (UserTask) getItem(position);

            if(convertView == null) {
                convertView = layoutInflater.inflate(R.layout.tasklist_adapter_layout, parent, false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            }else {
                vh = (ViewHolder) convertView.getTag();
            }

            if(task != null) {

                //Set all of the dataModel info to the views.
                vh.tv_task.setText(task.getTaskName());

                //Check the state of the task and set it accordingly in the image button.
                if(task.getTaskChecked()) {
                    //True, set the image to checked
                    vh.ib_action.setImageResource(R.drawable.ic_action_check);
                }else {
                    vh.ib_action.setImageResource(R.drawable.unchecked_circle);
                }

                //Set the click listeners to the views, and interface back to the fragment.
                vh.ib_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.actionTapped(task, position);
                    }
                });

                vh.tv_task.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.taskTapped(task, position);
                    }
                });
            }
        }

        return convertView;
    }

    //Below two methods are for setting a custom last row.
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int lastPos = this.getCount()-1;
        if(position == lastPos && position != 6) {
            return 0;
        }
        return 1;
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
