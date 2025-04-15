package edu.sjsu.sase.android.spoleralert;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link AddGroceryFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class AddGroceryFragment extends Fragment {

    private GroceryDatabase groceries_db;
    private ZoneId timezone = ZoneId.systemDefault();
    private LocalDate expiration_date = LocalDate.now(timezone);
    private LocalDate current_date = LocalDate.now(timezone);

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public AddGroceryFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment AddGroceryFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static AddGroceryFragment newInstance(String param1, String param2) {
//        AddGroceryFragment fragment = new AddGroceryFragment();
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
        // *********** Inflate the layout for this fragment ***************
        View add_groceries_view = inflater.inflate(R.layout.fragment_add_grocery, container, false);
        NavController controller = NavHostFragment.findNavController(this);

        // ********** get the current date at 12:00AM ***************
        LocalDate.now(timezone);

        //************* populate food groups spinner with choices ************
        Spinner fg_dropdown = (Spinner)add_groceries_view.findViewById(R.id.food_group_dropdown);
        ArrayAdapter<CharSequence> fg_adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.food_groups,
                android.R.layout.simple_spinner_item
        );
        fg_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fg_dropdown.setAdapter(fg_adapter);

        // ************ Implement CalendarView event listener ****************
        CalendarView expiration_cal = add_groceries_view.findViewById(R.id.item_expiration_calendar);
        expiration_cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //sets the expiration date as the year/month/day at 12:00AM
                //CalendarView month is 0-11, LocalDate month is 1-12
                //so add +1 to the month param to create LocalDate
                expiration_date = LocalDate.of(year, month+1, dayOfMonth);
            }
        });

        //back button functionality
        add_groceries_view.findViewById(R.id.add_item_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.action_addGroceryFragment_to_groceriesFragment);
            }
        });

        //add button functionality
        add_groceries_view.findViewById(R.id.add_item_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGrocery(add_groceries_view);
                controller.navigate(R.id.action_addGroceryFragment_to_groceriesFragment);
            }
        });

        return add_groceries_view;
    }

    /*
     * Method to take in the groceries and add them to the database
     */
    public void addGrocery(View view) {
        //get textboxes/dropdown/checkbox/calendar objects
        EditText name_et = view.findViewById(R.id.item_name_input);
        Spinner food_group_spin = view.findViewById(R.id.food_group_dropdown);
        EditText quantity_et = view.findViewById(R.id.item_quantity_input);
        EditText pounds_et = view.findViewById(R.id.item_pounds_input);
        EditText ounces_et = view.findViewById(R.id.item_ounces_input);
        EditText price_et = view.findViewById(R.id.item_price_input);
        CheckBox freezer_check = view.findViewById(R.id.item_freezer_checkbox);

        //get the values from the textboxes/dropdown/checkbox/calendar
        String name = name_et.getText().toString();
        String food_group = food_group_spin.getSelectedItem().toString();
        double quantity = Double.parseDouble(quantity_et.getText().toString());
        int pounds = Integer.parseInt(pounds_et.getText().toString());
        int ounces = Integer.parseInt(ounces_et.getText().toString());
        double price = Double.parseDouble(price_et.getText().toString());
        boolean in_freezer = freezer_check.isChecked();
        long expiration_milli = expiration_date.atStartOfDay(timezone).toInstant().toEpochMilli();
        long today_milli = current_date.atStartOfDay(timezone).toInstant().toEpochMilli();
        boolean is_expired = today_milli > expiration_milli;

        //create and populate the ContentValues object to pass into the insertGroceries() method
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

        //insert grocery into groceries database
        groceries_db.insertGrocery(vals);
    }
}