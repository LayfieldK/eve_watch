package net.evewatch;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {

/**
* Listens for Android's BOOT_COMPLETED broadcast and then executes
* the onReceive() method.
*/
@Override
public void onReceive(Context context, Intent arg1) {
	SharedPreferences settings =  context.getSharedPreferences("evewatch",0);
	if (settings.getBoolean("StartOnBootUp", false)){

Intent intent = new Intent(context, StarterService.class);
context.startService(intent);
	}
}
}