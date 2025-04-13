package edu.sjsu.sase.android.spoleralert;

import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    private static final long EXPIRING_SOON_UPPER_BOUND_MILLI = TimeUnit.DAYS.toMillis(1);
    private static final long READY_TO_USE_LOWER_BOUND_MILLI = TimeUnit.DAYS.toMillis(2);
    private static final long READY_TO_USE_UPPER_BOUND_MILLI = TimeUnit.DAYS.toMillis(7);
    private static final long FRESH_LOWER_BOUND_MILLI = TimeUnit.DAYS.toMillis(8);
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
     * Parameters are the attributes of a Grocery
     */
    public long insertGrocery(ContentValues vals){
        SQLiteDatabase groceries_db = getWritableDatabase();
        return groceries_db.insert(TABLE_NAME, null, vals);
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
            String name = alphabetical_cursor.getString(
                    alphabetical_cursor.getColumnIndexOrThrow(GROCERY_NAME));
            String food_group = alphabetical_cursor.getString(
                    alphabetical_cursor.getColumnIndexOrThrow(FOOD_GROUP));
            double quantity = alphabetical_cursor.getDouble(
                    alphabetical_cursor.getColumnIndexOrThrow(QUANTITY));
            int pounds = alphabetical_cursor.getInt(
                    alphabetical_cursor.getColumnIndexOrThrow(POUNDS));
            int ounces = alphabetical_cursor.getInt(
                    alphabetical_cursor.getColumnIndexOrThrow(OUNCES));
            double price = alphabetical_cursor.getDouble(
                    alphabetical_cursor.getColumnIndexOrThrow(PRICE));
            int freezer_status = alphabetical_cursor.getInt(
                    alphabetical_cursor.getColumnIndexOrThrow(FREEZER_STATUS));
            long expiration_milli = alphabetical_cursor.getLong(
                    alphabetical_cursor.getColumnIndexOrThrow(EXPIRATION_DATE));
            int expiration_status = alphabetical_cursor.getInt(
                    alphabetical_cursor.getColumnIndexOrThrow(EXPIRATION_STATUS));
            boolean in_freezer = freezer_status == 1;
            boolean is_expired = expiration_status == 1;
            Date expiration_date = new Date(expiration_milli);

            //create the Grocery object and add it to the ArrayList
            Grocery new_grocery = new Grocery(name, food_group, quantity,
                                              pounds, ounces, price,
                                              in_freezer, expiration_date, is_expired);

            alphabetical_groceries.add(new_grocery);
        }

        return alphabetical_groceries;
    }


//    /**
//     * Method to retrieve groceries that expire in 1 day (TBD)
//     */
//    public Cursor getExpiringSoonGroceries(){
//        long today_milli = Calendar.getInstance().getTimeInMillis();
//        long expiring_soon_current_threshold = today_milli + EXPIRING_SOON_UPPER_BOUND_MILLI;
//        //anything equal to or less then that threshold is considered expiring soon
//
//    }

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
