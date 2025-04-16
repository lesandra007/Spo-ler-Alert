package edu.sjsu.sase.android.spoleralert.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import edu.sjsu.sase.android.spoleralert.R;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Retrieve the custom string from input data
        String customMessage = getInputData().getString("custom_message");

        // Logic to show the notification
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android 8.0+
        NotificationChannel channel = new NotificationChannel("reminder_channel", "Reminders", NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "reminder_channel")
                .setContentTitle("SPO!LER ALERT")
                .setContentText(customMessage != null ? customMessage : "Use your food before they expire!")
                .setSmallIcon(R.drawable.spoiler_alert_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(101, builder.build());
        return Result.success();
    }
}
