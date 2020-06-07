package com.zoportfolio.tasklistproject.notifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zoportfolio.tasklistproject.contracts.PublicContracts;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTaskList;
import com.zoportfolio.tasklistproject.utility.FileUtility;

import java.util.ArrayList;

public class TaskCheckedBroadcast extends BroadcastReceiver {

    private static final String TAG = "TaskCheckedBroadcast.TAG";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            if(intent.hasExtra(PublicContracts.EXTRA_TASK_BYTEDATA)) {
                //Check for tasklists in storage as precaution.
                if(checkForTasklistsInStorage(context)) {
                    Log.i(TAG, "onReceive: files in storage.");
                    ArrayList<UserTaskList> taskLists = loadTasklistsFromStorage(context);
                    UserTask userTask = convertUserTaskFromByteData(intent.getByteArrayExtra(PublicContracts.EXTRA_TASK_BYTEDATA));
                    updateTask(context, userTask, taskLists);
                }
                
            }
        }
    }

    private UserTask convertUserTaskFromByteData(byte[] _byteData) {
        return UserTask.deserializeUserTaskByteData(_byteData);
    }

    private void updateTask(Context _context, UserTask _userTask, ArrayList<UserTaskList> _taskLists) {
        _userTask.setTaskChecked(true);

        //Have to find the task based off its name.
        //IMPORTANT eventually i will have to convert the tasks to ID based.
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
        saveTasklistsToStorage(_context, _taskLists);
    }

    private ArrayList<String> convertTasklistsForSaving(ArrayList<UserTaskList> _taskLists) {
        ArrayList<String> taskListsJSON = new ArrayList<>();
        for (int i = 0; i < _taskLists.size(); i++) {
            UserTaskList taskList = _taskLists.get(i);
            //Add the JSON tasklist to the arrayList.
            taskListsJSON.add(taskList.toJSONString());
        }
        return taskListsJSON;
    }

    private void saveTasklistsToStorage(Context _context, ArrayList<UserTaskList> _taskLists) {
        //Convert all the tasklists to JSON for saving.
        ArrayList<String> taskListsJSON = convertTasklistsForSaving(_taskLists);

        //Once all tasklists have been added to the string array, save them to storage.
        boolean saveStatus = FileUtility.saveToProtectedStorage(_context, PublicContracts.FILE_TASKLIST_NAME, PublicContracts.FILE_TASKLIST_FOLDER, taskListsJSON);
        //TODO: Can toast that saving was successful not sure.
        Log.i(TAG, "saveTasklistsToStorage: Save status: " + saveStatus);
    }

    private boolean checkForTasklistsInStorage(Context _context) {
        //If this returns 0, that means there are no files
        int fileCount = FileUtility.getCountOfFolderFromProtectedStorage(_context, PublicContracts.FILE_TASKLIST_FOLDER);
        return fileCount > 0;
    }

    private ArrayList<UserTaskList> loadTasklistsFromStorage(Context _context) {
        ArrayList<String> taskListJSONList = new ArrayList<>();
        Object obj = FileUtility.retrieveFromStorage(_context, PublicContracts.FILE_TASKLIST_NAME);
        if(obj instanceof ArrayList<?>) {
            ArrayList<?> arrayList = (ArrayList<?>) obj;
            if(arrayList.size() > 0) {
                for (int i = 0; i < arrayList.size(); i++) {
                    Object o = arrayList.get(i);
                    if(o instanceof String) {
                        taskListJSONList.add((String) o);
                    }
                }
            }
        }

        //Convert the tasklists from ArrayList<String> JSON
        return convertTasklistsFromLoading(taskListJSONList);
    }

    private ArrayList<UserTaskList> convertTasklistsFromLoading(ArrayList<String> _taskListJSONList) {
        ArrayList<UserTaskList> taskLists = new ArrayList<>();
        if(!_taskListJSONList.isEmpty()) {
            for (int i = 0; i < _taskListJSONList.size(); i++) {
                String taskListJSONString = _taskListJSONList.get(i);
                UserTaskList userTaskList = UserTaskList.fromJSONString(taskListJSONString);
                if(userTaskList != null) {
                    taskLists.add(userTaskList);
                }
            }
        }
        return taskLists;
    }


}
