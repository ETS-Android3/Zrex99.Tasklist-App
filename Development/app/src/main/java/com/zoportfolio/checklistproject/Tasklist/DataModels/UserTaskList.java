package com.zoportfolio.checklistproject.Tasklist.DataModels;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserTaskList {

    //TODO: Would like to take a day to write unit tests for the two classes.
    //TODO: Get rid of the get/set comments after testing this class.

    private static final String TAG = "UserTaskList.TAG";

    /**
     * Member Variables
     */

    //Keys/Contracts vars
    // private static final [type]
    private static final String KEY_TL_NAME = "KEY_TL_NAME";
    private static final String KEY_TL_TASKS = "KEY_TL_TASKS";

    //Class vars
    // private [type]

    private String mTaskListName;
    private ArrayList<UserTask> mTasks;



    /**
     * Constructors
     */


    //To be used when the user adds a taskList for the first time.
    public UserTaskList(String taskListName) {
        mTaskListName = taskListName;
        mTasks = new ArrayList<>();
    }

    //To be used for constructing the tasks from JSON.
    public UserTaskList(String taskListName, ArrayList<UserTask> taskListTasks) {
        mTaskListName = taskListName;
        mTasks = taskListTasks;
    }

    /**
     * Getters/Setters
     */

    public String getTaskListName() {
        return mTaskListName;
    }
    public void setTaskListName(String taskListName) {
        mTaskListName = taskListName;
    }

    public ArrayList<UserTask> getTasks() {
        return mTasks;
    }
    public void setTasks(ArrayList<UserTask> tasks) {
        mTasks = tasks;
    }



    /**
     * Custom Methods
     */

    public void addTaskToList(UserTask newTask) {
        mTasks.add(newTask);
    }

    public String toJSONString() {
        JSONObject object = new JSONObject();

        try {
            object.put(KEY_TL_NAME, mTaskListName);

            if(!mTasks.isEmpty()) {
                JSONArray taskArray = new JSONArray();
                for (int i = 0; i < mTasks.size(); i++) {
                    taskArray.put(mTasks.get(i).toJSONString());
                }

                object.put(KEY_TL_TASKS, taskArray.toString());
            }else {
                //Note the taskList as empty.
                object.put(KEY_TL_TASKS, "Empty");
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static UserTaskList fromJSONString(String _uTaskJSON) {

        try {
            JSONObject object = new JSONObject(_uTaskJSON);

            String taskListName = object.getString(KEY_TL_NAME);

            String taskListTasksJSON = object.getString(KEY_TL_TASKS);
            ArrayList<UserTask> taskListTasks = new ArrayList<>();

            if(taskListTasksJSON.equals("Empty")) {
                Log.i(TAG, "fromJSONString: returning a tasklist WITH NO tasks.");
                return new UserTaskList(taskListName, taskListTasks);
            }else {
                JSONArray taskArrayJSON = new JSONArray(taskListTasksJSON);
                for (int i = 0; i < taskArrayJSON.length(); i++) {
                    taskListTasks.add(UserTask.fromJSONString((String) taskArrayJSON.get(i)));
                }
                return new UserTaskList(taskListName, taskListTasks);
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }




}
