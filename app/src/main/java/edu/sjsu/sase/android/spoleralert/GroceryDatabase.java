package edu.sjsu.sase.android.spoleralert;

import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.sjsu.sase.android.spoleralert.notifications.Notification;

/**
 *
 */
public class GroceryDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "groceryDatabase";
    private static final int VERSION = 1;
    private ZoneId timezone = ZoneId.systemDefault();

    static final String CREATE_GROCERIES_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                                                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                GROCERY_NAME + " TEXT, " +
                                                FOOD_GROUP + " TEXT, " +
                                                QUANTITY + " REAL, " +
                                                POUNDS + " INTEGER, " +
                                                OUNCES + " INTEGER, " +
                                                PRICE + " REAL, " +
                                                FREEZER_STATUS + " INTEGER, " +
                                                EXPIRATION_DATE + " DATE, " +
                                                EXPIRATION_STATUS + " INTEGER, " +
                                                USED_STATUS + " INTEGER, " +
                                                WASTED_STATUS + " INTEGER, " +
                                                UPDATES_JSON + " TEXT, " +
                                                NOTIFICATIONS_JSON + " TEXT);";

    public GroceryDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase groceries_db) {
        groceries_db.execSQL(CREATE_GROCERIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase groceries_db, int oldVer, int newVer){
        groceries_db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(groceries_db);
    }

    /**
     * Parameter ContentValues vals contains the attributes of a Grocery
     */
    public long insertGrocery(ContentValues vals){
        SQLiteDatabase groceries_db = getWritableDatabase();
        return groceries_db.insert(TABLE_NAME, null, vals);
    }

    //create_grocery_from_db_row
    public Grocery createGroceryFromDbRow(Cursor cursor){
        int id = cursor.getInt(
                cursor.getColumnIndexOrThrow(ID));
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(GROCERY_NAME));
        String food_group = cursor.getString(
                cursor.getColumnIndexOrThrow(FOOD_GROUP));
        double quantity = cursor.getDouble(
                cursor.getColumnIndexOrThrow(QUANTITY));
        int pounds = cursor.getInt(
                cursor.getColumnIndexOrThrow(POUNDS));
        int ounces = cursor.getInt(
                cursor.getColumnIndexOrThrow(OUNCES));
        double price = cursor.getDouble(
                cursor.getColumnIndexOrThrow(PRICE));
        int freezer_status = cursor.getInt(
                cursor.getColumnIndexOrThrow(FREEZER_STATUS));
        long expiration_milli = cursor.getLong(
                cursor.getColumnIndexOrThrow(EXPIRATION_DATE));
        int expiration_status = cursor.getInt(
                cursor.getColumnIndexOrThrow(EXPIRATION_STATUS));
        int used_status = cursor.getInt(
                cursor.getColumnIndexOrThrow(USED_STATUS));
        int wasted_status = cursor.getInt(
                cursor.getColumnIndexOrThrow(WASTED_STATUS));
        boolean in_freezer = freezer_status == 1;
        boolean is_expired = expiration_status == 1;
        boolean is_used = used_status == 1;
        boolean is_wasted = wasted_status == 1;
        String updates_json = cursor.getString(
                cursor.getColumnIndexOrThrow(UPDATES_JSON));
        Type updates_list_type = new TypeToken<ArrayList<GroceryUsageUpdate>>(){}.getType();
        ArrayList<GroceryUsageUpdate> updates = new Gson().fromJson(updates_json, updates_list_type);
        //convert expiration date in milliseconds to LocalDate
        LocalDate expiration_date = Instant.ofEpochMilli(expiration_milli).atZone(timezone).toLocalDate();
        String jsonString = cursor.getString(
                cursor.getColumnIndexOrThrow(NOTIFICATIONS_JSON));
        // convert jsonString to arraylist of notifications
        Type listType = new TypeToken<ArrayList<Notification>>(){}.getType();
        ArrayList<Notification> notifications = new Gson().fromJson(jsonString, listType);

        //create the Grocery object
        return new Grocery(id, name, food_group, quantity,
                pounds, ounces, price,
                in_freezer, expiration_date, is_expired,
                is_used, is_wasted, updates, notifications);
    }

    //method to retrieve all groceries in alphabetical order as an ArrayList of Groceries
    public ArrayList<Grocery> getGroceriesAlphabetical(){
        SQLiteDatabase groceries_db = getReadableDatabase();

        //we want the groceries to be sorted alphabetically
        String order_alphabetically = GROCERY_NAME + " COLLATE NOCASE ASC";

        //only want items that aren't used or wasted
        String selection =
                USED_STATUS + " = ?" + " AND " +
                WASTED_STATUS + " = ?";
        String[] selection_args = { "0", "0" };

        //we want to get all columns,
        // so we pass null as the second param (projection param)
        Cursor alphabetical_cursor = groceries_db.query(
                TABLE_NAME,
                null, //columns to return, null means return all columns
                selection, //columns for the where clause, null for no where clause
                selection_args, //values for the where clause, null for no where clause
                null, //groupBy, null for no groupBy clause
                null, //having, null for no having clause
                order_alphabetically
        );

        //create the ArrayList to hold the groceries to return
        ArrayList<Grocery> alphabetical_groceries = new ArrayList<Grocery>();

        //now, iterate over all the rows in the returned Cursor
        while (alphabetical_cursor.moveToNext()){
            Grocery new_grocery = createGroceryFromDbRow(alphabetical_cursor);
            alphabetical_groceries.add(new_grocery);
        }

        return alphabetical_groceries;
    }

    //method to retrieve entries based on food group
    //pass in a param to determine which food group to get
    public ArrayList<Grocery> getGroceriesFoodGroup(String food_group){
        SQLiteDatabase groceries_db = getReadableDatabase();

        //we only want groceries that are of a specific food group
        //and that aren't used or wasted
        String selection = FOOD_GROUP + " = ?" + " AND " +
                            USED_STATUS + " = ?" + " AND " +
                            WASTED_STATUS + " = ?";
        String[] selection_args = { food_group, "0", "0" };

        //TODO: Do we want the groceries inside of each food group to be sorted? And if so, how?

        //we want to get all columns,
        // so we pass null as the second param (projection param)
        Cursor food_group_cursor = groceries_db.query(
                TABLE_NAME,
                null, //columns to return, null means return all columns
                selection, //columns for the where clause, column_name is represented by FOOD_GROUP
                selection_args, //values for the where clause, string is passed in food_group
                null, //groupBy, null for no groupBy clause
                null, //having, null for no having clause
                null //orderBy, null for no orderBy clause
        );

        //create the ArrayList to hold the groceries to return
        ArrayList<Grocery> food_group_groceries = new ArrayList<Grocery>();

        //now, iterate over all the rows in the returned Cursor
        //to create each grocery and add it to the arraylist
        while (food_group_cursor.moveToNext()){
            Grocery new_grocery = createGroceryFromDbRow(food_group_cursor);
            food_group_groceries.add(new_grocery);
        }

        return food_group_groceries;
    }


    /**
     * Method to retrieve groceries based on upper and lower time thresholds
     */
    public ArrayList<Grocery> getGroceriesExpirationDate(long lower_thresh, long upper_thresh){
        SQLiteDatabase groceries_db = getReadableDatabase();

        //check what date expiration_milli represents using Java Date
        Date upper_date = new Date(upper_thresh);
        Log.d("GET_EXPIRY", "Upper Thresh: " + upper_thresh);
        Date lower_date = new Date(lower_thresh);
        Log.d("GET_EXPIRY", "Lower Thresh: " + lower_thresh);

        Cursor expiration_date_cursor = groceries_db.rawQuery(
                "SELECT * " +
                " FROM " + TABLE_NAME +
                " WHERE " + USED_STATUS + " = 0" +
                " AND " + WASTED_STATUS + " = 0" +
                " AND " + EXPIRATION_DATE +
                " BETWEEN " + lower_thresh +
                " AND " + upper_thresh, null);

        //create the ArrayList to hold the groceries to return
        ArrayList<Grocery> expiration_date_groceries = new ArrayList<Grocery>();

        //now, iterate over all the rows in the returned Cursor
        //to create each grocery and add it to the arraylist
        while (expiration_date_cursor.moveToNext()){
            Grocery new_grocery = createGroceryFromDbRow(expiration_date_cursor);
            expiration_date_groceries.add(new_grocery);
        }

        return expiration_date_groceries;
    }

    //there will probably also be a method to remove an item from the groceries table
    public long deleteGroceries(int grocery_id){
        SQLiteDatabase groceries_db = getWritableDatabase();
        return groceries_db.delete(TABLE_NAME, "ID = ?", new String[]{""+grocery_id});
    }

    //and a method to edit groceries
    public long editGroceries(int grocery_id, ContentValues vals){
        SQLiteDatabase groceries_db = getWritableDatabase();
        return groceries_db.update(TABLE_NAME, vals, "ID = ?", new String[]{""+grocery_id});

    }

    public List<MonthlyStat> getMoneySpentData() {
        Map<YearMonth, Float> monthTotals = new HashMap<>();
        for (Grocery g : getAllGroceries()) {
            YearMonth ym = YearMonth.from(g.getExpirationDate());
            monthTotals.put(ym, monthTotals.getOrDefault(ym, 0f) + (float) g.getPrice());
        }
        return toLast6MonthsList(monthTotals);
    }

    public List<MonthlyStat> getMoneyWasteData() {
        return getMonthlyUpdateTotals(false, true);
    }

    public List<MonthlyStat> getMoneyUsedData() {
        Map<YearMonth, Float> spent = mapFromList(getMoneySpentData());
        Map<YearMonth, Float> wasted = mapFromList(getMoneyWasteData());

        Map<YearMonth, Float> used = new HashMap<>();
        for (YearMonth month : spent.keySet()) {
            float spentVal = spent.getOrDefault(month, 0f);
            float wasteVal = wasted.getOrDefault(month, 0f);
            used.put(month, Math.max(spentVal - wasteVal, 0f));
        }
        return toLast6MonthsList(used);
    }

    public List<MonthlyStat> getFoodBoughtData() {
        Map<YearMonth, Float> monthTotals = new HashMap<>();
        for (Grocery g : getAllGroceries()) {
            YearMonth ym = YearMonth.from(g.getExpirationDate());
            float weightLbs = (g.getPounds() * 16 + g.getOunces()) / 16f;
            monthTotals.put(ym, monthTotals.getOrDefault(ym, 0f) + weightLbs);
        }
        return toLast6MonthsList(monthTotals);
    }

    public List<MonthlyStat> getFoodWasteData() {
        return getMonthlyUpdateTotals(false, false);
    }

    public List<MonthlyStat> getFoodUsedData() {
        Map<YearMonth, Float> bought = mapFromList(getFoodBoughtData());
        Map<YearMonth, Float> wasted = mapFromList(getFoodWasteData());

        Map<YearMonth, Float> used = new HashMap<>();
        for (YearMonth month : bought.keySet()) {
            float boughtVal = bought.getOrDefault(month, 0f);
            float wasteVal = wasted.getOrDefault(month, 0f);
            used.put(month, Math.max(boughtVal - wasteVal, 0f));
        }
        return toLast6MonthsList(used);
    }

    public List<MonthlyStat> getMonthlyUpdateTotals(boolean isUse, boolean isMoney) {
        Log.d("DEBUG_STAT_CALL", "getMonthlyUpdateTotals() called with isUse=" + isUse + ", isMoney=" + isMoney);

        Map<YearMonth, Float> monthTotals = new HashMap<>();
        for (Grocery g : getAllGroceries()) {
            ArrayList<GroceryUsageUpdate> updates = g.getUpdates();
            if (updates == null) continue;
            Log.d("DEBUG_STAT_CALL", "Checking grocery: " + g.getName());

            for (GroceryUsageUpdate update : updates) {
                Log.d("DEBUG_STAT_CALL", "  Update found - type: " + update.getType() + ", date: " + update.getUpdateDate());
                if (update.getType() == isUse) {
                    try {
                        Log.d("DEBUG_STAT_CALL", "  ✅ Matched isUse=" + isUse + " for update on " + update.getUpdateDate());

                        LocalDate date = LocalDate.parse(update.getUpdateDate());
                        YearMonth ym = YearMonth.from(date);
                        float value = isMoney ? (float) update.getPrice() : (float) (update.getWeight() / 16f); // Convert oz to lbs
                        monthTotals.put(ym, monthTotals.getOrDefault(ym, 0f) + value);
                    } catch (Exception e) {
                        Log.e("GroceryDB", "Error parsing update date: " + update.getUpdateDate(), e);
                    }
                }
            }

        }
        return toLast6MonthsList(monthTotals);
    }

    private Map<YearMonth, Float> mapFromList(List<MonthlyStat> stats) {
        Map<YearMonth, Float> result = new HashMap<>();
        for (MonthlyStat stat : stats) {
            result.put(stat.month, stat.value);
        }
        return result;
    }

    public List<MonthlyStat> toLast6MonthsList(Map<YearMonth, Float> dataMap) {
        YearMonth now = YearMonth.now();
        List<MonthlyStat> stats = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth target = now.minusMonths(i);
            stats.add(new MonthlyStat(target, dataMap.getOrDefault(target, 0f)));
        }
        return stats;
    }

    public List<Grocery> getAllGroceries() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        List<Grocery> groceries = new ArrayList<>();
        while (cursor.moveToNext()) {
            groceries.add(createGroceryFromDbRow(cursor));
        }
        cursor.close();
        return groceries;
    }

    public void logAllGroceries() {
        List<Grocery> groceries = getAllGroceries();
        Log.d("DB_DUMP", "------ GROCERIES IN DATABASE ------");
        for (Grocery g : groceries) {
            Log.d("DB_DUMP", "Grocery: " + g.getName());
            Log.d("DB_DUMP", "  ID: " + g.getId());
            Log.d("DB_DUMP", "  Group: " + g.getFoodGroup());
            Log.d("DB_DUMP", "  Quantity: " + g.getQuantity());
            Log.d("DB_DUMP", "  Weight: " + g.getPounds() + " lbs " + g.getOunces() + " oz");
            Log.d("DB_DUMP", "  Price: $" + g.getPrice());
            Log.d("DB_DUMP", "  Freezer: " + g.getFreezerStatus());
            Log.d("DB_DUMP", "  Expiration Date: " + g.getExpirationDate());
            Log.d("DB_DUMP", "  Expired: " + g.getHasExpired());
            Log.d("DB_DUMP", "  Used: " + g.getUsedStatus());
            Log.d("DB_DUMP", "  Wasted: " + g.getWastedStatus());

            ArrayList<GroceryUsageUpdate> updates = g.getUpdates();
            if (updates == null || updates.isEmpty()) {
                Log.d("DB_DUMP", "  No updates.");
            } else {
                for (GroceryUsageUpdate u : updates) {
                    Log.d("DB_DUMP", "  Update:");
                    Log.d("DB_DUMP", "    Type: " + (u.getType() ? "Used" : "Wasted"));
                    Log.d("DB_DUMP", "    Price: $" + u.getPrice());
                    Log.d("DB_DUMP", "    Weight: " + u.getWeight() + " oz");
                    Log.d("DB_DUMP", "    Quantity Subtracted: " + u.getQuantitySubtracted());
                }
            }
        }
        Log.d("DB_DUMP", "-----------------------------------");
    }

    public List<Grocery> getRecentPurchases(int limit) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ID + " DESC", // Assuming ID is auto-increment
                String.valueOf(limit)
        );

        List<Grocery> recent = new ArrayList<>();
        while (cursor.moveToNext()) {
            recent.add(createGroceryFromDbRow(cursor));
        }
        cursor.close();
        return recent;
    }

    public List<GroceryUsageEntry> getRecentUpdates(boolean isUse, int limit) {
        List<GroceryUsageEntry> all = new ArrayList<>();
        for (Grocery g : getAllGroceries()) {
            if (g.getUpdates() == null) continue;
            for (GroceryUsageUpdate u : g.getUpdates()) {
                if (u.getType() == isUse) {
                    all.add(new GroceryUsageEntry(g.getName(), u));
                }
            }
        }
        // Sort by update date DESC
        all.sort((a, b) -> b.update.getUpdateDate().compareTo(a.update.getUpdateDate()));
        return all.subList(0, Math.min(limit, all.size()));
    }

    // Helper inner class
    public static class GroceryUsageEntry {
        public final String name;
        public final GroceryUsageUpdate update;

        public GroceryUsageEntry(String name, GroceryUsageUpdate update) {
            this.name = name;
            this.update = update;
        }
    }

}
