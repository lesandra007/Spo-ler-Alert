package edu.sjsu.sase.android.spoleralert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.sjsu.sase.android.spoleralert.notifications.Notification;

public class Grocery {
    private String name;
    private String food_group;
    private double quantity;
    private int pounds;
    private int ounces;
    private double price;
    private boolean in_freezer;
    private LocalDate expiration_date;
    private boolean has_expired;
    private ArrayList<Notification> notifications;

    public Grocery(String name, String food_group, double quantity,
                   int pounds, int ounces, double price,
                   boolean in_freezer, LocalDate expiration_date, boolean has_expired, ArrayList<Notification> notifications){
        this.name = name;
        this.food_group = food_group;
        this.quantity = quantity;
        this.pounds = pounds;
        this.ounces = ounces;
        this.price = price;
        this.in_freezer = in_freezer;
        this.expiration_date = expiration_date;
        this.has_expired = has_expired;
        this.notifications = notifications;
    }

    public String getName(){
        return name;
    }
    public LocalDate getExpirationDate(){
        return expiration_date;
    }

}
