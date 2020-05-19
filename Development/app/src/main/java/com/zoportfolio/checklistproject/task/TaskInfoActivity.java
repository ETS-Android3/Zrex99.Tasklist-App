package com.zoportfolio.checklistproject.task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zoportfolio.checklistproject.MainActivity;
import com.zoportfolio.checklistproject.R;
import com.zoportfolio.checklistproject.task.fragments.TaskInfoFragment;
import com.zoportfolio.checklistproject.tasklist.dataModels.UserTask;
import com.zoportfolio.checklistproject.tasklist.dataModels.UserTaskList;

import java.util.ArrayList;

public class TaskInfoActivity extends AppCompatActivity implements TaskInfoFragment.TaskInfoFragmentListener {

    private static final String TAG = "TaskInfoActivity.TAG";

    private static final String FRAGMENT_TASKINFO_TAG = "FRAGMENT_TASKINFO";

    //TODO: Need to change the position of the backbutton in the xml slightly.
    // Trying to acheive a more user friendly and less cluttered look.

    //These two vars will keep track of the user data.
    private UserTask mTaskOriginal;
    private UserTask mTaskEdited;
    private int mTaskListPosition;
    private ArrayList<UserTaskList> mTaskLists;

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
        mTaskEdited = mTaskOriginal;
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

    }

    @Override
    public void editNotificationTime(String notificationTime) {

    }

    @Override
    public void editTitle(String taskTitle) {

    }

    /**
     * Custom methods
     */

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

    private void loadTaskInfoFragment() {
        FrameLayout frameLayout = findViewById(R.id.fragment_Container_Task);
        frameLayout.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_Container_Task, TaskInfoFragment.newInstance(mTaskOriginal), FRAGMENT_TASKINFO_TAG)
                .commit();
    }


}
