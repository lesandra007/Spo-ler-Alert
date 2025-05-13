package edu.sjsu.sase.android.spoleralert;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link GroceriesFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class GroceriesFragment extends Fragment {

    private GroceryDatabase groceries_db;
    private RecyclerView grocery_list_rv;

    private ZoneId timezone = ZoneId.systemDefault();

    private static final long EXPIRED_UPPER_BOUND_MILLI = TimeUnit.DAYS.toMillis(1);
    private static final long EXPIRING_SOON_UPPER_BOUND_MILLI = TimeUnit.DAYS.toMillis(1);
    private static final long READY_TO_USE_LOWER_BOUND_MILLI = TimeUnit.DAYS.toMillis(2);
    private static final long READY_TO_USE_UPPER_BOUND_MILLI = TimeUnit.DAYS.toMillis(7);
    private static final long FRESH_LOWER_BOUND_MILLI = TimeUnit.DAYS.toMillis(8);


    public GroceriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groceries_db = new GroceryDatabase(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // ********** Inflate the layout for this fragment ************
        View groceries_view = inflater.inflate(R.layout.fragment_groceries, container, false);
        NavController controller = NavHostFragment.findNavController(this);

        //create RecyclerView (rv) for displaying list
        grocery_list_rv = groceries_view.findViewById(R.id.grocery_list_area);
        grocery_list_rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        //create an empty adapter
        GroceriesSublistAdapter empty_groceries_sublist_adapter = new GroceriesSublistAdapter(new ArrayList<Pair<String, ArrayList<Grocery>>>(), groceries_db);
        //initially set the adapter to the adapter created with the empty list
        grocery_list_rv.setAdapter(empty_groceries_sublist_adapter);

        //*********** Implement grocery sorting dropdown menu **************
        //populate grocery sorting (gs) dropdown with choices
        Spinner gs_dropdown = (Spinner)groceries_view.findViewById(R.id.grocery_list_sorting_dropdown);
        //set the adapter for the grocery sorting dropdown
        ArrayAdapter<CharSequence> gs_adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.grocery_sorting,
                android.R.layout.simple_spinner_item
        );
        gs_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gs_dropdown.setAdapter(gs_adapter);
        //set the listener for the grocery sorting dropdown
        gs_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sorting_type = parent.getItemAtPosition(position).toString();
                //call a different method to swap the adapter based on sorting_type
                if (sorting_type.equals("Alphabetical")){
                    showByAlphabetical();
                }
                else if (sorting_type.equals("Expiration Date")){
                    showByExpirationDate();
                }
                else if (sorting_type.equals("Food Group")){
                    showByFoodGroup();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //*********** ADD GROCERIES BUTTON NAVIGATION **************
        //clicking on the bottom right plus button (to add groceries)
        groceries_view.findViewById(R.id.add_groceries_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.action_groceriesFragment_to_addGroceryFragment);
            }
        });

        //*************** BOTTOM BAR BUTTON NAVIGATION ***************
        //clicking on the buttons in the bottom bar to go to the different main parts of the app
        groceries_view.findViewById(R.id.grocery_list_bottom_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.groceriesFragment);
            }
        });

        groceries_view.findViewById(R.id.recipes_bottom_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.recipesFragment);
            }
        });

        groceries_view.findViewById(R.id.statistics_bottom_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.statisticsFragment);
            }
        });

        return groceries_view;
    }

    //method to create ArrayList of groceries sorted by alphabetical order
    public void showByAlphabetical(){
        //so there needs to be a method to get all the items in the groceries database
        ArrayList<Grocery> alphabetical_groceries = groceries_db.getGroceriesAlphabetical();
        Log.d("SHOW_BY_ALPHABETICAL", "size of groceries: " + alphabetical_groceries.size());

        //for every grocery, print info about its updates
        for(Grocery g : alphabetical_groceries){
            Log.d("UPDATES_INFO", "For grocery: " + g.getName());
            for(GroceryUsageUpdate u : g.getUpdates()){
                Log.d("UPDATES_INFO", "weight: " + u.getWeight() + " price: " + u.getPrice() + " quantity: " + u.getQuantitySubtracted() + " date: " + u.getUpdateDate().toString());
            }
        }

        //create the sublist for alphabetical, where the label is an empty string
        Pair<String, ArrayList<Grocery>> alphabetical_pair = new Pair<>("ALPHABETICAL", alphabetical_groceries);
        ArrayList<Pair<String, ArrayList<Grocery>>> alphabetical_sublist = new ArrayList<Pair<String, ArrayList<Grocery>>>();
        alphabetical_sublist.add(alphabetical_pair);

        //create the new adapter based on this alphabetical_sublist
        GroceriesSublistAdapter alphabetical_groceries_adapter = new GroceriesSublistAdapter(alphabetical_sublist, groceries_db);
        grocery_list_rv.swapAdapter(alphabetical_groceries_adapter, false);
        //might change to true for second param if false doesn't work?

    }

    //method to create ArrayList of groceries sorted by food group
    public void showByFoodGroup(){
        //ArrayList to hold the Pairs for each food group
        ArrayList<Pair<String, ArrayList<Grocery>>> food_group_sublist = new ArrayList<Pair<String, ArrayList<Grocery>>>();

        //list of strings for us to loop through
        String[] food_group_names = {"Fruits", "Vegetables", "Grains", "Protein", "Dairy"};

        //loop over all food groups to call the database method
        //that returns the arraylist of groceries for each sublist
        for (String fg_name : food_group_names){
            //get the groceries from the database based on the food group name
            ArrayList<Grocery> fg_groceries = groceries_db.getGroceriesFoodGroup(fg_name);
            Pair<String, ArrayList<Grocery>> fg_pair = new Pair<>(fg_name, fg_groceries);
            food_group_sublist.add(fg_pair);
            Log.d("SHOW_BY_FOOD_GROUP", "size of fg groceries: " + fg_groceries.size());
        }

        Log.d("SHOW_BY_FOOD_GROUP", "size of sublist: " + food_group_sublist.size());

        //create the new adapter based on food_group_sublist
        GroceriesSublistAdapter food_group_groceries_adapter = new GroceriesSublistAdapter(food_group_sublist, groceries_db);
        grocery_list_rv.swapAdapter(food_group_groceries_adapter, false);
        //grocery_list_rv.setAdapter(food_group_groceries_adapter);

    }

    //method to create ArrayList of groceries sorted by expiration date
    public void showByExpirationDate(){
        //TODO: Should we show expired items in the grocery list? The ex. only has unexpired items...
        ArrayList<Pair<String, ArrayList<Grocery>>> expiration_date_sublist = new ArrayList<Pair<String, ArrayList<Grocery>>>();

        //set the current_date to the current date at 12:00AM
        LocalDate current_date = LocalDate.now();
        //convert today's date at 12:00AM to milliseconds
        long today_milli = current_date.atStartOfDay(timezone).toInstant().toEpochMilli();
        Log.d("GET_EXPIRY", "Current Milli: " + current_date.atStartOfDay(timezone).toInstant().toEpochMilli());


        //create the expired threshold
        long expired_upper_threshold = today_milli - EXPIRED_UPPER_BOUND_MILLI;
        long expired_lower_threshold = 0;

        //create the expiring soon thresholds
        long expiring_soon_upper_threshold = today_milli + EXPIRING_SOON_UPPER_BOUND_MILLI;
        long expiring_soon_lower_threshold = today_milli;

        //create the ready to use thresholds
        long ready_upper_threshold = today_milli + READY_TO_USE_UPPER_BOUND_MILLI;
        long ready_lower_threshold = today_milli + READY_TO_USE_LOWER_BOUND_MILLI;

        //create the fresh thresholds
        long fresh_lower_threshold = today_milli + FRESH_LOWER_BOUND_MILLI;
        long fresh_upper_threshold = Long.MAX_VALUE;

        //create the arraylists for each type of grocery
        ArrayList<Grocery> expired_groceries = groceries_db.getGroceriesExpirationDate(expired_lower_threshold, expired_upper_threshold);
        ArrayList<Grocery> expiring_soon_groceries = groceries_db.getGroceriesExpirationDate(expiring_soon_lower_threshold, expiring_soon_upper_threshold);
        ArrayList<Grocery> ready_groceries = groceries_db.getGroceriesExpirationDate(ready_lower_threshold, ready_upper_threshold);
        ArrayList<Grocery> fresh_groceries = groceries_db.getGroceriesExpirationDate(fresh_lower_threshold, fresh_upper_threshold);

        expiration_date_sublist.add(new Pair<>("EXPIRED", expired_groceries));
        expiration_date_sublist.add(new Pair<>("Expiring Soon", expiring_soon_groceries));
        expiration_date_sublist.add(new Pair<>("Ready", ready_groceries));
        expiration_date_sublist.add(new Pair<>("Fresh", fresh_groceries));

        GroceriesSublistAdapter expiration_date_groceries_adapter = new GroceriesSublistAdapter(expiration_date_sublist, groceries_db);
        grocery_list_rv.swapAdapter(expiration_date_groceries_adapter, false);
        //grocery_list_rv.setAdapter(expiration_date_groceries_adapter);

    }

}