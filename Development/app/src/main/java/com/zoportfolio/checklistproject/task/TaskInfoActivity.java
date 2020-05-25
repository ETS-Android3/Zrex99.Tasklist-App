package com.zoportfolio.checklistproject.task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.zoportfolio.checklistproject.MainActivity;
import com.zoportfolio.checklistproject.R;
import com.zoportfolio.checklistproject.alerts.EditTaskNotificationTimeAlertFragment;
import com.zoportfolio.checklistproject.alerts.EditTaskTitleAlertFragment;
import com.zoportfolio.checklistproject.task.fragments.TaskInfoFragment;
import com.zoportfolio.checklistproject.tasklist.dataModels.UserTask;
import com.zoportfolio.checklistproject.tasklist.dataModels.UserTaskList;
import com.zoportfolio.checklistproject.utility.TimeConversionUtility;

import java.util.ArrayList;

public class TaskInfoActivity extends AppCompatActivity implements TaskInfoFragment.TaskInfoFragmentListener,
        EditTaskTitleAlertFragment.EditTaskTitleAlertFragmentListener,
        EditTaskNotificationTimeAlertFragment.EditTaskNotificationTimeAlertFragmentListener {

    private static final String TAG = "TaskInfoActivity.TAG";

    private static final String FRAGMENT_TASKINFO_TAG = "FRAGMENT_TASKINFO";
    private static final String FRAGMENT_EDIT_TASK_TITLE_TAG = "FRAGMENT_EDIT_TASK_TITLE";
    private static final String FRAGMENT_EDIT_TASK_NOTIFICATION_TIME_TAG = "FRAGMENT_EDIT_TASK_TITLE";

    //TODO: Need to change the position of the backbutton in the xml slightly.
    // Trying to acheive a more user friendly and less cluttered look.

    //These two vars will keep track of the user data.
    private UserTask mTaskOriginal;
    private UserTask mTaskEdited;
    private int mTaskListPosition;
    private ArrayList<UserTaskList> mTaskLists;

    private boolean mEdited = false;
    private boolean mIsAlertUp = false;//TODO: NOTE: Since i have this variable i can use this as a way to disable buttons or views while the alert is up. Just check against this bool.

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

        //Need to grab the data from the intent.
        Intent intent = getIntent();
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
        mTaskLists = convertTasklistsFromLoading(taskListJSONList);

        //TODO: Look into why the ripple is not working on this button,
        // easy fix imo is to just use an image button potentially.
        Button backButton = findViewById(R.id.btn_Back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check that the original task is different or not from the edited task, and then send back the right result code based on that.
                if(mTaskOriginal.equals(mTaskEdited)) {
                    //No changes made.
                    setResult(MainActivity.RESULT_CODE_TASK_UNCHANGED);
                    finish();
                }else {
                    //Run the method to update the tasklists variable and then send it back as an intent.
                    updateTaskLists();

                    //Set the result intent so that the main activity can handle the changed data.
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(MainActivity.EXTRA_TASKLISTS, mTaskLists);
                    setResult(MainActivity.RESULT_CODE_TASK_CHANGED, resultIntent);
                    finish();
                }
            }
        });

        //Once all the data is gotten, load the taskInfoFragment.
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

        //May not need to reload the task info fragment in this . Will need to see.
        //loadTaskInfoFragment();
        //TODO: Need to add in a way to save the tasklists, copy the methods from the main activity.
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
        // TODO: Need to close and get rid of the alert fragment.
        Log.i(TAG, "cancelTappedEditTitle: closing alert");
        closeEditTaskTitleAlertFragment();
    }

    @Override
    public void saveTappedEditTitle(String taskNameEdited) {
        if(!mEdited) {
            mEdited = true;//If this was the first edit of the task, then set the edited bool to true.
            mTaskEdited.setTaskName(taskNameEdited);
            if(!mTaskEdited.getTaskName().equals(mTaskOriginal.getTaskName())) {
                Log.i(TAG, "saveTappedEditTitle: Task name has been edited and is different.");
            }
            closeEditTaskTitleAlertFragment();
            loadTaskInfoFragment();
        }else {
            //Not sure why i am checking, will look for reasons in the morning.
            //TODO: Need to save and get rid of the alert fragment.
            mTaskEdited.setTaskName(taskNameEdited);
            if(!mTaskEdited.getTaskName().equals(mTaskOriginal.getTaskName())) {
                Log.i(TAG, "saveTappedEditTitle: Task name has been edited and is different.");
            }
            closeEditTaskTitleAlertFragment();
            loadTaskInfoFragment();
        }
        //TODO: Need to add in a way to save the tasklists, copy the methods from the main activity.
    }

    //--- Edit Notification Time Alert Interface ---

    @Override
    public void cancelTappedEditNotificationTime() {
        closeEditTaskNotificationTimeAlertFragment();
    }

    @Override
    public void saveTappedEditNotificationTime(String taskNotificationTimeEdited) {
        //TODO: will need to copy parts from the saveTappedEditTitle method,
        // tomorrow when I come back to work on this.
        Log.i(TAG, "saveTappedEditNotificationTime: new notification time: " + taskNotificationTimeEdited);
    }

    /**
     * Custom methods
     */

    private UserTask createNewUserTaskForEditing(UserTask _userTaskOriginal) {
        if(_userTaskOriginal.getTaskDescription() != null) {//Create new task with description.
            return new UserTask(_userTaskOriginal.getTaskName(),
                    _userTaskOriginal.getTaskNotificationTime(),
                    _userTaskOriginal.getTaskDescription(),
                    _userTaskOriginal.getTaskChecked());
        }else {//Create new task without description.
            return new UserTask(_userTaskOriginal.getTaskName(),
                    _userTaskOriginal.getTaskNotificationTime(),
                    _userTaskOriginal.getTaskChecked());
        }
    }


    //TODO: Would be good to turn this into a static method on UserTaskList class.
    private ArrayList<UserTaskList> convertTasklistsFromLoading(ArrayList<String> taskListJSONList) {
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

    private void updateTaskLists() {

        int updatedTaskPosition = -1;
        UserTaskList taskList = mTaskLists.get(mTaskListPosition);
        for (int i = 0; i < taskList.getTasks().size(); i++) {
            if(taskList.getTasks().get(i).equals(mTaskOriginal)) {
                Log.i(TAG, "updateTaskLists: found the position of the task to change.");
                //Found the position of the task to change.
                updatedTaskPosition = i;
            }
        }

        if(updatedTaskPosition != -1) {
            //Set the edited task to the position.
            taskList.getTasks().set(updatedTaskPosition, mTaskEdited);
        }

    }

    /**
     * Custom methods - Loading and Closing Fragments.
     */

    private void loadTaskInfoFragment() {
        FrameLayout frameLayout = findViewById(R.id.fragment_Container_Task);
        frameLayout.setVisibility(View.VISIBLE);


        if(mEdited) {//This is a good spot to use the edited bool, and load the fragment with the right instance of the task object.
            Log.i(TAG, "loadTaskInfoFragment: Loading with edited task");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_Container_Task, TaskInfoFragment.newInstance(mTaskEdited), FRAGMENT_TASKINFO_TAG)
                    .commit();
        }else {
            Log.i(TAG, "loadTaskInfoFragment: Loading with original task");
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
                Log.i(TAG, "loadEditTaskTitleAlertFragment: TaskNames Array: " + taskNames.get(i).toString());
            }
        }

        FrameLayout frameLayout = findViewById(R.id.fragment_Container_AlertEditTaskTitle);
        frameLayout.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_Container_AlertEditTaskTitle, EditTaskTitleAlertFragment.newInstance(taskNames, mTaskLists.get(mTaskListPosition).getTaskListName(), _taskName), FRAGMENT_EDIT_TASK_TITLE_TAG)
                .commit();

        mIsAlertUp = true;
    }

    private void closeEditTaskTitleAlertFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_EDIT_TASK_TITLE_TAG);
        if(fragment != null) {
            //Hide the frame layout
            FrameLayout frameLayout = findViewById(R.id.fragment_Container_AlertEditTaskTitle);
            frameLayout.setVisibility(View.GONE);

            //Remove the fragment
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();

            //Notify the alertUp variable.
            mIsAlertUp = false;
        }
    }

    private void loadEditTaskNotificationTimeAlertFragment(String _taskNotificationTime) {
        FrameLayout frameLayout = findViewById(R.id.fragment_Container_AlertEditTaskNotificationTime);
        frameLayout.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_Container_AlertEditTaskNotificationTime, EditTaskNotificationTimeAlertFragment.newInstance(_taskNotificationTime), FRAGMENT_EDIT_TASK_NOTIFICATION_TIME_TAG)
                .commit();

        mIsAlertUp = true;
    }

    private void closeEditTaskNotificationTimeAlertFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_EDIT_TASK_NOTIFICATION_TIME_TAG);
        if(fragment != null) {
            //Hide the frame layout
            FrameLayout frameLayout = findViewById(R.id.fragment_Container_AlertEditTaskNotificationTime);
            frameLayout.setVisibility(View.GONE);

            //Remove the fragment
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();

            //Notify the alertUp variable.
            mIsAlertUp = false;
        }
    }



}
