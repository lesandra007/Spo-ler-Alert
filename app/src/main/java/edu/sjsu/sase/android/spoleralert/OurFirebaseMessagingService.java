package edu.sjsu.sase.android.spoleralert;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class OurFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FCM", "Token: +" + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        if (remoteMessage.getNotification() != null){
            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
            Log.d("message received", "notif exists :0");
        }
    }

    private void requestNotificationPermission(){
        // check if version Android13 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            // check if app has post notification permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                Log.d("request permission", "android 13 and has permissions");
            }
        }
    }

    private void showNotification(String title, String message){
        // check if version Android13 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            // check if app has post notification permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission();
                Log.d("show notif", "android 13 and has permissions");
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "FCM_CHANNEL")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.spoiler_alert_logo)
                .setAutoCancel(true);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
        Log.d("show notif", "build notif");
    }
}
