package com.zoportfolio.tasklistproject.tasklist.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTaskList;
import com.zoportfolio.tasklistproject.tasklist.fragments.TaskListFragment;

import java.util.ArrayList;

public class TaskListFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "TLFragmentPagerAd.TAG";

    private ArrayList<UserTaskList> mTaskLists;

    public TaskListFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<UserTaskList> _taskLists) {
        super(fm, behavior);
        mTaskLists = _taskLists;
        Log.i(TAG, "TaskListFragmentPagerAdapter: Constructed");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //Return a new instance of the TaskList Fragment, that is created with the right tasklist.
        return TaskListFragment.newInstance(mTaskLists.get(position));
    }

    @Override
    public int getCount() {
        return mTaskLists.size();
    }
}
