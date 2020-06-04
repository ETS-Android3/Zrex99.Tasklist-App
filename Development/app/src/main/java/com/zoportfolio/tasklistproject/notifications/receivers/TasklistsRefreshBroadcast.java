package com.zoportfolio.tasklistproject.notifications.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zoportfolio.tasklistproject.MainActivity;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTaskList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class TasklistsRefreshBroadcast extends BroadcastReceiver {

    private static final String TAG = "TlRefreshBroadcast.TAG";

    public static final String EXTRA_TASK_BYTEDATA = "EXTRA_TASK_BYTEDATA";

    private ArrayList<UserTaskList> mTaskLists;

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: This Receiver will be the one that handles refreshing all of the alarmManager sets
        // and assigning the times to notifiy based on the task, using the TaskReminderBroadcast.

        if(intent != null) {
            if(intent.hasExtra(MainActivity.EXTRA_TASKLISTS)) {
                //Grab the object from the intent and then call the method to convert the tasklists.
                Object obj = intent.getSerializableExtra(MainActivity.EXTRA_TASKLISTS);
                mTaskLists = convertTasklistsObjectFromJSON(obj);

                resetAllTasksToUnchecked();

                //Call method to set all tasks alarms
                setAllTasksAlarm(context);

                //End of if(intent.hasExtra(MainActivity.EXTRA_TASKLISTS))
            }
        }
    }

    private ArrayList<UserTaskList> convertTasklistsObjectFromJSON(Object _obj) {

        ArrayList<String> taskListJSONList = new ArrayList<>();
        if(_obj instanceof ArrayList<?>) {
            ArrayList<?> arrayList = (ArrayList<?>) _obj;
            if(arrayList.size() > 0) {
                for (int i = 0; i < arrayList.size(); i++) {
                    Object o = arrayList.get(i);
                    if(o instanceof String) {
                        taskListJSONList.add((String) o);
                    }
                }
            }
        }

        ArrayList<UserTaskList> taskLists = new ArrayList<>();
        if(!taskListJSONList.isEmpty()) {
            for (int i = 0; i < taskListJSONList.size(); i++) {
                String taskListJSONString = taskListJSONList.get(i);
                UserTaskList userTaskList = UserTaskList.fromJSONString(taskListJSONString);
                if(userTaskList != null) {
                    taskLists.add(userTaskList);
                }
            }
        }
        return taskLists;
    }

    private void resetAllTasksToUnchecked() {
        for (int i = 0; i < mTaskLists.size(); i++) {
            //Tasklist scope
            for (int j = 0; j < mTaskLists.get(i).getTasks().size(); j++) {
                //Task scope
                mTaskLists.get(i).getTasks().get(j).setTaskChecked(false);
            }
        }

        //TODO: Have to save the now unchecked taskslists to the storage.
    }

    private void setAllTasksAlarm(Context _context) {
        for (int i = 0; i < mTaskLists.size(); i++) {
            //Tasklist scope
            for (int j = 0; j < mTaskLists.get(i).getTasks().size(); j++) {
                //Task scope
                UserTask task = mTaskLists.get(i).getTasks().get(j);
                String id = i + String.valueOf(j);
                int idPosition = Integer.parseInt(id);
                setAlarmForTask(_context, task, idPosition);
            }
        }
    }

    private void setAlarmForTask(Context _context, UserTask _task, int _positionID) {
        Log.i(TAG, "setAlarmForTask: creating alarm manager for task");
        AlarmManager taskAlarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);

        //TODO: The intent im sending here is faulty and does not have any data attached to it.
        Intent taskIntent = new Intent(_context, TaskReminderBroadcast.class);

        byte[] userTaskByteData = UserTask.serializeUserTask(_task);
        taskIntent.putExtra(EXTRA_TASK_BYTEDATA, userTaskByteData);

        PendingIntent taskAlarmIntent = PendingIntent.getBroadcast(_context.getApplicationContext(),
                _positionID,
                taskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if(taskAlarmManager != null) {

            String notificationTime = _task.getTaskNotificationTime();
            String[] notificationTimeSplit = notificationTime.split("/");

//            String hour = notificationTimeSplit[0];
//            String minute = notificationTimeSplit[1];

            String hour = "0";
            String minute = "1";

            Calendar taskAlarmTime = Calendar.getInstance();
            taskAlarmTime.setTimeInMillis(System.currentTimeMillis());
            taskAlarmTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            taskAlarmTime.set(Calendar.MINUTE, Integer.parseInt(minute));
            taskAlarmTime.set(Calendar.SECOND, 0);

            taskAlarmManager.set(AlarmManager.RTC_WAKEUP,
                    taskAlarmTime.getTimeInMillis(),
                    taskAlarmIntent);
        }
    }


}
