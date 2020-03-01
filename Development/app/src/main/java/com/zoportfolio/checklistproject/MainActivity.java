package com.zoportfolio.checklistproject;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zoportfolio.checklistproject.Tasklist.Fragments.TaskListFragment;

public class MainActivity extends AppCompatActivity {

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

        //TODO: Assign the FAB its on click listener, load the tasklist fragment, and load the date for the textview.
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

}
