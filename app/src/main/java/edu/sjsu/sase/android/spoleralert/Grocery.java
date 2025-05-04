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

    }

    public int getId() { return id; }

    public String getName(){
        return name;
    }

    public void setName(String new_name){
        name = new_name;
    }

    public String getFoodGroup() {
        return food_group;
    }

    public void setFoodGroup(String new_fg){
        food_group = new_fg;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double new_quantity){
        quantity = new_quantity;
    }

    public int getPounds() {
        return pounds;
    }

    public void setPounds(int new_pounds){
        pounds = new_pounds;
    }

    public int getOunces() {
        return ounces;
    }

    public void setOunces(int new_ounces){
        ounces = new_ounces;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double new_price){
        price = new_price;
    }

    public boolean getFreezerStatus(){
        return in_freezer;
    }

    public void setFreezerStatus(boolean new_f_stat){
        in_freezer = new_f_stat;
    }

    public LocalDate getExpirationDate(){
        return expiration_date;
    }

    public void setExpirationDate(LocalDate new_date){
        expiration_date = new_date;
    }

    public boolean getHasExpired(){
        return has_expired;
    }

    public void setHasExpired(boolean new_e_status){
        has_expired = new_e_status;
    }

    public boolean getUsedStatus() {
        return is_used;
    }

    public void setUsedStatus(boolean new_u_stat){
        is_used = new_u_stat;
    }

    public boolean getWastedStatus(){
        return is_wasted;
    }

    public void setWastedStatus(boolean new_w_stat){
        is_wasted = new_w_stat;
    }

    public ArrayList<Notification> getNotifications(){
        return notifications;
    }

}
