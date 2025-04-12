package edu.sjsu.sase.android.spoleralert;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroceriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroceriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroceriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroceriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroceriesFragment newInstance(String param1, String param2) {
        GroceriesFragment fragment = new GroceriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groceries_view = inflater.inflate(R.layout.fragment_groceries, container, false);
        NavController controller = NavHostFragment.findNavController(this);

        //populate grocery sorting (gs) dropdown with choices
        Spinner gs_dropdown = (Spinner)groceries_view.findViewById(R.id.grocery_list_sorting_dropdown);
        ArrayAdapter<CharSequence> gs_adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.grocery_sorting,
                android.R.layout.simple_spinner_item
        );
        gs_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gs_dropdown.setAdapter(gs_adapter);

        //create RecyclerView for displaying list
        RecyclerView grocery_list = groceries_view.findViewById(R.id.grocery_list_area);

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
}