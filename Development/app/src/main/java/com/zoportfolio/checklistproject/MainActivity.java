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
import com.zoportfolio.checklistproject.Tasklist.Fragments.TaskListFragment;

public class MainActivity extends AppCompatActivity implements NewTaskListAlertFragment.NewTaskListAlertFragmentListener {

    public static final String TAG = "MainActivity.TAG";

    private static final String FRAGMENT_ALERT_NEWTASK_TAG = "FRAGMENT_ALERT_NEWTASK";

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

        loadFragment();

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

    @Override
    public void saveTapped() {

        //TODO: Refer to the onActivityCreated method for what todo for this callback.
        Log.i(TAG, "saveTapped: ");
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
    private void loadFragment() {
        FrameLayout frameLayout = findViewById(R.id.fragment_Container_Tasklist);
        frameLayout.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_Container_Tasklist, TaskListFragment.newInstance())
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


}
