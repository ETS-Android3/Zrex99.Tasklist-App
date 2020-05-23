package com.zoportfolio.checklistproject.alerts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.checklistproject.R;

import java.util.ArrayList;

public class EditTaskTitleAlertFragment extends Fragment {

    private static final String TAG = "ETaskTitleAFrag.TAG";

    private static final String ARG_TASKNAMES = "taskNames";
    private static final String ARG_TASKNAMETOEDIT = "taskNameToEdit";
    private static final String ARG_TASKLISTNAME = "taskListName";//Don't think i need this variable or arg in this fragment.

    //TODO: ALl that needs to be done here is just layout fixes and instanced state saves.

    private TextView mTvPrompt;
    private EditText mEtNameField;
    private TextView mTvConfirmAction;
    private TextView mTvCancelAction;

    public static EditTaskTitleAlertFragment newInstance(ArrayList<String> _taskNames, String _taskListName, String _taskNameToEdit) {
        
        Bundle args = new Bundle();
        args.putString(ARG_TASKNAMETOEDIT, _taskNameToEdit);
        args.putStringArrayList(ARG_TASKNAMES, _taskNames);
        args.putString(ARG_TASKLISTNAME, _taskListName);
        
        EditTaskTitleAlertFragment fragment = new EditTaskTitleAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private EditTaskTitleAlertFragmentListener mListener;
    public interface EditTaskTitleAlertFragmentListener {
        void cancelTappedEditTitle();
        void saveTappedEditTitle(String taskNameEdited);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof EditTaskTitleAlertFragmentListener) {
            mListener = (EditTaskTitleAlertFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_layout_alert_edit_task_title, container, false);
        mTvPrompt = view.findViewById(R.id.tv_AlertTitleText);
        mEtNameField = view.findViewById(R.id.et_NewTaskName);
        mTvConfirmAction = view.findViewById(R.id.tv_AlertConfirmText);
        mTvCancelAction = view.findViewById(R.id.tv_AlertCancelText);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Prompt text
        String taskNameToEdit = (getArguments() != null ? getArguments().getString(ARG_TASKNAMETOEDIT) : null);
        if(taskNameToEdit != null) {
            String promptString = "Please change the name of task: " + "\"" + taskNameToEdit;
            mTvPrompt.setText(promptString);
        }else {
            mTvPrompt.setText(getResources().getString(R.string.alert_newTaskTitleEdit));
        }

        //Cancel button
        mTvCancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancelTappedEditTitle();
            }
        });

        //Save button
        mTvConfirmAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateField(mEtNameField)) {

                    String newTaskName = mEtNameField.getText().toString();
                    ArrayList<String> taskNames = (getArguments() != null ? getArguments().getStringArrayList(ARG_TASKNAMES) : null);
                    boolean nameTaken = false;
                    if(taskNames != null) {
                        for (int i = 0; i < taskNames.size(); i++) {
                            if(newTaskName.equals(taskNames.get(i))) {
                                nameTaken = true;
                            }
                        }
                    }

                    String taskListName = (getArguments() != null ? getArguments().getString(ARG_TASKLISTNAME) : null);
                    if(!nameTaken && taskListName != null) {
                        mListener.saveTappedEditTitle(mEtNameField.getText().toString());
                    }else {
                        String toastString = getResources().getString(R.string.toast_Task_NameTaken1) +
                                " \"" + newTaskName +
                                "\" " + getResources().getString(R.string.toast_Task_NameTaken2);
                        Toast toastNameTaken = Toast.makeText(getActivity(),toastString,Toast.LENGTH_LONG);
                        toastNameTaken.show();
                    }
                }
            }
        });
    }


    /**
     * Custom Methods
     */

    //Will return a bool based on if the text is valid or not.
    private boolean validateField(EditText editText) {
        //Get the text and trim whitespace from it.
        String text = editText.getText().toString().trim();
        //If there is no text after trimming whitespace, return false.
        if(text.isEmpty()) {
            Toast toastNameTaken = Toast.makeText(getActivity(),getResources().getString(R.string.toast_TextInvalid),Toast.LENGTH_LONG);
            toastNameTaken.show();
            return false;
        }else {
            //Return true for valid text.
            return true;
        }

    }
}
