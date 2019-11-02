package com.sonpm_cloud.explorea;

import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Activity5_Fragment_ViewModel
        extends ViewModel {

    private List<Pair<Marker, String>> points;
    public List<Pair<Marker, String>> getPoints() { return new LinkedList<>(points); }
    public void clearPoints() { points = new LinkedList<>(); }

    public Activity5_Fragment_ViewModel() {
        points = new LinkedList<>();
    }

    public void addPoint(Pair<Marker, String> point) {
        points.add(point);
    }

    public void removePoint(Pair<Marker, String> point) {
        removePoint(point.first);
    }

    public void removePoint(Marker point) {
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).first.equals(point)) {
                points.remove(i);
                System.out.println("<><><><><><><><><>   " + i);
                break;
            }
        }
    }

    public void swapPoints(Pair<Marker, String> p1, Pair<Marker, String> p2) {
        int pos1 = -1, pos2 = -1;
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).first.equals(p1.first)) {
                pos1 = i;
                break;
            }
        }
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).first.equals(p2.first)) {
                pos2 = i;
                break;
            }
        }
        if (pos1 == -1 || pos2 == -1) return;
        Pair<Marker, String> temp = points.get(pos1);
        points.set(pos1, points.get(pos2));
        points.set(pos2, temp);

    public static class RecyclerAdapter
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

        public RecyclerAdapter() {
            this.data = new LinkedList<>();
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
}
