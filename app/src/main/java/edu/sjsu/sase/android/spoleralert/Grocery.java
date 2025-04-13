package edu.sjsu.sase.android.spoleralert;

import java.util.Date;

public class Grocery {
    private String name;
    private String food_group;
    private double quantity;
    private int pounds;
    private int ounces;
    private double price;
    private boolean in_freezer;
    private Date expiration_date;
    private boolean has_expired;

    public Grocery(String name, String food_group, double quantity,
                   int pounds, int ounces, double price,
                   boolean in_freezer, Date expiration_date, boolean has_expired){
        this.name = name;
        this.food_group = food_group;
        this.quantity = quantity;
        this.pounds = pounds;
        this.ounces = ounces;
        this.price = price;
        this.in_freezer = in_freezer;
        this.expiration_date = expiration_date;
        this.has_expired = has_expired;
    }

    public String getName(){
        return name;
    }

}
