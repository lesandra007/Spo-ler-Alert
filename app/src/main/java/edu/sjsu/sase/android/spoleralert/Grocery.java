package edu.sjsu.sase.android.spoleralert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.sjsu.sase.android.spoleralert.notifications.Notification;

public class Grocery {
    private int id;
    private String name;
    private String food_group;
    private double quantity;
    private int pounds;
    private int ounces;
    private double price;
    private boolean in_freezer;
    private LocalDate expiration_date;
    private boolean has_expired;
    private boolean is_used;
    private boolean is_wasted;
    private ArrayList<Notification> notifications;

    public Grocery(int id, String name, String food_group, double quantity,
                   int pounds, int ounces, double price,
                   boolean in_freezer, LocalDate expiration_date, boolean has_expired,
                   boolean is_used, boolean is_wasted, ArrayList<Notification> notifications){
        this.id = id;
        this.name = name;
        this.food_group = food_group;
        this.quantity = quantity;
        this.pounds = pounds;
        this.ounces = ounces;
        this.price = price;
        this.in_freezer = in_freezer;
        this.expiration_date = expiration_date;
        this.has_expired = has_expired;
        this.is_used = is_used;
        this.is_wasted = is_wasted;
        this.notifications = notifications;

        //set id to -1 by default since we don't know what id it will have when its created
//        this.id = -1;
    }

    public String getName(){
        return name;
    }
    public LocalDate getExpirationDate(){
        return expiration_date;
    }

    public int getId() { return id; }
//
//    public void setId(int id_input) { id = id_input; }

}
