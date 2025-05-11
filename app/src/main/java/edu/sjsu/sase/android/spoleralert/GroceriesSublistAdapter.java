package edu.sjsu.sase.android.spoleralert;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class GroceriesSublistAdapter extends RecyclerView.Adapter<GroceriesSublistAdapter.ViewHolder>{
    //list of pairs to hold the labels (keys) and arraylists containing groceries (values)
    //its a list just for the purposes of having an index to loop over for binding
    /*
    Note: The label should be empty when the list is sorted by alphabetical,
    The labels would be the food groups when the list is sorted by food groups,
    The labels would be the expiration labels (expiring soon, ready to use, fresh) when sorted
    by expiry date
     */
    ArrayList<Pair<String, ArrayList<Grocery>>> sublist;
    static GroceryDatabase groceries_db;

    public GroceriesSublistAdapter(ArrayList<Pair<String, ArrayList<Grocery>>> sublist_data, GroceryDatabase groceries_db){
        sublist = sublist_data;
        this.groceries_db = groceries_db;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView sublist_label;
        private final RecyclerView sublist_rv;

        public ViewHolder(View sublist_view) {
            super(sublist_view);
            // Define click listener for the ViewHolder's View
            sublist_label = (TextView) sublist_view.findViewById(R.id.sublist_label);
            sublist_rv = sublist_view.findViewById(R.id.sublist_rv);

            //set the sublist_rv's adapter to an empty one
            GroceriesAdapter empty_groceries_adapter = new GroceriesAdapter(new ArrayList<Grocery>(), groceries_db, "empty");
            //initially set the adapter to the adapter created with the empty list
            sublist_rv.setAdapter(empty_groceries_adapter);
        }

        public TextView getSublistLabel() {
            return sublist_label;
        }
        public RecyclerView getSublist() { return sublist_rv; }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewholder_view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grocery_sublist_appearance, parent, false);

        return new ViewHolder(viewholder_view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //pair containing the label and arraylist
        Pair<String, ArrayList<Grocery>> sublist_pair = sublist.get(position);
        String label = sublist_pair.first;
        ArrayList<Grocery> groceries = sublist_pair.second;

        //set the label of the sublist ex. "", "Fruits", "Expiring Soon", etc.
        holder.getSublistLabel().setText(label);

        //set up the inner recyclerview
        GroceriesAdapter groceries_adapter = new GroceriesAdapter(groceries, groceries_db, label);
        //attach the item touch helper
        ItemTouchHelper groceries_ith = new ItemTouchHelper(new GroceriesItemTouchHelper(groceries_adapter));
        groceries_ith.attachToRecyclerView(holder.getSublist());
        Log.d("SUBLIST_BINDING", "Adapter Name during sublist binding: " + label);
        holder.getSublist().swapAdapter(groceries_adapter, false);
    }

    @Override
    public int getItemCount() {
        return sublist.size();
    }
}
