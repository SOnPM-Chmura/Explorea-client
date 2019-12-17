package com.sonpm_cloud.explorea.A5_CreateRoad;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.sonpm_cloud.explorea.R;
import com.sonpm_cloud.explorea.data_classes.LiveList;
import com.sonpm_cloud.explorea.data_classes.MutablePair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FragmentViewModel extends ViewModel {

    private LiveList<MutablePair<LatLng, String>> points;

    private RecyclerAdapter adapter;
    public RecyclerAdapter getAdapter() { return adapter; }

    public List<MutablePair<LatLng, String>> getListPoints() { return new LinkedList<>(points); }
    public LiveList<MutablePair<LatLng, String>> getPoints() { return points; }
    public void clearPoints() {
        points.clear();
        adapter.clearItems();
    }

    public FragmentViewModel() {
        points = new LiveList<>(new LinkedList<>());
        adapter = new RecyclerAdapter(this);
    }

    public void addPoint(MutablePair<LatLng, String> point) {
        points.add(point);
        adapter.addItem(point.second);
    }

    public void removePoint(MutablePair<LatLng, String> point) { removePoint(point.first); }

    public void removePoint(LatLng point) {
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
        private FragmentViewModel model;

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView title;
            View rowView;

            public ViewHolder(View itemView) {
                super(itemView);

                rowView = itemView;
                title = itemView.findViewById(R.id.title);
            }
        }

        public RecyclerAdapter(FragmentViewModel model) {
            this.data = new LinkedList<>();
            this.model = model;
        }

        public RecyclerAdapter(List<String> data, FragmentViewModel model) {
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
        public int getItemCount() { return data.size(); }

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

    public static class ItemMoveCallback extends ItemTouchHelper.Callback {

        private final ItemTouchHelperContract adapter;

        public ItemMoveCallback(ItemTouchHelperContract adapter) { this.adapter = adapter; }

        @Override
        public boolean isLongPressDragEnabled() { return true; }

        @Override
        public boolean isItemViewSwipeEnabled() { return false; }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {  }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
            int ret = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            return makeMovementFlags(ret, 0);
        }

        @Override
        public boolean onMove(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder,
                @NonNull RecyclerView.ViewHolder target) {
            adapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder,
                                      int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder instanceof RecyclerAdapter.ViewHolder) {
                    RecyclerAdapter.ViewHolder holder =
                            (RecyclerAdapter.ViewHolder) viewHolder;
                    adapter.onRowSelected(holder);
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            if (viewHolder instanceof RecyclerAdapter.ViewHolder) {
                RecyclerAdapter.ViewHolder holder =
                        (RecyclerAdapter.ViewHolder) viewHolder;
                adapter.onRowClear(holder);
            }
        }

        public interface ItemTouchHelperContract {

            void onRowMoved(int fromPosition, int toPosition);
            void onRowSelected(RecyclerAdapter.ViewHolder viewHolder);
            void onRowClear(RecyclerAdapter.ViewHolder viewHolder);

        }
    }
}
