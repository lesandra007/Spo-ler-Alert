package edu.sjsu.sase.android.spoleralert.notifications;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Notification that = (Notification) obj;
        return number == that.number && time.equals(that.time);
    }

    public OneTimeWorkRequest scheduleItemBefore(String name, Calendar selectedDate) {
        // set notification day and message
        Data inputData = null;
        Calendar notifDay = (Calendar) selectedDate.clone(); // Clone to avoid modifying original
        int num = getNumber();
        if (getNotifEnum() == NotifEnum.DAYS) {
            notifDay.add(Calendar.DATE, -num);
            if (num == 1) {
                inputData = new Data.Builder()
                        .putString("custom_message", "Your " + name + " is expiring tomorrow!")
                        .build();
            }
            else if (num == 0) {
                inputData = new Data.Builder()
                        .putString("custom_message", "Your " + name + " is expiring today!")
                        .build();
            }
            else{
                inputData = new Data.Builder()
                        .putString("custom_message", "Your " + name + " is expiring in " + num + " days!")
                        .build();
            }
        }
        else if (getNotifEnum() == NotifEnum.WEEKS) {
            notifDay.add(Calendar.WEEK_OF_YEAR, -num);
            if (num == 1) {
                inputData = new Data.Builder()
                        .putString("custom_message", "Your " + name + " is expiring in 1 week!")
                        .build();
            }
            else {
                inputData = new Data.Builder()
                        .putString("custom_message", "Your " + name + " is expiring in " + num + " weeks!")
                        .build();
            }
        }
        else if (getNotifEnum() == NotifEnum.MONTHS) {
            notifDay.add(Calendar.MONTH, -num);
            if (num == 1) {
                inputData = new Data.Builder()
                        .putString("custom_message", "Your " + name + " is expiring in 1 month!")
                        .build();
            }
            else {
                inputData = new Data.Builder()
                        .putString("custom_message", "Your " + name + " is expiring in " + num + " months!")
                        .build();
            }
        }
        Log.d("notification", "notification date: " + notifDay);
        // calculate the delay from today in milliseconds
        Calendar today = Calendar.getInstance();
        long delayInMillis = notifDay.getTimeInMillis() - today.getTimeInMillis();
        long delayInMinutes = TimeUnit.MILLISECONDS.toMinutes(delayInMillis);

        int notifDayOfWeek = notifDay.get(Calendar.DAY_OF_WEEK);
        int todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        // notify immediately if notification date is today
        if (notifDayOfWeek == todayDayOfWeek && delayInMinutes < 10 && delayInMinutes > -10){
            delayInMinutes = 0;
            Log.d("notification", "notify today");
        }
        // don't schedule if notification date has already passed
        else if (notifDay.compareTo(today) < 0) {
            Log.d("notification", "notify in past");
            return null;
        }


        // Create a WorkManager request with the calculated delay
        OneTimeWorkRequest reminderRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delayInMinutes, TimeUnit.MINUTES) // Set the calculated delay
                .setInputData(inputData) // Pass the data to the worker
                .build();
        return reminderRequest;
    }
}
