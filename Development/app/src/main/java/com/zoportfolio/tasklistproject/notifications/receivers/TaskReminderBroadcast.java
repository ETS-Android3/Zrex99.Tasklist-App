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
import com.zoportfolio.tasklistproject.contracts.PublicContracts;
import com.zoportfolio.tasklistproject.task.TaskInfoActivity;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;

import java.util.Calendar;
import java.util.UUID;

public class TaskReminderBroadcast extends BroadcastReceiver {

    private static final String TAG = "TReminderBroadcast.TAG";

    //TODO: WIll have to put all contract vars into the contract class.
    // 200 is for task notification actions.
    // 100 is for launching the app.
    public static final int REQUEST_LAUNCH_APP_NOTIFICATION = 100;
    public static final int REQUEST_TASK_CHECKED_NOTIFICATION = 200;
    public static final int REQUEST_TASK_VIEW_NOTIFICATION = 210;


    public static final String ACTION_TASK_VIEW_NOTIFICATION = "ACTION_TASK_VIEW_NOTIFICATION";
    public static final String ACTION_LAUNCH_APP_NOTIFICATION = "ACTION_LAUNCH_APP_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            if(intent.hasExtra(PublicContracts.EXTRA_TASK_BYTEDATA)) {
                UserTask userTask = convertUserTaskFromByteData(intent.getByteArrayExtra(PublicContracts.EXTRA_TASK_BYTEDATA));
                if(checkForCorrectNotificationTime(userTask)) {
                    createNotificationForTask(context, userTask);    
                }
            }
        }
    }

    private UserTask convertUserTaskFromByteData(byte[] _byteData) {
        return UserTask.deserializeUserTaskByteData(_byteData);
    }

    private boolean checkForCorrectNotificationTime(UserTask _userTask) {
        boolean correctTime = false;

        String notificationTime = _userTask.getTaskNotificationTime();
        String[] notificationTimeSplit = notificationTime.split("/");
        String hour = notificationTimeSplit[0];
        String minute = notificationTimeSplit[1];

        Calendar rightNow = Calendar.getInstance();
        int currentHour24Format = rightNow.get(Calendar.HOUR_OF_DAY);
        int currentMinute = rightNow.get(Calendar.MINUTE);

        if(currentHour24Format == Integer.parseInt(hour) && currentMinute == Integer.parseInt(minute)) {
            correctTime = true;
        }
        return correctTime;
    }

    private void createNotificationForTask(Context _context, UserTask _userTask) {
        //Create the byte data of the task.
        byte[] userTaskByteData = UserTask.serializeUserTask(_userTask);

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


        //Create the actions for the notification.

        //Going to just implement this action as a View, that will open the taskinfoactivity and show it for the user.
        //viewIntent
        Intent taskInfoIntent = new Intent(_context, TaskInfoActivity.class);
        taskInfoIntent.putExtra(PublicContracts.EXTRA_TASK_BYTEDATA, userTaskByteData);
        taskInfoIntent.setAction(ACTION_TASK_VIEW_NOTIFICATION );

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(_context);
        stackBuilder.addNextIntentWithParentStack(taskInfoIntent);
        PendingIntent taskInfoPendingIntent = stackBuilder.getPendingIntent(REQUEST_TASK_VIEW_NOTIFICATION, PendingIntent.FLAG_UPDATE_CURRENT);

        //checkIntent
        Intent checkIntent = new Intent(_context, TaskCheckedBroadcast.class);
        checkIntent.putExtra(PublicContracts.EXTRA_TASK_BYTEDATA, userTaskByteData);
        PendingIntent checkPendingIntent = PendingIntent.getBroadcast(_context,
                REQUEST_TASK_CHECKED_NOTIFICATION,
                checkIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //openMainIntent
        Intent launchAppIntent = new Intent(_context, MainActivity.class);
        launchAppIntent.setAction(ACTION_LAUNCH_APP_NOTIFICATION);

        stackBuilder.addNextIntent(launchAppIntent);
        PendingIntent launchAppPendingIntent = stackBuilder.getPendingIntent(REQUEST_LAUNCH_APP_NOTIFICATION, PendingIntent.FLAG_UPDATE_CURRENT);


        //Create the notification.
        Bitmap largeIcon = BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_task_notification_placeholder);
        Notification notification = new NotificationCompat.Builder(_context, MainActivity.NOTIFICATION_CHANNELID_TASKREMINDER)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.ic_task_notification_placeholder)
                .setContentTitle("Reminder for completing task")
                .setContentIntent(launchAppPendingIntent)
                .addAction(R.drawable.ic_task_notification_check_placeholder, _context.getString(R.string.notification_task_check_action), checkPendingIntent)
                .addAction(R.drawable.ic_task_notification_placeholder, _context.getResources().getString(R.string.notification_task_view_action), taskInfoPendingIntent)
                .setStyle(bigTextStyle)
                //Going to use auto cancel, may change this based on beta test feedback.
                .setAutoCancel(true)
                .build();
        int notificationId = UUID.randomUUID().hashCode();

        NotificationManager notificationManager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null) {
            notificationManager.notify(notificationId, notification);
        }
    }



}
