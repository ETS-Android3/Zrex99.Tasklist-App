package com.zoportfolio.tasklistproject.task.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.tasklistproject.R;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;
import com.zoportfolio.tasklistproject.utility.TimeConversionUtility;

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
        void taskUpdated(UserTask updatedTask); //Used to update the variable editedTask in the Activity.
        void editNotificationTime(String notificationTime);
        void editTitle(String taskTitle);
    }

    /**
     * Lifecycle methods
     */

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

        mUserTask = (UserTask) (getArguments() != null ? getArguments().getSerializable(ARG_USERTASK) : null);
        if(mUserTask != null) {
            Log.i(TAG, "onActivityCreated: Task valid: " + mUserTask.getTaskName());

            mTvTitle.setText(mUserTask.getTaskName());
            Log.i(TAG, "onActivityCreated: title set.");

            String taskNotificationTime = mUserTask.getTaskNotificationTime();
            String[] notificationTimeSplit = taskNotificationTime.split("/");

            String hour = notificationTimeSplit[0];
            String minute = notificationTimeSplit[1];
            String meridies = notificationTimeSplit[2];

            String convertedTime = TimeConversionUtility.convertMilitaryHourFormatToStandardHourFormat(String.valueOf(hour), meridies) + ":" + String.valueOf(minute) + " " + meridies;

            String notificationTimeDisplayText = mTvNotificationTime.getText().toString() + " " + convertedTime;

            mTvNotificationTime.setText(notificationTimeDisplayText);
            Log.i(TAG, "onActivityCreated: notification time set.");

            if(mUserTask.getTaskDescription() != null) {
                mTvDescription.setText(mUserTask.getTaskDescription());
                Log.i(TAG, "onActivityCreated: description filled from data.");
            }else {
                mTvDescription.setText(getResources().getString(R.string.task_descriptionNoData));
                Log.i(TAG, "onActivityCreated: no description data.");
            }

            //Fill out the editing functionality.
            mIbEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Check the editing state, control flow from there
                    if(!mEditing) { //The task info is not being edited.
                        //Set the fragment to be editing the list view.
                        mEditing = true;
                        //Load the editing adapter.

                        //All of the editing is done and handled now.
                        setUpDescriptionEditing();

                        setUpNotificationEditing(mUserTask.getTaskNotificationTime());

                        setUpTitleEditing();

                    }else {//The task info is in edit state.
                        //Set the fragment back to its natural state.
                        //Save the updated task data.
                        mEditing = false;
                        takeDownEditingState();
                        mListener.taskUpdated(mUserTask);
                    }
                }
            });
        }

    }

    /**
     * Interface methods
     */

    /**
     * Custom methods
     */

    private void setUpTitleEditing() {
        mTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditing) {
                    //Interface to the activity to display the title change alert, only if the editing variable is true.
                    mListener.editTitle(mUserTask.getTaskName());
                }
            }
        });
    }

    private void setUpDescriptionEditing() {
        //Hide the tv description and show the et description
        String description = mTvDescription.getText().toString();
        mTvDescription.setVisibility(View.GONE);

        mEtDescription.setText(description);
        mEtDescription.setVisibility(View.VISIBLE);
        mEtDescription.addTextChangedListener(mTextEditorWatcher);
    }

    private void setUpNotificationEditing(final String notificationTime) {
        //Show the notification button
        mBtnChangeNotificationTime.setVisibility(View.VISIBLE);
        mBtnChangeNotificationTime.setEnabled(true);
        mBtnChangeNotificationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Have to create an alert for just the timepicker with cancel and save.
                // Then show this alert here.
                // Use interface to have the activity handle the alert fragment.
                mListener.editNotificationTime(notificationTime);
            }
        });
    }

    private void takeDownEditingState() {
        String editedDescription = mEtDescription.getText().toString();
        mUserTask.setTaskDescription(editedDescription);//Set the changed data to the UserTask.
        mEtDescription.setVisibility(View.GONE);

        mBtnChangeNotificationTime.setVisibility(View.GONE);
        mBtnChangeNotificationTime.setEnabled(false);

        mTvDescription.setText(editedDescription);
        mTvDescription.setVisibility(View.VISIBLE);
    }

    /**
     * Custom classes
     */

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count==250) {
                Log.i(TAG, "onTextChanged: character limit hit");
                //TODO: Need to test this
                Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
