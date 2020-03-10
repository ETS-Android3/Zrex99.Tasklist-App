package com.zoportfolio.checklistproject.Alerts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.checklistproject.R;

public class NewTaskListAlertFragment extends Fragment {

    private TextView mTvConfirmAction;
    private TextView mTvCancelAction;

    public static NewTaskListAlertFragment newInstance() {

        Bundle args = new Bundle();

        NewTaskListAlertFragment fragment = new NewTaskListAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private NewTaskListAlertFragmentListener mListener;
    public interface NewTaskListAlertFragmentListener {
        //TODO: Work on these callbacks next.
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
        //TODO: Will need to finish the alert layout.

        View view = getLayoutInflater().inflate(R.layout.fragment_layout_alert, container, false);
        mTvConfirmAction = view.findViewById(R.id.tv_AlertConfirmText);
        mTvCancelAction = view.findViewById(R.id.tv_AlertCancelText);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() != null) {

            //Assign the click listeners to the confirm and cancel actions.
            mTvConfirmAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Will have to verify the text data in the edit text,
                    // and then capture the name when the other components are done.
                    mListener.saveTapped();
                }
            });

            mTvCancelAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.cancelTapped();
                }
            });


        }
    }
}
