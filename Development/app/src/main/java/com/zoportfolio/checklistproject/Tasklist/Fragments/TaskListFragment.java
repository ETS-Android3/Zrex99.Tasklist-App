package com.zoportfolio.checklistproject.Tasklist.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.zoportfolio.checklistproject.Alerts.NewTaskAlertFragment;
import com.zoportfolio.checklistproject.Alerts.NewTaskListAlertFragment;
import com.zoportfolio.checklistproject.R;
import com.zoportfolio.checklistproject.Tasklist.Adapters.TasksAdapter;
import com.zoportfolio.checklistproject.Tasklist.DataModels.UserTask;
import com.zoportfolio.checklistproject.Tasklist.DataModels.UserTaskList;

import java.util.ArrayList;

public class TaskListFragment extends Fragment implements TasksAdapter.TasksAdapterListener, NewTaskAlertFragment.NewTaskAlertFragmentListener {

    private static final String TAG = "TaskListFragment.TAG";

    private static final String FRAGMENT_ALERT_NEWTASK_TAG = "FRAGMENT_ALERT_NEWTASK";
    
    //TODO: Implementing the rest of the logic, see TODOs below.

    private static final String ARG_USERTASKLIST = "userTaskList";

    private FragmentActivity mContext;

    //Views
    private ListView mLvTasks;
    private TextView mTvName;
    private ImageButton mIbEdit;
    private boolean mEditing = false;

    //DataModel
    private UserTaskList mTaskList;

    private static Boolean isAlertUp = false;

    public static TaskListFragment newInstance(UserTaskList _userTaskList) {
        
        Bundle args = new Bundle();
        args.putSerializable(ARG_USERTASKLIST, _userTaskList);

        TaskListFragment fragment = new TaskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TaskListFragmentListener mListener;

    public interface TaskListFragmentListener {
        //TODO: rename these callbacks accordingly.
        void taskTapped();

        void editTapped();

        //TODO: Will need a tasklist ID,
        // potential solution = use tasklist name, prevent user from entering duplicate names.
        void taskListUpdated(UserTaskList updatedTaskList);
    }

    @Override
    public void onAttach(Context context) {
        mContext = (FragmentActivity) context;
        super.onAttach(context);
        if(context instanceof TaskListFragmentListener) {
            mListener = (TaskListFragmentListener)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_layout_tasklist, container, false);
        mTvName = view.findViewById(R.id.tv_TaskListTitle);
        mIbEdit = view.findViewById(R.id.ib_Edit);
        mLvTasks = view.findViewById(R.id.lv_tasks);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //TODO: main logic for the fragment.
        mTaskList = (UserTaskList) (getArguments() != null ? getArguments().getSerializable(ARG_USERTASKLIST) : null);

        if(getActivity() != null && mTaskList != null) {
            mTvName.setText(mTaskList.getTaskListName());

            //Fill adapter and set it to the listView.
            if(mTaskList.getTasks() != null) {
                TasksAdapter ta = new TasksAdapter(getActivity(), mTaskList.getTasks(), this);
                mLvTasks.setAdapter(ta);
                mLvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.i(TAG, "onItemClick: Row:" + position + " Task: " + mTaskList.getTasks().get(position).getTaskName());
                    }
                });
            }

            //TODO: Set up the editing and edit button AFTER:
            // (listView is setup) DONE
            // (adding tasks) WORKING ON

        }else {
            Log.i(TAG, "onActivityCreated: Tasklist is null.");
        }
    }


    @Override
    public void actionTapped(UserTask userTask, int position) {
        Log.i(TAG, "actionTapped: Task: " + userTask.getTaskName()
                + " \nNotification Time: " + userTask.getTaskNotificationTime()
                + " \nPosition in tasklist: " + position
                + " \nChecked: " + userTask.getTaskChecked());
        //TODO: Update the task that was changed, and then update the tasklist.

        //If true set to false, if false set to true.
        //I have a feeling this may give some problems, will have to keep in mind for later.
        userTask.setTaskChecked(!userTask.getTaskChecked());

        //Grab the tasks from the tasklist.
        //Assign the updated task to the tasks.
        //Set the updated tasks to the tasklist.
        ArrayList<UserTask> tasks = mTaskList.getTasks();
        tasks.set(position, userTask);
        mTaskList.setTasks(tasks);

        //TODO: Need to update the tasklist in terms of local storage.

        //Set the adapter.
        TasksAdapter ta = new TasksAdapter(getActivity(), mTaskList.getTasks(), this);
        mLvTasks.setAdapter(ta);
    }

    @Override
    public void taskTapped(UserTask userTask, int position) {
        //TODO: Open the next activity that is a task info screen... [LATER]
    }

    @Override
    public void addTaskTapped() {
        Log.i(TAG, "addTaskTapped: adding task");

        //TODO: So far this method works perfectly, flaws are with the NewTaskAlertFragment Class.
        if(!isAlertUp) {
            Activity a = getActivity();

            if(a != null) {
                FrameLayout frameLayout = a.findViewById(R.id.fragment_Container_AlertNewTask);
                frameLayout.setVisibility(View.VISIBLE);

                mContext.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_Container_AlertNewTask, NewTaskAlertFragment.newInstance(), FRAGMENT_ALERT_NEWTASK_TAG)
                        .commit();
                Log.i(TAG, "addTaskTapped: Showing new task alert.");

                isAlertUp = true;
            }
        }

    }

    @Override
    public void cancelTapped() {
        closeAlertFragment();
    }

    @Override
    public void saveTapped(String taskListName, String taskNotificationTime) {

        //TODO: Do what needs to be done with the new task information and then close the alert.

        closeAlertFragment();

    }

    private void closeAlertFragment() {
        
        Activity a = getActivity();
        if(a != null) {
            //Get the fragment by its tag, and null check it.
            Fragment fragment = mContext.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ALERT_NEWTASK_TAG);
            if(fragment != null) {
                //Hide the frame layout.
                FrameLayout frameLayout = a.findViewById(R.id.fragment_Container_AlertNewTask);
                frameLayout.setVisibility(View.GONE);

                //Remove the fragment.
                mContext.getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
                Log.i(TAG, "closeAlertFragment: closing new task alert.");

                //Set the bool to false, so a new alert can appear.
                isAlertUp = false;
            }
        }
        

    }



}
