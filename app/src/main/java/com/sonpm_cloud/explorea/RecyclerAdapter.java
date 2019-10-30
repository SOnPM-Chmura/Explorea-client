package com.sonpm_cloud.explorea;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class RecyclerAdapter
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
        implements ItemMoveCallback.ItemTouchHelperContract {

    private List<String> data;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        View rowView;

        public ViewHolder(View itemView) {
            super(itemView);

            rowView = itemView;
            title = itemView.findViewById(R.id.title);
        }
    }

    public RecyclerAdapter(List<String> data) {
        this.data = data;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(data.get(position));
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.LTGRAY);

    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);

    }
}
