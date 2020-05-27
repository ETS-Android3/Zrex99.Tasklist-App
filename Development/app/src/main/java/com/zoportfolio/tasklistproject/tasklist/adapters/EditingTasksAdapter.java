package com.zoportfolio.tasklistproject.tasklist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zoportfolio.tasklistproject.R;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;

import java.util.ArrayList;

public class EditingTasksAdapter extends BaseAdapter {


    private static final String TAG = "EditTasksAdapter.TAG";

    private static final long BASE_ID = 0x122;

    private final Context mContext;
    private final ArrayList<UserTask> mTasks;

    private boolean mViewsEnabled;

    private EditingTasksAdapterListener mListener;
    public interface EditingTasksAdapterListener {
        void deleteActionTapped(UserTask userTask, int position);
        void taskTapped(UserTask userTask, int position);
    }

    public EditingTasksAdapter(Context _context, ArrayList<UserTask> _tasks, EditingTasksAdapterListener _listener, boolean _viewsEnabled) {
        mContext = _context;
        mTasks = _tasks;
        mListener = _listener;
        mViewsEnabled = _viewsEnabled;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        EditingTasksAdapter.ViewHolder vh;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.tasklist_editing_adapter_layout, parent, false);
            vh = new EditingTasksAdapter.ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh = (EditingTasksAdapter.ViewHolder) convertView.getTag();
        }

        //Get the task and fill the row with the regular information.
        final UserTask task = (UserTask) getItem(position);

        if(task != null) {

            //Set all of the dataModel info to the views.
            vh.tv_task.setText(task.getTaskName());

            //All image buttons should be the delete symbol at this point, they just need their click listener.
            //When setting the listeners check to see if the views should be enabled or not.
            if(mViewsEnabled) {
                //Set the click listeners to the views, and interface back to the fragment.
                vh.ib_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.deleteActionTapped(task, position);
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
