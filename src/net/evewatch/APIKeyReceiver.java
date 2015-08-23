package net.evewatch;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class APIKeyReceiver extends BroadcastReceiver {

/**
* Listens for Android's BOOT_COMPLETED broadcast and then executes
* the onReceive() method.
*/
@Override
public void onReceive(Context context, Intent arg1) {
	SharedPreferences settings =  context.getSharedPreferences("evewatch",0);
	if (settings.getBoolean("StartOnBootUp", false)){
		
	}
	Intent i = new Intent(context,APISettings.class);
	context.startActivity(i);
}
}