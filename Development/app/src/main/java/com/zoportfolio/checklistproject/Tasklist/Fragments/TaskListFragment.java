package com.zoportfolio.checklistproject.Tasklist.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.checklistproject.R;

public class TaskListFragment extends Fragment {

    //TODO: Just testing the view right now, need nothing else in here at the moment.

    public static TaskListFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TaskListFragment fragment = new TaskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TaskListFragmentListener mListener;
    public interface TaskListFragmentListener {
        //TODO: rename these callbacks accordingly.
        void taskTapped();
        void taskLongTapped();
        void editTapped();
        void addTaskTapped();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof TaskListFragmentListener) {
            mListener = (TaskListFragmentListener)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_layout_tasklist, container, false);
        //TODO: attach whatever views need to be attached to their objects.
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //TODO: main logic for the fragment.
        super.onActivityCreated(savedInstanceState);
    }
}
