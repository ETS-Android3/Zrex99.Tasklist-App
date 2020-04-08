package com.zoportfolio.checklistproject.Tasklist.DataModels;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class UserTask {

    //TODO: Would like to take a day to write unit tests for the two classes.

    private static final String TAG = "UserTask.TAG";

    /**
     * Member Variables
     */

    //Keys/Contracts vars
    // private static final [type]
    private static final String KEY_T_NAME = "KEY_T_NAME";
    private static final String KEY_T_DESCRIPTION = "KEY_T_DESCRIPTION";
    private static final String KEY_T_NOTIFICATION_TIME = "KEY_T_NOTIFICATION_TIME";
    private static final String KEY_T_CHECKED = "KEY_T_CHECKED";

    //Class vars
    // private [type]

    private String mTaskName;
    private String mTaskDescription;
    private String mTaskNotificationTime;
    private Boolean mTaskChecked;

    /**
     * Constructors
     */


    //To be used if the user adds a task WITHOUT a description.
    public UserTask(String taskName, String taskNotificationTime) {
        mTaskName = taskName;
        mTaskNotificationTime = taskNotificationTime;
        mTaskChecked = false;
    }

    //To be used if the user adds a task WITH a description.
    public UserTask(String taskName, String taskNotificationTime, String taskDescription) {
        mTaskName = taskName;
        mTaskNotificationTime = taskNotificationTime;
        mTaskDescription = taskDescription;
        mTaskChecked = false;
    }

    //To be used when converting the UserTask object FROM JSON WITH a description.
    public UserTask(String taskName, String taskNotificationTime, String taskDescription, Boolean taskChecked) {
        mTaskName = taskName;
        mTaskNotificationTime = taskNotificationTime;
        mTaskDescription = taskDescription;
        mTaskChecked = taskChecked;
    }

    //To be used when converting the UserTask object FROM JSON WITHOUT a description.
    public UserTask(String taskName, String taskNotificationTime, Boolean taskChecked) {
        mTaskName = taskName;
        mTaskNotificationTime = taskNotificationTime;
        mTaskChecked = taskChecked;
    }

    /**
     * Getters/Setters
     */

    public String getTaskName() {
        return mTaskName;
    }
    public void setTaskName(String taskName) {
        mTaskName = taskName;
    }

    public String getTaskDescription() {
        return mTaskDescription;
    }
    public void setTaskDescription(String taskDescription) {
        mTaskDescription = taskDescription;
    }

    public String getTaskNotificationTime() {
        return mTaskNotificationTime;
    }
    public void setTaskNotificationTime(String taskNotificationTime) {
        mTaskNotificationTime = taskNotificationTime;
    }

    public Boolean getTaskChecked() {
        return mTaskChecked;
    }
    public void setTaskChecked(Boolean taskChecked) {
        mTaskChecked = taskChecked;
    }


    /**
     * Custom Methods
     */

    public String toJSONString() {
        JSONObject object = new JSONObject();

        try {
            object.put(KEY_T_NAME, mTaskName);
            object.put(KEY_T_NOTIFICATION_TIME, mTaskNotificationTime);
            object.put(KEY_T_CHECKED, mTaskChecked);

            //Check the description for a null.
            if(mTaskDescription != null) {
                object.put(KEY_T_DESCRIPTION, mTaskDescription);
            }else {
                object.put(KEY_T_DESCRIPTION, "Null");
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static UserTask fromJSONString(String _uTaskJSON) {

        try {
            JSONObject object = new JSONObject(_uTaskJSON);

            String taskName = object.getString(KEY_T_NAME);
            String taskDescription = object.getString(KEY_T_DESCRIPTION);
            String taskNotificationTime = object.getString(KEY_T_NOTIFICATION_TIME);
            Boolean taskChecked = object.getBoolean(KEY_T_CHECKED);

            //The below if block has 2 test cases that need to be tested.
            if(taskDescription != null  && !taskDescription.equals("Null")) {
                //Construct with description.
                Log.i(TAG, "fromJSONString: Creating UserTask from JSON WITH description");
                return new UserTask(taskName, taskNotificationTime, taskDescription, taskChecked);
            }else {
                //Construct without description.
                return new UserTask(taskName, taskNotificationTime, taskChecked);
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


}
