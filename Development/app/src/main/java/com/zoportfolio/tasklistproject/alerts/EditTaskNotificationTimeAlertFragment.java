package com.zoportfolio.tasklistproject.alerts;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.tasklistproject.R;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;
import com.zoportfolio.tasklistproject.utility.TimeConversionUtility;

public class EditTaskNotificationTimeAlertFragment extends Fragment {

    private static final String TAG = "ETaskNotifTimeAFrag.TAG";

    private static final String ARG_TASK_NOTIFICATION_TIME_EDIT = "taskNotificationTimeEdit";

    private TextView mTvNotificationTimeEdited;
    private TimePicker mTpNotificationTime;
    private TextView mTvConfirmAction;
    private TextView mTvCancelAction;

    private String mNotificationTime;

    public static EditTaskNotificationTimeAlertFragment newInstance(String _notificationTimeEdit) {

        Bundle args = new Bundle();
        args.putString(ARG_TASK_NOTIFICATION_TIME_EDIT, _notificationTimeEdit);

        EditTaskNotificationTimeAlertFragment fragment = new EditTaskNotificationTimeAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private EditTaskNotificationTimeAlertFragmentListener mListener;
    public interface EditTaskNotificationTimeAlertFragmentListener {
        void cancelTappedEditNotificationTime();
        void saveTappedEditNotificationTime(String taskNotificationTimeEdited);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof EditTaskNotificationTimeAlertFragmentListener) {
            mListener = (EditTaskNotificationTimeAlertFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_layout_alert_edit_task_notification_time, container, false);
        mTvNotificationTimeEdited = view.findViewById(R.id.tv_AlertNewNotificationTime);
        mTpNotificationTime = view.findViewById(R.id.tp_NotificationTime);
        mTpNotificationTime.setIs24HourView(false);
        mTvConfirmAction = view.findViewById(R.id.tv_AlertConfirmText);
        mTvCancelAction = view.findViewById(R.id.tv_AlertCancelText);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        String notificationTimeToEdit = (getArguments() != null ? getArguments().getString(ARG_TASK_NOTIFICATION_TIME_EDIT) : null);
        if(notificationTimeToEdit != null) {

            String[] notificationTimeSplit = notificationTimeToEdit.split("/");
            String hour = notificationTimeSplit[0];
            String minute = notificationTimeSplit[1];

            mTpNotificationTime.setHour(Integer.parseInt(hour));
            mTpNotificationTime.setMinute(Integer.parseInt(minute));

            mTpNotificationTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    String meridies;
                    if(hourOfDay < 12) {
                        meridies = "AM";
                    } else {
                        meridies = "PM";
                    }

                    mNotificationTime = UserTask.formatNotificationTime(hourOfDay, minute, meridies);

                    //This will update the text view, as the user is changing the time.
                    String convertedTime = "";
                    if(minute < 10) {
                        convertedTime = TimeConversionUtility.convertMilitaryHourFormatToStandardHourFormat(String.valueOf(hourOfDay), meridies) + ":0" + String.valueOf(minute) + " " + meridies;
                    }else {
                        convertedTime = TimeConversionUtility.convertMilitaryHourFormatToStandardHourFormat(String.valueOf(hourOfDay), meridies) + ":" + String.valueOf(minute) + " " + meridies;
                    }
                    String tvString = getResources().getString(R.string.alert_TaskNotificationTimeEdited) + " " + convertedTime;
                    mTvNotificationTimeEdited.setText(tvString);
                }
            });
        }



        mTvCancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancelTappedEditNotificationTime();
            }
        });
        mTvConfirmAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNotificationTime != null) {
                    mListener.saveTappedEditNotificationTime(mNotificationTime);
                }else {
                    mListener.saveTappedEditNotificationTime("8/00/AM");
                }

            }
        });
    }

    /**
     * Custom Methods
     */
}
