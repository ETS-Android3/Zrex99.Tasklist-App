package com.zoportfolio.checklistproject.alerts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.checklistproject.R;
import com.zoportfolio.checklistproject.utility.TimeConversionUtility;

public class EditTaskNotificationTimeAlertFragment extends Fragment {

    private static final String TAG = "ETaskNotifTimeAFrag.TAG";

    private static final String ARG_TASK_NOTIFICATION_TIME_EDIT = "taskNotificationTimeEdit";

    //TODO: Working here nexts
    // This fragment needs everything: member variables, constructors, lifecycle methods, interface, and custom methods that EditTaskTitleFragment has.

    private TextView mTvNotificationTimeEdited;
    private TimePicker mTpNotificationTime;
    private TextView mTvConfirmAction;
    private TextView mTvCancelAction;

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
            //TODO: Set the time to the timepicker, so it opens at the time that was already set.

            String[] notificationTimeSplit = notificationTimeToEdit.split("/");

            String hour = notificationTimeSplit[0];
            String minute = notificationTimeSplit[1];
            String meridies = notificationTimeSplit[2];

            int convertedHour = TimeConversionUtility.convertStandardHourFormatToMilitaryHourFormat(hour, meridies);

            mTpNotificationTime.setHour(convertedHour);
            mTpNotificationTime.setMinute(Integer.parseInt(minute));

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

            }
        });
    }

    /**
     * Custom Methods
     */
}
