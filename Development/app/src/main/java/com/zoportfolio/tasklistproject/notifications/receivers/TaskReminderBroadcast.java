package com.zoportfolio.tasklistproject.notifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zoportfolio.tasklistproject.MainActivity;

public class TaskReminderBroadcast extends BroadcastReceiver {

    private static final String TAG = "TReminderBroadcast.TAG";

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: This Receiver will be the one that handles the notification building and functionality,
        // such as clicking the notification to launch the app and show the specific task,
        // checking off the specific task in the notification (if feasible).


        //TODO: The intent im getting here is fault and does not have any data attached to it.
        if(intent != null) {
            Log.i(TAG, "onReceive: Intent is not null");
            if(intent.hasExtra(TasklistsRefreshBroadcast.EXTRA_TASK_BYTEDATA)) {
                Log.i(TAG, "onReceive: Intent has data, task received, TasklistsRefresh broadcast working!!! WOOOO");
            }
        }
    }
}
