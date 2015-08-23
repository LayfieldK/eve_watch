package net.evewatch;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class NotificationBarAlarm extends BroadcastReceiver {



@Override
public void onReceive(Context context, Intent intent) {
	
 
	
// This Activity will be started when the user clicks the notification
// in the notification bar
Intent notificationIntent = new Intent(context , APIPoller.class);

PendingIntent contentIntent = PendingIntent.getService(context , 0, notificationIntent, 0);

try {
	contentIntent.send();
	
} catch (CanceledException e) {
	
}
/*AlarmManager alertManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

Calendar calendar = Calendar.getInstance();

calendar.setTimeInMillis(System.currentTimeMillis());

alertManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 600000, contentIntent);*/
	
}
}