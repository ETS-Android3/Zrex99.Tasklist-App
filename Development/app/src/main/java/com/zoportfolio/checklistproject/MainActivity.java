package com.zoportfolio.checklistproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zoportfolio.checklistproject.Alerts.NewTaskAlertFragment;
import com.zoportfolio.checklistproject.Alerts.NewTaskListAlertFragment;
import com.zoportfolio.checklistproject.Tasklist.Adapters.TaskListFragmentPagerAdapter;
import com.zoportfolio.checklistproject.Tasklist.DataModels.UserTask;
import com.zoportfolio.checklistproject.Tasklist.DataModels.UserTaskList;
import com.zoportfolio.checklistproject.Tasklist.Fragments.TaskListFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewTaskListAlertFragment.NewTaskListAlertFragmentListener, TaskListFragment.TaskListFragmentListener, NewTaskAlertFragment.NewTaskAlertFragmentListener {

    public static final String TAG = "MainActivity.TAG";

    private static final String FRAGMENT_ALERT_NEWTASKLIST_TAG = "FRAGMENT_ALERT_NEWTASKLIST";
    private static final String FRAGMENT_TASKLIST_TAG = "FRAGMENT_TASKLIST";

    public static final String FILE_TASKLIST_FOLDER = "TasklistFolder";
    public static final String FILE_TASKLIST_ = "TasklistChecklist";

    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    //TODO: This variable is the main way for the main activity to keep track of the task lists.
    private ArrayList<UserTaskList> mTaskLists;

    private static Boolean isAlertUp = false;

    /**
     * Lifecycle methods
     */

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

        //TODO: Will need to null check the mTaskList at crucial points through the app.
        // Coming back to this stuff after I handle the other features.
//        mPager = findViewById(R.id.vp_Tasklist);
//
//        if(mTaskLists == null || mTaskLists.isEmpty()) {
//            //Hide the view pager to display a textview to tell the user to input a new tasklist.
//            mPager.setVisibility(View.INVISIBLE);
//        }else {
//            //TODO: Test this out, alot.
//            mPager.setVisibility(View.VISIBLE);
//            pagerAdapter = new TaskListFragmentPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mTaskLists);
//            mPager.setAdapter(pagerAdapter);
//        }

        //Utilizing this method for now until I get the other features done and can focus more on the viewpager.
        TextView textView = findViewById(R.id.tv_noData);
        textView.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        //To handle the back event,
        // go back a tasklist if the first tasklist is not shown,
        // otherwise handle the back according to the system.
        if(mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }

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

            //TODO: Delete this testing stuff after.
            UserTask newTask1 = new UserTask("Code daily","333", false);
            UserTask newTask2 = new UserTask("ayayaya","222", true);
            newTaskList.addTaskToList(newTask1);
            newTaskList.addTaskToList(newTask2);


//            if(mTaskLists == null) {
//                mTaskLists = new ArrayList<UserTaskList>();
//                mTaskLists.add(newTaskList);
            //TODO: make it so that the new tasklist can be added,
            // only if the mTaskLists is not null,
            // if it is null then intiliaze a new array list.
            // Do this after handling local storage.
//            }
            //loadViewPager();

            loadTaskListFragment(newTaskList);
            //TODO: Need to save the taskList to storage here.


        }else {
            //If this happens I need to display to the user that something went wrong.
            //A toast that the saving went wrong.
            Log.i(TAG, "saveTapped: taskListName is null");
        }

    }

    //TaskListFragment Callbacks

    @Override
    public void taskTapped() {

    }

    @Override
    public void editTapped() {

    }

    @Override
    public void taskListUpdated(UserTaskList updatedTaskList) {

    }

    //NewTaskAlertFragment Callbacks

    @Override
    public void cancelTappedNewTaskAlert() {

        //TODO: MAJOR IMPORTANT,
        // realistically i should not be doing this at all,
        // and while it does work, it would be much better to handle all fragments ONLY in this activity
        // The fix would be to have the TaskListFragment interface the add task tapped all the way to the activity,
        // and then the activity will handle showing the new alert fragment, and then send the new data back to the TaskListFragment.

        //Tell the TaskListFragment to close the NewTaskAlertFragment
        TaskListFragment fragment = (TaskListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TASKLIST_TAG);
        if(fragment != null) {
            fragment.closeAlertFragment();
        }


    }

    @Override
    public void saveTappedNewTaskAlert(String taskListName, String taskNotificationTime) {

        //TODO: Do what needs to be done with the new task information and then close the alert.
        TaskListFragment fragment = (TaskListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TASKLIST_TAG);
        if(fragment != null) {
            fragment.closeAlertFragment();

            UserTask newTask = new UserTask(taskListName, taskNotificationTime);
            //Update the taskList when i start tracking it on the main activity, and update the fragments tasklist.
            fragment.addNewTaskToTaskList(newTask);
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
                .replace(R.id.fragment_Container_Tasklist, TaskListFragment.newInstance(_userTaskList), FRAGMENT_TASKLIST_TAG)
                .commit();
    }

    //This method will load the NewTaskListAlert Fragment.
    //TODO: I might need to setup back button support for this fragment, will also need to make sure no other taps on background views are possible.
    private void loadAlertFragment() {
        FrameLayout frameLayout = findViewById(R.id.fragment_Container_AlertNewTaskList);
        frameLayout.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_Container_AlertNewTaskList, NewTaskListAlertFragment.newInstance(), FRAGMENT_ALERT_NEWTASKLIST_TAG)
                .commit();
    }

    private void closeAlertFragment() {
        //Get the fragment by its tag, and null check it.
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_ALERT_NEWTASKLIST_TAG);
        if(fragment != null) {
            //Hide the frame layout.
            FrameLayout frameLayout = findViewById(R.id.fragment_Container_AlertNewTaskList);
            frameLayout.setVisibility(View.GONE);

            //Remove the fragment.
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();

            //Set the bool to false, so a new alert can appear.
            isAlertUp = false;
        }
    }

    private void loadViewPager() {
        TextView textView = findViewById(R.id.tv_noData);
        textView.setVisibility(View.GONE);
        mPager.setVisibility(View.VISIBLE);
        pagerAdapter = new TaskListFragmentPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mTaskLists);
        mPager.setAdapter(pagerAdapter);
    }

}
