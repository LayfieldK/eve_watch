package net.evewatch;

import java.io.IOException;
import java.util.Calendar;

import net.evewatch.APIPoller;
import net.evewatch.AlertLevel;
import net.evewatch.R;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

 
public class MainActivity extends Activity {
	
	private PendingIntent pendingIntent;
	
	private static DataBaseHelper dbHelper = null;
	
	public static Context context;
	
	public static boolean firstRun = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		SharedPreferences settings =  getSharedPreferences("evewatch",0);
		
		
		Intent tintent = getIntent();
		String activity = tintent.getStringExtra("activity");
		
		if (dbHelper == null){
			dbHelper = new DataBaseHelper(this);
			SQLiteDatabase db=dbHelper.openDataBase();
	 
		}
		
		if (Character.readAvailableCharacterIDs(this).size() > 0){
			setContentView(R.layout.activity_main);

			
		} else {
			Intent intent = new Intent(this, APISettings.class);
	    	startActivity(intent);

			
		}
		
		

	}
	@Override
	protected void onStart(){
		super.onStart();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		if (item.getItemId() == R.id.action_settings){
		Intent intent = new Intent(this, APISettings.class);
    	startActivity(intent);
		} else if (item.getItemId() == R.id.action_alerts || item.getItemId() == android.R.id.home){
			Intent intent = new Intent(this, NewAlerts.class);
			intent.putExtra("activity", "menu");
	    	startActivity(intent);
		} else if (item.getItemId() == R.id.action_about){
			Intent intent = new Intent(this, About.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_menu){
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		return true;
	}
	
	public void alertTypeChosen(View view) {
    	Intent intent = new Intent(this, AlertLevel.class);
    	long value=-1;
    	switch (view.getId()){
    	case R.id.character:
    		value=1;
    		break;
    	case R.id.corporation:
    		value=2;
    		break;
    	case R.id.war:
    		value=3;
    		break;
    	case R.id.bills:
    		value=4;
    		break;
    	case R.id.fw:
    		value=8;
    		break;
    	case R.id.structures:
    		value=7;
    		break;
    	case R.id.agents:
    		value=9;
    		break;
    	case R.id.recruitment:
    		value=6;
    		break;
    	case R.id.bounties:
    		value=5;
    		break;
    	}
    	intent.putExtra("groupID", value);
    	startActivity(intent);
    }
	
	
	
	

}
