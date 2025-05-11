package edu.sjsu.sase.android.spoleralert;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

public class GroceriesItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private GroceriesAdapter groceries_adapter;

    public GroceriesItemTouchHelper(GroceriesAdapter groceries_adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.groceries_adapter = groceries_adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        //if you swipe to the left, you use the item
        if (direction == ItemTouchHelper.LEFT){
            //remove item from adapter
            Log.d("SWIPE_TO_USE", "Adapter Name: " + groceries_adapter.getAdapter_name());
            groceries_adapter.useItem(viewHolder.getAbsoluteAdapterPosition());
        }

        //if you swipe to the right, you waste the item
        if (direction == ItemTouchHelper.RIGHT){
            //remove item from adapter
            Log.d("SWIPE_TO_WASTE", "Adapter Name: " + groceries_adapter.getAdapter_name());
            groceries_adapter.wasteItem(viewHolder.getAbsoluteAdapterPosition());
        }
    }
}
