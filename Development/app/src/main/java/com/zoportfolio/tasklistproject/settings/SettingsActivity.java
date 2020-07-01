package com.zoportfolio.tasklistproject.settings;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.zoportfolio.tasklistproject.R;
import com.zoportfolio.tasklistproject.settings.fragments.DeveloperDataFragment;

public class SettingsActivity extends AppCompatActivity implements DeveloperDataFragment.DeveloperDataFragmentListener {

    private static final String TAG = "SettingsActivity.TAG";

    private static final String FRAGMENT_DEVELOPERDATA_TAG = "FRAGMENT_DEVELOPERDATA";

    private boolean mIsAlertUp = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Button btn_Back = findViewById(R.id.btn_Back);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_PrivacyPolicy = findViewById(R.id.tv_PrivacyPolicy);
        TextView tv_TermsConditions = findViewById(R.id.tv_TermsConditions);

        setupDeveloperDataTextViews(tv_PrivacyPolicy, tv_TermsConditions);
    }

    /**
     * Lifecycle methods
     */


    /**
     * Interface methods
     */

    @Override
    public void alertCloseTapped() {
        closeDeveloperDataFragment();
    }

    /**
     * Custom methods
     */


    private void setupDeveloperDataTextViews(TextView _tv_PrivacyPolicy, TextView _tv_TermsConditions) {

        _tv_PrivacyPolicy.setEnabled(true);
        _tv_PrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsAlertUp) {
                    String title = getResources().getString(R.string.privacyPolicy);
                    String developerData = getResources().getString(R.string.DeveloperData_PrivacyPolicy);
                    loadDeveloperDataFragment(title, developerData);
                }
            }
        });

        _tv_TermsConditions.setEnabled(true);
        _tv_TermsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsAlertUp) {
                    String title = getResources().getString(R.string.termsConditions);
                    String developerData = getResources().getString(R.string.DeveloperData_TermsConditions);
                    loadDeveloperDataFragment(title, developerData);
                }
            }
        });
    }

    private void loadDeveloperDataFragment(String _title, String _developerData) {
        FrameLayout frameLayout = findViewById(R.id.fragment_Container_DeveloperData);
        frameLayout.setVisibility(View.VISIBLE);

        DeveloperDataFragment fragment = DeveloperDataFragment.newInstance(_title, _developerData);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        animation.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                try {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_Container_DeveloperData, fragment, FRAGMENT_DEVELOPERDATA_TAG);
                    fragmentTransaction.commit();
                } catch (Exception e) {
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
    }

    private void closeDeveloperDataFragment() {

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_DEVELOPERDATA_TAG);
        if(fragment != null) {
            FrameLayout frameLayout = findViewById(R.id.fragment_Container_DeveloperData);

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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            frameLayout.startAnimation(animation);
            mIsAlertUp = false;
        }

    }



}
