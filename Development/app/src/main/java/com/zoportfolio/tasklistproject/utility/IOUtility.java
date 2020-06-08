package com.zoportfolio.tasklistproject.utility;

import android.content.Context;
import android.widget.Toast;

import com.zoportfolio.tasklistproject.contracts.PublicContracts;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTaskList;

import java.util.ArrayList;

public class IOUtility {

    private static final String TAG = "IOUtility.TAG";

    //This class is specific to this project, and handles the Saving and loading methods.

    public static boolean checkForTasklistsInStorage(Context _context) {
        //If this returns 0, that means there are no files
        int fileCount = FileUtility.getCountOfFolderFromProtectedStorage(_context, PublicContracts.FILE_TASKLIST_FOLDER);
        return fileCount > 0;
    }

    /**
     * Saving methods
     */


    public static void saveTasklistsToStorage(Context _context, ArrayList<UserTaskList> _taskLists) {
        //Convert all the tasklists to JSON for saving.
        ArrayList<String> taskListsJSON = convertTasklistsForSaving(_taskLists);
        //Once all tasklists have been added to the string array, save them to storage.
        boolean saveStatus = FileUtility.saveToProtectedStorage(_context, PublicContracts.FILE_TASKLIST_NAME, PublicContracts.FILE_TASKLIST_FOLDER, taskListsJSON);
        String saveStatusMessage = "";
        if(saveStatus) {
            saveStatusMessage = "Tasklist updated and saved succesfully";
        }else {
            saveStatusMessage = "Oops, something went wrong with the tasklist saving and it failed, please try again";
        }

        Toast toast = Toast.makeText(_context, saveStatusMessage, Toast.LENGTH_LONG);
        toast.show();
    }

    public static ArrayList<String> convertTasklistsForSaving(ArrayList<UserTaskList> _taskLists) {
        ArrayList<String> taskListsJSON = new ArrayList<>();
        for (int i = 0; i < _taskLists.size(); i++) {
            UserTaskList taskList = _taskLists.get(i);
            //Add the JSON tasklist to the arrayList.
            taskListsJSON.add(taskList.toJSONString());
        }
        return taskListsJSON;
    }

    /**
     * Loading methods
     */

    public static ArrayList<UserTaskList> loadTasklistsFromStorage(Context _context) {
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

    public static ArrayList<UserTaskList> convertTasklistsFromLoading(ArrayList<String> _taskListJSONList) {
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
