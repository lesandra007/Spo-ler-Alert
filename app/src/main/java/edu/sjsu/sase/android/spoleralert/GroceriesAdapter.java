package edu.sjsu.sase.android.spoleralert;

import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.EXPIRATION_DATE;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.EXPIRATION_STATUS;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.FOOD_GROUP;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.FREEZER_STATUS;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.GROCERY_NAME;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.OUNCES;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.POUNDS;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.PRICE;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.QUANTITY;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.UPDATES_JSON;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.USED_STATUS;
import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.WASTED_STATUS;

import android.app.Dialog;
import android.content.ContentValues;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.gson.Gson;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import edu.sjsu.sase.android.spoleralert.notifications.Notification;

public class GroceriesAdapter extends RecyclerView.Adapter<GroceriesAdapter.ViewHolder>{
    private ArrayList<Grocery> groceries;
    private GroceryDatabase groceries_db;

    private String adapter_name;

    private ZoneId timezone = ZoneId.systemDefault();

    //calendar variables for editing
    private LocalDate new_expiration_date;
    private LocalDate update_date;

    public GroceriesAdapter(ArrayList<Grocery> groceries_data, GroceryDatabase groceries_db, String adapter_name){
        groceries = groceries_data;
        this.groceries_db = groceries_db;
        this.adapter_name = adapter_name;
    }

    public String getAdapter_name() {
        return adapter_name;
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

//            view.setOnClickListener(new View.OnClickListener(){
//
//                @Override
//                public void onClick(View v) {
//                    Log.d("ON CLICK", "Clicked Item at position" + getBindingAdapterPosition() + "!");
//                    showItemInfo(v, getBindingAdapterPosition());
//                }
//            });
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

        for (Grocery g : groceries){
            Log.d("INSIDE_BINDING",g.getName());
        }

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

        View divider = holder.itemView.findViewById(R.id.divider);
        if (position == getItemCount() - 1) {
            divider.setVisibility(View.GONE);
        } else {
            divider.setVisibility(View.VISIBLE);
        }

        Log.d("GROCERIES_ADAPTER_BIND_VIEW_HOLDER", "Entered Bind View Holder: " + grocery_name);

    }

    @Override
    public int getItemCount() {
        return groceries.size();
    }

    public void showItemInfo(View view, int position){
        //get Dialog
        Dialog item_info = new Dialog(view.getContext());
        item_info.setContentView(R.layout.grocery_info_popup);

        for (Grocery g : groceries){
            Log.d("SHOW_ITEM_INFO",g.getName());
        }

        //get grocery
        Grocery grocery_to_view = groceries.get(position);

        //get grocery info
        String name = grocery_to_view.getName();
        String food_group = grocery_to_view.getFoodGroup();
        double quantity = grocery_to_view.getQuantity();
        int pounds = grocery_to_view.getPounds();
        int ounces = grocery_to_view.getOunces();
        double price = grocery_to_view.getPrice();
        boolean freezer_status = grocery_to_view.getFreezerStatus();
        LocalDate expiration_date = grocery_to_view.getExpirationDate();
        ArrayList<Notification> notification_list = grocery_to_view.getNotifications();

        //get the text views
        TextView fill_in_name = item_info.findViewById(R.id.item_name_info);
        TextView fill_in_food_group = item_info.findViewById(R.id.item_food_group_info);
        TextView fill_in_quantity = item_info.findViewById(R.id.item_quantity_info);
        TextView fill_in_weight = item_info.findViewById(R.id.item_weight_info);
        TextView fill_in_price = item_info.findViewById(R.id.item_price_info);
        TextView fill_in_freezer_status = item_info.findViewById(R.id.item_freezer_status_info);
        TextView fill_in_expiration_date = item_info.findViewById(R.id.item_expiration_date_info);
        TextView fill_in_notifications = item_info.findViewById(R.id.item_notification_info);

        //for every update, collect the quantity consumed
        //and display what is left in the info section
        double quantity_difference = 0;
        for (GroceryUsageUpdate update : grocery_to_view.getUpdates()) {
            quantity_difference += update.getQuantitySubtracted();
        }
        double current_quantity = quantity - quantity_difference;


        //populate info in dialog with grocery info
        fill_in_name.setText(name);
        fill_in_food_group.setText(food_group);
        String quantity_str = current_quantity + " / " + quantity;
        fill_in_quantity.setText(quantity_str);
        String weight_str = pounds + " pounds and " + ounces + " ounces";
        fill_in_weight.setText(weight_str);
        String price_str = "$" + price;
        fill_in_price.setText(price_str);
        String freezer_str = "No";
        if (freezer_status) {
            freezer_str = "Yes";
        }
        fill_in_freezer_status.setText(freezer_str);
        fill_in_expiration_date.setText(expiration_date.toString());
        String notification_str = "";
        for (Notification n : notification_list){
            notification_str += n.toString() + "\n";
        }
        fill_in_notifications.setText(notification_str);

        //show pop up for item info
        item_info.show();

    }

//    public void useItem(int position){
//        for (Grocery g : groceries){
//            Log.d("USE_ITEM_BEFORE_USING",g.getName());
//        }
//
//        //remove the item from the list
//        Grocery used_grocery = groceries.remove(position);
//        ContentValues vals = new ContentValues();
//        vals.put(USED_STATUS, 1);
//        groceries_db.editGroceries(used_grocery.getId(), vals);
//        notifyItemRemoved(position);
//
//        for (Grocery g : groceries){
//            Log.d("USE_ITEM_AFTER_USING",g.getName());
//        }
//    }

    public void partialUseItem(View view, int position) {

        Dialog partial_use = new Dialog(view.getContext());
        partial_use.setContentView(R.layout.grocery_usage_waste_dialog);
        partial_use.show();

        //set the calendar functionality
        CalendarView expiration_cal = partial_use.findViewById(R.id.item_partial_consume_calendar);
        expiration_cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //sets the expiration date as the year/month/day at 12:00AM
                //CalendarView month is 0-11, LocalDate month is 1-12
                //so add +1 to the month param to create LocalDate
                update_date = LocalDate.of(year, month+1, dayOfMonth);
            }
        });

        //set the onclick event for save button
        Button save_button = partial_use.findViewById(R.id.used_wasted_save);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the percentage is not filled in, show a toast
                //can add more error checking later
                EditText val_to_subtract_textbox = partial_use.findViewById((R.id.val_to_subtract));
                EditText percent_to_modify_textbox = partial_use.findViewById((R.id.percent_to_consume));
                RadioButton use_radio_button = partial_use.findViewById(R.id.use_radio_button);
                RadioButton waste_radio_button = partial_use.findViewById(R.id.waste_radio_button);
                if (percent_to_modify_textbox.getText().toString().isEmpty() && val_to_subtract_textbox.getText().toString().isEmpty()){
                    Toast.makeText(partial_use.getContext(), "Please Input Value to Subtract", Toast.LENGTH_SHORT).show();
                }
                else if (!percent_to_modify_textbox.getText().toString().isEmpty() && !val_to_subtract_textbox.getText().toString().isEmpty()) {
                    Toast.makeText(partial_use.getContext(), "Cannot input both a value and a percent", Toast.LENGTH_SHORT).show();
                }
                else if (!use_radio_button.isChecked() && !waste_radio_button.isChecked()) {
                    Toast.makeText(partial_use.getContext(), "Must indicate whether amount was used or wasted", Toast.LENGTH_SHORT).show();
                }
                else{
                    //if the update_date is null, then the user didn't click the calendar,
                    //so its the current date by default
                    if (update_date == null) {
                        update_date = LocalDate.now();
                    }
                    //get the type of activity (either use or waste)
                    //bool is 0 if wasted, 1 if used
                    boolean type_of_activity = false;

                    if (use_radio_button.isChecked()){
                        type_of_activity = true;
                    }
                    if(waste_radio_button.isChecked()){
                        type_of_activity = false;
                    }

                    Grocery grocery = groceries.get(position);
                    //get original values
                    double original_quantity = grocery.getQuantity();
                    double original_price = grocery.getPrice();
                    double original_pounds = grocery.getPounds();
                    double original_ounces = grocery.getOunces();
                    double original_total_ounces = (original_pounds * 16) + original_ounces;

                    double percent = 0;

                    //if val is used, convert it into a percent of the original amount
                    if (!val_to_subtract_textbox.getText().toString().isEmpty()){
                        double val_to_subtract = Double.parseDouble(val_to_subtract_textbox.getText().toString());
                        //get original quantity
                        percent = val_to_subtract / original_quantity;
                    }
                    else{
                        double percent_val = Double.parseDouble(percent_to_modify_textbox.getText().toString());
                        percent = percent_val / 100;

                    }

                    //turn percent into decimal val for multiplication purposes
                    //double percent_decimal = percent / 100;

                    //loop through ArrayList of previous updates to sum up difference
                    ArrayList<GroceryUsageUpdate> updates = grocery.getUpdates();
                    double price_difference = 0;
                    double total_ounces_difference = 0;
                    double quantity_difference = 0;
                    for (GroceryUsageUpdate update : updates) {
                        price_difference += update.getPrice();
                        total_ounces_difference += update.getWeight();
                        quantity_difference += update.getQuantitySubtracted();
                    }

                    //get the actual unconsumed price, weight, quantity
//                    double unconsumed_price = original_price - price_difference;
//                    double unconsumed_weight = original_total_ounces - total_ounces_difference;
//                    double unconsumed_quantity = original_quantity - quantity_difference;

                    //multiply the unconsumed values by the given percent to get the newly_consumed
//                    double consumed_price = unconsumed_price * percent_decimal;
//                    double consumed_weight = unconsumed_weight * percent_decimal;
//                    double consumed_quantity = unconsumed_quantity * percent_decimal;


                    double consumed_price = original_price * percent;
                    double consumed_weight = original_total_ounces * percent;
                    double consumed_quantity = original_quantity * percent;

                    //create the new GroceryUsageUpdate
                    GroceryUsageUpdate new_update = new GroceryUsageUpdate(update_date, type_of_activity, consumed_weight, consumed_price, consumed_quantity);

                    //add the new update to the grocery's list of updates
                    updates.add(new_update);

                    //edit the database to reflect the change in the updates arraylist
                    Gson gson = new Gson();
                    String updates_json = gson.toJson(updates);

                    ContentValues vals = new ContentValues();
                    vals.put(UPDATES_JSON, updates_json);

                    //check if the current update causes the quantity to be 0 or less than 0
                    //if it does, then its been completely used and either the wasted/used var will be set to 1
                    //to indicate it shouldn't show up in the list anymore
                    //TODO: really should be combined into one variable like ALL_CONSUMED but dunno if i have time for that
                    double most_update_quantity_difference = quantity_difference + consumed_quantity;
                    if ((original_quantity - most_update_quantity_difference) <= 0){
                        grocery.setUsedStatus(true);
                        vals.put(USED_STATUS, 1);
                        groceries.remove(position);
                        notifyItemRemoved(position);

                    }
                    //officially edit the grocery in the DB
                    groceries_db.editGroceries(grocery.getId(), vals);

                    partial_use.dismiss();
                }
            }
        });
    }

//    public void wasteItem(int position){
//        for (Grocery g : groceries){
//            Log.d("WASTE_ITEM_BEFORE_WASTING",g.getName());
//        }
//
//        Grocery used_grocery = groceries.remove(position);
//        ContentValues vals = new ContentValues();
//        vals.put(WASTED_STATUS, 1);
//        groceries_db.editGroceries(used_grocery.getId(), vals);
//        notifyItemRemoved(position);
//
//        for (Grocery g : groceries){
//            Log.d("WASTE_ITEM_AFTER_WASTING",g.getName());
//        }
//    }

    public void deleteItem(int position){
        for (Grocery g : groceries){
            Log.d("DELETE_ITEM_BEFORE_DELETING",g.getName());
        }

        Grocery deleted_grocery = groceries.remove(position);
        groceries_db.deleteGroceries(deleted_grocery.getId());
        notifyItemRemoved(position);

        for (Grocery g : groceries){
            Log.d("DELETE_ITEM_AFTER_DELETING",g.getName());
        }
    }

    public void editItem(View view, int position){
        //show pop up for item options
        Dialog edit_grocery = new Dialog(view.getContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        edit_grocery.setContentView(R.layout.fragment_edit_grocery);
        edit_grocery.show();

        //set the onclick event for back button
        Button edit_back_button = edit_grocery.findViewById(R.id.edit_item_back_button);
        edit_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss the dialog without saving any changes
                edit_grocery.dismiss();
            }
        });

        //set the food group dropdown menu
        Spinner fg_dropdown_spin = (Spinner)edit_grocery.findViewById(R.id.edit_food_group_dropdown);
        ArrayAdapter<CharSequence> fg_adapter_edit = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.food_groups,
                android.R.layout.simple_spinner_item
        );
        fg_adapter_edit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fg_dropdown_spin.setAdapter(fg_adapter_edit);

        //set the calendar functionality
        CalendarView expiration_cal = edit_grocery.findViewById(R.id.edit_item_expiration_calendar);
        expiration_cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //sets the expiration date as the year/month/day at 12:00AM
                //CalendarView month is 0-11, LocalDate month is 1-12
                //so add +1 to the month param to create LocalDate
                new_expiration_date = LocalDate.of(year, month+1, dayOfMonth);
            }
        });

        //get the info already in the system for that grocery
        Grocery grocery_to_edit = groceries.get(position);
        String name = grocery_to_edit.getName();
        String food_group = grocery_to_edit.getFoodGroup();
        double quantity = grocery_to_edit.getQuantity();
        int pounds = grocery_to_edit.getPounds();
        int ounces = grocery_to_edit.getOunces();
        double price = grocery_to_edit.getPrice();
        boolean freezer_status = grocery_to_edit.getFreezerStatus();
        LocalDate expiration_date = grocery_to_edit.getExpirationDate();

        //get the objects in the edit_grocery dialog
        EditText name_et = edit_grocery.findViewById(R.id.edit_item_name_input);
        EditText quantity_et = edit_grocery.findViewById(R.id.edit_item_quantity_input);
        EditText pounds_et = edit_grocery.findViewById(R.id.edit_item_pounds_input);
        EditText ounces_et = edit_grocery.findViewById(R.id.edit_item_ounces_input);
        EditText price_et = edit_grocery.findViewById(R.id.edit_item_price_input);
        CheckBox freezer_check = edit_grocery.findViewById(R.id.edit_item_freezer_checkbox);

        //set the default text to the original info from the grocery
        name_et.setText(name);
        int position_of_fg = fg_adapter_edit.getPosition(food_group);
        fg_dropdown_spin.setSelection(position_of_fg);
        quantity_et.setText(String.valueOf(quantity));
        pounds_et.setText(String.valueOf(pounds));
        ounces_et.setText(String.valueOf(ounces));
        price_et.setText(String.valueOf(price));
        freezer_check.setChecked(freezer_status);
        expiration_cal.setDate(expiration_date.atStartOfDay(timezone).toInstant().toEpochMilli());

        //if the item has been used, lock the quantity/price/weight from being changed
        if (!grocery_to_edit.getUpdates().isEmpty()){
            quantity_et.setEnabled(false);
            pounds_et.setEnabled(false);
            ounces_et.setEnabled(false);
            price_et.setEnabled(false);
        }

        //set functionality for save edit
        Button edit_save_button = edit_grocery.findViewById(R.id.item_edit_button);
        edit_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save the changes
                //saveEdit(v, grocery_to_edit);
                //get the values at the time of saving
                String name = name_et.getText().toString();
                String food_group = fg_dropdown_spin.getSelectedItem().toString();
                double quantity = Double.parseDouble(quantity_et.getText().toString());
                int pounds = Integer.parseInt(pounds_et.getText().toString());
                int ounces = Integer.parseInt(ounces_et.getText().toString());
                double price = Double.parseDouble(price_et.getText().toString());
                boolean in_freezer = freezer_check.isChecked();
                //if the date was never changed, keep it as the old expiration date
                if (new_expiration_date == null){
                    new_expiration_date = expiration_date;
                }

                long expiration_milli = new_expiration_date.atStartOfDay(timezone).toInstant().toEpochMilli();
                long today_milli = LocalDate.now(timezone).atStartOfDay(timezone).toInstant().toEpochMilli();
                boolean is_expired = today_milli > expiration_milli;

                //move the values to a content vals object
                ContentValues vals = new ContentValues();
                vals.put(GROCERY_NAME, name);
                vals.put(FOOD_GROUP, food_group);
                vals.put(QUANTITY, quantity);
                vals.put(POUNDS, pounds);
                vals.put(OUNCES, ounces);
                vals.put(PRICE, price);
                vals.put(FREEZER_STATUS, in_freezer);
                vals.put(EXPIRATION_DATE, expiration_milli);
                vals.put(EXPIRATION_STATUS, is_expired);
                //by default, items that can be edited haven't been used/wasted yet, so set both to false
                vals.put(USED_STATUS, false);
                vals.put(WASTED_STATUS, false);

                //pass in the vals and the grocery to edit's id into the edit function so its changed in the db
                groceries_db.editGroceries(grocery_to_edit.getId(), vals);

                //change it in the arraylist too so it looks updated after editing it w/o having to refresh
                groceries.get(position).setName(name);
                groceries.get(position).setFoodGroup(food_group);
                groceries.get(position).setQuantity(quantity);
                groceries.get(position).setPounds(pounds);
                groceries.get(position).setOunces(ounces);
                groceries.get(position).setPrice(price);
                groceries.get(position).setFreezerStatus(in_freezer);
                groceries.get(position).setExpirationDate(new_expiration_date);
                groceries.get(position).setHasExpired(is_expired);
                groceries.get(position).setUsedStatus(false);
                groceries.get(position).setWastedStatus(false);
                //^ this wont really work with the sorting via alphabetical/date/food group though
                //since that would require complete re-sorting into different sublists, etc.
                //and assuming everything else works, i think its fine just having the user refresh the list
                //instead of somehow refreshing everything right here
                //and besides, its normal for a user to refresh to see their changes reflected anyways in other products

                notifyItemChanged(position);
                edit_grocery.dismiss();
            }
        });

    }

    public void showItemOptions(View view, int position){
        for (Grocery g : groceries){
            Log.d("ITEM_OPTIONS",g.getName());
        }

        //show pop up for item options
        Dialog item_options = new Dialog(view.getContext());
        item_options.setContentView(R.layout.grocery_item_options);
        item_options.show();

        //show info functionality
        Button info_button = item_options.findViewById(R.id.grocery_options_show_info);
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemInfo(v, position);
                item_options.dismiss();
            }
        });

        //use button functionality
//        Button use_button = item_options.findViewById(R.id.grocery_options_use);
//        use_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                useItem(position);
//                item_options.dismiss();
//            }
//        });

        //use button functionality
//        Button waste_button = item_options.findViewById(R.id.grocery_options_waste);
//        waste_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                wasteItem(position);
//                item_options.dismiss();
//            }
//        });

        //delete button functionality
        Button delete_button = item_options.findViewById(R.id.grocery_options_delete);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
                item_options.dismiss();
            }
        });

        //edit button functionality
        Button edit_button = item_options.findViewById(R.id.grocery_options_edit);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItem(v, position);
                item_options.dismiss();
            }
        });

        //partialUseItem
        Button partial_button = item_options.findViewById(R.id.grocery_options_subtract);
        partial_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partialUseItem(v, position);
                item_options.dismiss();
            }
        });
    }

}
