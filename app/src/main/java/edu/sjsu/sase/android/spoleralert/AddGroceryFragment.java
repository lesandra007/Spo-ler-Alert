package edu.sjsu.sase.android.spoleralert;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import edu.sjsu.sase.android.spoleralert.notifications.NotificationWorker;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link AddGroceryFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class AddGroceryFragment extends Fragment {

    private GroceryDatabase groceries_db;
    Calendar selectedDate;

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
        // Inflate the layout for this fragment
        View add_groceries_view = inflater.inflate(R.layout.fragment_add_grocery, container, false);
        NavController controller = NavHostFragment.findNavController(this);

        //populate food groups spinner with choices
        Spinner fg_dropdown = (Spinner)add_groceries_view.findViewById(R.id.food_group_dropdown);
        ArrayAdapter<CharSequence> fg_adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.food_groups,
                android.R.layout.simple_spinner_item
        );
        fg_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fg_dropdown.setAdapter(fg_adapter);

        //back button functionality
        add_groceries_view.findViewById(R.id.add_item_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.action_addGroceryFragment_to_groceriesFragment);
            }
        });

        CalendarView calendarView = add_groceries_view.findViewById(R.id.item_expiration_calendar);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // create instance
            selectedDate = Calendar.getInstance();
            // set year, month, day of month for the selected date
            selectedDate.set(year, month, dayOfMonth);
        });

        //add button functionality
        //add_groceries_view.findViewById(R.id.add_item_add_button).setOnClickListener(this::addGrocery);
        add_groceries_view.findViewById(R.id.add_item_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDate != null) {
                    //Log.d("ADD_GROCERY_BUTTON", "User tapped the add button");
                    addGrocery(add_groceries_view);
                    // schedule reminder
                    scheduleReminder(add_groceries_view, getContext());
                    // navigate
                    controller.navigate(R.id.action_addGroceryFragment_to_groceriesFragment);
                }
                else{
                    Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
                }
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
        CalendarView expiration_cal = view.findViewById(R.id.item_expiration_calendar);

        //get the values from the textboxes/dropdown/checkbox/calendar
        String name = name_et.getText().toString();
        String food_group = food_group_spin.getSelectedItem().toString();
        double quantity = Double.parseDouble(quantity_et.getText().toString());
        int pounds = Integer.parseInt(pounds_et.getText().toString());
        int ounces = Integer.parseInt(ounces_et.getText().toString());
        double price = Double.parseDouble(price_et.getText().toString());
        boolean in_freezer = freezer_check.isChecked();
        long expiration_milli = expiration_cal.getDate();
        long today_milli = Calendar.getInstance().getTimeInMillis();
        boolean is_expired = today_milli > expiration_milli;
        //not sure if the milliseconds would be off due to timezones
        //i think expiration_milli is based on device's timezone
        //and today_milli is based on UTC timezone

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

        Log.d("ADD_GROCERY_BUTTON", "Groceries have been inserted");


    }

    public void scheduleReminder(View view, Context context) {
        if (selectedDate == null) {
            Log.e("schedule reminder", "selected date is null");
            return;
        }
        OneTimeWorkRequest reminderRequest = null;
        // Create the input data to pass to the worker
        Data inputData = new Data.Builder()
                .putString("custom_message", "custom message to pass here")
                .build();

        if (isToday(selectedDate)){
            reminderRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInitialDelay(0, TimeUnit.SECONDS) // Delay for 1 second
                    .setInputData(inputData) // Pass the data to the worker
                    .build();
        }
        else {
            Calendar today = Calendar.getInstance();
            // Calculate the delay in milliseconds
            long delayInMillis = selectedDate.getTimeInMillis() - today.getTimeInMillis();

            if (delayInMillis > 0) {
                // Convert the delay to minutes
                long delayInMinutes = TimeUnit.MILLISECONDS.toMinutes(delayInMillis);

                // Create a WorkManager request with the calculated delay
                reminderRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                        .setInitialDelay(delayInMinutes, TimeUnit.MINUTES) // Set the calculated delay
                        .build();

                WorkManager.getInstance(context).enqueue(reminderRequest);
            } else {
                // Handle past dates
                Toast.makeText(context, "Selected date is in the past. Please choose a future date.", Toast.LENGTH_SHORT).show();
            }

        }
        assert reminderRequest != null;
        WorkManager.getInstance(context).enqueue(reminderRequest);
    }

    public boolean isToday(Calendar date) {
        Calendar today = Calendar.getInstance();

        return date.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                date.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }
}