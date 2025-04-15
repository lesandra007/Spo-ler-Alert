package edu.sjsu.sase.android.spoleralert;

import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
                                                EXPIRATION_STATUS + " INTEGER);";

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
        boolean in_freezer = freezer_status == 1;
        boolean is_expired = expiration_status == 1;
        LocalDate expiration_date = Instant.ofEpochMilli(expiration_milli).atZone(timezone).toLocalDate();

        //check what date expiration_milli represents using Java Date
        Log.d("CREATE_GROCERY", "Expiration Milli: " + expiration_milli + "For Grocery: " + name);
        Log.d("CREATE_GROCERY", "Expiration Date: " + expiration_date.toString() + "For Grocery: " + name);
        //create the Grocery object
        return new Grocery(name, food_group, quantity,
                pounds, ounces, price,
                in_freezer, expiration_date, is_expired);
    }

    //method to retrieve all groceries in alphabetical order as an ArrayList of Groceries
    public ArrayList<Grocery> getGroceriesAlphabetical(){
        SQLiteDatabase groceries_db = getReadableDatabase();

        //we want the groceries to be sorted alphabetically
        String order_alphabetically = GROCERY_NAME + " COLLATE NOCASE ASC";

        //we want to get all columns,
        // so we pass null as the second param (projection param)
        Cursor alphabetical_cursor = groceries_db.query(
                TABLE_NAME,
                null, //columns to return, null means return all columns
                null, //columns for the where clause, null for no where clause
                null, //values for the where clause, null for no where clause
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
        String selection = FOOD_GROUP + " = ?";
        String[] selection_args = { food_group };

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
     * Method to retrieve groceries that expire in 1 day (TBD)
     * lol i think it works now that im using LocalDate
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
                " WHERE " + EXPIRATION_DATE +
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

//    /**
//     * Method to retrieve groceries that expire in 2-7 days (TBD)
//     */
//    public Cursor getReadyToUseGroceries(){
//
//    }

//    /**
//     * Method to retrieve groceries that expire in over a week (TBD)
//     */
//    public Cursor getFreshGroceries(){
//
//    }


    //there will probably also be a method to remove an item from the groceries table





}
