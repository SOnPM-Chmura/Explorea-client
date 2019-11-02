package com.sonpm_cloud.explorea;

import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.Marker;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Activity5_Fragment_ViewModel
        extends ViewModel {

    private List<Pair<Marker, String>> points;

    private RecyclerAdapter adapter;
    public RecyclerAdapter getAdapter() { return adapter; }

    public List<Pair<Marker, String>> getPoints() { return new LinkedList<>(points); }
    public void clearPoints() {
        points.clear();
        adapter.clearItems();
    }

    public Activity5_Fragment_ViewModel() {
        points = new LinkedList<>();
        adapter = new RecyclerAdapter(this);
    }

    public void addPoint(Pair<Marker, String> point) {
        points.add(point);
        adapter.addItem(point.second);
    }

    public void removePoint(Pair<Marker, String> point) { removePoint(point.first); }

    public void removePoint(Marker point) {
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).first.equals(point)) {
                points.remove(i);
                adapter.removeItem(i);
                break;
            }
        }
    }

    public void movePoint(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(points, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(points, i, i - 1);
            }
        }
    }

    public static class RecyclerAdapter
            extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
            implements ItemMoveCallback.ItemTouchHelperContract {

        private List<String> data;
        private Activity5_Fragment_ViewModel model;

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView title;
            View rowView;

            public ViewHolder(View itemView) {
                super(itemView);

                rowView = itemView;
                title = itemView.findViewById(R.id.title);
            }
        }

        public RecyclerAdapter(Activity5_Fragment_ViewModel model) {
            this.data = new LinkedList<>();
            this.model = model;
        }

        public RecyclerAdapter(List<String> data, Activity5_Fragment_ViewModel model) {
            this.data = data;
            this.model = model;
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
            model.movePoint(fromPosition, toPosition);
        }

        @Override
        public void onRowSelected(ViewHolder myViewHolder) {
            myViewHolder.rowView.setBackgroundColor(Color.LTGRAY);

        }

        @Override
        public void onRowClear(ViewHolder myViewHolder) {
            myViewHolder.rowView.setBackgroundColor(Color.WHITE);

        }

        public void addItem(String item) {
            data.add(item);
            notifyItemInserted(data.size() - 1 );
        }

        public void removeItem(int item) {
            data.remove(item);
            notifyItemRemoved(item);
        }

        public void clearItems() {
            int size = data.size();
            data.clear();
            notifyItemRangeRemoved(0, size);
        }
    }
}
