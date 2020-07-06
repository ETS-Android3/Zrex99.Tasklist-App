package com.zoportfolio.tasklistproject.notifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zoportfolio.tasklistproject.contracts.PublicContracts;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTaskList;
import com.zoportfolio.tasklistproject.utility.IOUtility;

import java.util.ArrayList;

public class TaskCheckedBroadcast extends BroadcastReceiver {

    private static final String TAG = "TaskCheckedBroadcast.TAG";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            if(intent.hasExtra(PublicContracts.EXTRA_TASK_BYTEDATA)) {
                //Check for tasklists in storage as precaution.

                if(IOUtility.checkForTasklistsInStorage(context)) {
                    ArrayList<UserTaskList> taskLists = IOUtility.loadTasklistsFromStorage(context);
                    UserTask userTask = convertUserTaskFromByteData(intent.getByteArrayExtra(PublicContracts.EXTRA_TASK_BYTEDATA));
                    updateTask(context, userTask, taskLists);

                    Intent taskCheckedIntent = new Intent();
                    taskCheckedIntent.setAction(PublicContracts.ACTION_TASK_CHECKED_NOTIFICATION);
                    context.sendBroadcast(taskCheckedIntent);
                }
            }
        }
    }

    private UserTask convertUserTaskFromByteData(byte[] _byteData) {
        return UserTask.deserializeUserTaskByteData(_byteData);
    }

    private void updateTask(Context _context, UserTask _userTask, ArrayList<UserTaskList> _taskLists) {
        _userTask.setTaskChecked(true);

        //IMPORTANT eventually i will have to convert the tasks to be searchable by ID.
        for (int i = 0; i < _taskLists.size(); i++) {
            //Super Tasklist scope
            for (int j = 0; j < _taskLists.get(i).getTasks().size(); j++) {
                //Tasklist scope
                if(_taskLists.get(i).getTasks().get(j).getTaskName().equals(_userTask.getTaskName())) {
                    _taskLists.get(i).getTasks().set(j, _userTask);
                }
            }
        }

        //Save the updated tasklists.
        IOUtility.saveTasklistsToStorage(_context, _taskLists);
    }
}
