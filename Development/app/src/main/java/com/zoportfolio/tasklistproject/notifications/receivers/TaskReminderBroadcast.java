package com.zoportfolio.tasklistproject.notifications.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.zoportfolio.tasklistproject.MainActivity;
import com.zoportfolio.tasklistproject.R;
import com.zoportfolio.tasklistproject.task.TaskInfoActivity;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;

import java.util.UUID;

public class TaskReminderBroadcast extends BroadcastReceiver {

    private static final String TAG = "TReminderBroadcast.TAG";

    public static final String EXTRA_TEST = "EXTRA_TEST";

    //TODO: WIll have to put all contract vars into the contract class.
    // 200 is for task notification actions.
    public static final int REQUEST_TASK_CHECKED_NOTIFICATION = 200;

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: This Receiver will be the one that handles the notification building and functionality,
        // such as clicking the notification to launch the app and show the specific task,
        // checking off the specific task in the notification (if feasible).
        if(intent != null) {
            if(intent.hasExtra(TasklistsRefreshBroadcast.EXTRA_TASK_BYTEDATA)) {
                UserTask userTask = convertUserTaskFromByteData(intent.getByteArrayExtra(TasklistsRefreshBroadcast.EXTRA_TASK_BYTEDATA));
                createNotificationForTask(context, userTask);
            }
        }
    }

    private UserTask convertUserTaskFromByteData(byte[] _byteData) {
        return UserTask.deserializeUserTaskByteData(_byteData);
    }

    private void createNotificationForTask(Context _context, UserTask _userTask) {
        Log.i(TAG, "createNotificationForTask: Creating Notification");
        Log.i(TAG, "createNotificationForTask: TaskName: " + _userTask.getTaskName());
        //TODO: Return here to work next. Will keep the tasklists refresh broadcast with testing variables for now.

        //https://www.reddit.com/r/androiddev/comments/gcwkre/android_notification_as_deep_as_possible/

        //TODO: Create a notification that displays the task name + description (if there is one),
        // and has an action for checking off the task in the notification, and an action for viewing the task.
        // The action that checks off the task will fire off another intent im assuming,
        // that will update the tasklists and if this is the case, I will need to implement the rand UUID for all tasks,
        // or send the tasklist into this broadcast receiver OR load the tasklists from storage and look for the task with the same name in the code/work/receiver.
        // The action that views the task will launch the taskinfoactivity, this will need all tasklists and the task.

        //Create the string messages for the notification.
        String taskDescription = _userTask.getTaskDescription();
        String bigTextMessage = "";
        if(taskDescription != null) {
            bigTextMessage = taskDescription;
        }else {
            bigTextMessage = "No description set for this task.";
        }

        String bigContentTitleMessage = "Reminder for task: " + _userTask.getTaskName();
        String summaryTextMessage = "Task Reminder";

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(bigTextMessage)
                .setBigContentTitle(bigContentTitleMessage)
                .setSummaryText(summaryTextMessage);

        //TODO: Work on adding the two actions "Check" and "View".
        // Check action works, need to work on view action.
        //Create the actions for the notification.
        //TODO: Decide if this should be the content intent or its own action.
        Intent taskInfoIntent = new Intent(_context, TaskInfoActivity.class);
        taskInfoIntent.putExtra(EXTRA_TEST, "Hello World");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(_context);
        stackBuilder.addNextIntentWithParentStack(taskInfoIntent);

        PendingIntent taskInfoPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent checkIntent = new Intent(_context, TaskCheckedBroadcast.class);
        //TODO: Replace extra with the byte data of the task.
        checkIntent.putExtra(EXTRA_TEST, "Replace this extra with the task in the notification");
        PendingIntent checkPendingIntent = PendingIntent.getBroadcast(_context,
                REQUEST_TASK_CHECKED_NOTIFICATION,
                checkIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Create the notification.
        Bitmap largeIcon = BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_task_notification_placeholder);
        Notification notification = new NotificationCompat.Builder(_context, MainActivity.NOTIFICATION_CHANNELID_TASKREMINDER)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.ic_task_notification_placeholder)
                .setContentTitle("Get rid of setContentTitle if this appears.")
                .addAction(R.drawable.ic_task_notification_check_placeholder, _context.getString(R.string.notification_task_check_action), checkPendingIntent)
                .setStyle(bigTextStyle)
                .build();
        int notificationId = UUID.randomUUID().hashCode();

        NotificationManager notificationManager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null) {
            notificationManager.notify(notificationId, notification);
            Log.i(TAG, "createNotificationForTask: notification should be showing up.");
        }

    }



}
