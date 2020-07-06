package com.zoportfolio.tasklistproject.task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.zoportfolio.tasklistproject.MainActivity;
import com.zoportfolio.tasklistproject.R;
import com.zoportfolio.tasklistproject.alerts.EditTaskNotificationTimeAlertFragment;
import com.zoportfolio.tasklistproject.alerts.EditTaskTitleAlertFragment;
import com.zoportfolio.tasklistproject.contracts.PublicContracts;
import com.zoportfolio.tasklistproject.notifications.receivers.TaskReminderBroadcast;
import com.zoportfolio.tasklistproject.task.fragments.TaskInfoFragment;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTaskList;
import com.zoportfolio.tasklistproject.utility.IOUtility;
import com.zoportfolio.tasklistproject.utility.KeyboardUtility;

import java.util.ArrayList;

public class TaskInfoActivity extends AppCompatActivity implements TaskInfoFragment.TaskInfoFragmentListener,
        EditTaskTitleAlertFragment.EditTaskTitleAlertFragmentListener,
        EditTaskNotificationTimeAlertFragment.EditTaskNotificationTimeAlertFragmentListener {

    private static final String TAG = "TaskInfoActivity.TAG";

    private static final String FRAGMENT_TASKINFO_TAG = "FRAGMENT_TASKINFO";
    private static final String FRAGMENT_EDIT_TASK_TITLE_TAG = "FRAGMENT_EDIT_TASK_TITLE";
    private static final String FRAGMENT_EDIT_TASK_NOTIFICATION_TIME_TAG = "FRAGMENT_EDIT_TASK_TITLE";

    //These vars will keep track of the user data.
    private UserTask mTaskOriginal;
    private UserTask mTaskEdited;
    private int mTaskListPosition;
    private ArrayList<UserTaskList> mTaskLists;

    private boolean mEdited = false;
    private boolean mIsAlertUp = false;

    /**
     * Lifecycle methods
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskinfo);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Get the intent.
        Intent intent = getIntent();

        if(intent.getAction() != null) {
            if(intent.getAction().equals(TaskReminderBroadcast.ACTION_TASK_VIEW_NOTIFICATION)) {//Start the activity from the notification.

                mTaskOriginal = convertUserTaskFromByteData(intent.getByteArrayExtra(PublicContracts.EXTRA_TASK_BYTEDATA));
                mTaskEdited = createNewUserTaskForEditing(mTaskOriginal);
                mTaskLists = IOUtility.loadTasklistsFromStorage(this);
                mTaskListPosition = findTaskListPosition(mTaskOriginal, mTaskLists);

            }else if(intent.getAction().equals(MainActivity.ACTION_TASK_VIEW_ACTIVITY)) {//Start the activity the normal way.

                mTaskOriginal = (UserTask) intent.getSerializableExtra(MainActivity.EXTRA_TASK);
                mTaskEdited = createNewUserTaskForEditing(mTaskOriginal);
                mTaskListPosition = intent.getIntExtra(MainActivity.EXTRA_TASKLISTPOSITION, -1);

                ArrayList<String> taskListJSONList = new ArrayList<>();
                Object obj = intent.getSerializableExtra(MainActivity.EXTRA_TASKLISTS);
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
                mTaskLists = convertTasklistsFromIntentJSON(taskListJSONList);
            }
        }


        Button backButton = findViewById(R.id.btn_Back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check that the original task is different or not from the edited task, and then send back the right result code based on that.
                if(mTaskOriginal.equals(mTaskEdited)) {
                    setResult(MainActivity.RESULT_CODE_TASK_UNCHANGED);
                    finish();
                }else {
                    updateTaskLists();

                    //Set the result intent so that the main activity can handle the changed data.
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(MainActivity.EXTRA_TASKLISTS, mTaskLists);
                    resultIntent.putExtra(MainActivity.EXTRA_TASKLISTPOSITION, mTaskListPosition);
                    setResult(MainActivity.RESULT_CODE_TASK_CHANGED, resultIntent);
                    finish();
                }
            }
        });

        loadTaskInfoFragment();
    }

    /**
     * Interface methods
     */

    @Override
    public void taskUpdated(UserTask updatedTask) {
        if(!mEdited) {
            mEdited = true;//If this was the first edit of the task, then set the edited bool to true.
        }
        mTaskEdited.setTaskName(updatedTask.getTaskName());
        mTaskEdited.setTaskDescription(updatedTask.getTaskDescription());
        mTaskEdited.setTaskNotificationTime(updatedTask.getTaskNotificationTime());

        updateTaskLists();
        if(checkIfNotificationTimeIsAfterCurrentTime(mTaskEdited)) {
            updateAlarmForTask(getApplicationContext());
        }
    }

    @Override
    public void editNotificationTime(String notificationTime) {
        loadEditTaskNotificationTimeAlertFragment(notificationTime);
    }

    @Override
    public void editTitle(String taskTitle) {
        loadEditTaskTitleAlertFragment(taskTitle);
    }

    //--- Edit Title Alert Interface ---

    @Override
    public void cancelTappedEditTitle() {
        closeEditTaskTitleAlertFragment();
        KeyboardUtility.hideKeyboard(this);
    }

    @Override
    public void saveTappedEditTitle(String taskNameEdited) {
        if(!mEdited) {
            mEdited = true;//If this was the first edit of the task, then set the edited bool to true.
            mTaskEdited.setTaskName(taskNameEdited);
            if(!mTaskEdited.getTaskName().equals(mTaskOriginal.getTaskName())) {
            }
            closeEditTaskTitleAlertFragment();
            loadTaskInfoFragment();
        }else {
            //Not sure why i am checking, will look for reasons in the morning.
            mTaskEdited.setTaskName(taskNameEdited);
            if(!mTaskEdited.getTaskName().equals(mTaskOriginal.getTaskName())) {
            }
            closeEditTaskTitleAlertFragment();
            loadTaskInfoFragment();
        }
        updateTaskLists();
        if(checkIfNotificationTimeIsAfterCurrentTime(mTaskEdited)) {
            updateAlarmForTask(getApplicationContext());
        }
        KeyboardUtility.hideKeyboard(this);
    }

    //--- Edit Notification Time Alert Interface ---

    @Override
    public void cancelTappedEditNotificationTime() {
        closeEditTaskNotificationTimeAlertFragment();
    }

    @Override
    public void saveTappedEditNotificationTime(String taskNotificationTimeEdited) {
        if(!mEdited) {
            mEdited = true;//If this was the first edit of the task, then set the edited bool to true.
        }
        mTaskEdited.setTaskNotificationTime(taskNotificationTimeEdited);
        if(!mTaskEdited.getTaskNotificationTime().equals(mTaskOriginal.getTaskName())) {
        }
        closeEditTaskNotificationTimeAlertFragment();
        loadTaskInfoFragment();
        updateTaskLists();
        if(checkIfNotificationTimeIsAfterCurrentTime(mTaskEdited)) {
            updateAlarmForTask(getApplicationContext());
        }
    }

    /**
     * Custom methods
     */

    private void updateAlarmForTask(Context _context) {

        UserTask _task = mTaskEdited;

        AlarmManager taskAlarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
        //IMPORTANT, Had to convert the task data into byte data in order to get this to work properly.
        // Filling the intent with the byte array of the task data,
        // implementing SerializationUtils from Apache commons lang3,
        // and adding compileOptions to utilize java 1.8 in gradle.
        Intent taskIntent = new Intent(_context, TaskReminderBroadcast.class);

        byte[] userTaskByteData = UserTask.serializeUserTask(_task);
        taskIntent.putExtra(PublicContracts.EXTRA_TASK_BYTEDATA, userTaskByteData);

        PendingIntent taskAlarmIntent = PendingIntent.getBroadcast(_context.getApplicationContext(),
                mTaskEdited.getTaskId(),
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

    private boolean checkIfNotificationTimeIsAfterCurrentTime(UserTask _userTask) {
        boolean afterCurrentTime = false;

        String notificationTime = _userTask.getTaskNotificationTime();
        String[] notificationTimeSplit = notificationTime.split("/");
        String hour = notificationTimeSplit[0];
        String minute = notificationTimeSplit[1];

        java.util.Calendar rightNow = java.util.Calendar.getInstance();
        int currentHour24Format = rightNow.get(java.util.Calendar.HOUR_OF_DAY);
        int currentMinute = rightNow.get(java.util.Calendar.MINUTE);

        if(Integer.parseInt(hour) > currentHour24Format) {
            //If the hour is past the current hour, then it is true.
            afterCurrentTime = true;
        }else if(Integer.parseInt(hour) == currentHour24Format && Integer.parseInt(minute) > currentMinute) {
            //If the hour is the current hour, and the minute is greater than the current minute, then it is true.
            afterCurrentTime = true;
        }

        return afterCurrentTime;
    }

    private int findTaskListPosition(UserTask _userTask, ArrayList<UserTaskList> _taskLists) {
        int position =  -1;
        for (int i = 0; i < _taskLists.size(); i++) {
            //Super Tasklist scope
            for (int j = 0; j < _taskLists.get(i).getTasks().size(); j++) {
                //Tasklist scope
                if(_taskLists.get(i).getTasks().get(j).getTaskName().equals(_userTask.getTaskName())) {
                    position = i;
                }
            }
        }
        return position;
    }

    private UserTask convertUserTaskFromByteData(byte[] _byteData) {
        return UserTask.deserializeUserTaskByteData(_byteData);
    }

    private UserTask createNewUserTaskForEditing(UserTask _userTaskOriginal) {
        if(_userTaskOriginal.getTaskDescription() != null) {//Create new task with description.
            return new UserTask( _userTaskOriginal.getTaskId(),
                    _userTaskOriginal.getTaskName(),
                    _userTaskOriginal.getTaskNotificationTime(),
                    _userTaskOriginal.getTaskDescription(),
                    _userTaskOriginal.getTaskChecked());
        }else {//Create new task without description.
            return new UserTask( _userTaskOriginal.getTaskId(),
                    _userTaskOriginal.getTaskName(),
                    _userTaskOriginal.getTaskNotificationTime(),
                    _userTaskOriginal.getTaskChecked());
        }
    }

    private void updateTaskLists() {

        int updatedTaskPosition = -1;
        UserTaskList taskList = mTaskLists.get(mTaskListPosition);
        for (int i = 0; i < taskList.getTasks().size(); i++) {
            if(taskList.getTasks().get(i).getTaskName().equals(mTaskOriginal.getTaskName())) {
                //Found the position of the task to change.
                updatedTaskPosition = i;
            }
        }

        if(updatedTaskPosition != -1) {
            //Set the edited task to the position.
            taskList.getTasks().set(updatedTaskPosition, mTaskEdited);
        }

        IOUtility.saveTasklistsToStorage(this, mTaskLists);
    }

    private void setIsAlertUpInTaskInfoFragment(boolean _alertUp) {
        TaskInfoFragment fragment = (TaskInfoFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TASKINFO_TAG);
        if(fragment != null) {
            fragment.setIsAlertUp(_alertUp);
        }
    }

    /**
     * Custom methods - Loading and Closing Fragments.
     */

    private void loadTaskInfoFragment() {
        FrameLayout frameLayout = findViewById(R.id.fragment_Container_Task);
        frameLayout.setVisibility(View.VISIBLE);

        if(mEdited) {//This is a good spot to use the edited bool, and load the fragment with the right instance of the task object.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_Container_Task, TaskInfoFragment.newInstance(mTaskEdited), FRAGMENT_TASKINFO_TAG)
                    .commit();
        }else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_Container_Task, TaskInfoFragment.newInstance(mTaskOriginal), FRAGMENT_TASKINFO_TAG)
                    .commit();
        }
    }

    private void loadEditTaskTitleAlertFragment(String _taskName) {
        ArrayList<String> taskNames = new ArrayList<>();
        //Check that mTaskLists is available and instantiated.
        if(mTaskLists != null && !mTaskLists.isEmpty()) {
            for (int i = 0; i < mTaskLists.get(mTaskListPosition).getTasks().size(); i++) {
                taskNames.add(mTaskLists.get(mTaskListPosition).getTasks().get(i).getTaskName());
            }
        }

        FrameLayout frameLayout = findViewById(R.id.fragment_Container_AlertEditTaskTitle);
        frameLayout.setVisibility(View.VISIBLE);

        EditTaskTitleAlertFragment fragment = EditTaskTitleAlertFragment.newInstance(taskNames, mTaskLists.get(mTaskListPosition).getTaskListName(), _taskName);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        animation.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                try {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_Container_AlertEditTaskTitle, fragment, FRAGMENT_EDIT_TASK_TITLE_TAG);
                    fragmentTransaction.commit();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        frameLayout.startAnimation(animation);

        mIsAlertUp = true;
        setIsAlertUpInTaskInfoFragment(true);
    }

    private void closeEditTaskTitleAlertFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_EDIT_TASK_TITLE_TAG);
        if(fragment != null) {
            FrameLayout frameLayout = findViewById(R.id.fragment_Container_AlertEditTaskTitle);

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);
            animation.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    try {
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.remove(fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        //Hide the frame layout.
                        frameLayout.setVisibility(View.GONE);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            frameLayout.startAnimation(animation);

            //Notify the alertUp variable.
            mIsAlertUp = false;
            setIsAlertUpInTaskInfoFragment(false);
        }
    }

    private void loadEditTaskNotificationTimeAlertFragment(String _taskNotificationTime) {
        FrameLayout frameLayout = findViewById(R.id.fragment_Container_AlertEditTaskNotificationTime);
        frameLayout.setVisibility(View.VISIBLE);

        EditTaskNotificationTimeAlertFragment fragment = EditTaskNotificationTimeAlertFragment.newInstance(_taskNotificationTime);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        animation.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                try {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_Container_AlertEditTaskNotificationTime, fragment, FRAGMENT_EDIT_TASK_NOTIFICATION_TIME_TAG);
                    fragmentTransaction.commit();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        frameLayout.startAnimation(animation);

        mIsAlertUp = true;
        setIsAlertUpInTaskInfoFragment(true);
    }

    private void closeEditTaskNotificationTimeAlertFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_EDIT_TASK_NOTIFICATION_TIME_TAG);
        if(fragment != null) {
            FrameLayout frameLayout = findViewById(R.id.fragment_Container_AlertEditTaskNotificationTime);

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);
            animation.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    try {
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.remove(fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        //Hide the frame layout.
                        frameLayout.setVisibility(View.GONE);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            frameLayout.startAnimation(animation);

            //Notify the alertUp variable.
            mIsAlertUp = false;
            setIsAlertUpInTaskInfoFragment(false);
        }
    }

    /**
     * Custom methods - FILE I/O
     */

    private ArrayList<UserTaskList> convertTasklistsFromIntentJSON(ArrayList<String> _taskListJSONList) {
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
