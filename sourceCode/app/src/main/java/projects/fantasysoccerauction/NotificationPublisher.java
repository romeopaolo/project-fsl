package projects.fantasysoccerauction;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID_FIELD = "notification_id";
    public static String NOTIFICATION_FIELD = "notification";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION_FIELD);
        int id = intent.getIntExtra(NOTIFICATION_ID_FIELD, 0);
        if (notificationManager != null) {
            notificationManager.notify(id, notification);
        }
    }
}
