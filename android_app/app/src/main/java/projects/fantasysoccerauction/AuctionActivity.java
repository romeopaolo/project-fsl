package projects.fantasysoccerauction;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import projects.fantasysoccerauction.database.DatabaseHelper;
import projects.fantasysoccerauction.database.DatabaseNomenclature;

import static projects.fantasysoccerauction.LoginActivity.USER_IDENTIFIER_EXTRA;

public class AuctionActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "Auction definition reminder";
    private static final int NOTIFICATION_ID = 77;
    private static final int DELAY = 10000;
    private String userIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction);

        createNotificationChannel();

        Button btn_org = findViewById(R.id.a_auction_btn_new);
        btn_org.setOnClickListener(v -> Toast.makeText(this, R.string.t_not_yet_implemented, Toast.LENGTH_LONG).show());

        // go to the mock-up of the auction management
        Button btn_live = findViewById(R.id.a_auction_btn_example);
        btn_live.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LiveAuctionActivity.class);
            startActivity(intent);
        });
    }

    private void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID_FIELD, NOTIFICATION_ID);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_FIELD, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
        } else {
            Toast.makeText(this, R.string.t_not_yet_implemented, Toast.LENGTH_LONG).show();
        }
    }

    private Notification createNotification() {
        // Define notification tap behaviour
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_ball)
                .setContentTitle(getString(R.string.a_auction_n_reminder_title))
                .setContentText(getString(R.string.a_auction_n_reminder_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder.build();
    }

    // Create the NotificationChannel, but only on API 26+
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder channel";
            String description = "Channel to send send reminders for the auctions";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                Toast.makeText(this, R.string.t_not_yet_implemented, Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Calendar calendar = Calendar.getInstance();
        int current_day = calendar.get(Calendar.DAY_OF_YEAR);

        // get the username from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userIdentifier = extras.getString(USER_IDENTIFIER_EXTRA);
        }

        // schedule notification only once per day at maximum
        if (current_day != getLastNotificationDay(DatabaseHelper.getInstance(this), userIdentifier)) {
            scheduleNotification(createNotification(), DELAY);
            updateLastNotificationDay(DatabaseHelper.getInstance(this), userIdentifier, current_day);
        }
    }

    private int getLastNotificationDay(SQLiteDatabase db, String userIdentifier) {
        String selection = DatabaseNomenclature.Notification.COLUMN_NAME_USER_ID + " LIKE ?";
        String[] selectionArgs = {userIdentifier};
        String[] projection = {
                DatabaseNomenclature.Notification.COLUMN_NAME_LAST_AUCTION_REMINDER
        };
        Cursor c = db.query(
                DatabaseNomenclature.Notification.TABLE_NAME,   // The table to query
                projection,             // get all columns
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (c.moveToFirst()) {
            int day = c.getInt(c.getColumnIndex(DatabaseNomenclature.Notification.COLUMN_NAME_LAST_AUCTION_REMINDER));
            c.close();
            return day;
        } else {
            return -1;
        }
    }

    private void updateLastNotificationDay(SQLiteDatabase db, String userIdentifier, int newDay) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(DatabaseNomenclature.Notification.COLUMN_NAME_LAST_AUCTION_REMINDER, newDay);

        // Which row to update, based on the title
        String selection = DatabaseNomenclature.Notification.COLUMN_NAME_USER_ID + " LIKE ?";
        String[] selectionArgs = {userIdentifier};

        db.update(
                DatabaseNomenclature.Notification.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
}
