package edu.sjsu.sase.android.spoleralert.notifications;

import androidx.annotation.NonNull;

public class Notification {

    private int number;
    private NotifEnum notifEnum;
    private String time;

    // used to populate attributes when convert from notifications json in db
    public Notification(){}

    public Notification(int number, String time){
        this.number = number;
        this.notifEnum = NotifEnum.fromString(time);
        this.time = time;
    }

    public int getNumber() {
        return number;
    }

    public String getTime() {
        return time;
    }

    public NotifEnum getNotifEnum() {
        return notifEnum;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setNotifEnum(NotifEnum notifEnum) {
        this.notifEnum = notifEnum;
    }

    @NonNull
    @Override
    public String toString() {
        return getNumber() + " " + getTime();
    }
}
