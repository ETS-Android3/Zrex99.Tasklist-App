package com.zoportfolio.checklistproject.Alerts;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.checklistproject.R;
import com.zoportfolio.checklistproject.Tasklist.DataModels.UserTask;

public class NewTaskAlertFragment extends Fragment {

    private static final String TAG = "TAlertFragment.TAG";

    private EditText mEtNameField;
    private TimePicker mTpNotificationTime;
    private TextView mTvConfirmAction;
    private TextView mTvCancelAction;

    private String mNotificationTime;

    public static NewTaskAlertFragment newInstance() {

        Bundle args = new Bundle();

        NewTaskAlertFragment fragment = new NewTaskAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private NewTaskAlertFragmentListener mListener;
    public interface NewTaskAlertFragmentListener {
        void cancelTappedNewTaskAlert();
        void saveTappedNewTaskAlert(String taskListName, String taskNotificationTime);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof NewTaskAlertFragmentListener) {
            mListener = (NewTaskAlertFragmentListener) context;
            Log.i(TAG, "onAttach: Listener attached");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.fragment_layout_alert_task, container, false);
        mEtNameField = view.findViewById(R.id.et_NewTaskListName);
        mTpNotificationTime = view.findViewById(R.id.tp_NotificationTime);
        //Meridies options were not showing due to constraints being too small.
        mTpNotificationTime.setIs24HourView(false);
        mTvConfirmAction = view.findViewById(R.id.tv_AlertConfirmText);
        mTvCancelAction = view.findViewById(R.id.tv_AlertCancelText);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() != null) {

            //mTpNotificationTime.setIs24HourView(false);
            mTpNotificationTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    String meridies;
                    if(hourOfDay < 12) {
                        meridies = "AM";
                    } else {
                        meridies = "PM";
                    }

                    Log.i(TAG, "onTimeChanged: Hour: " + hourOfDay + " Minute: " + minute + " Meridies: " + meridies);
                    mNotificationTime = UserTask.formatNotificationTime(hourOfDay, minute, meridies);
                }
            });



            //Assign the click listeners to the confirm and cancel actions.
            mTvConfirmAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ValidateField(mEtNameField)) {

                        mListener.saveTappedNewTaskAlert(mEtNameField.getText().toString(), mNotificationTime);
                        //TODO: Need to check the new name against all current tasklist names here.
                    }
                }
            });

            mTvCancelAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.cancelTappedNewTaskAlert();
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
            //TODO: Toast here that the field is not valid and needs text.
            return false;
        }else {
            //Return true for valid text.
            return true;
        }

    }


}
