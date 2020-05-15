package com.zoportfolio.checklistproject.tasklist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zoportfolio.checklistproject.R;
import com.zoportfolio.checklistproject.tasklist.dataModels.UserTask;

import java.util.ArrayList;

public class TasksAdapter extends BaseAdapter {

    private static final String TAG = "TasksAdapter.TAG";

    private static final long BASE_ID = 0x104;

    private final Context mContext;
    private final ArrayList<UserTask> mTasks;

    private boolean mViewsEnabled;

    private TasksAdapterListener mListener;
    public interface TasksAdapterListener {
        void checkActionTapped(UserTask userTask, int position);
        void taskTapped(UserTask userTask, int position);
        void addTaskTapped();
    }

    public TasksAdapter(Context _context, ArrayList<UserTask> _tasks, TasksAdapterListener _listener, boolean _viewsEnabled) {
        mContext = _context;
        mTasks = _tasks;
        mListener = _listener;
        mViewsEnabled = _viewsEnabled;
    }

    @Override
    public int getCount() {
        if(mTasks != null) {
            //NOTE: This is part of the fix to the problem of only having the last row be custom when the total tasks was less than 5.
            // Since I only want the last row when there is 4 or less tasks, I return .size() + 1. Otherwise if there are 5 tasks then it returns just 5.
            if(mTasks.size() == 5) {
                return mTasks.size();
            }else {//Adding 1 for the last row, only if there is space (less than 5 tasks.)
                return mTasks.size() + 1;
            }
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

        int totalTasks;

        //NOTE: this was part of the fix to the problem of only having the last row be custom when the total tasks was less than 5.
        // Basically I've created a scenario where the add row will only be built if the totalTasks variable = 4 or less.
        // Since position variable indexes from 0 onward, 4 is technically the last row, so if totalTasks = 5 the add row won't appear.
        // But if I subtract 1 from totalTasks, I allow for position to equal totalTasks, and when that happens the add row is built and appears.
        if(mTasks.size() == 5) {
            totalTasks = 5;
        }else {
            totalTasks = getCount() - 1;
        }

        int viewType = getItemViewType(position);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //If the position is less than the number of total tasks, create a regular row.
        if(position < totalTasks) {
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

                //When setting the listeners check to see if the views should be enabled or not.
                if(mViewsEnabled) {
                    //Set the click listeners to the views, and interface back to the fragment.
                    vh.ib_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.checkActionTapped(task, position);
                        }
                    });

                    vh.tv_task.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.taskTapped(task, position);
                        }
                    });
                } else {
                    //Set the views to be not clickable or focused.
                    vh.ib_action.setClickable(false);
                    vh.tv_task.setClickable(false);
                }


            }
        }else if(position == totalTasks) { //When the position has reached the last row.

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

            if(mViewsEnabled) {
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
                //Set the views to be not clickable or focused.
                vh.ib_action.setClickable(false);
                vh.tv_task.setClickable(false);
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
