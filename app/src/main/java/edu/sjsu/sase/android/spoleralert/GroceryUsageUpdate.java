package edu.sjsu.sase.android.spoleralert;

import java.time.LocalDate;
import java.util.ArrayList;

import edu.sjsu.sase.android.spoleralert.notifications.Notification;

public class GroceryUsageUpdate {
    //date when the grocery was updated (date of either wasted or used)
    private LocalDate date;
    private boolean type_of_activity; //false if wasted, true if used
    //if type_of_activity is 0, then this represents weight wasted
    //if type_of_activity is 1, then this represents weight used
    private double weight_in_ounces;
    //if type_of_activity is 0, then this represents price wasted
    //if type_of_activity is 1, then this represents price used
    private double price;
    //quantity subtracted from original
    private double quantity_subtracted;

    public GroceryUsageUpdate(LocalDate date, boolean type_of_activity, double weight_in_ounces, double price, double quantity_subtracted){
        this.date = date;
        this.type_of_activity = type_of_activity;
        this.weight_in_ounces = weight_in_ounces;
        this.price = price;
        this.quantity_subtracted = quantity_subtracted;
    }

    public LocalDate getDate(){
        return date;
    }

    public boolean getType() {
        return type_of_activity;
    }

    public double getWeight() {
        return weight_in_ounces;
    }

    public double getPrice() {
        return price;
    }
    public double getQuantitySubtracted() {
        return quantity_subtracted;
    }


}
