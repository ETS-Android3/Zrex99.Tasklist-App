package com.zoportfolio.checklistproject.Alerts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.checklistproject.R;

import java.util.ArrayList;

public class NewTaskListAlertFragment extends Fragment {

    private static final String TAG = "TLAlertFragment.TAG";

    private static final String ARG_TASKLISTNAMES = "taskListNames";

    private EditText mEtNameField;
    private TextView mTvConfirmAction;
    private TextView mTvCancelAction;

    public static NewTaskListAlertFragment newInstance(ArrayList<String> _taskListNames) {

        Bundle args = new Bundle();
        args.putStringArrayList(ARG_TASKLISTNAMES, _taskListNames);

        NewTaskListAlertFragment fragment = new NewTaskListAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private NewTaskListAlertFragmentListener mListener;
    public interface NewTaskListAlertFragmentListener {
        void cancelTapped();
        void saveTapped(String taskListName);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof NewTaskListAlertFragmentListener) {
            mListener = (NewTaskListAlertFragmentListener)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //TODO: Will need to check the alert tasklist layout for any fixes that may need to be made.
        View view = getLayoutInflater().inflate(R.layout.fragment_layout_alert_tasklist, container, false);
        mEtNameField = view.findViewById(R.id.et_NewTaskListName);
        mTvConfirmAction = view.findViewById(R.id.tv_AlertConfirmText);
        mTvCancelAction = view.findViewById(R.id.tv_AlertCancelText);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() != null) {

            mEtNameField.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        if(ValidateField(mEtNameField)) {
                            mListener.saveTapped(mEtNameField.getText().toString());
                            return false;
                        }
                    }
                    return false;
                }
            });

            //Assign the click listeners to the confirm and cancel actions.
            mTvConfirmAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ValidateField(mEtNameField)) {
                        String newTaskListName = mEtNameField.getText().toString();
                        boolean nameTaken = false;

                        //Check all the names taken and then toast if the new name is already taken.
                        ArrayList<String> taskListNames = (getArguments() != null ? getArguments().getStringArrayList(ARG_TASKLISTNAMES) : null);
                        if(taskListNames != null && !taskListNames.isEmpty()) {
                            for (int i = 0; i < taskListNames.size(); i++) {
                                if(taskListNames.get(i).equals(newTaskListName)) {
                                    nameTaken = true;
                                    String toastString = getResources().getString(R.string.toast_NameTaken1) + " \"" + newTaskListName + "\" " + getResources().getString(R.string.toast_NameTaken2);

                                    Toast toastNameTaken = Toast.makeText(getActivity(),toastString,Toast.LENGTH_LONG);
                                    toastNameTaken.show();
                                }
                            }
                            if(!nameTaken) {
                                mListener.saveTapped(newTaskListName);
                            }
                        }else {
                            mListener.saveTapped(newTaskListName);
                        }
                    }
                }
            });

            mTvCancelAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.cancelTapped();
                }
            });


        }
    }

    /**
     * Custom Methods
     */

    //TODO: Fix the comments here later.
    //Will return a bool based on if the text is valid or not.
    private boolean ValidateField(EditText editText) {
        //Get the text and trim whitespace from it.
        String text = editText.getText().toString().trim();
        //If there is no text after trimming whitespace, return false.
        if(text.isEmpty()) {
            Activity a = getActivity();
            if(a != null) {
                Toast toastInvalidText = Toast.makeText(a, R.string.toast_TextInvalid, Toast.LENGTH_LONG);
                toastInvalidText.show();
            }
            return false;
        }else {
            //Return true for valid text.
            return true;
        }

    }

}
