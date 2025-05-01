package edu.sjsu.sase.android.spoleralert;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import edu.sjsu.sase.android.spoleralert.notifications.NotifEnum;
import edu.sjsu.sase.android.spoleralert.notifications.Notification;
import edu.sjsu.sase.android.spoleralert.notifications.NotificationWorker;
import edu.sjsu.sase.android.spoleralert.notifications.NotificationFragment;

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
    Calendar selectedDate;
    NotificationFragment notifFragment;
    View add_groceries_view;

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
        add_groceries_view = inflater.inflate(R.layout.fragment_add_grocery, container, false);
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
                // create instance
                selectedDate = Calendar.getInstance();
                // set year, month, day of month for the selected date
                selectedDate.set(year, month, dayOfMonth);
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
                if (selectedDate != null) {
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

        // ************ Implement Notifications ****************
        notifFragment = (NotificationFragment) getChildFragmentManager().findFragmentById(R.id.notifList);
        // add notification button
        Button addNotif = add_groceries_view.findViewById(R.id.addNotifBtn);
        addNotif.setOnClickListener(this::showNotifDialog);

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
        //by default, items added to the list haven't been used/wasted yet, so set both to false
        vals.put(USED_STATUS, false);
        vals.put(WASTED_STATUS, false);

        // convert arraylist of notifications to json
        Gson gson = new Gson();
        String jsonNotifications = gson.toJson(notifFragment.getNotifications());
        if (jsonNotifications == null || jsonNotifications.isEmpty()) {
            jsonNotifications = "[]";
        }
        vals.put(NOTIFICATIONS_JSON, jsonNotifications);

        //insert grocery into groceries database
        groceries_db.insertGrocery(vals);
    }

    /**
     * Shows Dialog where user can select when they want notifications relative to the expiration date
     * @param view the view
     */
    private void showNotifDialog(View view) {
        // show dialog view
        Dialog notifDialog = new Dialog(view.getContext());
        notifDialog.setContentView(R.layout.notification_dialog);
        notifDialog.show();

        // set spinner options
        Spinner notif_spin = (Spinner) notifDialog.findViewById(R.id.notif_spinner);
        ArrayAdapter<NotifEnum> fg_adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, NotifEnum.values());
        fg_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notif_spin.setAdapter(fg_adapter);

        // save button
        Button saveBtn = notifDialog.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(v -> onClickNotifDialogSave(notifDialog, notif_spin));

        // cancel button
        Button cancelBtn = notifDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(v -> notifDialog.dismiss());
    }

    /**
     *
     * @param dialog the notification dialog
     * @param spinner the spinner for notification times
     */
    public void onClickNotifDialogSave(Dialog dialog, Spinner spinner) {
        // retrieve input values
        EditText numText = dialog.findViewById(R.id.editTextNumber);
        String numStr = numText.getText().toString();
        String notifTime = spinner.getSelectedItem().toString();

        // add notification to add groceries screen and close dialog
        if (!numStr.matches("") && !notifTime.matches("")){
            int num = Integer.parseInt(numText.getText().toString());
            addNotification(num, notifTime);
            dialog.dismiss();
        }
    }

    /**
     * Adds notification to notification fragment in add groceries screen
     * @param num the number of days/weeks/months to schedule the notification before expiration date
     * @param notifTime days/weeks/months
     */
    public void addNotification(int num, String notifTime) {
        notifFragment.addNotification(num, notifTime);
    }

    /**
     * Schedules a notification that reminds the user of the expiration date of an item
     * @param view the view
     * @param context the context
     */
    public void scheduleReminder(View view, Context context) {
        ArrayList<Notification> notifications = notifFragment.getNotifications();
        // check if date is selected
        if (selectedDate == null) {
            Log.e("schedule reminder", "selected date is null");
            return;
        }
        // return if no notifications are scheduled
        if (notifications.isEmpty()) {
            return;
        }

        OneTimeWorkRequest reminderRequest = null;
        // get name of grocery item
        EditText name_et = view.findViewById(R.id.item_name_input);
        String name = name_et.getText().toString();

        // for each notification
        for (Notification notif: notifications) {
            // TODO: check if notification is already in the database
            // set notification day and message
            Data inputData = null;
            Calendar notifDay = (Calendar) selectedDate.clone(); // Clone to avoid modifying original
            int num = notif.getNumber();
            if (notif.getNotifEnum() == NotifEnum.DAYS) {
                notifDay.add(Calendar.DATE, -num);
                if (num == 1) {
                    inputData = new Data.Builder()
                            .putString("custom_message", "Your " + name + " is expiring tomorrow!")
                            .build();
                }
                else if (num == 0) {
                    inputData = new Data.Builder()
                            .putString("custom_message", "Your " + name + " is expiring today!")
                            .build();
                }
                else{
                    inputData = new Data.Builder()
                            .putString("custom_message", "Your " + name + " is expiring in " + num + " days!")
                            .build();
                }
            }
            else if (notif.getNotifEnum() == NotifEnum.WEEKS) {
                notifDay.add(Calendar.WEEK_OF_YEAR, -num);
                if (num == 1) {
                    inputData = new Data.Builder()
                            .putString("custom_message", "Your " + name + " is expiring in 1 week!")
                            .build();
                }
                else {
                    inputData = new Data.Builder()
                            .putString("custom_message", "Your " + name + " is expiring in " + num + " weeks!")
                            .build();
                }
            }
            else if (notif.getNotifEnum() == NotifEnum.MONTHS) {
                notifDay.add(Calendar.MONTH, -num);
                if (num == 1) {
                    inputData = new Data.Builder()
                            .putString("custom_message", "Your " + name + " is expiring in 1 month!")
                            .build();
                }
                else {
                    inputData = new Data.Builder()
                            .putString("custom_message", "Your " + name + " is expiring in " + num + " months!")
                            .build();
                }
            }
            Log.d("notification", "notification date: " + notifDay);
            // calculate the delay from today in milliseconds
            Calendar today = Calendar.getInstance();
            long delayInMillis = notifDay.getTimeInMillis() - today.getTimeInMillis();
            long delayInMinutes = TimeUnit.MILLISECONDS.toMinutes(delayInMillis);

            int notifDayOfWeek = notifDay.get(Calendar.DAY_OF_WEEK);
            int todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
            // notify immediately if notification date is today
            if (notifDayOfWeek == todayDayOfWeek && delayInMinutes < 10 && delayInMinutes > -10){
                delayInMinutes = 0;
                Log.d("notification", "notify today");
            }
            // don't schedule if notification date has already passed
            else if (notifDay.compareTo(today) < 0) {
                Log.d("notification", "notify in past");
                return;
            }


            // Create a WorkManager request with the calculated delay
            reminderRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInitialDelay(delayInMinutes, TimeUnit.MINUTES) // Set the calculated delay
                    .setInputData(inputData) // Pass the data to the worker
                    .build();

            // enqueue the request
            WorkManager.getInstance(context).enqueue(reminderRequest);
        }
    }
}