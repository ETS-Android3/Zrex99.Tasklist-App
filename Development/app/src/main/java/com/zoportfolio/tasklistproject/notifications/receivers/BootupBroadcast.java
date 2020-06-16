package com.zoportfolio.tasklistproject.notifications.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.util.Log;

import com.zoportfolio.tasklistproject.MainActivity;
import com.zoportfolio.tasklistproject.contracts.PublicContracts;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTaskList;
import com.zoportfolio.tasklistproject.utility.IOUtility;

import java.util.ArrayList;

public class BootupBroadcast extends BroadcastReceiver {

    private static final String TAG = "BootupBroadcast.TAG";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            if(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                //Run method to restart the tasks alarms and the tasklist refresh broadcast.
                Log.i(TAG, "onReceive: Starting all work in BootupBroadcast");
                ArrayList<UserTaskList> taskLists = IOUtility.loadTasklistsFromStorage(context);
                setupTasklistsRefreshBroadcast(context, taskLists);
                resetAllTasksToUnchecked(context, taskLists);
                setAllTasksAlarm(context, taskLists);
                Log.i(TAG, "onReceive: Completed all work in BootupBroadcast");
            }
        }
    }

    private void setupTasklistsRefreshBroadcast(Context _context, ArrayList<UserTaskList> _taskLists) {

        //Going to null check the mTasklists global variable just to be safe.
        if(!_taskLists.isEmpty()) {
            AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(_context, TasklistsRefreshBroadcast.class);
            intent.setAction(PublicContracts.ACTION_RESET_TASKLISTS_BROADCAST);

            //Using flag update current for this pending intent, so that whenever it gets created it just updates the intent data.
            PendingIntent alarmIntent = PendingIntent.getBroadcast(_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if(alarmManager != null) {
                Calendar alarmTime = Calendar.getInstance();
                alarmTime.setTimeInMillis(System.currentTimeMillis());
                alarmTime.set(Calendar.HOUR_OF_DAY, 0);
                alarmTime.set(Calendar.MINUTE, 0);
                alarmTime.set(Calendar.SECOND, 0);

                //Testing alarmManager block
//                alarmManager.set(AlarmManager.RTC,
//                        alarmTime.getTimeInMillis(),
//                        alarmIntent);

//                alarmManager.setInexactRepeating(AlarmManager.RTC,
//                        alarmTime.getTimeInMillis(),
//                        AlarmManager.INTERVAL_DAY,
//                        alarmIntent);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        alarmTime.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        alarmIntent);

                //To keep from continually adding and setting this alarmManager whenever the main activity runs,
                // utilize the shared preferences to check if it needs to be set.
                saveTasklistRefreshBroadcastStateToSharedPreferences(_context, true);
            }
        }else {
            Log.i(TAG, "setupTasklistsRefreshBroadcast: Tasklists were null or the tasklists were empty, " +
                    "did not set up refreshBroadcast.");
            saveTasklistRefreshBroadcastStateToSharedPreferences(_context, false);
        }

    }

    private void saveTasklistRefreshBroadcastStateToSharedPreferences(Context _context, Boolean _state) {
        SharedPreferences preferences = _context.getSharedPreferences(MainActivity.FILE_REFRESH_BROADCAST_ACTIVE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(PublicContracts.PREF_TASKLIST_REFRESH_ACTIVE_KEY, _state);
        editor.apply();
    }

    private void resetAllTasksToUnchecked(Context _context, ArrayList<UserTaskList> _taskLists) {
        for (int i = 0; i < _taskLists.size(); i++) {
            //Tasklist scope
            if(!_taskLists.get(i).getTasks().isEmpty()) {
                for (int j = 0; j < _taskLists.get(i).getTasks().size(); j++) {
                    //Task scope
                    _taskLists.get(i).getTasks().get(j).setTaskChecked(false);
                }
            }
        }
        IOUtility.saveTasklistsToStorage(_context, _taskLists);
    }

    private void setAllTasksAlarm(Context _context, ArrayList<UserTaskList> _taskLists) {
        for (int i = 0; i < _taskLists.size(); i++) {
            //Tasklist scope
            if(!_taskLists.get(i).getTasks().isEmpty()) {
                for (int j = 0; j < _taskLists.get(i).getTasks().size(); j++) {
                    //Task scope
                    UserTask task = _taskLists.get(i).getTasks().get(j);
                    setAlarmForTask(_context, task, task.getTaskId());
                }
            }
        }
    }

    private void setAlarmForTask(Context _context, UserTask _task, int _ID) {
        AlarmManager taskAlarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
        //IMPORTANT, Had to convert the task data into byte data in order to get this to work properly.
        // Filling the intent with the byte array of the task data,
        // implementing SerializationUtils from Apache commons lang3,
        // and adding compileOptions to utilize java 1.8 in gradle.
        Intent taskIntent = new Intent(_context, TaskReminderBroadcast.class);

        byte[] userTaskByteData = UserTask.serializeUserTask(_task);
        taskIntent.putExtra(PublicContracts.EXTRA_TASK_BYTEDATA, userTaskByteData);

        PendingIntent taskAlarmIntent = PendingIntent.getBroadcast(_context.getApplicationContext(),
                _ID,
                taskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if(taskAlarmManager != null) {

            String notificationTime = _task.getTaskNotificationTime();
            String[] notificationTimeSplit = notificationTime.split("/");

            String hour = notificationTimeSplit[0];
            String minute = notificationTimeSplit[1];

            java.util.Calendar taskAlarmTime = java.util.Calendar.getInstance();
            taskAlarmTime.setTimeInMillis(System.currentTimeMillis());
            taskAlarmTime.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            taskAlarmTime.set(java.util.Calendar.MINUTE, Integer.parseInt(minute));
            taskAlarmTime.set(java.util.Calendar.SECOND, 0);

            taskAlarmManager.set(AlarmManager.RTC_WAKEUP,
                    taskAlarmTime.getTimeInMillis(),
                    taskAlarmIntent);
        }
    }

}
