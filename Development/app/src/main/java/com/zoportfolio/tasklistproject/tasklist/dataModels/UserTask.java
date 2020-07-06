package com.zoportfolio.tasklistproject.tasklist.dataModels;

import android.util.Log;

import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class UserTask implements Serializable {

    private static final String TAG = "UserTask.TAG";

    /**
     * Member Variables
     */

    //Keys/Contracts vars
    // private static final [type]
    private static final String KEY_T_ID = "KEY_T_ID";
    private static final String KEY_T_NAME = "KEY_T_NAME";
    private static final String KEY_T_DESCRIPTION = "KEY_T_DESCRIPTION";
    private static final String KEY_T_NOTIFICATION_TIME = "KEY_T_NOTIFICATION_TIME";
    private static final String KEY_T_CHECKED = "KEY_T_CHECKED";

    //Class vars
    // private [type]

    private int mTaskId;
    private String mTaskName;
    private String mTaskDescription;
    private String mTaskNotificationTime;//IMPORTANT NOTE: Hour in the notification time will be stored and transferred around as 24 hour notation ie. 2pm = 14
    private Boolean mTaskChecked;

    /**
     * Constructors
     */


    //To be used if the user adds a task WITHOUT a description.
    public UserTask(int taskId, String taskName, String taskNotificationTime) {
        mTaskId = taskId;
        mTaskName = taskName;
        mTaskNotificationTime = taskNotificationTime;
        mTaskChecked = false;
    }

    //To be used if the user adds a task WITH a description.
    public UserTask(int taskId, String taskName, String taskNotificationTime, String taskDescription) {
        mTaskId = taskId;
        mTaskName = taskName;
        mTaskNotificationTime = taskNotificationTime;
        mTaskDescription = taskDescription;
        mTaskChecked = false;
    }

    //To be used when converting the UserTask object FROM JSON WITH a description.
    public UserTask(int taskId, String taskName, String taskNotificationTime, String taskDescription, Boolean taskChecked) {
        mTaskId = taskId;
        mTaskName = taskName;
        mTaskNotificationTime = taskNotificationTime;
        mTaskDescription = taskDescription;
        mTaskChecked = taskChecked;
    }

    //To be used when converting the UserTask object FROM JSON WITHOUT a description.
    public UserTask(int taskId, String taskName, String taskNotificationTime, Boolean taskChecked) {
        mTaskId = taskId;
        mTaskName = taskName;
        mTaskNotificationTime = taskNotificationTime;
        mTaskChecked = taskChecked;
    }

    /**
     * Getters/Setters
     */

    public int getTaskId() {
        return mTaskId;
    }

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
    public String getTaskNotificationTimeAsReadable() {
        String notificationTimeFormatted = getTaskNotificationTime();
        String[] notificationTimeSplit = notificationTimeFormatted.split("/");

        String hour = notificationTimeSplit[0];
        String minute = notificationTimeSplit[1];
        String meridies = notificationTimeSplit[2];

        return  hour + ":" + minute + " " + meridies;
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

    public static String formatNotificationTime(int hour, int minute, String meridies) {
        return hour + "/" + minute + "/" + meridies;
    }

    public static String formatNotificationTimeAsReadable(String hour, String minute, String meridies) {


        return  hour + ":" + minute + " " + meridies;
    }


    public String toJSONString() {
        JSONObject object = new JSONObject();

        try {
            object.put(KEY_T_ID, mTaskId);
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

            int taskId = object.getInt(KEY_T_ID);
            String taskName = object.getString(KEY_T_NAME);
            String taskDescription = object.getString(KEY_T_DESCRIPTION);
            String taskNotificationTime = object.getString(KEY_T_NOTIFICATION_TIME);
            Boolean taskChecked = object.getBoolean(KEY_T_CHECKED);

            //The below if block has 2 test cases that need to be tested.
            if(taskDescription != null  && !taskDescription.equals("Null")) {
                //Construct with description.
                Log.i(TAG, "fromJSONString: Creating UserTask from JSON WITH description");
                return new UserTask(taskId, taskName, taskNotificationTime, taskDescription, taskChecked);
            }else {
                //Construct without description.
                return new UserTask(taskId, taskName, taskNotificationTime, taskChecked);
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static byte[] serializeUserTask(UserTask _userTask) {
        return SerializationUtils.serialize(_userTask);
    }

    public static UserTask deserializeUserTaskByteData(byte[] _userTaskByteData) {
        return (UserTask) SerializationUtils.deserialize(_userTaskByteData);
    }

}
