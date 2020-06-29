package com.zoportfolio.tasklistproject.settings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zoportfolio.tasklistproject.R;

public class DeveloperDataFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DEVELOPERDATA = "developerData";

    private TextView mTvTitle;
    private TextView mTvDeveloperData;
    private ImageButton mIbClose;

    public static DeveloperDataFragment newInstance(String _title, String _developerData) {
        
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, _title);
        args.putString(ARG_DEVELOPERDATA, _developerData);
        
        DeveloperDataFragment fragment = new DeveloperDataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private DeveloperDataFragmentListener mListener;
    public interface DeveloperDataFragmentListener {
        void alertCloseTapped();
    }

    /**
     * Lifecycle methods
     */

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof DeveloperDataFragmentListener) {
            mListener = (DeveloperDataFragmentListener) context;
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.fragment_layout_settings_developerdata, container, false);
        mTvTitle = view.findViewById(R.id.tv_DeveloperDataTitle);
        mTvDeveloperData = view.findViewById(R.id.tv_DeveloperDataText);
        mIbClose = view.findViewById(R.id.ib_Close);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String title = (getArguments() != null ? getArguments().getString(ARG_TITLE) : null);
        String developerData = (getArguments() != null ? getArguments().getString(ARG_DEVELOPERDATA) : null);

        if(title != null && developerData != null) {
            mTvTitle.setText(title);
            mTvDeveloperData.setText(developerData);
            mTvDeveloperData.setMovementMethod(new ScrollingMovementMethod());
        }
        mIbClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.alertCloseTapped();
            }
        });

    }
}
