package com.sonpm_cloud.explorea;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract adapter;

    public ItemMoveCallback(ItemTouchHelperContract adapter) {
        this.adapter = adapter;
    }

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
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof Activity5_Fragment_ViewModel.RecyclerAdapter.ViewHolder) {
                Activity5_Fragment_ViewModel.RecyclerAdapter.ViewHolder holder =
                        (Activity5_Fragment_ViewModel.RecyclerAdapter.ViewHolder) viewHolder;
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

        if (viewHolder instanceof Activity5_Fragment_ViewModel.RecyclerAdapter.ViewHolder) {
            Activity5_Fragment_ViewModel.RecyclerAdapter.ViewHolder holder =
                    (Activity5_Fragment_ViewModel.RecyclerAdapter.ViewHolder) viewHolder;
            adapter.onRowClear(holder);
        }
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(Activity5_Fragment_ViewModel.RecyclerAdapter.ViewHolder viewHolder);
        void onRowClear(Activity5_Fragment_ViewModel.RecyclerAdapter.ViewHolder viewHolder);

    }
}
