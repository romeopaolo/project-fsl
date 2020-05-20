package projects.fantasysoccerauction.utils;

import android.content.Context;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateManager {
    public DateManager() {
    }

    // transform Date to milliseconds to be stored as INTEGER
    public static long transformDateToMilliseconds(Context context, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date realDate = null;
        try {
            realDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(context, "Your date is not in the right format!", Toast.LENGTH_LONG).show();
        }
        long millis = 0;
        if (realDate != null) {
            millis = realDate.getTime();
        }
        return millis;
    }

    public static String transformMillisecondsToDate(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(date));
        return dateString;
    }
}
