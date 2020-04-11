package com.zoportfolio.checklistproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zoportfolio.checklistproject.Alerts.NewTaskListAlertFragment;
import com.zoportfolio.checklistproject.Tasklist.DataModels.UserTask;
import com.zoportfolio.checklistproject.Tasklist.DataModels.UserTaskList;
import com.zoportfolio.checklistproject.Tasklist.Fragments.TaskListFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewTaskListAlertFragment.NewTaskListAlertFragmentListener {

    public static final String TAG = "MainActivity.TAG";

    private static final String FRAGMENT_ALERT_NEWTASK_TAG = "FRAGMENT_ALERT_NEWTASK";

    //TODO: This variable is the main way for the main activity to keep track of the task lists.
    private ArrayList<UserTaskList> mTaskLists;

    private static Boolean isAlertUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Check for action bar and hide it if it is up.
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Grab the current date text view to get the date.
        TextView tvCurrentDate = findViewById(R.id.tv_currentDate);
        loadCurrentDate(tvCurrentDate);


        loadTaskListFragment(null);


        //TODOS...
        //TODO: I have to fix the nuemorphic container drawable. SOLVED: Couldn't fix the problem so I will move on for now.


        FloatingActionButton fabAddTaskList = findViewById(R.id.fab_newTaskList);
        fabAddTaskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isAlertUp) {
                    //When the fab is clicked the new task list alert should pop up.
                    loadAlertFragment();
                    isAlertUp = true;
                }
            }
        });
    }

    /**
     * Interface methods
     */

    //NewTaskListAlertFragment Callbacks

    @Override
    public void cancelTapped() {
        //TODO: Revert the background views to being touchable.
        closeAlertFragment();
    }

    @Override
    public void saveTapped(String taskListName) {

        if(taskListName != null) {
            Log.i(TAG, "saveTapped: New taskList name: " + taskListName);
            //Create a new taskList
            UserTaskList newTaskList = new UserTaskList(taskListName);
            //Have to reset the views in the back, and then load the fragment that holds the ListView.

            //Close the Alert fragment before showing the taskList fragment.
            closeAlertFragment();

            UserTask newTask1 = new UserTask("Code daily","333", false);
            UserTask newTask2 = new UserTask("ayayaya","222", true);

            newTaskList.addTaskToList(newTask1);
            newTaskList.addTaskToList(newTask2);

            loadTaskListFragment(newTaskList);
            //TODO: Need to save the taskList to storage here.


        }else {
            //If this happens I need to display to the user that something went wrong.
            //A toast that the saving went wrong.
            Log.i(TAG, "saveTapped: taskListName is null");
        }

    }


    /**
     * Custom methods
     */

    private void loadCurrentDate(TextView _tvCurrentDateDisplay) {

        //Create the variable instances needed for getting the date.
        Calendar calendar;
        SimpleDateFormat simpleDateFormat;
        String currentDate;

        //Get the calendar instance, set the date pattern, grab the time from the calendar instance, finally set the date to the textview.
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("EEEE d, yyyy");
        currentDate = simpleDateFormat.format(calendar.getTime());
        _tvCurrentDateDisplay.setText(currentDate);
    }

    //This method will load the TaskList Fragment.
    private void loadTaskListFragment(UserTaskList _userTaskList) {
        FrameLayout frameLayout = findViewById(R.id.fragment_Container_Tasklist);
        frameLayout.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                //TODO: Remove the null argument when cleaning code.
                .replace(R.id.fragment_Container_Tasklist, TaskListFragment.newInstance(_userTaskList))
                .commit();
    }

    //This method will load the NewTaskListAlert Fragment.
    //TODO: I might need to setup back button support for this fragment, will also need to make sure no other taps on background views are possible.
    private void loadAlertFragment() {
        FrameLayout frameLayout = findViewById(R.id.fragment_Container_Alert);
        frameLayout.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_Container_Alert, NewTaskListAlertFragment.newInstance(), FRAGMENT_ALERT_NEWTASK_TAG)
                .commit();
    }

    private void closeAlertFragment() {
        //Get the fragment by its tag, and null check it.
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_ALERT_NEWTASK_TAG);
        if(fragment != null) {
            //Hide the frame layout.
            FrameLayout frameLayout = findViewById(R.id.fragment_Container_Alert);
            frameLayout.setVisibility(View.GONE);

            //Remove the fragment.
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();

            //Set the bool to false, so a new alert can appear.
            isAlertUp = false;
        }
    }


}
