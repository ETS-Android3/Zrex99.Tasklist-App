package com.zoportfolio.checklistproject.task.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.checklistproject.R;
import com.zoportfolio.checklistproject.tasklist.dataModels.UserTask;

public class TaskInfoFragment extends Fragment {

    private static final String TAG = "TaskInfoFragment.TAG";

    private static final String ARG_USERTASK = "userTask";

    //Views
    private TextView mTvTitle;
    private TextView mTvNotificationTime;
    private TextView mTvDescription;
    private EditText mEtDescription;
    private Button mBtnChangeNotificationTime;
    private ImageButton mIbEdit;
    private ImageButton mIbTrash;
    private boolean mEditing = false;

    //DataModel
    private UserTask mUserTask;

    private static boolean isAlertUp = false;

    public static TaskInfoFragment newInstance(UserTask _userTask) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_USERTASK, _userTask);

        TaskInfoFragment fragment = new TaskInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TaskInfoFragmentListener mListener;
    public interface TaskInfoFragmentListener {
        //TODO: What callbacks are needed?
        // Edit tapped?

        void taskUpdated(UserTask updatedTask); //Used to update the variable editedTask in the Activity.
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof TaskInfoFragmentListener) {
            mListener = (TaskInfoFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_layout_taskinfo, container, false);
        mTvTitle = view.findViewById(R.id.tv_TaskTitle);
        mTvNotificationTime = view.findViewById(R.id.tv_TaskNotificationTime);
        mTvDescription = view.findViewById(R.id.tv_TaskDescription);
        mEtDescription = view.findViewById(R.id.et_TaskDescription);
        mBtnChangeNotificationTime = view.findViewById(R.id.btn_NotificationTimeChange);
        mIbEdit = view.findViewById(R.id.ib_Edit);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //TODO: Return here, filling out the fragment functionality and linking it up.

        //TODO:
        // Assign the views their data,
        // grab the task from the args,
        // and then work on editing features.
        
    }
}
