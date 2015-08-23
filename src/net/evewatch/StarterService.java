package net.evewatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class StarterService extends Service {
private static final String TAG = "MyService";
private static DataBaseHelper dbHelper = null;
private static SQLiteDatabase db =null;

/**
* The started service starts the AlarmManager.
*/
@Override
public void onStart(Intent intent, int startid) {
Intent i = new Intent(this, NotificationBarAlarm.class);
SharedPreferences settings =  getSharedPreferences("evewatch",0);
SharedPreferences.Editor ed = settings.edit();

if (dbHelper == null){
	dbHelper = new DataBaseHelper(this);
	
	try {

		db = dbHelper.openDataBase();
		//db = dbHelper.getReadableDatabase();
		
	}catch(SQLException sqle){

		throw sqle;

	}
}

VersionUpgrade(this);














	
	if((APIPoller.isRunning == false && APIPoller.monitoringScheduled==false) || APISettings.overrideReschedule){
		APIPoller.monitoringScheduled=true;
		//APIPoller.charIDs = APIPoller.commaStringToArrayList(settings.getString("charIDs", ""));
		//APIPoller.activeCharIDs = APIPoller.commaStringToArrayList(settings.getString("activeCharIDs", ""));
		APIPoller.charIDs = Character.readAvailableCharacterIDs(this);
		APIPoller.activeCharIDs = Character.readActiveCharacterIDs(this);
PendingIntent pi = PendingIntent.getBroadcast(StarterService.this, 0, i, 0);

// Repeat the notification every 15 seconds (15000)
AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
am.cancel(pi);
am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, settings.getInt("frequency", 900001), pi);
//am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, 180000, pi);
	APISettings.overrideReschedule=false;
	}
}


public static void VersionUpgrade(Context context){
	
	SharedPreferences settings =  context.getSharedPreferences("evewatch",0);
	SharedPreferences.Editor ed = settings.edit();
	PackageInfo pInfo;
	try {
		pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

		String version = pInfo.versionName;
		
		String table = "notifications";
		String columns[] = {"_id"};
		if (dbHelper == null){
			dbHelper = new DataBaseHelper(context);
			
			try {

				db = dbHelper.openDataBase();
				//db = dbHelper.getReadableDatabase();
				
			}catch(SQLException sqle){
				try {
					dbHelper.createDataBase();
					db = dbHelper.openDataBase();
				} catch(SQLException e){
					throw e;
				}
				

			}
		}
		if (settings.getString("alertIDs", "").equals("")){
			String alertIDs="-- APICallProblems, skillComplete, skillQueueEmpty, roomInSkillQueue, orderBought, orderSold, orderExpired, jobDelivered, jobWorkComplete, researchComplete, walletEntry, messageReceived, upcomingEvent, cloneOutOfDate,";
			Cursor c = db.query(table,columns,null,null,null,null,null);
			if (c != null ) {
				if  (c.moveToFirst()) {
	    			do {
	    				
	    				alertIDs += " " + c.getString(c.getColumnIndex("_id")) + ",";
				    
			    	} while (c.moveToNext());
			    
			    	

			    }
			}
			c.close();
			ed.putString("alertIDs", alertIDs);
	    	ed.apply();
		}
		
		if (settings.getString("minIsk", "").equals("")){
			ed.putString("minIsk", "0.0");
			ed.apply();
		}
		
		if (settings.getString("walletTransactionTypeIDs","").equals("")){
			String walletTransactionIDs="--  1010, 1028, 1045, 1091, 1099, 1020, 1003, 1011,";
			/*Cursor c = db.query(table,columns,null,null,null,null,null);
			if (c != null ) {
				if  (c.moveToFirst()) {
	    			do {
	    				
	    				walletTransactionIDs += " " + c.getString(c.getColumnIndex("_id")) + ",";
				    
			    	} while (c.moveToNext());
			    
			    	

			    }
			}
			c.close();*/
			ed.putString("walletTransactionTypeIDs", walletTransactionIDs);
	    	ed.apply();
		}
		double onRecordVersion = Double.parseDouble(settings.getString("version", "0.0"));
		double appVersion = Double.parseDouble(version);
		if (onRecordVersion != appVersion){
			String alertIDs = settings.getString("alertIDs", "");
			if (onRecordVersion < 1.02){
				alertIDs = alertIDs + " orderExpired,";
			}
			
			ed.putString("version", version);
			ed.putString("alertIDs", alertIDs);
			ed.apply();
			try {
				 
				dbHelper.createDataBase();

			} catch (IOException ioe) {

				throw new Error("Unable to create database");

			}
			
			DeleteAllPastAndPrevFiles(context);
			
			
		}
		DeleteFilesOlderThan24Hours(context);
		if (!settings.getString("charIDs", "").equals("")){
			ArrayList<Character> characters = new ArrayList<Character>();
			ArrayList<Long> oldCharacters = APIPoller.commaStringToArrayList(settings.getString("charIDs", ""));
			for (int j = 0; j < oldCharacters.size(); j++){
				characters.add(new Character(oldCharacters.get(j), settings.getInt("KeyID", 0), settings.getString("vCode",""),""));
			}
			Character.writeAvailableCharacterIDs(characters, context);
			ed.remove("charIDs");
		}
		if (!settings.getString("activeCharIDs", "").equals("")){
			ArrayList<Character> characters = new ArrayList<Character>();
			ArrayList<Long> oldCharacters = APIPoller.commaStringToArrayList(settings.getString("activeCharIDs", ""));
			for (int j = 0; j < oldCharacters.size(); j++){
				characters.add(new Character(oldCharacters.get(j), settings.getInt("KeyID", 0), settings.getString("vCode",""),""));
			}
			Character.writeActiveCharacterIDs(characters, context);
			ed.remove("activeCharIDs");
			ed.remove("KeyID");
			ed.remove("vCode");
			ed.apply();
		}
		//Log.d("debugging","is this being reached?");
	} catch (NameNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}


public static void DeleteAllPastAndPrevFiles(Context context){
	try{
		File file = new File(context.getFilesDir()+File.separator+"charIDBaseLoadedHM");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevSkillQueue");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevOrders");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevResearch");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevWalletEntries");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevCharInfo");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevCloneInfo");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastEventIDs");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastNotificationIDs");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastJobIDsDelivered");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastJobIDsWorkCompleted");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastMarketOrdersExpired");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastMessageIDs");
		file.delete();
	}catch(Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastSkills");
		file.delete();
	}catch(Exception e){}
}


private static void DeleteFilesOlderThan24Hours(Context context){
	try{
		File file = new File(context.getFilesDir()+File.separator+"charIDBaseLoadedHM");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	    if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevSkillQueue");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevOrders");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevResearch");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevWalletEntries");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevCharInfo");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"prevCloneInfo");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	    if (DateUtil.addDays(new Date(), -1).after(d)){
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastEventIDs");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastNotificationIDs");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastJobIDsDelivered");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastJobIDsWorkCompleted");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastMarketOrdersExpired");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastMessageIDs");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
	
	try{
		File file = new File(context.getFilesDir()+File.separator+"pastSkills");
	    long datetime = file.lastModified();
	    Date d = new Date(datetime);
	  if (DateUtil.addDays(new Date(), -1).after(d)){
	    
	    	file.delete();
	    }
	} catch (Exception e){}
}


@Override
public IBinder onBind(Intent intent) {
return null;
}

}