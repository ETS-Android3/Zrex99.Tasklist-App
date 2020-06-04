package com.zoportfolio.tasklistproject.notifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zoportfolio.tasklistproject.MainActivity;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;

public class TaskReminderBroadcast extends BroadcastReceiver {

    private static final String TAG = "TReminderBroadcast.TAG";

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: This Receiver will be the one that handles the notification building and functionality,
        // such as clicking the notification to launch the app and show the specific task,
        // checking off the specific task in the notification (if feasible).
        if(intent != null) {
            if(intent.hasExtra(TasklistsRefreshBroadcast.EXTRA_TASK_BYTEDATA)) {
                UserTask userTask = convertUserTaskFromByteData(intent.getByteArrayExtra(TasklistsRefreshBroadcast.EXTRA_TASK_BYTEDATA));
                createNotificationForTask(userTask);
            }
        }
    }

    private UserTask convertUserTaskFromByteData(byte[] _byteData) {
        return UserTask.deserializeUserTaskByteData(_byteData);
    }

    private void createNotificationForTask(UserTask _userTask) {
        Log.i(TAG, "createNotificationForTask: Creating Notification");
        Log.i(TAG, "createNotificationForTask: TaskName: " + _userTask.getTaskName());
        //TODO: Return here to work next. Will keep the tasklists refresh broadcast with testing variables for now.


        //TODO: Create a notification that displays the task name + description (if there is one),
        // and has an action for checking off the task in the notification, and an action for viewing the task.
        // The action that checks off the task will fire off another intent im assuming,
        // that will update the tasklists and if this is the case, I will need to implement the rand UUID for all tasks,
        // or send the tasklist into this broadcast receiver OR load the tasklists from storage and look for the task with the same name in the code/work/receiver.
        // The action that views the task will launch the taskinfoactivity, this will need all tasklists and the task.
    }



}
