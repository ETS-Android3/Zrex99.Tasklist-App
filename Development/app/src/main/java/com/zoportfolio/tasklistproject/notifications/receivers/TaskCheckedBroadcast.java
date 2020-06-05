package com.zoportfolio.tasklistproject.notifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TaskCheckedBroadcast extends BroadcastReceiver {

    private static final String TAG = "TaskCheckedBroadcast.TAG";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            if(intent.hasExtra(TaskReminderBroadcast.EXTRA_TEST)) {
                Log.i(TAG, "onReceive: Intent has data. Message: " + intent.getStringExtra(TaskReminderBroadcast.EXTRA_TEST));
            }
        }
    }
}
