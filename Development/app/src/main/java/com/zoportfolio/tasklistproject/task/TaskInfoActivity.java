package com.zoportfolio.tasklistproject.task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.zoportfolio.tasklistproject.MainActivity;
import com.zoportfolio.tasklistproject.R;
import com.zoportfolio.tasklistproject.alerts.EditTaskNotificationTimeAlertFragment;
import com.zoportfolio.tasklistproject.alerts.EditTaskTitleAlertFragment;
import com.zoportfolio.tasklistproject.contracts.FileContracts;
import com.zoportfolio.tasklistproject.task.fragments.TaskInfoFragment;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTaskList;
import com.zoportfolio.tasklistproject.utility.FileUtility;

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

    //TODO: NOTE: The saving works on the edited tasks, still need to communicate with the main activity though,
    // Will need to test this exstensively.

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
                    resultIntent.putExtra(MainActivity.EXTRA_TASKLISTPOSITION, mTaskListPosition);
                    setResult(MainActivity.RESULT_CODE_TASK_CHANGED, resultIntent);
                    finish();
                    //TODO: When returning to the main activity I will compare the returned task to the tasklist there and make sure there is a difference,
                    // then load the tasklists from storage.
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
        updateTaskLists();
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
            mTaskEdited.setTaskName(taskNameEdited);
            if(!mTaskEdited.getTaskName().equals(mTaskOriginal.getTaskName())) {
                Log.i(TAG, "saveTappedEditTitle: Task name has been edited and is different.");
            }
            closeEditTaskTitleAlertFragment();
            loadTaskInfoFragment();
        }
        updateTaskLists();
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
            mTaskEdited.setTaskNotificationTime(taskNotificationTimeEdited);
            if(!mTaskEdited.getTaskNotificationTime().equals(mTaskOriginal.getTaskName())) {
                Log.i(TAG, "saveTappedEditNotificationTime: Task notification time has been edited and is different.");
            }
            closeEditTaskNotificationTimeAlertFragment();
            loadTaskInfoFragment();
        }else {
            mTaskEdited.setTaskNotificationTime(taskNotificationTimeEdited);
            if(!mTaskEdited.getTaskNotificationTime().equals(mTaskOriginal.getTaskName())) {
                Log.i(TAG, "saveTappedEditNotificationTime: Task notification time has been edited and is different.");
            }
            closeEditTaskNotificationTimeAlertFragment();
            loadTaskInfoFragment();
        }
        updateTaskLists();
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

    private void updateTaskLists() {

        int updatedTaskPosition = -1;
        UserTaskList taskList = mTaskLists.get(mTaskListPosition);
        for (int i = 0; i < taskList.getTasks().size(); i++) {
            if(taskList.getTasks().get(i).getTaskName().equals(mTaskOriginal.getTaskName())) {
                Log.i(TAG, "updateTaskLists: found the position of the task to change.");
                //Found the position of the task to change.
                updatedTaskPosition = i;
            }
        }

        if(updatedTaskPosition != -1) {
            //Set the edited task to the position.
            taskList.getTasks().set(updatedTaskPosition, mTaskEdited);
        }

        saveTasklistsToStorage();
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

    /**
     * Custom methods - FILE I/O
     */

    //TODO: Convert these methods into a utility class for saving and loading.

    private ArrayList<String> convertTasklistsForSaving() {
        ArrayList<String> taskListsJSON = new ArrayList<>();
        for (int i = 0; i < mTaskLists.size(); i++) {
            UserTaskList taskList = mTaskLists.get(i);
            //Add the JSON tasklist to the arrayList.
            taskListsJSON.add(taskList.toJSONString());
        }
        return taskListsJSON;
    }


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

    private void saveTasklistsToStorage() {
        //Convert all the tasklists to JSON for saving.
        ArrayList<String> taskListsJSON = convertTasklistsForSaving();

        //Once all tasklists have been added to the string array, save them to storage.
        boolean saveStatus = FileUtility.saveToProtectedStorage(this, FileContracts.FILE_TASKLIST_NAME, FileContracts.FILE_TASKLIST_FOLDER, taskListsJSON);
        //TODO: Can toast that saving was successful not sure.
        Log.i(TAG, "saveTasklistsToStorage: Save status: " + saveStatus);
    }

    private boolean checkForTasklistsInStorage() {
        //If this returns 0, that means there are no files
        int fileCount = FileUtility.getCountOfFolderFromProtectedStorage(this, FileContracts.FILE_TASKLIST_FOLDER);
        return fileCount > 0;
    }


}
