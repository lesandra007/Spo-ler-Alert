package edu.sjsu.sase.android.spoleralert;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link GroceriesFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class GroceriesFragment extends Fragment {

    private GroceryDatabase groceries_db;
    private RecyclerView grocery_list_rv;

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public GroceriesFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment GroceriesFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static GroceriesFragment newInstance(String param1, String param2) {
//        GroceriesFragment fragment = new GroceriesFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groceries_db = new GroceryDatabase(getContext());
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
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
        GroceriesAdapter empty_groceries_adapter = new GroceriesAdapter(new ArrayList<Grocery>());
        //initially set the adapter to the adapter created with the empty list
        grocery_list_rv.setAdapter(empty_groceries_adapter);

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
                //String sorting_type = gs_dropdown.getSelectedItem().toString();
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

        //populate the view based on what sorting type the dropdown menu is currently on
        //String sorting_type = gs_dropdown.getSelectedItem().toString();



        //grocery_list_rv.setAdapter();

        //and then everytime you change the list, you have to set the adapter with
        //the new type of sorting?

        //so there should be methods here that get called when you switch the dropdown

        //clicking on the bottom right plus button (to add groceries)
        groceries_view.findViewById(R.id.add_groceries_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.action_groceriesFragment_to_addGroceryFragment);
            }
        });

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
    //and then create a new GroceriesAdapter by passing in that alphabetical list
    //and possibly other variables if i need to
    //will the view update the moment setadapter is set?
    //probbaly use swapAdapter to change the items in the displayed list
    //gets called when dropdown menu is set to alphabetical
    public void showByAlphabetical(){
        //so there needs to be a method to get all the items in the groceries database
        ArrayList<Grocery> alphabetical_groceries = groceries_db.getGroceriesAlphabetical();
        Log.d("SHOW_BY_ALPHABETICAL", "size of groceries: " + alphabetical_groceries.size());
        //create the new adapter based on this alphabetical_groceries
        GroceriesAdapter alphabetical_groceries_adapter = new GroceriesAdapter(alphabetical_groceries);
        grocery_list_rv.swapAdapter(alphabetical_groceries_adapter, false);
        //might change to true for second param if false doesn't work?

    }

    //method to create ArrayList of groceries sorted by food group
    public void showByFoodGroup(){
        //not implemented yet, so keep it empty for now
        //so there needs to be a method to get all the items in the groceries database
        //ArrayList<Grocery> alphabetical_groceries = groceries_db.getGroceriesAlphabetical();
        //create the new adapter based on this alphabetical_groceries
        GroceriesAdapter food_group_groceries_adapter = new GroceriesAdapter(new ArrayList<Grocery>());
        grocery_list_rv.swapAdapter(food_group_groceries_adapter, false);

    }

    //method to create ArrayList of groceries sorted by expiration date
    public void showByExpirationDate(){
        //not implemented yet, so keep it empty for now
        GroceriesAdapter expiration_date_groceries_adapter = new GroceriesAdapter(new ArrayList<Grocery>());
        grocery_list_rv.swapAdapter(expiration_date_groceries_adapter, false);


    }

    //method to switch between sorting types


}