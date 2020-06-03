package com.zoportfolio.tasklistproject.notifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TasklistsRefreshBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: This Receiver will be the one that handles refreshing all of the alarmManager sets
        // and assigning the times to notifiy based on the task, using the TaskReminderBroadcast.
    }
}
