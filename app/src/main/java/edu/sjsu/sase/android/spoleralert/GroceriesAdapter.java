package edu.sjsu.sase.android.spoleralert;

import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.GROCERY_NAME;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.USED_STATUS;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.WASTED_STATUS;

import android.app.Dialog;
import android.content.ContentValues;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GroceriesAdapter extends RecyclerView.Adapter<GroceriesAdapter.ViewHolder>{
    private ArrayList<Grocery> groceries;
    private GroceryDatabase groceries_db;

    private ZoneId timezone = ZoneId.systemDefault();

    public GroceriesAdapter(ArrayList<Grocery> groceries_data, GroceryDatabase groceries_db){
        groceries = groceries_data;
        this.groceries_db = groceries_db;
    }

    //maybe a variable for grocery list sorting type?

    /**
     * ViewHolder class which holds the data for each item in the list
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView grocery_name;
        private final TextView days_left;

        private final ImageButton item_options_btn;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            grocery_name = view.findViewById(R.id.grocery_list_item);
            days_left = view.findViewById(R.id.grocery_days_left);
            item_options_btn = view.findViewById(R.id.grocery_options);

            view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Log.d("ON CLICK", "Clicked Item!");
                }
            });
        }

        public TextView getGroceryNameTextView() {
            return grocery_name;
        }

        public TextView getDaysLeftTextView(){
            return days_left;
        }

        public ImageButton getItemOptionsImageBtn(){
            return item_options_btn;
        }
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

        //get the name of the grocery
        String grocery_name = grocery_item.getName();

        //string for days left
        String days_left = "";

        //get the expiration date
        LocalDate expiration_date = grocery_item.getExpirationDate();

        //convert the date to long milliseconds
        long expiration_date_milli = expiration_date.atStartOfDay(timezone).toInstant().toEpochMilli();

        //get the current date at 12:00AM in milli
        LocalDate current_date = LocalDate.now();
        long today_milli = current_date.atStartOfDay(timezone).toInstant().toEpochMilli();

        //subtract the expiration date from current date
        long difference_milli = expiration_date_milli - today_milli;

        //convert the difference in milli to days
        if (difference_milli >= 0){
            long days_left_num = TimeUnit.MILLISECONDS.toDays(difference_milli);
            days_left = days_left_num + " days left";

        }
        else{
            long days_over_num = TimeUnit.MILLISECONDS.toDays(Math.abs(difference_milli));
            days_left = days_over_num + " days overdue";
        }

        holder.getGroceryNameTextView().setText(grocery_name);
        holder.getDaysLeftTextView().setText(days_left);

        //set the onclick event for item options button
        holder.getItemOptionsImageBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemOptions(v, holder.getBindingAdapterPosition());
            }
        });


        Log.d("GROCERIES_ADAPTER_BIND_VIEW_HOLDER", "Entered Bind View Holder: " + grocery_name);


    }

    @Override
    public int getItemCount() {
        return groceries.size();
    }

    public void useItem(int position){
        //remove the item from the list
        Grocery used_grocery = groceries.remove(position);
        ContentValues vals = new ContentValues();
        vals.put(USED_STATUS, 1);
        groceries_db.editGroceries(used_grocery.getId(), vals);
        notifyItemRemoved(position);
    }

    public void wasteItem(int position){
        Grocery used_grocery = groceries.remove(position);
        ContentValues vals = new ContentValues();
        vals.put(WASTED_STATUS, 1);
        groceries_db.editGroceries(used_grocery.getId(), vals);
        notifyItemRemoved(position);
    }

    public void deleteItem(int position){
        Grocery deleted_grocery = groceries.remove(position);
        groceries_db.deleteGroceries(deleted_grocery.getId());
        notifyItemRemoved(position);
    }

    public void showItemOptions(View view, int position){
        //show pop up for item options
        Dialog item_options = new Dialog(view.getContext());
        item_options.setContentView(R.layout.grocery_item_options);
        item_options.show();

        //delete button functionality
        Button delete_button = item_options.findViewById(R.id.grocery_options_delete);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
                item_options.dismiss();
            }
        });
    }

}
