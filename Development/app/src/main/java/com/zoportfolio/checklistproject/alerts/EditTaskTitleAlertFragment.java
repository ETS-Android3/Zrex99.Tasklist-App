package com.zoportfolio.checklistproject.alerts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class EditTaskTitleAlertFragment extends Fragment {

    private static final String TAG = "ETaskTitleAFrag.TAG";

    private static final String ARG_TASKNAMES = "taskNames";
    private static final String ARG_TASKLISTNAME = "taskListName";

    //TODO: fill out the functionality in here

    public static EditTaskTitleAlertFragment newInstance() {
        
        Bundle args = new Bundle();
        
        EditTaskTitleAlertFragment fragment = new EditTaskTitleAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
