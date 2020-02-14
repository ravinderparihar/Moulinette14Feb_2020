package com.moulinette.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moulinette.R;
import com.moulinette.activities.Login;
import com.moulinette.utilities.MainApplication;

import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String pId = null;
        String pFragmentType = null;

        if (remoteMessage.getData().size() > 0) {
            pId = remoteMessage.getData().get("object_id");
            pFragmentType = remoteMessage.getData().get("fragment_type");
        }

        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), pId, pFragmentType);
        }

    }

    private void sendNotification(String messageBody, String title, String objectId, String fragmentType) {

        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("fragment_type", fragmentType);
        intent.putExtra("object_id", objectId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, MainApplication.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random rn = new Random();
        int notification_id = rn.nextInt(101);
        notificationManager.notify(notification_id, mBuilder.build());

    }
}