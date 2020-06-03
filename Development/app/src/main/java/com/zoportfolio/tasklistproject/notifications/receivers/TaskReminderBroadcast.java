package com.zoportfolio.tasklistproject.notifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TaskReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: This Receiver will be the one that handles the notification building and functionality,
        // such as clicking the notification to launch the app and show the specific task,
        // checking off the specific task in the notification (if feasible).
    }
}
