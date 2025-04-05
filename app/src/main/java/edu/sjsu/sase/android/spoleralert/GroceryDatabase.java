package edu.sjsu.sase.android.spoleralert;

import static edu.sjsu.sase.android.spoleralert.GroceryDBSchema.GroceryDBColumns.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                                                ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                NAME + "TEXT, " +
                                                FOOD_GROUP + "TEXT, " +
                                                QUANTITY + "REAL, " +
                                                POUNDS + "INTEGER, " +
                                                OUNCES + "INTEGER, " +
                                                PRICE + "REAL, " +
                                                FREEZER_STATUS + "INTEGER, " +
                                                EXPIRATION_DATE + "DATE, " +
                                                EXPIRATION_STATUS + "INTEGER);";

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



}
