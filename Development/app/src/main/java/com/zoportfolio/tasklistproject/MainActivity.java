package com.zoportfolio.tasklistproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zoportfolio.tasklistproject.alerts.NewTaskAlertFragment;
import com.zoportfolio.tasklistproject.alerts.NewTaskListAlertFragment;
import com.zoportfolio.tasklistproject.contracts.FileContracts;
import com.zoportfolio.tasklistproject.task.TaskInfoActivity;
import com.zoportfolio.tasklistproject.tasklist.adapters.TaskListFragmentPagerAdapter;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTask;
import com.zoportfolio.tasklistproject.tasklist.dataModels.UserTaskList;
import com.zoportfolio.tasklistproject.tasklist.fragments.TaskListFragment;
import com.zoportfolio.tasklistproject.utility.FileUtility;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewTaskListAlertFragment.NewTaskListAlertFragmentListener,
        TaskListFragment.TaskListFragmentListener,
        NewTaskAlertFragment.NewTaskAlertFragmentListener {

    public static final String TAG = "MainActivity.TAG";

    private static final String FRAGMENT_ALERT_NEWTASKLIST_TAG = "FRAGMENT_ALERT_NEWTASKLIST";
    private static final String FRAGMENT_TASKLIST_TAG = "FRAGMENT_TASKLIST";

    public static final String KEY_TASKLISTS = "KEY_TASKLISTS";

    public static final String EXTRA_TASK = "EXTRA_TASK";
    public static final String EXTRA_TASKLISTPOSITION = "EXTRA_TASKLISTPOSITION";
    public static final String EXTRA_TASKLISTS = "EXTRA_TASKLISTS";

    public static final int RESULT_CODE_TASK_CHANGED = 10;
    public static final int RESULT_CODE_TASK_UNCHANGED = 20;
    public static final int REQUEST_CODE_TASK_VIEWING = 3;

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

        //testStorageFeatures();

        loadOnFreshAppOpen();


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

//        loadTaskListFragment(null);

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

//        if(mPager.getCurrentItem() == 0) {
//            super.onBackPressed();
//        } else {
//            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //TODO: Will have to check if their is other stuff that i need to save,
        // Example: which fragment is up/what is that fragment looking at.. etc..

        //Get the tasklists as an arraylist of strings, and then save it to the outState.
        ArrayList<String> taskListsJSON = convertTasklistsForSaving();
        outState.putStringArrayList(KEY_TASKLISTS,taskListsJSON);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        ArrayList<String> taskListsJSON = savedInstanceState.getStringArrayList(KEY_TASKLISTS);

        if(taskListsJSON != null && !taskListsJSON.isEmpty()) {
            if(mTaskLists == null) {
                mTaskLists = new ArrayList<>();
            }else {
                mTaskLists.clear();
            }
            mTaskLists = convertTasklistsFromLoading(taskListsJSON);
            loadTaskListFragment(mTaskLists.get(0));
        }

        //Load the UI.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO: Handle the changed task data and save the updated tasklists.
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
        Log.i(TAG, "saveTapped: New taskList name: " + taskListName);
        if(taskListName != null) {
            Log.i(TAG, "saveTapped: New taskList name: " + taskListName);
            //Create a new taskList
            UserTaskList newTaskList = new UserTaskList(taskListName);

            //Close the Alert fragment before showing the taskList fragment.
            closeAlertFragment();

            if(mTaskLists == null) {
                //For the first time adding a tasklist.
                mTaskLists = new ArrayList<UserTaskList>();
                mTaskLists.add(newTaskList);
            }else {
                mTaskLists.add(newTaskList);
            }

            //Save the tasklists to storage.
            saveTasklistsToStorage();


            loadTaskListFragment(newTaskList);
            //Testing loadViewPager();
        }else {
            //If this happens I need to display to the user that something went wrong.
            //A toast that the saving went wrong.
            Toast toastSaveNewTasklistFailed = Toast.makeText(this, R.string.toast_TaskListSavingFailed, Toast.LENGTH_LONG);
            toastSaveNewTasklistFailed.show();
        }

    }

    //TaskListFragment Callbacks

    @Override
    public void taskTapped(UserTaskList taskList, UserTask task, int taskPosition) {
        //Call method to load the taskInfoActivity.
        loadTaskInfoActivity(task, taskList.getTaskListName());
    }

    @Override
    public void trashTapped(UserTaskList taskList) {
        Log.i(TAG, "trashTapped: running");
        int taskListPosition = -1;
        //Two ways to do this, declare an alert before deleting or just delete the tasklist, for now i will just delete tasklist.
        //Get the tasklist to delete.
        for (int i = 0; i < mTaskLists.size(); i++) {
            if(mTaskLists.get(i).equals(taskList)) {
                taskListPosition = i;
                deleteTaskList(taskListPosition);
            }
        }
    }

    @Override
    public void deleteTask(UserTaskList taskList, UserTask task, int position) {
        int taskListPosition = -1;
        //Get the tasklist and task to delete, and then delete it from the tasklist and make sure the tasklist is up to date.
        for (int i = 0; i < mTaskLists.size(); i++) {
            if(mTaskLists.get(i).equals(taskList)) {
                taskListPosition = i;
                deleteTaskFromTaskList(taskListPosition, position);
            }
        }
    }

    @Override
    public void taskListUpdated(UserTaskList updatedTaskList) {
        //TODO: Need to know which tasklist was updated in order to update the proper tasklist.
        int indexPositionForTasklist = -1;
        for (int i = 0; i < mTaskLists.size(); i++) {
            if(mTaskLists.get(i).getTaskListName().equals(updatedTaskList.getTaskListName())) {
                Log.i(TAG, "taskListUpdated: Found the tasklist to update");
                indexPositionForTasklist = i;
            }
        }

        if(indexPositionForTasklist != -1) {
            //Set the updated tasklist and save it.
            mTaskLists.set(indexPositionForTasklist, updatedTaskList);
            saveTasklistsToStorage();
            Toast toast = Toast.makeText(this, getResources().getString(R.string.toast_TaskListSavingSuccesful), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    //NewTaskAlertFragment Callbacks

    @Override
    public void cancelTappedNewTaskAlert() {

        //TODO: MAJOR IMPORTANT,
        // realistically i should not be doing this at all,
        // and while it does work, it would be much better to handle all fragments ONLY in this activity
        // The fix would be to have the TaskListFragment interface the add task tapped all the way to the activity,
        // and then the activity will handle showing the new alert fragment, and then send the new data back to the TaskListFragment.

        closeAlertFragmentFromTaskListFragment();
    }

    @Override
    public void saveTappedNewTaskAlert(String taskName, String taskNotificationTime, String taskListName) {

        UserTask newTask = new UserTask(taskName, taskNotificationTime);
        for (int i = 0; i < mTaskLists.size(); i++) {
            if(mTaskLists.get(i).getTaskListName().equals(taskListName)) {
                Log.i(TAG, "saveTappedNewTaskAlert: tasklist found.");
                //Add the new task.
                mTaskLists.get(i).getTasks().add(newTask);
                //Save the updated tasklists
                saveTasklistsToStorage();
                //Reload the taskListFragment.
                loadTaskListFragment(mTaskLists.get(i));
                break;
            }
        }

        Toast toast = Toast.makeText(this, getResources().getString(R.string.toast_TaskSavingSuccesful), Toast.LENGTH_LONG);
        toast.show();

        //Have to close out the alert.
        closeAlertFragmentFromTaskListFragment();
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
        TextView textView = findViewById(R.id.tv_noData);
        textView.setVisibility(View.GONE);

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

        ArrayList<String> taskListNames = new ArrayList<>();
        //Check that mTaskLists is available and instantiated.
        if(mTaskLists != null && !mTaskLists.isEmpty()) {
            for (int i = 0; i < mTaskLists.size(); i++) {
                taskListNames.add(mTaskLists.get(i).getTaskListName());
            }
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_Container_AlertNewTaskList, NewTaskListAlertFragment.newInstance(taskListNames), FRAGMENT_ALERT_NEWTASKLIST_TAG)
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

    private void closeAlertFragmentFromTaskListFragment() {
        //Tell the TaskListFragment to close the NewTaskAlertFragment
        TaskListFragment fragment = (TaskListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TASKLIST_TAG);
        if(fragment != null) {
            fragment.closeAlertFragment();
        }
    }

    private void loadViewPager() {
        TextView textView = findViewById(R.id.tv_noData);
        textView.setVisibility(View.GONE);
        mPager.setVisibility(View.VISIBLE);
        pagerAdapter = new TaskListFragmentPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mTaskLists);
        mPager.setAdapter(pagerAdapter);
    }

    private void loadTaskInfoActivity(UserTask selectedTask, String taskListName) {//The tasklist name will be how we identify the tasklist that holds the selected task.

        //Find the tasklist position.
        int taskListPosition = -1;
        for (int i = 0; i < mTaskLists.size(); i++) {
            if(mTaskLists.get(i).getTaskListName().equals(taskListName)) {
                //Position found.
                taskListPosition = i;
                Log.i(TAG, "loadTaskInfoActivity: position of tasklist found.");
            }
        }

        ArrayList<String> taskListsJSON = convertTasklistsForSaving();

        Log.i(TAG, "loadTaskInfoActivity: preparing intent.");
        Intent intent = new Intent(this, TaskInfoActivity.class);
        intent.putExtra(EXTRA_TASK, selectedTask);
        intent.putExtra(EXTRA_TASKLISTPOSITION, taskListPosition);

        intent.putExtra(EXTRA_TASKLISTS, taskListsJSON);

        startActivityForResult(intent, REQUEST_CODE_TASK_VIEWING);
        Log.i(TAG, "loadTaskInfoActivity: activity started.");
    }

    /**
     * Custom methods - FILE I/O
     */

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
        FileUtility.saveToProtectedStorage(this, FileContracts.FILE_TASKLIST_NAME, FileContracts.FILE_TASKLIST_FOLDER, taskListsJSON);
    }

    private boolean checkForTasklistsInStorage() {
        //If this returns 0, that means there are no files
        int fileCount = FileUtility.getCountOfFolderFromProtectedStorage(this, FileContracts.FILE_TASKLIST_FOLDER);
        return fileCount > 0;
    }

    //No null task names anymore, so the tasks are saving fine. I think I fixed this unintentionally when I was adding in null checks elsewhere.
    //NOTE: I can make this method better by having it return the taskLists, and not handling the loading of the tasklist fragment.
    private void loadTasklistsFromStorage() {
        //Check that the mTaskList is not null,
        // and if it isn't clear it so that the stored data can overwrite it.
        if(mTaskLists == null) {
            mTaskLists = new ArrayList<>();
        }else {
            mTaskLists.clear();
        }

        ArrayList<String> taskListJSONList = new ArrayList<>();
        Object obj = FileUtility.retrieveFromStorage(this, FileContracts.FILE_TASKLIST_NAME);
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
        mTaskLists = convertTasklistsFromLoading(taskListJSONList);

        if(!mTaskLists.isEmpty()) {
            loadTaskListFragment(mTaskLists.get(0));
        }
    }

    private void loadOnFreshAppOpen() {
        if(checkForTasklistsInStorage()) {
            loadTasklistsFromStorage();
        }else {
            //If there are no files saved, display the no data text view.
            TextView textView = findViewById(R.id.tv_noData);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void testStorageFeatures() {
        UserTaskList newTaskList = new UserTaskList("Test");
        UserTask newTask1 = new UserTask("Code daily","333", false);
        UserTask newTask2 = new UserTask("ayayaya","222", true);
        newTaskList.addTaskToList(newTask1);
        newTaskList.addTaskToList(newTask2);

        mTaskLists = new ArrayList<UserTaskList>();
        mTaskLists.add(newTaskList);

        boolean tasklistStored = checkForTasklistsInStorage();
        Log.i(TAG, "onCreate: Tasklist in storage: " + tasklistStored);

        saveTasklistsToStorage();

        tasklistStored = checkForTasklistsInStorage();
        Log.i(TAG, "onCreate: Tasklist in storage: " + tasklistStored);

        loadTasklistsFromStorage();
    }

    private void deleteTaskFromTaskList(int taskListPosition, int taskPosition) {
        if(taskListPosition == -1) {
            //Toast for an error deleting.
            Toast toast = Toast.makeText(this, getResources().getString(R.string.toast_TaskDeletingFailed), Toast.LENGTH_LONG);
            toast.show();
        }else {
            //The tasklist fragment needs to update itself independently for now.
            //Ideally i could just reload the tasklist fragment.
            mTaskLists.get(taskListPosition).getTasks().remove(taskPosition);
            saveTasklistsToStorage();

            Toast toast = Toast.makeText(this, getResources().getString(R.string.toast_TaskDeletingSuccesful), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void deleteTaskList(int taskListPosition) {
        if(taskListPosition == -1) {
            //Toast for an error deleting.
        }else {
            mTaskLists.remove(taskListPosition);
            //Save the updated tasklists.
            saveTasklistsToStorage();
            //Check for any remaining tasklists, if not then load the no data text view.
            if(mTaskLists.isEmpty()) {
                loadOnFreshAppOpen();
            }else {
                //Reload the taskListFragment.
                loadTaskListFragment(mTaskLists.get(0));
            }

        }
    }


}
