package com.zoportfolio.tasklistproject.tasklist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zoportfolio.tasklistproject.R;

public class PaginationDotsAdapter extends RecyclerView.Adapter<PaginationDotsAdapter.PaginationDotsViewHolder> {

    private int mCount;
    private int mSelectedPosition;

    public PaginationDotsAdapter(int _count, int _selectedPostion) {
        mCount = _count;
        mSelectedPosition = _selectedPostion;
    }

    @NonNull
    @Override
    public PaginationDotsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pagination_adapter_layout, parent, false);

        PaginationDotsViewHolder vh = new PaginationDotsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PaginationDotsViewHolder holder, int position) {

        if(position == mSelectedPosition) {
            holder.imageView.setImageResource(R.drawable.pagination_selected_circle);
        }else {
            holder.imageView.setImageResource(R.drawable.pagination_unselected_circle);
        }

    }

    @Override
    public int getItemCount() {
        return mCount;
    }


    static class PaginationDotsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        PaginationDotsViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.iv_paginationDot);
        }
    }


}
