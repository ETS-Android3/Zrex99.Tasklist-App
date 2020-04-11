package com.zoportfolio.checklistproject.Alerts;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

    private static final String TAG = "TLAlertFragment.TAG";

    private EditText mEtNameField;
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
        void cancelTapped();
        void saveTapped(String taskListName);
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
        mEtNameField = view.findViewById(R.id.et_NewTaskListName);
        mTvConfirmAction = view.findViewById(R.id.tv_AlertConfirmText);
        mTvCancelAction = view.findViewById(R.id.tv_AlertCancelText);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() != null) {

            mEtNameField.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        if(ValidateField(mEtNameField)) {
                            mListener.saveTapped(mEtNameField.getText().toString());
                            return false;
                        }
                    }
                    return false;
                }
            });

            //Assign the click listeners to the confirm and cancel actions.
            mTvConfirmAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ValidateField(mEtNameField)) {
                        mListener.saveTapped(mEtNameField.getText().toString());
                        //TODO: Need to check the new name against all current tasklist names here.
                    }
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

    /**
     * Custom Methods
     */

    //TODO: Fix the comments here later.
    //Will return a bool based on if the text is valid or not.
    private boolean ValidateField(EditText editText) {
        //Get the text and trim whitespace from it.
        String text = editText.getText().toString().trim();
        //If there is no text after trimming whitespace, return false.
        if(text.isEmpty()) {
            //TODO: Toast here that the field is not valid and needs text.
            return false;
        }else {
            //Return true for valid text.
            return true;
        }

    }

}
