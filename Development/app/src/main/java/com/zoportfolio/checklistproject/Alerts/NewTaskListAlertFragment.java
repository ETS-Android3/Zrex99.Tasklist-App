package com.zoportfolio.checklistproject.Alerts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.checklistproject.R;

public class NewTaskListAlertFragment extends Fragment {

    public static NewTaskListAlertFragment newInstance() {

        Bundle args = new Bundle();

        NewTaskListAlertFragment fragment = new NewTaskListAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private NewTaskListAlertFragmentListener mListener;
    public interface NewTaskListAlertFragmentListener {
        void cancelTapped();
        void saveTapped();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof NewTaskListAlertFragmentListener) {
            mListener = (NewTaskListAlertFragmentListener)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragme)
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
