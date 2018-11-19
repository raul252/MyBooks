package com.cifo.rgonzalezgall.mybooks;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "BooksMessagingService";
    private static final String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
    public static final String ACTION_DELETE = "ACTION_DELETE";
    public static final String ACTION_VIEW = "ACTION_VIEW";
    public static final String BOOK_POSITION = "BOOK_POSITION";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Map<String, String> data = null;
        String body = "";
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            data = remoteMessage.getData();
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            body = remoteMessage.getNotification().getBody();

            // Mostrar una notificación al recibir un mensaje de Firebase exercici 1
            //sendNotification1(body);
            // Mostrar una notificación al recibir un mensaje de Firebase exercici 2
            sendNotificationExpanded(body, data);
        }
    }

    /**
     * @param messageBody
     * @param messageData
     * Notificacion expandida ejemplo exercici 2
     */
    private void sendNotificationExpanded(String messageBody, Map<String, String> messageData) {

        String posicion = messageData.get("book_position");
        if (posicion == "") {
            Log.d(TAG, "No hay notificación expandida");
            sendNotification1(messageBody);
            return;
        } else {
            Log.d(TAG, "Notificación expandida");
        }
        // Intent que se mostrará al pulsar en la acción de la notificación
        Intent intentDelete = new Intent(this, BookListActivity.class);
        intentDelete.setAction(ACTION_DELETE);
        intentDelete.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentDelete.putExtra(BOOK_POSITION, posicion);
        PendingIntent pendingIntentDelete = PendingIntent.getActivity(this, 0, intentDelete,
                PendingIntent.FLAG_ONE_SHOT);

        Intent intentView = new Intent(this, BookListActivity.class);
        intentView.setAction(ACTION_VIEW);
        intentView.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentView.putExtra(BOOK_POSITION, posicion);
        PendingIntent pendingIntentView = PendingIntent.getActivity(this, 0, intentView,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_book)
                        .setContentTitle("Notificación expandida")
                        .setContentText(messageBody)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setLights(Color.BLUE,500,500)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Estos son los detalles expandidos de la notificación."))
                        .addAction(new NotificationCompat.Action(R.drawable.ic_delete, "Borrar", pendingIntentDelete))
                        .addAction(new NotificationCompat.Action(R.drawable.ic_pageview, "Ver", pendingIntentView));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Canal",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, mBuilder.build());
    }

    /**
     * @param messageBody
     * Notificacion ejemplo exercici 1
     */
    private void sendNotification1(String messageBody) {
        Intent intent = new Intent(this, BookListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Exercici 1")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Canal",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }
}
