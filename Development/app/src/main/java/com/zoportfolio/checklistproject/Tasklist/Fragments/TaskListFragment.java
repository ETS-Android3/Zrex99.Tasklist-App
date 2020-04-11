package com.zoportfolio.checklistproject.Tasklist.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.checklistproject.R;
import com.zoportfolio.checklistproject.Tasklist.Adapters.TasksAdapter;
import com.zoportfolio.checklistproject.Tasklist.DataModels.UserTaskList;

public class TaskListFragment extends Fragment implements TasksAdapter.TasksAdapterListener {

    private static final String TAG = "TaskListFragment.TAG";
    
    //TODO: Implementing the rest of the logic, see TODOs below.

    private static final String ARG_USERTASKLIST = "userTaskList";

    //Views
    private ListView mLvTasks;
    private TextView mTvName;
    private ImageButton mIbEdit;
    private boolean mEditing = false;

    //DataModel
    private UserTaskList mTaskList;

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
            }

            //Set up the editing and edit button AFTER listView is setup.

        }else {
            Log.i(TAG, "onActivityCreated: Tasklist is null.");
        }

    }

    @Override
    public void actionTapped() {

    }
}
