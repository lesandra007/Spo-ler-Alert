package edu.sjsu.sase.android.spoleralert;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroceriesAdapter extends RecyclerView.Adapter<GroceriesAdapter.ViewHolder>{
    private ArrayList<Grocery> groceries;

    //maybe a variable for grocery list sorting type?

    /**
     * ViewHolder class which holds the data for each item in the list
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public ViewHolder(View view) {
//            super(view);
//        }

        /*
        ------ Temporary viewholder code for testing -------
        Taken from the official Android Studio tutorial
         */

        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.myTextView);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public GroceriesAdapter(ArrayList<Grocery> groceries_data){
        groceries = groceries_data;
    }

    //public GroceriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    /**
     * This method creates new views (which are the items in the displayed list?
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return a new ViewHolder object
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /* grocery_item_appearance is the name of the xml file that determines
        what each item in the list looks like.
        so maybe when switching the type of sorting, there can be a different
        xml file for each one? Things to consider... */
        View viewholder_view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grocery_item_appearance, parent, false);

        return new ViewHolder(viewholder_view);
    }

    /**
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    //public void onBindViewHolder(@NonNull GroceriesAdapter.ViewHolder holder, int position) {
    public void onBindViewHolder(ViewHolder holder, int position) {
        //this populates each view in the list with the actual data
        //get Grocery Item
        Grocery grocery_item = groceries.get(position);
        String grocery_name = grocery_item.getName();

        holder.getTextView().setText(grocery_name);

        Log.d("BIND_VIEW_HOLDER", "Entered Bind View Holder: " + grocery_name);


    }

    @Override
    public int getItemCount() {
        return groceries.size();
    }
}
