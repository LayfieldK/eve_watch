package net.evewatch;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.client.ClientProtocolException;


import org.w3c.dom.Document;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import net.evewatch.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.audiofx.BassBoost.Settings;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.util.Xml;
//import android.widget.Toast;
import android.widget.Toast;

public final class APIPoller extends IntentService {
	
	public APIPoller() {
		super("APIPoller");
		// TODO Auto-generated constructor stub
	}


	Document dom;
	
	//public static boolean onoff = false;

	public static ArrayList<Character> charIDs = new ArrayList<Character>();
	public static ArrayList<Character> activeCharIDs = new ArrayList<Character>();
	public static volatile boolean monitoringScheduled = false;
	
	public static boolean hasReadFromFileOnce = false;
	
	//HashMap<Long,ArrayList> typeIDs = new HashMap<Long,ArrayList>(20000);
	//HashMap<Long,ArrayList> locations =  new HashMap<Long,ArrayList>(15000);
	//HashMap<Long,ArrayList> agents =  new HashMap<Long,ArrayList>(1000);
	
	//private static boolean isFirstRun = true;
	//private static ArrayList<Long> charIDBaseLoaded = new ArrayList<Long>();
	//private static ArrayList<Long> toBeLoaded = new ArrayList<Long>();
	private static HashMap<Long, String> charIDBaseLoadedHM = new HashMap<Long, String>();
	private static HashMap<Long, String> toBeLoadedHM = new HashMap<Long, String>();
	private static HashMap<Long, HashMap<String, Date>> cachedUntil = new HashMap<Long, HashMap<String, Date>>();
	private static HashMap<String, Integer> apiCallErrors = new HashMap<String, Integer>();
	
	private static boolean clearCharIDBaseLoadedHM = false;
	private static boolean removeCharFromHM = false;
	private static boolean addCharToHM = false;
	private static ArrayList<Long> charIDsMarkedForRemoval = new ArrayList<Long>();
	private static ArrayList<Character> charsMarkedForAddition = new ArrayList<Character>();
	private static ArrayList<HashMapAction> hmas = new ArrayList<HashMapAction>();
	
	private static boolean eveTimeRetrieved = false;
	private static Date eveTime;
	private static int timeShiftHours;
	
	/*private static ArrayList<SkillQueue> prevSkillQueue = new ArrayList<SkillQueue>();
	private static ArrayList<Orders> prevOrders = new ArrayList<Orders>();
	private static ArrayList<Research> prevResearch = new ArrayList<Research>();
	private static ArrayList<WalletEntries> prevWalletEntries = new ArrayList<WalletEntries>();
	//private static ArrayList<Messages> prevMessages = new ArrayList<Messages>();
	//private static ArrayList<Skills> prevSkills = new ArrayList<Skills>();
	private static ArrayList<CharInfo> prevCharInfo = new ArrayList<CharInfo>();
	private static ArrayList<Clone> prevCloneInfo = new ArrayList<Clone>();
	//private static ArrayList<SkillInTraining> prevSkillInTraining = new ArrayList<SkillInTraining>();
	
	private static ArrayList<SkillQueue> curSkillQueue = new ArrayList<SkillQueue>();
	private static ArrayList<Orders> curOrders = new ArrayList<Orders>();
	private static ArrayList<Research> curResearch = new ArrayList<Research>();
	private static ArrayList<Jobs> curJobs = new ArrayList<Jobs>();
	private static ArrayList<WalletEntries> curWalletEntries = new ArrayList<WalletEntries>();
	private static ArrayList<UpcomingEvents> curUpcomingEvents = new ArrayList<UpcomingEvents>();
	private static ArrayList<Notifications> curNotifications = new ArrayList<Notifications>();
	private static ArrayList<Messages> curMessages = new ArrayList<Messages>();
	private static ArrayList<Skills> curSkills = new ArrayList<Skills>();
	private static ArrayList<CharInfo> curCharInfo = new ArrayList<CharInfo>();
	private static ArrayList<Clone> curCloneInfo = new ArrayList<Clone>();
	//private static ArrayList<SkillInTraining> curSkillInTraining = null;*/
	
	private static HashMap<Long, ArrayList<SkillQueue>> prevSkillQueue = new HashMap<Long,ArrayList<SkillQueue>>();
	private static HashMap<Long, ArrayList<Orders>> prevOrders = new HashMap<Long,ArrayList<Orders>>();
	private static HashMap<Long, ArrayList<Research>> prevResearch = new HashMap<Long,ArrayList<Research>>();
	private static HashMap<Long, ArrayList<WalletEntries>> prevWalletEntries = new HashMap<Long,ArrayList<WalletEntries>>();
	//private static ArrayList<Messages> prevMessages = new ArrayList<Messages>();
	//private static ArrayList<Skills> prevSkills = new ArrayList<Skills>();
	private static HashMap<Long, ArrayList<CharInfo>> prevCharInfo = new HashMap<Long,ArrayList<CharInfo>>();
	private static HashMap<Long, ArrayList<Clone>> prevCloneInfo = new HashMap<Long,ArrayList<Clone>>();
	//private static ArrayList<SkillInTraining> prevSkillInTraining = new ArrayList<SkillInTraining>();
	
	private static HashMap<Long, ArrayList<SkillQueue>> curSkillQueue = new HashMap<Long,ArrayList<SkillQueue>>();
	private static HashMap<Long, ArrayList<Orders>> curOrders = new HashMap<Long,ArrayList<Orders>>();
	private static HashMap<Long, ArrayList<Research>> curResearch = new HashMap<Long,ArrayList<Research>>();
	private static HashMap<Long, ArrayList<Jobs>> curJobs = new HashMap<Long,ArrayList<Jobs>>();
	private static HashMap<Long, ArrayList<WalletEntries>> curWalletEntries = new HashMap<Long,ArrayList<WalletEntries>>();
	private static HashMap<Long, ArrayList<UpcomingEvents>> curUpcomingEvents = new HashMap<Long,ArrayList<UpcomingEvents>>();
	private static HashMap<Long, ArrayList<Notifications>> curNotifications = new HashMap<Long,ArrayList<Notifications>>();
	private static HashMap<Long, ArrayList<Messages>> curMessages = new HashMap<Long,ArrayList<Messages>>();
	private static HashMap<Long, ArrayList<Skills>> curSkills = new HashMap<Long,ArrayList<Skills>>();
	private static HashMap<Long, ArrayList<CharInfo>> curCharInfo = new HashMap<Long,ArrayList<CharInfo>>();
	private static HashMap<Long, ArrayList<Clone>> curCloneInfo = new HashMap<Long,ArrayList<Clone>>();
	//private static ArrayList<SkillInTraining> curSkillInTraining = null;
	
	
	
	private static ArrayList<NotificationTexts> curNotificationTexts = new ArrayList<NotificationTexts>();
	private static ArrayList<MailBodies> curMailBodies = new ArrayList<MailBodies>();
	
	private static ArrayList<UpcomingEvents> pastEventIDs = new ArrayList<UpcomingEvents>();
	private static ArrayList<Notifications> pastNotificationIDs = new ArrayList<Notifications>();
	private static ArrayList<Jobs> pastJobIDsDelivered = new ArrayList<Jobs>();
	private static ArrayList<Jobs> pastJobIDsWorkCompleted = new ArrayList<Jobs>();
	private static ArrayList<Orders> pastMarketOrdersExpired = new ArrayList<Orders>();
	private static ArrayList<Messages> pastMessageIDs = new ArrayList<Messages>();
	private static ArrayList<Skills> pastSkills = new ArrayList<Skills>();
	
	private static ArrayList<Long> charIDsWarnedAboutRoomInSkillQueue = new ArrayList<Long>();
	private static ArrayList<Long> charIDsWarnedAboutSkillQueueEmpty = new ArrayList<Long>();
	private static ArrayList<Long> charIDsWarnedAboutCloneOutOfDate = new ArrayList<Long>();
	
	private static final String ns = null;
	   
	private static DataBaseHelper dbHelper = null;
	private static SQLiteDatabase db =null;
    

	public static volatile boolean isRunning = false;
	//private static ArrayList<Alert> alerts = new ArrayList<Alert>();
	private static ArrayList<Alert> archivedAlerts = new ArrayList<Alert>();
	private static ArrayList<String> newAlertIDs = new ArrayList<String>();
	
	@Override
	public void onCreate(){
		
		SharedPreferences settings =  getSharedPreferences("evewatch",0);
	    
		 charIDs = Character.readAvailableCharacterIDs(this);
		 activeCharIDs = Character.readActiveCharacterIDs(this);	
		
		if (dbHelper == null){
			dbHelper = new DataBaseHelper(this);
				 
			try {
	 
				db = dbHelper.openDataBase();
								
			}catch(SQLException sqle){
	 
				throw sqle;
	 
			}
		}
		
		super.onCreate();
	}
	
	public static ArrayList commaStringToArrayList(String commaList){
		ArrayList charIDs = new ArrayList();
		
		String[] temp = commaList.split(",");
		if (commaList.indexOf(",") > -1){
			for (int i = 0; i < temp.length; i ++){
				charIDs.add(Long.parseLong(temp[i]));
			}
		}
		return charIDs;
	}
	
	public static String getSkillName(long typeID){
		String table = "type";
		String columns[] = {"name"};
		String select = "_id=" + typeID;
		
		String name = "an unidentified skill";
		
		Cursor c = db.query(table,columns,select,null,null,null,null);
		if (c != null ) {
		    if  (c.moveToFirst()) {
		        
		            name = c.getString(c.getColumnIndex("name"));
		            
		    }
		}
		c.close();
		return name;
	}
	
	public static String getWalletTransactionDescription(long refTypeID){
		String table = "wallettransactiontypes";
		String columns[] = {"description"};
		String select = "_id=" + refTypeID;
		
		String description = "Unknown Transaction Type";
		
		Cursor c = db.query(table,columns,select,null,null,null,null);
		if (c != null ) {
		    if  (c.moveToFirst()) {
		        
		    	description = c.getString(c.getColumnIndex("description"));
		            
		    }
		}
		c.close();
		return description;
	}
	
	public static String getItemName(long typeID){
		String table = "type";
		String columns[] = {"name"};
		String select = "_id=" + typeID;
		
		String name = "an unidentifiable item";
		
		Cursor c = db.query(table,columns,select,null,null,null,null);
		if (c != null ) {
		    if  (c.moveToFirst()) {
		        
		            name = c.getString(c.getColumnIndex("name"));
		            
		    }
		}
		c.close();
		return name;
	}
	
	public static ArrayList getAgentName(long agentID){
		String table = "agent";
		String columns[] = {"name","location_id"};
		String select = "_id=" + agentID;
		
		String name = "an unidentifiable agent";
		long locationID = -1;
		
		Cursor c = db.query(table,columns,select,null,null,null,null);
		if (c != null ) {
		    if  (c.moveToFirst()) {
		        
		            name = c.getString(c.getColumnIndex("name"));
		            locationID = c.getInt(c.getColumnIndex("location_id"));
		            
		    }
		}
		c.close();
		ArrayList results = new ArrayList();
		results.add(name);
		results.add(locationID);
		return results;
	}
	
	public static String getStationName(long locationID){
		String table = "location";
		String columns[] = {"name"};
		String select = "_id=" + locationID;
		
		String name = "an unidentifiable station";
		
		Cursor c = db.query(table,columns,select,null,null,null,null);
		if (c != null ) {
		    if  (c.moveToFirst()) {
		        
		            name = c.getString(c.getColumnIndex("name"));
		            
		    }
		}
		c.close();
		return name;
	}
	
	public static String getSystemName(long locationID){
		String table = "systems";
		String columns[] = {"name"};
		String select = "_id=" + locationID;
		
		String name = "an unidentifiable system";
		
		Cursor c = db.query(table,columns,select,null,null,null,null);
		if (c != null ) {
		    if  (c.moveToFirst()) {
		        
		            name = c.getString(c.getColumnIndex("name"));
		            
		    }
		}
		c.close();
		return name;
	}
	
	private HashMap getNotificationDescription(long typeID){
		HashMap info = new HashMap();
		String table = "notifications";
		String columns[] = {"description","group_id"};
		String select = "_id=" + typeID;
		
		String name = "a notification";
		long groupID = -1;
		
		Cursor c = db.query(table,columns,select,null,null,null,null);
		if (c != null ) {
		    if  (c.moveToFirst()) {
		        
		            name = c.getString(c.getColumnIndex("description"));
		            groupID = c.getLong(c.getColumnIndex("group_id"));
		    }
		}
		c.close();
		info.put("name", name);
		info.put("groupID", groupID);
		return info;
	}
	
	private String getAlertCategory(long groupID){
		/*
		 * 	messageReceived		1
			upcomingEvent		1
			walletEntry		1
			jobDelivered		1
			researchComplete	1
			cloneOutOfDate		1
			orderSold		1
			orderBought		1
			skillComplete		1
			skillQueueEmpty		1
			roomInSkillQueue	1
			
			Characters/Misc		1
			Corp/Alliance		2
			War			3
			Bills/Insurance/Clones	4
			Bounties/Kill Rights	5
			Recruitment		6
			Owned Structures	7
			Faction Warfare		8
			Agents			9
		 */
		String returnValue = "Unknown Notification Type";
		switch ((int)groupID){
		case 1:
			returnValue= "BscChar/Misc";
			break;
		case 2:
			returnValue= "Corp/Alliance";
			break;
		case 3:
			returnValue= "War";
			break;
		case 4:
			returnValue= "Bills/Ins/Clones";
			break;
		case 5:
			returnValue= "Bounties/Kill Rights";
			break;
		case 6:
			returnValue= "Recruitment";
			break;
		case 7:
			returnValue= "Sov/Structures";
			break;
		case 8:
			returnValue= "Faction Warfare";
			break;
		case 9:
			returnValue= "Agents";
			break;
		}
		return returnValue;
	}
	
	
	/*private void parseEveDBXML(String fileType) throws XmlPullParserException, IOException, ParseException {
	    
	    
		XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

        AssetManager am = getResources().getAssets();
        InputStream in = am.open(fileType + ".xml");
        
        parser.setInput(in, null);
        parser.nextTag();
        
	    parser.require(XmlPullParser.START_TAG, ns, "Root");
	    
	    while (parser.next() != XmlPullParser.END_TAG || parser.getName() == "Row") {
	    	Log.d("debugging",parser.getName());
	    	
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals("Row")) {
	            parseEveDBXMLRow(parser,fileType);
	        }
	    }  
	    
	}
	
	private void parseEveDBXMLRow(XmlPullParser parser, String fileType) throws XmlPullParserException, IOException, ParseException {
		//Log.d("debugging","readResult");
	    parser.require(XmlPullParser.START_TAG, ns, "Row");
	    
	    
	        
	        if (fileType == "agents"){
	        	ArrayList al = new ArrayList();
	        	al.add(parser.getAttributeValue(null,"Field_1"));
	        	al.add(Long.parseLong(parser.getAttributeValue(null,"Field_2")));
	        	agents.put(Long.parseLong(parser.getAttributeValue(null,"Field_0")),al);
	        } else if (fileType == "locations"){
	        	ArrayList al = new ArrayList();
	        	al.add(parser.getAttributeValue(null,"Field_1"));
	        	locations.put(Long.parseLong(parser.getAttributeValue(null,"Field_0")),al);
	        } else if (fileType == "typeIDs"){
	        	ArrayList al = new ArrayList();
	        	al.add(parser.getAttributeValue(null,"Field_1"));
	        	typeIDs.put(Long.parseLong(parser.getAttributeValue(null,"Field_0")),al);
	        }
	    
	    
	}*/
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags,startId);
		
		return START_NOT_STICKY;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	
	@Override
	public void onDestroy(){
		//Toast.makeText(this, "destroy", Toast.LENGTH_LONG).show();
		isRunning=false;
		
		super.onDestroy();
	}
	
	private synchronized void checkForNewAlerts() throws ClientProtocolException, URISyntaxException, IOException, InterruptedException, ParseException, ClassNotFoundException{
		//new NotificationTranslator().translate("aggressorAllianceID: 99002425<br>aggressorCorpID: 1166036710<br>aggressorID: 91419424<br>planetID: 40189531<br>planetTypeID: 2015<br>shieldLevel: 0.6752086269233896<br>solarSystemID: 30002987<br>typeID: 2233");
		SharedPreferences settings =  getSharedPreferences("evewatch",0);
	    //Log.d("debugging","starting");
		
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()){
			if ((activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE && settings.getBoolean("AllowCellularData", false)) || activeNetworkInfo.getType() != ConnectivityManager.TYPE_MOBILE){
				
			
  		
		if (clearCharIDBaseLoadedHM){
			ClearCharIDBaseLoaded();
		}
		if (removeCharFromHM || addCharToHM){
			ExecuteHashMapActions(this);
		}

		charIDs = Character.readAvailableCharacterIDs(this);
		activeCharIDs = Character.readActiveCharacterIDs(this);
		ArrayList<Alert> newAlerts = new ArrayList<Alert>();
		if (charIDBaseLoadedHM.isEmpty() || toBeLoadedHM.isEmpty() || apiCallErrors.isEmpty() || cachedUntil.isEmpty()
				|| curSkillQueue.isEmpty() || prevSkillQueue.isEmpty() || curOrders.isEmpty() || prevOrders.isEmpty()
				|| curResearch.isEmpty() || prevResearch.isEmpty() || curWalletEntries.isEmpty() || prevWalletEntries.isEmpty()
				|| curCharInfo.isEmpty() || prevCharInfo.isEmpty() || curCloneInfo.isEmpty() || prevCloneInfo.isEmpty()
				|| curMessages.isEmpty() || curNotifications.isEmpty() || curJobs.isEmpty() || curSkills.isEmpty()
				|| curUpcomingEvents.isEmpty()  ){
			InitializeHashMaps();
		}
		ArrayList<Character> tempCharIDs = (ArrayList<Character>) charIDs.clone();
		ArrayList<Character> tempActiveCharIDs = (ArrayList<Character>) activeCharIDs.clone();
		//if (retrieveAPICredentials()){
			
			//read from files to populate past and prev variables
				
			//Log.d("debugging","1");
			if (!hasReadFromFileOnce){	
				readPastAndPrevFromFiles();	
				hasReadFromFileOnce = true;
			}
				
				//boolean acurSkillInTraining=true;
			
			for (int i = 0; i < tempActiveCharIDs.size(); i++){ 
				
				ArrayList<Orders> fcurOrders = new ArrayList<Orders>();
				ArrayList<CharInfo> fcurCharInfo  = new ArrayList<CharInfo>();
				ArrayList<Skills> fcurSkills  = new ArrayList<Skills>();
				ArrayList<Clone> fcurCloneInfo  = new ArrayList<Clone>();
				ArrayList<Research> fcurResearch  = new ArrayList<Research>();
				ArrayList<Jobs> fcurJobs = new ArrayList<Jobs>();
				ArrayList<WalletEntries> fcurWalletEntries = new ArrayList<WalletEntries>();
				ArrayList<UpcomingEvents> fcurUpcomingEvents  = new ArrayList<UpcomingEvents>();
				ArrayList<Notifications> fcurNotifications = new ArrayList<Notifications>();
				ArrayList<Messages> fcurMessages  = new ArrayList<Messages>();
				//ArrayList<NotificationTexts> fcurNotificationTexts;
				//ArrayList<MailBodies> fcurMailBodies ;
				ArrayList<SkillQueue> fcurSkillQueue  = new ArrayList<SkillQueue>();
				//ArrayList<SkillInTraining> fcurSkillInTraining = new ArrayList<SkillInTraining>();
				
				boolean acurOrders=true  ;
				boolean acurCharInfo=true  ;
				boolean acurSkills =true ;
				boolean acurCloneInfo =true ;
				boolean acurResearch =true ;
				boolean acurJobs=true ;
				boolean acurWalletEntries=true ;
				boolean acurUpcomingEvents =true ;
				boolean acurNotifications=true ;
				boolean acurMessages=true  ;
				//boolean fcurNotificationTexts=true ;
				//boolean fcurMailBodies=true  ;
				boolean acurSkillQueue=true ;
				
				String urls[] = new String[50];
				urls[0] = "https://api.eveonline.com/char/SkillQueue.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID) + "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				urls[1] = "https://api.eveonline.com/char/MarketOrders.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID) + "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				urls[2] = "https://api.eveonline.com/eve/CharacterInfo.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID) + "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				urls[3] = "https://api.eveonline.com/char/CharacterSheet.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID) + "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				urls[4] = "https://api.eveonline.com/char/Research.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID) + "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				urls[5] = "https://api.eveonline.com/char/IndustryJobs.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID) + "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				urls[6] = "https://api.eveonline.com/char/WalletJournal.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID) + "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				urls[7] = "https://api.eveonline.com/char/UpcomingCalendarEvents.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID) + "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				urls[8] = "https://api.eveonline.com/char/Notifications.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID) + "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				urls[9] = "https://api.eveonline.com/char/MailMessages.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID) + "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				urls[10] = "https://api.eveonline.com/char/NotificationTexts.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID) + "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				urls[11] = "https://api.eveonline.com/char/MailBodies.xml.aspx?keyId=" + String.valueOf(tempActiveCharIDs.get(i).keyID)+ "&vCode=" + tempActiveCharIDs.get(i).vCode + "&characterID=" + tempActiveCharIDs.get(i).characterID;
				//urls[12] = "https://api.eveonline.com/char/SkillInTraining.xml.aspx?keyId=" + String.valueOf(keyID) + "&vCode=" + vCode + "&characterID=" + tempActiveCharIDs.get(i);

				
				ArrayList<Orders> tcurOrders;
				ArrayList<CharInfo> tcurCharInfo;
				ArrayList<Skills> tcurSkills;
				ArrayList<Clone> tcurCloneInfo;
				ArrayList<Research> tcurResearch;
				ArrayList<Jobs> tcurJobs;
				ArrayList<WalletEntries> tcurWalletEntries;
				ArrayList<UpcomingEvents> tcurUpcomingEvents;
				ArrayList<Notifications> tcurNotifications;
				ArrayList<Messages> tcurMessages;
				//ArrayList<NotificationTexts> tcurNotificationTexts = new DownloadWebpageText(tempActiveCharIDs.get(i),"NotificationTexts").readURL(urls[10]);
				//ArrayList<MailBodies> tcurMailBodies = new DownloadWebpageText(tempActiveCharIDs.get(i),"MailBodies").readURL(urls[11]);
				ArrayList<SkillQueue> tcurSkillQueue;
				//ArrayList<SkillInTraining> tcurSkillInTraining = new DownloadWebpageText(tempActiveCharIDs.get(i),"SkillInTraining").readURL(urls[12]);
				String alertString = settings.getString("alertIDs","");
				
				if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Orders,") || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Orders") == null || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Orders").before(new Date())){
					tcurOrders = new DownloadWebpageText(tempActiveCharIDs.get(i).characterID,"Orders").readURL(urls[1]);
					
					if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Orders,")){
						if (tcurOrders != null){
							String currentVal = toBeLoadedHM.get(tempActiveCharIDs.get(i).characterID);
							toBeLoadedHM.put(tempActiveCharIDs.get(i).characterID, currentVal + "Orders,");
						}
					}
					
					if (tcurOrders != null) { 
						 fcurOrders.addAll(tcurOrders); 
						 apiCallErrors.put("Orders", (apiCallErrors.get("Orders") > 0)?(apiCallErrors.get("Orders") - 1):apiCallErrors.get("Orders"));
						 }else {
							 acurOrders=false;
							 if(alertString.contains(" " + "APICallProblems,")){
								 apiCallErrors.put("Orders", apiCallErrors.get("Orders") + 1);
								 if (apiCallErrors.get("Orders") == 4){
									 apiCallErrors.put("Orders", apiCallErrors.get("Orders") + 1);
									 newAlerts.add(0,new Alert("APICallProblems", tempActiveCharIDs.get(i).characterID, "Problems retrieving market orders", "(BscChar/Wallet/Misc)", "Eve Online's API servers are either having problems or could not be reached.  Alerts concerning market orders may be inaccurate until API calls stabilize.", Calendar.getInstance().getTime()));
									 newAlertIDs.add("APICallProblems");
								 }
							 }
						 }
				} else{
					acurOrders=false;
				}
				if (acurOrders){
					curOrders.put(tempActiveCharIDs.get(i).characterID, fcurOrders) ;
				}
				if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("CharacterInfo,") || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("CharacterInfo") == null || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("CharacterInfo").before(new Date())){
					tcurCharInfo = new DownloadWebpageText(tempActiveCharIDs.get(i).characterID,"CharacterInfo").readURL(urls[2]);
					
					if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("CharacterInfo,")){
						if (tcurCharInfo != null){
							String currentVal = toBeLoadedHM.get(tempActiveCharIDs.get(i).characterID);
							toBeLoadedHM.put(tempActiveCharIDs.get(i).characterID, currentVal + "CharacterInfo,");
						}
					}	
					
					if (tcurCharInfo != null){
						 fcurCharInfo.addAll(tcurCharInfo);
						 apiCallErrors.put("CharacterInfo", (apiCallErrors.get("CharacterInfo") > 0)?(apiCallErrors.get("CharacterInfo") - 1):apiCallErrors.get("CharacterInfo"));
					 } else {
						 acurCharInfo=false;
						 if(alertString.contains(" " + "APICallProblems,")){
							 apiCallErrors.put("CharacterInfo", apiCallErrors.get("CharacterInfo") + 1);
							 if (apiCallErrors.get("CharacterInfo") == 4){
								 apiCallErrors.put("CharacterInfo", apiCallErrors.get("CharacterInfo") + 1);
								 newAlerts.add(0,new Alert("APICallProblems", tempActiveCharIDs.get(i).characterID, "Problems retrieving character info", "(BscChar/Wallet/Misc)", "Eve Online's API servers are either having problems or could not be reached.  Some alerts may be inaccurate until API calls stabilize.", Calendar.getInstance().getTime()));
								 newAlertIDs.add("APICallProblems");
							 }
						 }
					 }
				}else{
					acurCharInfo=false;
				}
				if (acurCharInfo){
					curCharInfo.put(tempActiveCharIDs.get(i).characterID, fcurCharInfo)  ;
				}
				if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("CharacterSheet,") || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("CharacterSheet") == null || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("CharacterSheet").before(new Date())){
					tcurSkills = new DownloadWebpageText(tempActiveCharIDs.get(i).characterID,"CharacterSheet").readURL(urls[3]);
					
					if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("CharacterSheet,")){
						if (tcurSkills != null){
							String currentVal = toBeLoadedHM.get(tempActiveCharIDs.get(i).characterID);
							toBeLoadedHM.put(tempActiveCharIDs.get(i).characterID, currentVal + "CharacterSheet,");
						}
					}
					
					if (tcurSkills != null) { 
						 fcurSkills.addAll(tcurSkills);
						 apiCallErrors.put("CharacterSheet",(apiCallErrors.get("CharacterSheet") > 0)?(apiCallErrors.get("CharacterSheet") - 1):apiCallErrors.get("CharacterSheet"));
					 }
					 else {
						 acurSkills=false;
						 if(alertString.contains(" " + "APICallProblems,")){
							 apiCallErrors.put("CharacterSheet", apiCallErrors.get("CharacterSheet") + 1);
							 if (apiCallErrors.get("CharacterSheet") == 4){
								 apiCallErrors.put("CharacterSheet", apiCallErrors.get("CharacterSheet") + 1);
								 newAlerts.add(0,new Alert("APICallProblems", tempActiveCharIDs.get(i).characterID, "Problems retrieving character sheet", "(BscChar/Wallet/Misc)", "Eve Online's API servers are either having problems or could not be reached.  Alerts concerning your character sheet (including Skill Training Complete) may be inaccurate until API calls stabilize.", Calendar.getInstance().getTime()));
								 newAlertIDs.add("APICallProblems");
							 }
						 }
					 }
				}else{
					acurSkills=false;
				}
				if (acurSkills){
					curSkills.put(tempActiveCharIDs.get(i).characterID, fcurSkills) ;
				}
				if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Clone,") || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Clone") == null || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Clone").before(new Date())){
					tcurCloneInfo = new DownloadWebpageText(tempActiveCharIDs.get(i).characterID,"Clone").readURL(urls[3]);
					
					if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Clone,")){
						if (tcurCloneInfo != null){
							String currentVal = toBeLoadedHM.get(tempActiveCharIDs.get(i).characterID);
							toBeLoadedHM.put(tempActiveCharIDs.get(i).characterID, currentVal + "Clone,");
						}
					}
					
					if (tcurCloneInfo != null){
						 fcurCloneInfo.addAll(tcurCloneInfo);
						 apiCallErrors.put("Clone",(apiCallErrors.get("Clone") > 0)?(apiCallErrors.get("Clone") - 1):apiCallErrors.get("Clone"));
					 } else {
						 acurCloneInfo=false;
						 if(alertString.contains(" " + "APICallProblems,")){
							 apiCallErrors.put("Clone", apiCallErrors.get("Clone") + 1);
							 if (apiCallErrors.get("Clone") == 4){
								 apiCallErrors.put("Clone", apiCallErrors.get("Clone") + 1);
								 newAlerts.add(0,new Alert("APICallProblems", tempActiveCharIDs.get(i).characterID, "Problems retrieving clone info", "(BscChar/Wallet/Misc)", "Eve Online's API servers are either having problems or could not be reached.  Some alerts concerning clone info may be inaccurate until API calls stabilize.", Calendar.getInstance().getTime()));
								 newAlertIDs.add("APICallProblems");
							 }
						 }
					 }
				}else{
					acurCloneInfo=false;
				}
				if (acurCloneInfo){
					curCloneInfo.put(tempActiveCharIDs.get(i).characterID, fcurCloneInfo) ;
				}
				if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Research,") || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Research") == null || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Research").before(new Date())){
					tcurResearch = new DownloadWebpageText(tempActiveCharIDs.get(i).characterID,"Research").readURL(urls[4]);
					
					if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Research,")){
						if (tcurResearch != null){
							String currentVal = toBeLoadedHM.get(tempActiveCharIDs.get(i).characterID);
							toBeLoadedHM.put(tempActiveCharIDs.get(i).characterID, currentVal + "Research,");
						}
					}
					
					if (tcurResearch != null) { 
						 fcurResearch.addAll(tcurResearch);
						 apiCallErrors.put("Research", (apiCallErrors.get("Research") > 0)?(apiCallErrors.get("Research") - 1):apiCallErrors.get("Research"));
						 }else {
							 acurResearch=false;
							 if(alertString.contains(" " + "APICallProblems,")){
								 apiCallErrors.put("Research", apiCallErrors.get("Research") + 1);
								 if (apiCallErrors.get("Research") == 4){
									 apiCallErrors.put("Research", apiCallErrors.get("Research") + 1);
									 newAlerts.add(0,new Alert("APICallProblems", tempActiveCharIDs.get(i).characterID, "Problems retrieving research info", "(BscChar/Wallet/Misc)", "Eve Online's API servers are either having problems or could not be reached.  Alerts concerning research may be inaccurate until API calls stabilize.", Calendar.getInstance().getTime()));
									 newAlertIDs.add("APICallProblems");
								 }
							 }
						 }
				}else{
					acurResearch=false;
				}
				if (acurResearch){
					curResearch.put(tempActiveCharIDs.get(i).characterID, fcurResearch)  ;
				}
				if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Jobs,") || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Jobs") == null || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Jobs").before(new Date())){
					tcurJobs = new DownloadWebpageText(tempActiveCharIDs.get(i).characterID,"Jobs").readURL(urls[5]);
					
					if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Jobs,")){
						if (tcurJobs != null){
							String currentVal = toBeLoadedHM.get(tempActiveCharIDs.get(i).characterID);
							toBeLoadedHM.put(tempActiveCharIDs.get(i).characterID, currentVal + "Jobs,");
						}
					}
					
					if (tcurJobs != null) { 
						 fcurJobs.addAll(tcurJobs);
						 apiCallErrors.put("Jobs",(apiCallErrors.get("Jobs") > 0)?(apiCallErrors.get("Jobs") - 1):apiCallErrors.get("Jobs"));
						 }else {
							 acurJobs = false;
							 if(alertString.contains(" " + "APICallProblems,")){
								 apiCallErrors.put("Jobs", apiCallErrors.get("Jobs") + 1);
								 if (apiCallErrors.get("Orders") == 4){
									 apiCallErrors.put("Jobs", apiCallErrors.get("Jobs") + 1);
									 newAlerts.add(0,new Alert("APICallProblems", tempActiveCharIDs.get(i).characterID, "Problems retrieving industrial jobs", "(BscChar/Wallet/Misc)", "Eve Online's API servers are either having problems or could not be reached.  Alerts concerning industrial jobs may be inaccurate until API calls stabilize.", Calendar.getInstance().getTime()));
									 newAlertIDs.add("APICallProblems");
								 }
							 }
						 }
				}else{
					acurJobs=false;
				}
				if (acurJobs){
					curJobs.put(tempActiveCharIDs.get(i).characterID, fcurJobs)  ;
				}
				if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("WalletEntries,") || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("WalletEntries") == null || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("WalletEntries").before(new Date())){
					tcurWalletEntries = new DownloadWebpageText(tempActiveCharIDs.get(i).characterID,"WalletEntries").readURL(urls[6]);
					
					if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("WalletEntries,")){
						if (tcurWalletEntries != null){
							String currentVal = toBeLoadedHM.get(tempActiveCharIDs.get(i).characterID);
							toBeLoadedHM.put(tempActiveCharIDs.get(i).characterID, currentVal + "WalletEntries,");
						}
					}
					
					if (tcurWalletEntries != null) { 
						 fcurWalletEntries.addAll(tcurWalletEntries);
						 apiCallErrors.put("WalletEntries", (apiCallErrors.get("WalletEntries") > 0)?(apiCallErrors.get("WalletEntries") - 1):apiCallErrors.get("WalletEntries"));
						 }else {
							 acurWalletEntries=false;
							 if(alertString.contains(" " + "APICallProblems,")){
								 apiCallErrors.put("WalletEntries", apiCallErrors.get("WalletEntries") + 1);
								 if (apiCallErrors.get("WalletEntries") == 4){
									 apiCallErrors.put("WalletEntries", apiCallErrors.get("WalletEntries") + 1);
									 newAlerts.add(0,new Alert("APICallProblems", tempActiveCharIDs.get(i).characterID, "Problems retrieving wallet entries", "(BscChar/Wallet/Misc)", "Eve Online's API servers are either having problems or could not be reached.  Alerts concerning wallet entries may be inaccurate until API calls stabilize.", Calendar.getInstance().getTime()));
									 newAlertIDs.add("APICallProblems");
								 }
							 }
						 }
				}else{
					acurWalletEntries=false;
				}
				if (acurWalletEntries){
					curWalletEntries.put(tempActiveCharIDs.get(i).characterID, fcurWalletEntries)  ;
				}
				if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("UpcomingEvents,") || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("UpcomingEvents") == null || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("UpcomingEvents").before(new Date())){
					tcurUpcomingEvents = new DownloadWebpageText(tempActiveCharIDs.get(i).characterID,"UpcomingEvents").readURL(urls[7]);
					
					if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("UpcomingEvents,")){
						if (tcurUpcomingEvents != null){
							String currentVal = toBeLoadedHM.get(tempActiveCharIDs.get(i).characterID);
							toBeLoadedHM.put(tempActiveCharIDs.get(i).characterID, currentVal + "UpcomingEvents,");
						}
					}
					
					if (tcurUpcomingEvents != null) { 
						 fcurUpcomingEvents.addAll(tcurUpcomingEvents);
						 apiCallErrors.put("UpcomingEvents", (apiCallErrors.get("UpcomingEvents") > 0)?(apiCallErrors.get("UpcomingEvents") - 1):apiCallErrors.get("UpcomingEvents"));
						 }else {
							 acurUpcomingEvents=false;
							 if(alertString.contains(" " + "APICallProblems,")){
								 apiCallErrors.put("UpcomingEvents", apiCallErrors.get("UpcomingEvents") + 1);
								 if (apiCallErrors.get("UpcomingEvents") == 4){
									 apiCallErrors.put("UpcomingEvents", apiCallErrors.get("UpcomingEvents") + 1);
									 newAlerts.add(0,new Alert("APICallProblems", tempActiveCharIDs.get(i).characterID, "Problems retrieving upcoming events", "(BscChar/Wallet/Misc)", "Eve Online's API servers are either having problems or could not be reached.  Alerts concerning upcoming events may be inaccurate until API calls stabilize.", Calendar.getInstance().getTime()));
									 newAlertIDs.add("APICallProblems");
								 }
							 }
						 }
				}else{
					acurUpcomingEvents=false;
				}
				if (acurUpcomingEvents){
					curUpcomingEvents.put(tempActiveCharIDs.get(i).characterID, fcurUpcomingEvents)  ;
				}
				if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Notifications,") || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Notifications") == null || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Notifications").before(new Date())){
					tcurNotifications = new DownloadWebpageText(tempActiveCharIDs.get(i).characterID,"Notifications").readURL(urls[8]);
					
					if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Notifications,")){
						if (tcurNotifications != null){
							String currentVal = toBeLoadedHM.get(tempActiveCharIDs.get(i).characterID);
							toBeLoadedHM.put(tempActiveCharIDs.get(i).characterID, currentVal + "Notifications,");
						}
					}
					
					if (tcurNotifications != null) { 
						 fcurNotifications.addAll(tcurNotifications);
						 apiCallErrors.put("Notifications",(apiCallErrors.get("Notifications") > 0)?(apiCallErrors.get("Notifications") - 1):apiCallErrors.get("Notifications"));
						 }
					 else {
						 acurNotifications = false;
						 if(alertString.contains(" " + "APICallProblems,")){
							 apiCallErrors.put("Notifications", apiCallErrors.get("Notifications") + 1);
							 if (apiCallErrors.get("Notifications") == 4){
								 apiCallErrors.put("Notifications", apiCallErrors.get("Notifications") + 1);
								 newAlerts.add(0,new Alert("APICallProblems", tempActiveCharIDs.get(i).characterID, "Problems retrieving notifications", "(BscChar/Wallet/Misc)", "Eve Online's API servers are either having problems or could not be reached.  Alerts derived from Eve Online notifications may be inaccurate until API calls stabilize.", Calendar.getInstance().getTime()));
								 newAlertIDs.add("APICallProblems");
							 }
						 }
					 }
				}else{
					acurNotifications=false;
				}
				if (acurNotifications){
					curNotifications.put(tempActiveCharIDs.get(i).characterID, fcurNotifications)  ;
				}
				if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Messages,") || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Messages") == null || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("Messages").before(new Date())){
					tcurMessages = new DownloadWebpageText(tempActiveCharIDs.get(i).characterID,"Messages").readURL(urls[9]);
					
					if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("Messages,")){
						if (tcurMessages != null){
							String currentVal = toBeLoadedHM.get(tempActiveCharIDs.get(i).characterID);
							toBeLoadedHM.put(tempActiveCharIDs.get(i).characterID, currentVal + "Messages,");
						}
					}
					
					if (tcurMessages != null) { 
						 fcurMessages.addAll(tcurMessages);
						 apiCallErrors.put("Messages", (apiCallErrors.get("Messages") > 0)?(apiCallErrors.get("Messages") - 1):apiCallErrors.get("Messages"));
					 }
					 else {
						 acurMessages=false;
						 if(alertString.contains(" " + "APICallProblems,")){
							 apiCallErrors.put("Messages", apiCallErrors.get("Messages") + 1);
							 if (apiCallErrors.get("Messages") == 4){
								 apiCallErrors.put("Messages", apiCallErrors.get("Messages") + 1);
								 newAlerts.add(0,new Alert("APICallProblems", tempActiveCharIDs.get(i).characterID, "Problems retrieving mail messages", "(BscChar/Wallet/Misc)", "Eve Online's API servers are either having problems or could not be reached.  Alerts concerning mail messages may be inaccurate until API calls stabilize.", Calendar.getInstance().getTime()));
								 newAlertIDs.add("APICallProblems");
							 }
						 }
					 }
				}else{
					acurMessages=false;
				}
				if (acurMessages){
					curMessages.put(tempActiveCharIDs.get(i).characterID, fcurMessages)  ;
				}
				if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("SkillQueue,") || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("SkillQueue") == null || cachedUntil.get(tempActiveCharIDs.get(i).characterID).get("SkillQueue").before(new Date())){
					tcurSkillQueue = new DownloadWebpageText(tempActiveCharIDs.get(i).characterID,"SkillQueue").readURL(urls[0]);
					
					if (!charIDBaseLoadedHM.get(tempActiveCharIDs.get(i).characterID).contains("SkillQueue,")){
						if (tcurSkillQueue != null){
							String currentVal = toBeLoadedHM.get(tempActiveCharIDs.get(i).characterID);
							toBeLoadedHM.put(tempActiveCharIDs.get(i).characterID, currentVal + "SkillQueue,");
						}
					}
					
					if (tcurSkillQueue != null){
						 fcurSkillQueue.addAll(tcurSkillQueue);
						 apiCallErrors.put("SkillQueue",(apiCallErrors.get("SkillQueue") > 0)?(apiCallErrors.get("SkillQueue") - 1):apiCallErrors.get("SkillQueue"));
					 } else {
						 acurSkillQueue = false;
						 if(alertString.contains(" " + "APICallProblems,")){
							 apiCallErrors.put("SkillQueue", apiCallErrors.get("SkillQueue") + 1);
							 if (apiCallErrors.get("SkillQueue") == 4){
								 apiCallErrors.put("SkillQueue", apiCallErrors.get("SkillQueue") + 1);
								 newAlerts.add(0,new Alert("APICallProblems", tempActiveCharIDs.get(i).characterID, "Problems retrieving skill queue", "(BscChar/Wallet/Misc)", "Eve Online's API servers are either having problems or could not be reached.  Alerts concerning your skill queue may be inaccurate until API calls stabilize.", Calendar.getInstance().getTime()));
								 newAlertIDs.add("APICallProblems");
							 }
						 }
					 }
				}else{
					acurSkillQueue=false;
				}
				if (acurSkillQueue){
					curSkillQueue.put(tempActiveCharIDs.get(i).characterID, fcurSkillQueue)  ;
				}

			}
			//Log.d("debugging","4");
			
			
			
			//if (acurSkillInTraining){
			//	curSkillInTraining = fcurSkillInTraining;
			//}
			
			
			
			
			
			
			
			boolean cleanUpPastVars = false;
			
			
			for (int j=0;j<tempActiveCharIDs.size();j++){
				//Log.d("debugging","5");
				String alertString = settings.getString("alertIDs","");
				String walletTransactionString = settings.getString("walletTransactionTypeIDs", "");
				double minIsk = Double.parseDouble(settings.getString("minIsk", "0.0"));
				
				newAlerts.addAll(0,checkForRoomInSkillQueue(tempActiveCharIDs.get(j).characterID,alertString));
				newAlerts.addAll(0,checkForSkillQueueEmpty(tempActiveCharIDs.get(j).characterID,alertString));
				newAlerts.addAll(0,checkForSkillTrainingComplete(tempActiveCharIDs.get(j).characterID,alertString));
				
				newAlerts.addAll(0,checkForCloneOutOfDate(tempActiveCharIDs.get(j).characterID,alertString));
				
				newAlerts.addAll(0,checkForMarketOrderBought(tempActiveCharIDs.get(j).characterID,alertString));
				newAlerts.addAll(0,checkForMarketOrderSold(tempActiveCharIDs.get(j).characterID,alertString));
				newAlerts.addAll(0,checkForMarketOrderExpired(tempActiveCharIDs.get(j).characterID,alertString));
				
				//newAlerts.addAll(0,checkForResearchComplete(tempActiveCharIDs.get(j),alertString));
				
				newAlerts.addAll(0,checkForIndustryJobDelivered(tempActiveCharIDs.get(j).characterID,alertString));
				newAlerts.addAll(0,checkForIndustryJobWorkComplete(tempActiveCharIDs.get(j).characterID,alertString));
				
				newAlerts.addAll(0,checkForWalletTransaction(tempActiveCharIDs.get(j).characterID,alertString, walletTransactionString,minIsk));
				
				newAlerts.addAll(0,checkForCalendarEvent(tempActiveCharIDs.get(j).characterID,alertString));
				
				getNewNotificationIDs(tempActiveCharIDs.get(j).characterID,alertString);
				
				getNewMessageIDs(tempActiveCharIDs.get(j).characterID,alertString);
				
				downloadMailBodies(tempActiveCharIDs.get(j).characterID,tempActiveCharIDs.get(j).keyID, tempActiveCharIDs.get(j).vCode);
				
				downloadNotificationTexts(tempActiveCharIDs.get(j).characterID,tempActiveCharIDs.get(j).keyID, tempActiveCharIDs.get(j).vCode);
				
				newAlerts.addAll(0,checkForNotifications(tempActiveCharIDs.get(j).characterID,tempActiveCharIDs.get(j).keyID,tempActiveCharIDs.get(j).vCode,alertString));
				
				newAlerts.addAll(0,checkForMessages(tempActiveCharIDs.get(j).characterID,tempActiveCharIDs.get(j).keyID,tempActiveCharIDs.get(j).vCode,alertString));
				
				
			}
			
			if (new Date(settings.getLong("lastPastVarCleanUpDate", DateUtil.addHours(new Date(), -25).getTime())).before(DateUtil.addDays(new Date(), -1))){
			//if (new Date(settings.getLong("lastPastVarCleanUpDate", DateUtil.addHours(new Date(), -25).getTime())).before(DateUtil.addHours(new Date(), -1))){
				cleanUpPastVars(tempCharIDs);
				SharedPreferences.Editor ed = settings.edit();
				ed.putLong("lastPastVarCleanUpDate", new Date().getTime());
				ed.apply();
			}
			
			ArrayList<Alert> temp = Alert.getAlertsArrayList(this);
			temp.addAll(0,newAlerts);
			Alert.writeAlertsToFile(temp, this);
			//write past and prev variables to files here
			eveTimeRetrieved = false;
			curMailBodies.clear();
			curNotificationTexts.clear();
			for (int i = 0; i < tempActiveCharIDs.size();i++){
				
			
				if (curSkillQueue.get(tempActiveCharIDs.get(i).characterID) != null){
					 prevSkillQueue.put(tempActiveCharIDs.get(i).characterID, (ArrayList<SkillQueue>) curSkillQueue.get(tempActiveCharIDs.get(i).characterID).clone());
				}
				if (curOrders.get(tempActiveCharIDs.get(i).characterID) != null){
				 prevOrders.put(tempActiveCharIDs.get(i).characterID,(ArrayList<Orders>) curOrders.get(tempActiveCharIDs.get(i).characterID).clone()) ;
				}
				if (curResearch.get(tempActiveCharIDs.get(i).characterID) != null){
				 prevResearch.put(tempActiveCharIDs.get(i).characterID,(ArrayList<Research>) curResearch.get(tempActiveCharIDs.get(i).characterID).clone()) ;
				}
				if (curWalletEntries.get(tempActiveCharIDs.get(i).characterID) != null){
				 prevWalletEntries.put(tempActiveCharIDs.get(i).characterID,(ArrayList<WalletEntries>) curWalletEntries.get(tempActiveCharIDs.get(i).characterID).clone()) ;
				}
				/*if (curSkills != null){
				  prevSkills = (ArrayList<Skills>) curSkills.clone();
				}*/
				if (curCharInfo.get(tempActiveCharIDs.get(i).characterID) != null) {
				 prevCharInfo.put(tempActiveCharIDs.get(i).characterID,(ArrayList<CharInfo>) curCharInfo.get(tempActiveCharIDs.get(i).characterID).clone()) ;
				}
				if (curCloneInfo.get(tempActiveCharIDs.get(i).characterID) != null){
				 prevCloneInfo.put(tempActiveCharIDs.get(i).characterID,(ArrayList<Clone>) curCloneInfo.get(tempActiveCharIDs.get(i).characterID).clone()) ;
				}
			}	
			writePastAndPrevToFiles();
			//Log.d("debugging","done");
		//}
			}
		}
	}
	
	private void cleanUpPastVars(ArrayList<Character> characters){
		//clean up pastSkills once every 24 hours
				
					for (int j = pastSkills.size()-1; j >=0; j--){
						boolean match = false;
						for (int k=0;k<characters.size();k++){
							if (pastSkills.get(j).charID==characters.get(k).characterID){
								if (curSkills.get(characters.get(k).characterID).size()==0){
									match = true;
								}
								for (int i = 0; i < curSkills.get(characters.get(k).characterID).size(); i++){
									if (curSkills.get(characters.get(k).characterID).get(i).typeID == pastSkills.get(j).typeID && pastSkills.get(j).charID == characters.get(k).characterID){
										match= true;
									}
								}
							}
						}
						if(!match){
							pastSkills.remove(j);
						}
					}
					
					
						for (int j = pastMarketOrdersExpired.size()-1; j >=0; j--){
							boolean match = false;
							for (int k=0;k<characters.size();k++){
								if (pastMarketOrdersExpired.get(j).charID==characters.get(k).characterID){
									if (curOrders.get(characters.get(k).characterID).size()==0){
										match = true;
									}
									for (int i = 0; i < curOrders.get(characters.get(k).characterID).size(); i++){
										if (curOrders.get(characters.get(k).characterID).get(i).orderID == pastMarketOrdersExpired.get(j).orderID ){
											match= true;
										}
									}
								}
							}
							if(!match){
								pastMarketOrdersExpired.remove(j);
							}
						}
						
						
							for (int j = pastJobIDsDelivered.size()-1; j >=0; j--){
								boolean match = false;
								for (int k=0;k<characters.size();k++){
									if (pastJobIDsDelivered.get(j).charID==characters.get(k).characterID){
										if (curJobs.get(characters.get(k).characterID).size()==0){
											match = true;
										}
										for (int i = 0; i < curJobs.get(characters.get(k).characterID).size(); i++){
											if (curJobs.get(characters.get(k).characterID).get(i).jobID == pastJobIDsDelivered.get(j).jobID ){
												match= true;
											}
										}
									}
								}
								if(!match){
									pastJobIDsDelivered.remove(j);
								}
							}
							
							
								for (int j = pastJobIDsWorkCompleted.size()-1; j >=0; j--){
									boolean match = false;
									for (int k=0;k<characters.size();k++){
										if (pastJobIDsWorkCompleted.get(j).charID==characters.get(k).characterID){
											if (curJobs.get(characters.get(k).characterID).size()==0){
												match = true;
											}
											for (int i = 0; i < curJobs.get(characters.get(k).characterID).size(); i++){
												if (curJobs.get(characters.get(k).characterID).get(i).jobID == pastJobIDsWorkCompleted.get(j).jobID ){
													match= true;
												}
											}
										}
									}
									if(!match){
										pastJobIDsWorkCompleted.remove(j);
									}
								}
								
								
									for (int j = pastEventIDs.size()-1; j >=0; j--){
										boolean match = false;
										for (int k=0;k<characters.size();k++){
											if (pastEventIDs.get(j).charID==characters.get(k).characterID){
												if (curUpcomingEvents.get(characters.get(k).characterID).size()==0){
													match = true;
												}
												for (int i = 0; i < curUpcomingEvents.get(characters.get(k).characterID).size(); i++){
													if (curUpcomingEvents.get(characters.get(k).characterID).get(i).eventID == pastEventIDs.get(j).eventID ){
														match= true;
													}
												}
											}
										}
										if(!match){
											pastEventIDs.remove(j);
										}
									}
									
									for (int j = pastNotificationIDs.size()-1; j >=0; j--){
										boolean match = false;
										for (int k=0;k<characters.size();k++){
											if (pastNotificationIDs.get(j).charID==characters.get(k).characterID){
												if (curNotifications.get(characters.get(k).characterID).size()==0){
													match = true;
												}
												for (int i = 0; i < curNotifications.get(characters.get(k).characterID).size(); i++){
													if (curNotifications.get(characters.get(k).characterID).get(i).notificationID == pastNotificationIDs.get(j).notificationID ){
														match= true;
													}
												}
											}
										}
										if(!match){
											pastNotificationIDs.remove(j);
										}
									}
									
									for (int j = pastMessageIDs.size()-1; j >=0; j--){
										boolean match = false;
										for (int k=0;k<characters.size();k++){
											if (pastMessageIDs.get(j).charID==characters.get(k).characterID){
												if (curMessages.get(characters.get(k).characterID).size()==0){
													match = true;
												}
												for (int i = 0; i < curMessages.get(characters.get(k).characterID).size(); i++){
													if (curMessages.get(characters.get(k).characterID).get(i).messageID == pastMessageIDs.get(j).messageID ){
														match= true;
													}
												}
											}
										}
										if(!match){
											pastMessageIDs.remove(j);
										}
									}
								
							
						
					
				
	}
	
	private void readPastAndPrevFromFiles() throws IOException, ClassNotFoundException{
		FileInputStream fileIn;
		ObjectInputStream in;
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"charIDBaseLoadedHM");
			in = new ObjectInputStream(fileIn);
			charIDBaseLoadedHM = (HashMap<Long,String>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			for (int i = 0; i < charIDs.size();i++){
				charIDBaseLoadedHM.put(charIDs.get(i).characterID, "");
			}
		}
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"prevSkillQueue");
			in = new ObjectInputStream(fileIn);
			prevSkillQueue = (HashMap<Long,ArrayList<SkillQueue>>) in.readObject();
			curSkillQueue = (HashMap<Long,ArrayList<SkillQueue>>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("SkillQueue,",""));
		    }
		}
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"prevOrders");
			in = new ObjectInputStream(fileIn);
			prevOrders = (HashMap<Long,ArrayList<Orders>>) in.readObject();
			curOrders = (HashMap<Long,ArrayList<Orders>>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("Orders,",""));
		    }
		}
		
		
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"prevResearch");
			in = new ObjectInputStream(fileIn);
			prevResearch = (HashMap<Long,ArrayList<Research>>) in.readObject();
			curResearch = (HashMap<Long,ArrayList<Research>>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("Research,",""));
		    }
		}
		
	
		
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"prevWalletEntries");
			in = new ObjectInputStream(fileIn);
			prevWalletEntries = (HashMap<Long,ArrayList<WalletEntries>>) in.readObject();
			curWalletEntries = (HashMap<Long,ArrayList<WalletEntries>>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("WalletEntries,",""));
		    }
		}
		
		
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"prevCharInfo");
			in = new ObjectInputStream(fileIn);
			prevCharInfo = (HashMap<Long,ArrayList<CharInfo>>) in.readObject();
			curCharInfo = (HashMap<Long,ArrayList<CharInfo>>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("CharacterInfo,",""));
		    }
		}
		
		
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"prevCloneInfo");
			in = new ObjectInputStream(fileIn);
			prevCloneInfo = (HashMap<Long,ArrayList<Clone>>) in.readObject();
			curCloneInfo = (HashMap<Long,ArrayList<Clone>>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("Clone,",""));
		    }
		}
		
		
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"pastEventIDs");
			in = new ObjectInputStream(fileIn);
			pastEventIDs = (ArrayList<UpcomingEvents>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("UpcomingEvents,",""));
		    }
		}
		
		
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"pastNotificationIDs");
			in = new ObjectInputStream(fileIn);
			pastNotificationIDs = (ArrayList<Notifications>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("Notifications,",""));
		    }
		}
		
		
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"pastJobIDsDelivered");
			in = new ObjectInputStream(fileIn);
			pastJobIDsDelivered = (ArrayList<Jobs>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("Jobs,",""));
		    }
		}
		
		
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"pastJobIDsWorkCompleted");
			in = new ObjectInputStream(fileIn);
			pastJobIDsWorkCompleted = (ArrayList<Jobs>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("Jobs,",""));
		    }
		}
		
		
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"pastMarketOrdersExpired");
			in = new ObjectInputStream(fileIn);
			pastMarketOrdersExpired = (ArrayList<Orders>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("Orders,",""));
		    }
		}
		
		
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"pastMessageIDs");
			in = new ObjectInputStream(fileIn);
			pastMessageIDs = (ArrayList<Messages>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("Messages,",""));
		    }
		}
		
		
		
		try{
			fileIn = new FileInputStream(this.getFilesDir()+File.separator+"pastSkills");
			in = new ObjectInputStream(fileIn);
			pastSkills = (ArrayList<Skills>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e){
			Iterator it = charIDBaseLoadedHM.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry<Long,String> pairs = (HashMap.Entry<Long,String>)it.next();
		        pairs.setValue(pairs.getValue().replace("CharacterSheet,",""));
		    }
		}
		
		
		
	}
	
	private void writePastAndPrevToFiles() throws IOException{
		FileOutputStream fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"charIDBaseLoadedHM");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(charIDBaseLoadedHM);
		out.close();
		fileOut.close();
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"prevSkillQueue");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(prevSkillQueue);
		out.close();
		fileOut.close();
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"prevOrders");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(prevOrders);
		out.close();
		fileOut.close();
	
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"prevResearch");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(prevResearch);
		out.close();
		fileOut.close();
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"prevWalletEntries");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(prevWalletEntries);
		out.close();
		fileOut.close();
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"prevCharInfo");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(prevCharInfo);
		out.close();
		fileOut.close();
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"prevCloneInfo");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(prevCloneInfo);
		out.close();
		fileOut.close();
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"pastEventIDs");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(pastEventIDs);
		out.close();
		fileOut.close();
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"pastNotificationIDs");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(pastNotificationIDs);
		out.close();
		fileOut.close();
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"pastJobIDsDelivered");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(pastJobIDsDelivered);
		out.close();
		fileOut.close();
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"pastJobIDsWorkCompleted");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(pastJobIDsWorkCompleted);
		out.close();
		fileOut.close();
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"pastMarketOrdersExpired");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(pastMarketOrdersExpired);
		out.close();
		fileOut.close();
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"pastMessageIDs");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(pastMessageIDs);
		out.close();
		fileOut.close();
		
		
		fileOut = new FileOutputStream(this.getFilesDir()+File.separator+"pastSkills");
		out = new ObjectOutputStream(fileOut);
		out.writeObject(pastSkills);
		out.close();
		fileOut.close();
	}
	
	private boolean IsActiveChar(long charID){
		ArrayList<Character> characters = Character.readActiveCharacterIDs(this);
		for (int i = 0; i < characters.size(); i++){
			if (characters.get(i).characterID == charID){
				return true;
			}
		}
		return false;
	}
	
	private void sendNotification(ArrayList<String> newAlertIDs, boolean vibrate, boolean sound) throws IOException, ParseException{
		//if (!isFirstRun){
		ArrayList<Alert> alerts = Alert.getAlertsArrayList(this);
		ArrayList<Alert> archivedAlerts = Alert.getArchivedAlertsArrayList(this);
		//alerts.add(new Alert("test", 91235773, "test", "cat","message", Calendar.getInstance().getTime()));
			if (newAlertIDs.size() > 0){
				while (alerts.size() + archivedAlerts.size() > 200){
					if (archivedAlerts.size() > 0){
						archivedAlerts.remove(archivedAlerts.size()-1);
					} else {
						alerts.remove(alerts.size() -1);
					}
				}
				Alert.writeAlertsToFile(alerts, this);
				Alert.writeArchivedAlertsToFile(archivedAlerts, this);
				if (alerts.size() > 0 ){
					NotificationCompat.Builder mBuilder =
					        new NotificationCompat.Builder(this)
					        .setSmallIcon(R.drawable.evealerticon64)
					        .setContentTitle("You have " + alerts.size() +" new alerts!")
					        //.setContentText("You have " + alerts.size() +" new alerts!")
					        .setAutoCancel(true);
					
					
					String text = "\"" + alerts.get(0).title + "\"";
					if (alerts.size() > 1){
						text += " and " + (alerts.size() -1) + " more new alerts." ;
					}
					mBuilder.setContentText(text);
					
					if (vibrate && sound){
						mBuilder.setDefaults(Notification.DEFAULT_ALL);
					} else if (vibrate){
						mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
					} else if (sound ) {
						mBuilder.setDefaults(Notification.DEFAULT_SOUND);
					}
					
					NotificationCompat.InboxStyle inboxStyle =
					        new NotificationCompat.InboxStyle();
					//String[] events = new String[6];
					// Sets a title for the Inbox style big view
					inboxStyle.setBigContentTitle("You have " + alerts.size() +" new alerts!");
					//...
					// Moves events into the big view
					for (int i=0; i < alerts.size(); i++) {
						if (i < 7){
							inboxStyle.addLine(alerts.get(i).title);
						}
					}
					if (alerts.size() > 7){
						inboxStyle.setSummaryText((alerts.size() - 7) + " more alerts...");
					}
					// Moves the big view style object into the notification object.
					mBuilder.setStyle(inboxStyle); 
					//...
					// Issue the notification here.
					// Creates an explicit intent for an Activity in your app
					Intent resultIntent = new Intent(this, NewAlerts.class);
					
					// The stack builder object will contain an artificial back stack for the
					// started Activity.
					// This ensures that navigating backward from the Activity leads out of
					// your application to the Home screen. 
					//TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
					// Adds the back stack for the Intent (but not the Intent itself)
					//stackBuilder.addParentStack(NewAlerts.class);
					// Adds the Intent that starts the Activity to the top of the stack
					//stackBuilder.addNextIntent(resultIntent);
					//PendingIntent resultPendingIntent =
					        //stackBuilder.getPendingIntent(
					            //0,
					            //PendingIntent.FLAG_UPDATE_CURRENT
					        //);
					Intent notificationIntent = new Intent(this , NewAlerts.class);
					notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(this , 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
					mBuilder.setContentIntent(contentIntent);
					NotificationManager mNotificationManager =
					    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					// mId allows you to update the notification later on.
					mNotificationManager.notify(1, mBuilder.build());
				}
				
			}
		/*} else {
			alerts.clear();
			isFirstRun=false;
		}*/
		
	}
	
	/*public static ArrayList<Alert> getAlerts(){
		return (ArrayList<Alert>) alerts.clone();
	}
	public static ArrayList<Alert> getArchivedAlerts(){
		return (ArrayList<Alert>) archivedAlerts.clone();
	}*/
	
	private ArrayList<Long> getPastMarketOrdersExpiredAL(){
		ArrayList<Long> al = new ArrayList<Long>();
		for (int i = 0; i < pastMarketOrdersExpired.size();i++){
			al.add(pastMarketOrdersExpired.get(i).orderID);
		}
		return al;
	}
	
	private ArrayList<Long> getPastIndustryJobsDeliveredAL(){
		ArrayList<Long> al = new ArrayList<Long>();
		for (int i = 0; i < pastJobIDsDelivered.size();i++){
			al.add(pastJobIDsDelivered.get(i).jobID);
		}
		return al;
	}
	
	private ArrayList<Long> getPastIndustryJobsCompletedAL(){
		ArrayList<Long> al = new ArrayList<Long>();
		for (int i = 0; i < pastJobIDsWorkCompleted.size();i++){
			al.add(pastJobIDsWorkCompleted.get(i).jobID);
		}
		return al;
	}
	
	private ArrayList<Long> getPastEventAL(){
		ArrayList<Long> al = new ArrayList<Long>();
		for (int i = 0; i < pastEventIDs.size();i++){
			al.add(pastEventIDs.get(i).eventID);
		}
		return al;
	}
	
	private ArrayList<Long> getPastNotificationsAL(){
		ArrayList<Long> al = new ArrayList<Long>();
		for (int i = 0; i < pastNotificationIDs.size();i++){
			al.add(pastNotificationIDs.get(i).notificationID);
		}
		return al;
	}
	
	private ArrayList<Long> getPastMessagesAL(){
		ArrayList<Long> al = new ArrayList<Long>();
		for (int i = 0; i < pastMessageIDs.size();i++){
			al.add(pastMessageIDs.get(i).messageID);
		}
		return al;
	}
	
	
	public ArrayList<Alert> checkForRoomInSkillQueue(long charID, String alertString) throws URISyntaxException, ClientProtocolException, IOException{
		//https://api.eveonline.com/char/SkillQueue.xml.aspx
		Date tempDate = DateUtil.addDays(new Date(), 1);
		Date endDate = DateUtil.addDays(new Date(), 7);
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		boolean warnedAboutRoomInSkillQueue = false;
		if (curSkillQueue.get(charID).size() > 0){
			for (int j=0; j < curSkillQueue.get(charID).size();j++){
				if (curSkillQueue.get(charID).get(j).charID == charID){
					endDate = curSkillQueue.get(charID).get(j).endTime;
				}
			}
			if (endDate.before(tempDate)){
				for (int i = 0; i < charIDsWarnedAboutRoomInSkillQueue.size();i++){
					if (charIDsWarnedAboutRoomInSkillQueue.get(i) == charID){
						warnedAboutRoomInSkillQueue = true;
					}
				}
				if (warnedAboutRoomInSkillQueue==false){
					if (alertString.contains(" " + "roomInSkillQueue,") && IsActiveChar(charID)){
						if (charIDBaseLoadedHM.get(charID).contains("SkillQueue,")){
							alerts.add(0,new Alert("roomInSkillQueue", charID, "There is room available in your skill queue.", "(BscChar/Wallet/Misc)", "", Calendar.getInstance().getTime()));
							newAlertIDs.add("roomInSkillQueue");
							charIDsWarnedAboutRoomInSkillQueue.add(charID);
						}
						
					}
				}
				
			} else {
				for (int i = 0; i < charIDsWarnedAboutRoomInSkillQueue.size();i++){
					if (charIDsWarnedAboutRoomInSkillQueue.get(i) == charID){
						charIDsWarnedAboutRoomInSkillQueue.remove(i);
					}
				}
				
			}
		}
        
        
		return alerts;
		
	}
	
	public ArrayList<Alert> checkForSkillQueueEmpty(long charID, String alertString){
		boolean empty=true;
		boolean warnedAboutSkillQueueEmpty = false;
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		for (int i = 0; i < curSkillQueue.get(charID).size();i++){
			if (curSkillQueue.get(charID).get(i).charID == charID){
				empty = false;
			}
		}
		if (empty){
			for (int i = 0; i < charIDsWarnedAboutSkillQueueEmpty.size();i++){
				if (charIDsWarnedAboutSkillQueueEmpty.get(i) == charID){
					warnedAboutSkillQueueEmpty = true;
				}
			}
			if (warnedAboutSkillQueueEmpty==false){
				if (alertString.contains(" " + "skillQueueEmpty,") && IsActiveChar(charID)){
					if (charIDBaseLoadedHM.get(charID).contains("SkillQueue,")){
						alerts.add(0,new Alert("skillQueueEmpty", charID,"Your skill queue is empty.",  "(BscChar/Wallet/Misc)","", Calendar.getInstance().getTime()));
						newAlertIDs.add("skillQueueEmpty");
						charIDsWarnedAboutSkillQueueEmpty.add(charID);
					}
					
				}
			}

			
		} else {
			for (int i = 0; i < charIDsWarnedAboutSkillQueueEmpty.size();i++){
				if (charIDsWarnedAboutSkillQueueEmpty.get(i) == charID){
					charIDsWarnedAboutSkillQueueEmpty.remove(i);
				}
			}
			
		}

		return alerts;
	}
	
	public ArrayList<Alert> checkForSkillTrainingComplete(long charID, String alertString){
		//alerts contains any "skill complete" events detected
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		//loop through each of the skills possessed by the character from the latest API grab
		for (int i = 0;i<curSkills.get(charID).size();i++){
			//match variable designates whether the skill currently being examined in the curSkills list has a 
			//counterpart in the pastSkills list
			boolean match = false;
			boolean skillWasPresent = false;
			//loop through each of the skills possessed by the character from the previous API grab
			for (int j=0;j<pastSkills.size();j++){
				//if the skill id from the current skill list matches the skill id of the previous skill list
				//and both skills' character ids match the current character, then set the match value to true
				if (curSkills.get(charID).get(i).typeID == pastSkills.get(j).typeID && curSkills.get(charID).get(i).charID == charID && pastSkills.get(j).charID == charID ){
					match = true;
				}
				//if a match was found between the two skill lists and the current skill level is higher than the previous
				//skill level, then the character has learned a new level of the skill
				if (match && curSkills.get(charID).get(i).level > pastSkills.get(j).level){
					//if the app user has chosen to receive alerts about skill completion, and the character
					//is also designated as an "active" character, then we want to send an alert
					if (alertString.contains(" " + "skillComplete,") && IsActiveChar(charID)){
						//if EveWatch app has prviously successfully loaded the CharacterSheet data from the 
						//Eve Online API, then we proceed with recording the alert.  This stops the app
						//from reporting hundreds of skill complete messages the first time the app grabs api data
						if (charIDBaseLoadedHM.get(charID).contains("CharacterSheet,")){
							//stores the alert
							alerts.add(0,new Alert("skillComplete", charID ,"Skill training complete: " + getSkillName(curSkills.get(charID).get(i).typeID) + " " + curSkills.get(charID).get(i).level,  "(BscChar/Wallet/Misc)", "",Calendar.getInstance().getTime()));
							//track the "type of alert" because the app lets users receive different types of notifications based on 
							//what types of alerts are detected.
							//for example, in one batch of alerts, there is a skill complete alert, and a market order alert.
							//user has specified that he wants sound notifications for market orders, but would prefer silent notifications
							//for skill complete.  In this case, the sound notification for market order would override the silent notification
							newAlertIDs.add("skillComplete");
						}
					}
					//store the new level of the skill in the pastSkills datastructure
					pastSkills.get(j).level = curSkills.get(charID).get(i).level;
				}
				//if at this point, we know that both skill lists contained the skill, we can break out of the function
				if (match){
					break;
				}
			}
			//if the skill that is currently being examined was not on the previous skill list then there are two possibilities
			//one: the user has "injected" the skill so he has the ability to train it, but has not actually completed the first level of it
			//two: the user has "injected" the skill AND he has also learned at least the first level of it
			//so here we check that there was no match for this particular skill betweeen the current and past version of the skill lists
			//and we also check to make sure that the level of the skill is greater than 0.
			//we have no interest in alerting about a skill just being injected without level 1 being learned because
			//the player has to actively inject the skill and would not need to be told about something he just actively did
			if (match == false && curSkills.get(charID).get(i).level > 0 && curSkills.get(charID).get(i).charID == charID){
				//if the app user has chosen to receive alerts about skill completion, and the character
				//is also designated as an "active" character, then we want to send an alert
				if (alertString.contains(" " + "skillComplete,") && IsActiveChar(charID)){
					//if EveWatch app has prviously successfully loaded the CharacterSheet data from the 
					//Eve Online API, then we proceed with recording the alert.  This stops the app
					//from reporting hundreds of skill complete messages the first time the app grabs api data
					if (charIDBaseLoadedHM.get(charID).contains("CharacterSheet,")){
						//stores the alert
						alerts.add(0,new Alert("skillComplete", charID, "Skill training complete: " + getSkillName(curSkills.get(charID).get(i).typeID) + " " + curSkills.get(charID).get(i).level,  "(BscChar/Wallet/Misc)", "",Calendar.getInstance().getTime()));
						newAlertIDs.add("skillComplete");
					}
				}
				pastSkills.add(new Skills(charID, curSkills.get(charID).get(i).typeID, curSkills.get(charID).get(i).level));
			}
		}
		
		//return all "skill complete" events to be merged with all other alerts detected during this batch
		return alerts;
	}
	
	public ArrayList<Alert> checkForMarketOrderBought(long charID, String alertString){
		//https://api.eveonline.com/char/MarketOrders.xml.aspx
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		for (int i=0;i<curOrders.get(charID).size();i++){
			boolean newOrder=true;
			long prevRemainingOrders = 0;
			for (int j=0;j<prevOrders.get(charID).size();j++){
				if (curOrders.get(charID).get(i).orderID == prevOrders.get(charID).get(j).orderID && curOrders.get(charID).get(i).volRemaining == prevOrders.get(charID).get(j).volRemaining ){
					newOrder=false;
					
				} else if (curOrders.get(charID).get(i).orderID == prevOrders.get(charID).get(j).orderID) {
					prevRemainingOrders = prevOrders.get(charID).get(j).volRemaining;
				}
			}
			if (prevRemainingOrders == 0){
				prevRemainingOrders = curOrders.get(charID).get(i).volEntered ;
			}
			if (newOrder && curOrders.get(charID).get(i).bid==1 && curOrders.get(charID).get(i).volEntered != curOrders.get(charID).get(i).volRemaining && curOrders.get(charID).get(i).volRemaining > 0 && curOrders.get(charID).get(i).charID == charID){
				if (alertString.contains(" " + "orderBought,") && IsActiveChar(charID)){
					if (charIDBaseLoadedHM.get(charID).contains("Orders,")){
						alerts.add(0,new Alert("orderBought", charID, "You bought " + (prevRemainingOrders - curOrders.get(charID).get(i).volRemaining)  + " units of " + getItemName(curOrders.get(charID).get(i).typeID) + ".",  "(BscChar/Wallet/Misc)", curOrders.get(charID).get(i).volRemaining + " units in your buy order remain unfulfilled.",Calendar.getInstance().getTime()));
						newAlertIDs.add("orderBought");
					}
				}
			}
			
		}
		return alerts;
	}
	
	public ArrayList<Alert> checkForMarketOrderSold(long charID, String alertString){
		//https://api.eveonline.com/char/MarketOrders.xml.aspx
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		for (int i=0;i<curOrders.get(charID).size();i++){
			boolean newOrder=true;
			long prevRemainingOrders = 0;
			for (int j=0;j<prevOrders.get(charID).size();j++){
				if (curOrders.get(charID).get(i).orderID == prevOrders.get(charID).get(j).orderID && curOrders.get(charID).get(i).volRemaining == prevOrders.get(charID).get(j).volRemaining ){
					newOrder=false;
					
				} else if (curOrders.get(charID).get(i).orderID == prevOrders.get(charID).get(j).orderID) {
					prevRemainingOrders = prevOrders.get(charID).get(j).volRemaining;
				}
			}
			if (prevRemainingOrders == 0){
				prevRemainingOrders = curOrders.get(charID).get(i).volEntered ;
			}
			if (newOrder && curOrders.get(charID).get(i).bid==0 && curOrders.get(charID).get(i).volEntered != curOrders.get(charID).get(i).volRemaining && curOrders.get(charID).get(i).volRemaining > 0 && curOrders.get(charID).get(i).charID == charID){
				if (alertString.contains(" " + "orderSold,") && IsActiveChar(charID)){
					if (charIDBaseLoadedHM.get(charID).contains("Orders,")){
						alerts.add(0,new Alert("orderSold", charID, "You sold " + (prevRemainingOrders - curOrders.get(charID).get(i).volRemaining)  + " units of " + getItemName(curOrders.get(charID).get(i).typeID) + "." ,"(BscChar/Wallet/Misc)", curOrders.get(charID).get(i).volRemaining + " units in your sell order remain unfulfilled.",Calendar.getInstance().getTime()));
						newAlertIDs.add("orderSold");
					}
				}
			}
		}
		return alerts;
	}
	
	public ArrayList<Alert> checkForMarketOrderExpired(long charID, String alertString){
		//https://api.eveonline.com/char/MarketOrders.xml.aspx
		//Log.d("debugging",alertString);
		if (!charIDBaseLoadedHM.get(charID).contains("Orders,")){
			for (int i = 0; i < curOrders.get(charID).size(); i++){
				if (curOrders.get(charID).get(i).orderState != 0 && !getPastMarketOrdersExpiredAL().contains(curOrders.get(charID).get(i).orderID)){
					pastMarketOrdersExpired.add(new Orders(charID,curOrders.get(charID).get(i).orderID,curOrders.get(charID).get(i).issued,curOrders.get(charID).get(i).bid,curOrders.get(charID).get(i).duration,curOrders.get(charID).get(i).orderState,curOrders.get(charID).get(i).volEntered,curOrders.get(charID).get(i).volRemaining,curOrders.get(charID).get(i).typeID));
				}
			}
		}
		
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		for (int i=0;i<curOrders.get(charID).size();i++){
			if (DateUtil.addDays(curOrders.get(charID).get(i).issued, curOrders.get(charID).get(i).duration).before(new Date()) && curOrders.get(charID).get(i).volRemaining > 0 && !(getPastMarketOrdersExpiredAL().contains(curOrders.get(charID).get(i).orderID))){
				if ( curOrders.get(charID).get(i).charID == charID){
					if (alertString.contains(" " + "orderExpired,") && IsActiveChar(charID)){
						if (charIDBaseLoadedHM.get(charID).contains("Orders,")){
							if (curOrders.get(charID).get(i).bid==0){
								alerts.add(0,new Alert("orderExpired", charID, "Your market order selling units of " + getItemName(curOrders.get(charID).get(i).typeID) + " has been fulfilled, cancelled, or has expired." ,"(BscChar/Wallet/Misc)", "",Calendar.getInstance().getTime()));
								newAlertIDs.add("orderExpired");
							} else if (curOrders.get(charID).get(i).bid==1){
								alerts.add(0,new Alert("orderExpired", charID, "Your market order buying units of " + getItemName(curOrders.get(charID).get(i).typeID) + " has been fulfilled, cancelled, or has expired." ,"(BscChar/Wallet/Misc)", "",Calendar.getInstance().getTime()));
								newAlertIDs.add("orderExpired");
							}
							
						}
					}
					pastMarketOrdersExpired.add(new Orders(charID,curOrders.get(charID).get(i).orderID,curOrders.get(charID).get(i).issued,curOrders.get(charID).get(i).bid,curOrders.get(charID).get(i).duration,curOrders.get(charID).get(i).orderState,curOrders.get(charID).get(i).volEntered,curOrders.get(charID).get(i).volRemaining,curOrders.get(charID).get(i).typeID));
				}
			}
			for (int j=0;j<prevOrders.get(charID).size();j++){
				if (curOrders.get(charID).get(i).orderID == prevOrders.get(charID).get(j).orderID && curOrders.get(charID).get(i).orderState != prevOrders.get(charID).get(j).orderState && curOrders.get(charID).get(i).orderState != 0 && !(getPastMarketOrdersExpiredAL().contains(curOrders.get(charID).get(i).orderID))){
					if ( curOrders.get(charID).get(i).charID == charID){
						if (alertString.contains(" " + "orderExpired,") && IsActiveChar(charID)){
							if (charIDBaseLoadedHM.get(charID).contains("Orders,")){
								if (curOrders.get(charID).get(i).bid==0){
									alerts.add(0,new Alert("orderExpired", charID, "Your market order selling units of " + getItemName(curOrders.get(charID).get(i).typeID) + " has been fulfilled, cancelled, or has expired." ,"(BscChar/Wallet/Misc)", "",Calendar.getInstance().getTime()));
									newAlertIDs.add("orderExpired");
								} else if (curOrders.get(charID).get(i).bid==1){
									alerts.add(0,new Alert("orderExpired", charID, "Your market order buying units of " + getItemName(curOrders.get(charID).get(i).typeID) + " has been fulfilled, cancelled, or has expired." ,"(BscChar/Wallet/Misc)", "",Calendar.getInstance().getTime()));
									newAlertIDs.add("orderExpired");
								}
								
							}
						}
						pastMarketOrdersExpired.add(new Orders(charID,curOrders.get(charID).get(i).orderID,curOrders.get(charID).get(i).issued,curOrders.get(charID).get(i).bid,curOrders.get(charID).get(i).duration,curOrders.get(charID).get(i).orderState,curOrders.get(charID).get(i).volEntered,curOrders.get(charID).get(i).volRemaining,curOrders.get(charID).get(i).typeID));
					}
					
				
				}
			}
			
			
		}
		
		
		return alerts;
	}
	
	//public boolean checkForAddedAsContact(){
		//https://api.eveonline.com/char/ContactNotifications.xml.aspx
	//	return false;
	//}
	

	
	public ArrayList<Alert> checkForCloneOutOfDate(long charID, String alertString){
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		boolean outOfDate=false;
		boolean warnedAboutCloneOutOfDate = false;
		for (int i = 0; i < curCharInfo.get(charID).size();i++){
			if (curCharInfo.get(charID).get(i).charID == charID){
				for (int j = 0; j< curCloneInfo.get(charID).size();j++){
					if (curCloneInfo.get(charID).get(j).charID == charID){
						if (curCharInfo.get(charID).get(i).skillPoints > curCloneInfo.get(charID).get(i).skillPoints){
							outOfDate = true;
						}
					}
				}
				
			}
		}
		if (charIDBaseLoadedHM.get(charID).contains("CharacterInfo,") && charIDBaseLoadedHM.get(charID).contains("Clone,")){
			if (outOfDate){
				for (int i = 0; i < charIDsWarnedAboutCloneOutOfDate.size();i++){
					if (charIDsWarnedAboutCloneOutOfDate.get(i) == charID){
						warnedAboutCloneOutOfDate = true;
					}
				}
				if (warnedAboutCloneOutOfDate==false){
					if (alertString.contains(" " + "cloneOutOfDate,") && IsActiveChar(charID)){
						
							alerts.add(0,new Alert("cloneOutOfDate", charID,"Your clone is out of date.",  "(BscChar/Wallet/Misc)","",Calendar.getInstance().getTime()));
							newAlertIDs.add("cloneOutOfDate");
						
						charIDsWarnedAboutCloneOutOfDate.add(charID);
					}
				}
	
				
			}  else {
				for (int i = 0; i < charIDsWarnedAboutCloneOutOfDate.size();i++){
					if (charIDsWarnedAboutCloneOutOfDate.get(i) == charID){
						charIDsWarnedAboutCloneOutOfDate.remove(i);
					}
				}
				
			}
		}

		
		return alerts;
	}
	
	public ArrayList<Alert> checkForResearchComplete(long charID, String alertString){
		//https://api.eveonline.com/char/Research.xml.aspx
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		for (int i=0;i<curResearch.get(charID).size();i++){
			boolean newResearch = true;
			
			for (int j=0;j<prevResearch.get(charID).size();j++){
				if (curResearch.get(charID).get(i).agentID == prevResearch.get(charID).get(j).agentID){
					newResearch=false;
				} 
			}
			if (newResearch && curResearch.get(charID).get(i).charID == charID){
				if (alertString.contains(" " + "researchComplete,") && IsActiveChar(charID)){
					if (charIDBaseLoadedHM.get(charID).contains("Research,")){
						ArrayList agentSearchResults = getAgentName(curResearch.get(charID).get(i).agentID);
						alerts.add(0,new Alert("researchComplete", charID, "Research has either completed or been canceled at " + getStationName((Integer)agentSearchResults.get(1)) + " with agent: " + agentSearchResults.get(0) + ".",  "(BscChar/Wallet/Misc)","",Calendar.getInstance().getTime()));
						newAlertIDs.add("researchComplete");
					}
				}
			}
		}
		return alerts;
	}
	
	public ArrayList<Alert> checkForIndustryJobDelivered(long charID, String alertString){
		//https://api.eveonline.com/char/IndustryJobs.xml.aspx
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		for (int i=0;i<curJobs.get(charID).size();i++){
			boolean newJob = true;
			
			for (int j=0;j<pastJobIDsDelivered.size();j++){
				if (curJobs.get(charID).get(i).jobID == pastJobIDsDelivered.get(j).jobID){
					newJob=false;
				} 
			}
			if (newJob && (curJobs.get(charID).get(i).completedSuccessfully == 1 || curJobs.get(charID).get(i).completed == 1) && curJobs.get(charID).get(i).charID == charID){
				if (alertString.contains(" " + "jobDelivered,") && IsActiveChar(charID)){
					if (charIDBaseLoadedHM.get(charID).contains("Jobs,")){
						if (curJobs.get(charID).get(i).completedStatus == 1){
							alerts.add(0,new Alert("jobDelivered", charID, "Your industry job concerning '" + getItemName(curJobs.get(charID).get(i).outputTypeID) +"' that was installed in " + getSystemName(curJobs.get(charID).get(i).outputLocationID) + " has been delivered.",  "(BscChar/Wallet/Misc)","",Calendar.getInstance().getTime()));
						} else {
							alerts.add(0,new Alert("jobDelivered", charID, "Your industry job concerning '" + getItemName(curJobs.get(charID).get(i).outputTypeID) +"' that was installed in " + getSystemName(curJobs.get(charID).get(i).outputLocationID) + " has been completed unsuccessfully.",  "(BscChar/Wallet/Misc)","",Calendar.getInstance().getTime()));
						}
						newAlertIDs.add("jobDelivered");
					}
				}
				pastJobIDsDelivered.add(new Jobs(charID,curJobs.get(charID).get(i).outputLocationID,curJobs.get(charID).get(i).outputTypeID,curJobs.get(charID).get(i).completed,curJobs.get(charID).get(i).completedSuccessfully,curJobs.get(charID).get(i).jobID,curJobs.get(charID).get(i).endProductionTime,curJobs.get(charID).get(i).pauseProductionTime,curJobs.get(charID).get(i).completedStatus));
				
			}
		}
		
		
		return alerts;
	}
	
	public ArrayList<Alert> checkForIndustryJobWorkComplete(long charID, String alertString){
		//https://api.eveonline.com/char/IndustryJobs.xml.aspx
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		for (int i=0;i<curJobs.get(charID).size();i++){
			boolean newJob = true;
			
			for (int j=0;j<pastJobIDsWorkCompleted.size();j++){
				if (curJobs.get(charID).get(i).jobID == pastJobIDsWorkCompleted.get(j).jobID){
					newJob=false;
				} 
			}
			if (newJob && curJobs.get(charID).get(i).endProductionTime.before(new Date()) && curJobs.get(charID).get(i).pauseProductionTime.equals("0001-01-01 00:00:00") && curJobs.get(charID).get(i).charID == charID && curJobs.get(charID).get(i).completedStatus != 2 && curJobs.get(charID).get(i).completedStatus != 3 && curJobs.get(charID).get(i).completedStatus != 4 && curJobs.get(charID).get(i).completedStatus != 5){
				if (alertString.contains(" " + "jobWorkComplete,") && IsActiveChar(charID)){
					if (charIDBaseLoadedHM.get(charID).contains("Jobs,")){
						alerts.add(0,new Alert("jobWorkComplete", charID, "Work has completed on your industry job concerning '" + getItemName(curJobs.get(charID).get(i).outputTypeID) +"' that was installed in " + getSystemName(curJobs.get(charID).get(i).outputLocationID) + ".  It may now be delivered.",  "(BscChar/Wallet/Misc)","",Calendar.getInstance().getTime()));
						newAlertIDs.add("jobWorkComplete");
					}
				}
				pastJobIDsWorkCompleted.add(new Jobs(charID,curJobs.get(charID).get(i).outputLocationID,curJobs.get(charID).get(i).outputTypeID,curJobs.get(charID).get(i).completed,curJobs.get(charID).get(i).completedSuccessfully,curJobs.get(charID).get(i).jobID,curJobs.get(charID).get(i).endProductionTime,curJobs.get(charID).get(i).pauseProductionTime,curJobs.get(charID).get(i).completedStatus));
				
			}
		}
		
		
		return alerts;
	}
	
	public ArrayList<Alert> checkForWalletTransaction(long charID, String alertString, String walletTransactionString, double minIsk){
		//https://api.eveonline.com/char/WalletJournal.xml.aspx
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		for (int i=0;i<curWalletEntries.get(charID).size();i++){
			boolean newEntry = true;
			for (int j=0;j<prevWalletEntries.get(charID).size();j++){
				if (curWalletEntries.get(charID).get(i).refID == prevWalletEntries.get(charID).get(j).refID && prevWalletEntries.get(charID).get(j).charID == charID){
					newEntry=false;
				} 
			}
			double thisIsk = Math.abs(curWalletEntries.get(charID).get(i).amount);
			String thisrefTypeID=String.valueOf(curWalletEntries.get(charID).get(i).refTypeID + 1000);
			if (newEntry && curWalletEntries.get(charID).get(i).charID == charID && Math.abs(curWalletEntries.get(charID).get(i).amount) >= minIsk){
				if (alertString.contains(" " + "walletEntry,") && walletTransactionString.contains(" " + (curWalletEntries.get(charID).get(i).refTypeID + 1000) +",") && IsActiveChar(charID)){
					String name1 = curWalletEntries.get(charID).get(i).ownerName1;
					String name2 = curWalletEntries.get(charID).get(i).ownerName2;
					if (name1.equals("") || name1 == null){
						name1 = "Unknown Player (currently unavailable in Eve API)";
					}
					if (name2.equals("") || name1 == null){
						name2 = "Unknown Player (currently unavailable in Eve API)";
					}
					if (charIDBaseLoadedHM.get(charID).contains("WalletEntries,")){
						alerts.add(0,new Alert("walletEntry", charID, getWalletTransactionDescription(curWalletEntries.get(charID).get(i).refTypeID + 1000) + ": " + name1 + " has sent " + String.format("%1$,.2f",Math.abs(curWalletEntries.get(charID).get(i).amount)) + " isk to " + name2 + ".",  "(BscChar/Wallet/Misc)","",Calendar.getInstance().getTime()));
						newAlertIDs.add("walletEntry");
					}
				}
			}
		}

		return alerts;
		
	}
	
	public ArrayList<Alert> checkForCalendarEvent(long charID, String alertString){
		//https://api.eveonline.com/char/UpcomingCalendarEvents.xml.aspx
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		for (int i=0;i<curUpcomingEvents.get(charID).size();i++){
			if(curUpcomingEvents.get(charID).get(i).eventDate.before((new Date()))){
				if (!getPastEventAL().contains(curUpcomingEvents.get(charID).get(i).eventID)){
					pastEventIDs.add(new UpcomingEvents(charID,curUpcomingEvents.get(charID).get(i).eventID,curUpcomingEvents.get(charID).get(i).eventTitle,curUpcomingEvents.get(charID).get(i).eventDate));
				}
			}
			boolean newEvent = true;
			for (int j=0;j<pastEventIDs.size();j++){
				if (curUpcomingEvents.get(charID).get(i).eventID == pastEventIDs.get(j).eventID){
					newEvent=false;
				} 
			}

			if (newEvent && curUpcomingEvents.get(charID).get(i).eventDate.before(DateUtil.addHours(new Date(),24)) && curUpcomingEvents.get(charID).get(i).charID == charID){
				if (alertString.contains(" " + "upcomingEvent,") && IsActiveChar(charID)){
					if (charIDBaseLoadedHM.get(charID).contains("UpcomingEvents,")){
						SimpleDateFormat display = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", Locale.ENGLISH);
						display.setTimeZone(TimeZone.getDefault());
						alerts.add(0,new Alert("upcomingEvent", charID, "The calendar event \"" + curUpcomingEvents.get(charID).get(i).eventTitle + "\" is scheduled at " + display.format(curUpcomingEvents.get(charID).get(i).eventDate)  + ".",  "(BscChar/Wallet/Misc)","",Calendar.getInstance().getTime()));
						newAlertIDs.add("upcomingEvent");
					}
				}
				if (charIDBaseLoadedHM.get(charID).contains("UpcomingEvents,")){
					pastEventIDs.add(new UpcomingEvents(charID,curUpcomingEvents.get(charID).get(i).eventID,curUpcomingEvents.get(charID).get(i).eventTitle,curUpcomingEvents.get(charID).get(i).eventDate));
				}
			}
		}
		
		
		return alerts;
	}
	
	public ArrayList<Alert> checkForNotifications(long charID, long keyID, String vCode, String alertString) throws InterruptedException{
		//https://api.eveonline.com/char/Notifications.xml.aspx
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		for (int i=0;i<curNotifications.get(charID).size();i++){
			if (curNotifications.get(charID).get(i).shouldAlert && curNotifications.get(charID).get(i).charID == charID){
				curNotifications.get(charID).get(i).shouldAlert = false;
					HashMap info = getNotificationDescription(curNotifications.get(charID).get(i).typeID);
					String message = "";
					for (int k = 0; k < curNotificationTexts.size();k++){
						if (curNotifications.get(charID).get(i).notificationID == curNotificationTexts.get(k).notificationID ){
							message = "Notified by: " + new NotificationTranslator(keyID, vCode).getSenderName(curNotifications.get(charID).get(i).senderID) + "<br><br>Semi-Raw Data:<br><br>" + new NotificationTranslator(keyID, vCode).translate(curNotificationTexts.get(k).cdata);
						}
					}
					if (message == null || message.equals("")){
						message = "Details could not be retrieved.  Eve Online's API servers may have had problems at the time of this alert's detection.";
					}
					Long groupID = (Long)info.get("groupID");
					if (charIDBaseLoadedHM.get(charID).contains("Notifications,")){
						alerts.add(0,new Alert(String.valueOf(curNotifications.get(charID).get(i).notificationID), charID,(String)info.get("name") ,"(" + getAlertCategory(groupID) + ")",message,Calendar.getInstance().getTime()));
						newAlertIDs.add(String.valueOf(curNotifications.get(charID).get(i).notificationID));
					}
				}
		}
		
		return alerts;
	}
	
	public boolean getNewNotificationIDs(long charID, String alertString){
		//https://api.eveonline.com/char/Notifications.xml.aspx
		
		for (int i=0;i<curNotifications.get(charID).size();i++){
			boolean newNotification = true;
			for (int j=0;j<pastNotificationIDs.size();j++){
				if (curNotifications.get(charID).get(i).notificationID == pastNotificationIDs.get(j).notificationID){
					newNotification=false;
				} 
			}
			if (newNotification && curNotifications.get(charID).get(i).charID == charID){
				pastNotificationIDs.add(new Notifications(charID, curNotifications.get(charID).get(i).notificationID, curNotifications.get(charID).get(i).typeID,curNotifications.get(charID).get(i).sentDate,curNotifications.get(charID).get(i).senderID));
				if (alertString.contains(" " + String.valueOf(curNotifications.get(charID).get(i).typeID) + ",") && IsActiveChar(charID)){
					if (charIDBaseLoadedHM.get(charID).contains("Notifications,")){
						curNotifications.get(charID).get(i).shouldAlert=true;
					}
				}
			}
		}
		
		
		return false;
	}
	
	public boolean downloadNotificationTexts(long charID, long keyID, String vCode){
		//if(!isFirstRun){
			String notificationIDs = "";
			for (int i = 0; i < curNotifications.get(charID).size(); i++){
				if (curNotifications.get(charID).get(i).shouldAlert){
					notificationIDs += curNotifications.get(charID).get(i).notificationID + ",";
					
				}
			}
			if (!notificationIDs.equals("")){
				ArrayList temp = new DownloadWebpageText(charID,"NotificationTexts").readURL("https://api.eveonline.com/char/NotificationTexts.xml.aspx?keyId=" + String.valueOf(keyID) + "&vCode=" + vCode + "&characterID=" + charID + "&ids=" + notificationIDs);
				if (temp != null){
					if(temp.size() > 0 ){
				
						curNotificationTexts.addAll(temp);
					}
				}
			}
		return false;
	}
	
	public ArrayList<Alert> checkForMessages(long charID,long keyID, String vCode, String alertString){
		//https://api.eveonline.com/char/MailMessages.xml.aspx
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		for (int i=0;i<curMessages.get(charID).size();i++){
			if (curMessages.get(charID).get(i).shouldAlert  && curMessages.get(charID).get(i).charID == charID){
				curMessages.get(charID).get(i).shouldAlert = false;
					String message = "";
					for (int k = 0; k < curMailBodies.size();k++){
						if (curMessages.get(charID).get(i).messageID == curMailBodies.get(k).messageID ){
							message = curMailBodies.get(k).cdata;
						}
					}
					if (message == null || message.equals("")){
						message = "Message could not be retrieved.  Eve Online's API servers may have had problems at the time of this alert's detection.";
					}
					if (charIDBaseLoadedHM.get(charID).contains("Messages,")){
						alerts.add(0,new Alert("messageReceived", charID,"New message sent by " + new NotificationTranslator(keyID, vCode).getNameFromID(curMessages.get(charID).get(i).senderID) + ":  " + curMessages.get(charID).get(i).title , "(BscChar/Wallet/Misc)",message,Calendar.getInstance().getTime()));
						newAlertIDs.add("messageReceived");
					}
			}
		}
			
		return alerts;
	}
	
	public boolean getNewMessageIDs(long charID, String alertString){
		//https://api.eveonline.com/char/MailMessages.xml.aspx
		
		for (int i=0;i<curMessages.get(charID).size();i++){
			boolean newMessage = true;
			for (int j=0;j<pastMessageIDs.size();j++){
				if (curMessages.get(charID).get(i).messageID == pastMessageIDs.get(j).messageID){
					newMessage=false;
				} 
			}
			if (newMessage && curMessages.get(charID).get(i).charID == charID && curMessages.get(charID).get(i).senderID != charID ){
			//if (newMessage && curMessages.get(i).charID == charID){
				pastMessageIDs.add(new Messages(charID,curMessages.get(charID).get(i).messageID,curMessages.get(charID).get(i).senderID,curMessages.get(charID).get(i).sentDate,curMessages.get(charID).get(i).title));
				if (alertString.contains(" " + "messageReceived") && IsActiveChar(charID)){
					if (charIDBaseLoadedHM.get(charID).contains("Messages,")){
						curMessages.get(charID).get(i).shouldAlert=true;
					}
				}
			}
		}
		
		
		return false;
	}
	
	public boolean downloadMailBodies(long charID, long keyID, String vCode){
		String messageIDs = "";
		//if(!isFirstRun){
			for (int i = 0; i < curMessages.get(charID).size(); i++){
				if (curMessages.get(charID).get(i).shouldAlert){
					messageIDs += curMessages.get(charID).get(i).messageID + ",";
					
				}
			}
			if (!messageIDs.equals("")){
				ArrayList temp = new DownloadWebpageText(charID,"MailBodies").readURL("https://api.eveonline.com/char/MailBodies.xml.aspx?keyId=" + String.valueOf(keyID) + "&vCode=" + vCode + "&characterID=" + charID + "&ids=" + messageIDs);
				if (temp != null){
					if (temp.size() > 0 ){
						curMailBodies.addAll(temp);
					}
				}
		}
		
		return false;
	}
	
	/*public boolean checkForContractOffered(){
		return false;
	}
	
	public boolean checkForContractCompleted(){
		return false;
	}
	public boolean checkForCorpWarDecced(){
		//
		return false;
	}*/
	
	

	
	//private class DownloadWebpageText extends AsyncTask<String,Void,ArrayList> {
	private class DownloadWebpageText{
		String xmlFileName;
		long charID;

		private DownloadWebpageText(long charID, String xmlName){
			xmlFileName = xmlName;
			this.charID = charID;
		}
        //@Override
        protected ArrayList readURL(String... urls) {
              //Log.d("debugging","readURL");
              ArrayList results = null;
            // params comes from the execute() call: params[0] is the url.
            try {
            	InputStream temp = downloadUrl(urls[0]);
            	if (temp != null){
            		results = parse(temp); 
            	
	            	
	            	
            	}
            } catch (IOException e) {
            	return null;
                //return "Unable to retrieve web page. URL may be invalid.";
            } catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
				 
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Log.d("debugging",e.getMessage());
				return null;
			} catch (Exception e){
				return null;
			}
            return results;
			
        }
        
		// Given a URL, establishes an HttpUrlConnection and retrieves
		// the web page content as a InputStream, which it returns as
		// a string.
		private InputStream downloadUrl(String myurl) throws IOException {
			//Log.d("debugging","downloadUrl");
		    InputStream is = null;
		    // Only display the first 500 characters of the retrieved
		    // web page content.
		    try{ 
		    int len = 10000;
		        
		    
		        URL url = new URL(myurl);
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(30000 /* milliseconds */);
		        conn.setConnectTimeout(30000 /* milliseconds */);
		        conn.setRequestMethod("GET");
		        conn.setDoInput(true);
		        // Starts the query
		        conn.connect();
		        int response = conn.getResponseCode();
		        //Log.d("HttpExample", "Url - " + myurl);
		        //Log.d("HttpExample", "The response is: " + response);
		        if (response == 200){
		        	is = conn.getInputStream();
		        } else {
		        	is = null;
		        }
		    } catch(Exception e){
		    	return null;
		    }
		        // Convert the InputStream into a string
		        //String contentAsString = readIt(is,len);
		        return is;
		        
		}
		

		
		public ArrayList parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","parse");
			ArrayList results = new ArrayList();
	        try {
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

	            parser.setInput(in, null);
	            parser.nextTag();
	            results = readFeed(parser);
	            
	        } catch (Exception e){
	        	//Log.d("debugging",e.toString());
	        	return null;
	        } finally {
	            in.close();
	        }
	        return results;
	    }
		
		private ArrayList readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
		    
			//Log.d("debugging","readFeed");
		    parser.require(XmlPullParser.START_TAG, ns, "eveapi");
		    ArrayList result = null;
		    boolean finishedParse = false;
		    boolean errorPresent = false;
		    try{
		    while (parser.next() != XmlPullParser.END_DOCUMENT) {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        String name = parser.getName();
		        // Starts by looking for the entry tag
		        if (name.equals("currentTime") && eveTimeRetrieved == false){
		        	parser.next();
		        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		        	df.setTimeZone(TimeZone.getTimeZone("GMT"));
		            eveTime = df.parse(parser.getText());
		        	eveTimeRetrieved = true;
		        	long diff = new Date().getTime() - eveTime.getTime();
		        	timeShiftHours = (int) ((diff/1000 * 60 * 60) % 24);
		        	parser.next();
		        }
		        else if (name.equals("result")) {
		            result = readResult(parser);
		        } else if (name.equals("cachedUntil")) {
		        	parser.next();
		        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		        	df.setTimeZone(TimeZone.getTimeZone("GMT"));
		            try{
		            	cachedUntil.get(charID).put(xmlFileName,df.parse(parser.getText()));
		            } catch (Exception e){
		            	//do nothing
		            }
		        	
		            finishedParse = true;
		        } else if (name.equals("error")) {
		            errorPresent = true;
		        } else {
		            skip(parser);
		        }
		    }  
		    } catch (Exception e){
		    	return null;
		    }
		    
			    if (finishedParse == true && result != null && !errorPresent){
			    	return result;
			    } else {
			    	return null;
			    }
		    
		    
		}
		
		private ArrayList readResult(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException, InterruptedException {
			//Log.d("debugging","readResult");
		    parser.require(XmlPullParser.START_TAG, ns, "result");
		    ArrayList rows = null;
		    while (parser.next() != XmlPullParser.END_TAG) {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        String name = parser.getName();
		        
		        if (name.equals("rowset")) {
		            rows = readRowset(parser);
		            return rows;
		        } else if (name.equals("skillPoints") && xmlFileName == "CharacterInfo"){
		        	
		        	long l = Long.parseLong(parser.nextText());
		        	CharInfo ci = new CharInfo(charID, l);
		        	rows = new ArrayList();
		        	rows.add(ci);
		        	return rows;
		        } else if (name.equals("cloneSkillPoints") && xmlFileName == "Clone"){
		        	long l = Long.parseLong(parser.nextText());
		        	Clone c = new Clone(charID, l);
		        	rows = new ArrayList();
		        	rows.add(c);
		        	return rows;
		        } else{
		        	
		            skip(parser);
		        }
		    }
		    return rows;
		}
		
		private ArrayList readRowset(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException, InterruptedException {
			//Log.d("debugging","readRowset");
		    parser.require(XmlPullParser.START_TAG, ns, "rowset");
		    
		    String rowsetName = parser.getAttributeValue(null,"name");
		    ArrayList rows = new ArrayList();
		    
		    while ( parser.getEventType() != XmlPullParser.END_TAG || parser.getName().equals("row")) {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		        	parser.nextTag();
		            continue;
		        }
		        String name = parser.getName();
		       
		        
		        if (name.equals("row")) {
		        	if (rows == null){
		        		rows = new ArrayList();
		        	}
		        	if (xmlFileName.equals("SkillQueue")){
		        		rows.add( readSkillQueue(parser));
		        	} else if (xmlFileName.equals("WalletEntries")) {
		        		rows.add( readWalletEntries(parser));
		        	} else if (xmlFileName.equals("UpcomingEvents")) {
		        		rows.add( readUpcomingEvents(parser));
		        	} else if (xmlFileName.equals("Orders")) {
		        		rows.add( readOrders(parser));
		        	} else if (xmlFileName.equals("Research")) {
		        		rows.add( readResearch(parser));
		        	} else if (xmlFileName.equals("Jobs")) {
		        		rows.add( readJobs(parser));
		        	} else if (xmlFileName.equals("Messages")) {
		        		Messages m = readMessages(parser);
		        		//time zones don't match up but two days covers it anyway
		        		if (m.sentDate.after(DateUtil.addDays(new Date(), -2))){
		        			rows.add(m);
		        		}
		        	} else if (xmlFileName.equals("Notifications")) {
		        		Notifications n = readNotifications(parser);
		        		//time zones don't match up but two days covers it anyway
		        		if (n.sentDate.after(DateUtil.addDays(new Date(), -2))){
		        			rows.add(n);
		        		}
		        	} else if (xmlFileName.equals("NotificationTexts")){
		        		rows.add( readNotificationTexts(parser));
		        	} else if (xmlFileName.equals("MailBodies")){
		        		rows.add( readMailBodies(parser));
		        	//} else if (xmlFileName.equals("SkillInTraining")){
		        	//	rows.add( readSkillInTraining(parser));
		        	} else if (xmlFileName.equals("CharacterSheet") && rowsetName.equals("skills")){
		        		rows.add(readSkills(parser));
		        	}
		        	
		        } else {
		            //skip(parser);
		        }
		        parser.nextTag();
		    }

		    
		    return rows;
		}
		
		private Skills readSkills(XmlPullParser parser) throws XmlPullParserException, IOException {
			//Log.d("debugging","readSkills");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		    int level=-1;
		    long typeID=-1;
		   
		        	level = Integer.parseInt(parser.getAttributeValue(null, "level"));
		            typeID = Long.parseLong(parser.getAttributeValue(null, "typeID"));
		        
		    return new Skills(charID,  typeID,  level);
		}
		
		private SkillQueue readSkillQueue(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","readSkillQueue");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		    int queuePosition=-1;
		    long typeID=-1;
		    Date startTime= DateUtil.addDays(new Date(), 7);
		    Date endTime= DateUtil.addDays(new Date(), 7);
		    int level = 0;
		    
		        	queuePosition = Integer.parseInt(parser.getAttributeValue(null, "queuePosition"));
		            typeID = Long.parseLong(parser.getAttributeValue(null, "typeID"));
		            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		            df.setTimeZone(TimeZone.getTimeZone("GMT"));
		            try{
		            	startTime = df.parse(parser.getAttributeValue(null, "startTime"));
		            } catch (Exception e){
		            	//do nothing, date was just empty
		            }
		            try{
		            endTime = df.parse(parser.getAttributeValue(null, "endTime"));
		            } catch (Exception e){
		            	//do nothing, date was just empty
		            }
		            level = Integer.parseInt(parser.getAttributeValue(null, "level"));
		        
		    return new SkillQueue(charID, queuePosition,  typeID,  startTime,  endTime, level);
		}
		
		/*private SkillInTraining readSkillInTraining(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			Log.d("debugging","readSkillInTraining");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		   
			Date trainingEndTime = new Date();
			long trainingTypeID = -1;
			int trainingToLevel = -1;
		    
		    
					trainingToLevel = Integer.parseInt(parser.getAttributeValue(null, "trainingToLevel"));
					trainingTypeID = Long.parseLong(parser.getAttributeValue(null, "trainingTypeID"));
		            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		            df.setTimeZone(TimeZone.getTimeZone("GMT"));
		            trainingEndTime = df.parse(parser.getAttributeValue(null, "trainingEndTime"));
		            
		        
		    return new SkillInTraining(charID, trainingEndTime,  trainingTypeID,  trainingToLevel);
		}*/
		
		private WalletEntries readWalletEntries(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","readWalletEntries");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		    double amount=0;
		    String ownerName1="";
		    String ownerName2="";
		    Date date=new Date();
		    long refID = -1;
		    int refTypeID = -1;
		    
		        	amount = Double.parseDouble(parser.getAttributeValue(null, "amount"));
		         
		            ownerName1 = parser.getAttributeValue(null, "ownerName1");
		            ownerName2 = parser.getAttributeValue(null, "ownerName2");
		            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		            df.setTimeZone(TimeZone.getTimeZone("GMT"));
		            date = df.parse(parser.getAttributeValue(null, "date"));
		            refID = Long.parseLong(parser.getAttributeValue(null,"refID"));
		            refTypeID = Integer.parseInt(parser.getAttributeValue(null,"refTypeID"));
		        
		    
		    return new WalletEntries(charID,  amount,  ownerName1, ownerName2, date, refID, refTypeID);
		}
		
		private UpcomingEvents readUpcomingEvents(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","readUpcomingEvents");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		    long eventID=-1;
		    String eventTitle="";
		    Date eventDate= new Date();
		    
		    		eventID = Long.parseLong(parser.getAttributeValue(null,"eventID"));
		            eventTitle = parser.getAttributeValue(null, "eventTitle");
		            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		            df.setTimeZone(TimeZone.getTimeZone("GMT"));
		            eventDate = df.parse(parser.getAttributeValue(null, "eventDate"));
		        
		    
		    return new UpcomingEvents(charID, eventID, eventTitle,  eventDate);
		}
		
		private Orders readOrders(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","readOrders");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		    long orderID=-1;
		    Date issued=new Date();
		    int bid=-1;
		    int duration=-1;
		    int orderState=-1;
		    int volEntered=-1;
		    int volRemaining=-1;
		    long typeID = -1;
		    
		        	orderID = Long.parseLong(parser.getAttributeValue(null, "orderID"));
		            bid = Integer.parseInt(parser.getAttributeValue(null, "bid"));
		            duration = Integer.parseInt(parser.getAttributeValue(null, "duration"));
		            orderState = Integer.parseInt(parser.getAttributeValue(null, "orderState"));
		            volEntered = Integer.parseInt(parser.getAttributeValue(null, "volEntered"));
		            volRemaining = Integer.parseInt(parser.getAttributeValue(null, "volRemaining"));
		            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		            df.setTimeZone(TimeZone.getTimeZone("GMT"));
		            issued = df.parse(parser.getAttributeValue(null, "issued"));
		            typeID = Long.parseLong(parser.getAttributeValue(null,"typeID"));
		        
		    return new Orders(charID,  orderID,  issued,  bid,  duration,  orderState,  volEntered,  volRemaining, typeID);
		}
		
		private Research readResearch(XmlPullParser parser) throws XmlPullParserException, IOException {
			//Log.d("debugging","readResearch");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		    long agentID=-1;
		    
		        	agentID = Long.parseLong(parser.getAttributeValue(null, "agentID"));
		        
		    return new Research(charID, agentID);
		}
		
		private Jobs readJobs(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","readJobs");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		    long outputLocationID =-1;
		    long outputTypeID = -1;
		    int completed = -1;
		    int completedSuccessfully =-1;
		    long jobID=-1;
		    Date endProductionTime = new Date();
		    String pauseProductionTime = "";
		    int completedStatus = -1;
		    
		        	outputLocationID = Long.parseLong(parser.getAttributeValue(null, "installedInSolarSystemID"));
		        	completed = Integer.parseInt(parser.getAttributeValue(null, "completed"));
		        	completedSuccessfully = Integer.parseInt(parser.getAttributeValue(null, "completedSuccessfully"));
		        	jobID = Long.parseLong(parser.getAttributeValue(null, "jobID"));
		        	outputTypeID = Long.parseLong(parser.getAttributeValue(null, "outputTypeID"));
		        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		        	df.setTimeZone(TimeZone.getTimeZone("GMT"));
		        	endProductionTime = df.parse(parser.getAttributeValue(null, "endProductionTime"));
		        	pauseProductionTime = parser.getAttributeValue(null, "pauseProductionTime");
		        	completedStatus = Integer.parseInt(parser.getAttributeValue(null, "completedStatus"));
		        
		    return new Jobs(charID,  outputLocationID, outputTypeID, completed, completedSuccessfully,  jobID, endProductionTime, pauseProductionTime, completedStatus);
		}
		
		private Messages readMessages(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","readMessages");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		    long messageID = -1;
		    long senderID = -1;
		    Date sentDate =new Date();
		    String title="";
		    
		        	messageID = Long.parseLong(parser.getAttributeValue(null, "messageID"));
		        	senderID = Long.parseLong(parser.getAttributeValue(null, "senderID"));
		        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		        	df.setTimeZone(TimeZone.getTimeZone("GMT"));
		        	sentDate = df.parse(parser.getAttributeValue(null, "sentDate"));
		        	title = parser.getAttributeValue(null, "title");
		        
		    return new Messages(charID,  messageID,  senderID,  sentDate,  title);
		}
		
		private MailBodies readMailBodies(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","readMailBodies");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		    long messageID = -1;
		    String cdata = "";
		    
		        	messageID = Long.parseLong(parser.getAttributeValue(null, "messageID"));
		        	while (cdata.equals("") || cdata == null){
		        		if (parser.nextToken() == parser.CDSECT){
		        			cdata = parser.getText();
		        		}
		        		
		        	}
		        	
		        
		    return new MailBodies( charID, messageID,  cdata);
		}
		
		private Notifications readNotifications(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","readNotifications");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		    int notificationID=-1;
		    long typeID=-1;
		    Date sentDate= new Date();
		    long senderID=-1;
		    
		        	notificationID = Integer.parseInt(parser.getAttributeValue(null, "notificationID"));
		        	typeID = Long.parseLong(parser.getAttributeValue(null, "typeID"));
		        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		        	df.setTimeZone(TimeZone.getTimeZone("GMT"));
		        	sentDate =  df.parse(parser.getAttributeValue(null, "sentDate"));  
		        	senderID = Long.parseLong(parser.getAttributeValue(null, "senderID"));
		        
		    return new Notifications( charID, notificationID,  typeID,  sentDate,  senderID);
		}
		
		private NotificationTexts readNotificationTexts(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException, InterruptedException {
			//Log.d("debugging","readNotificationTexts");
		    parser.require(XmlPullParser.START_TAG, ns, "row");
		    long notificationID = -1;
		    String cdata = "";
		    
		    notificationID = Long.parseLong(parser.getAttributeValue(null, "notificationID"));
		    while (cdata.equals("") || cdata == null){
        		if (parser.nextToken() == parser.CDSECT){
        			cdata = parser.getText();
        		}
        		
        	}
		        
		    return new NotificationTexts( charID, notificationID,  cdata.replace("\n", "<br>"));
		}

		
		private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
			
		    if (parser.getEventType() != XmlPullParser.START_TAG) {
		        throw new IllegalStateException();
		    }
		    int depth = 1;
		    while (depth != 0) {
		        switch (parser.next()) {
		        case XmlPullParser.END_TAG:
		            depth--;
		            break;
		        case XmlPullParser.START_TAG:
		            depth++;
		            break;
		        }
		    }
		 }
    }

	public static class SkillQueue implements Serializable {
		public final long charID;
	    public final int queuePosition;
	    public final long typeID;
	    public final Date startTime;
	    public final Date endTime;
	    public final int level;

	    private SkillQueue(long charID, int queuePosition, long typeID, Date startTime, Date endTime, int level) {
	    	this.charID = charID;
	        this.queuePosition = queuePosition;
	        this.typeID = typeID;
	        this.startTime = startTime;
	        this.endTime = endTime;
	        this.level = level;
	    }
	}
	public static class WalletEntries implements Serializable{
		public final long charID;
	    public final double amount;
	    public final String ownerName1;
	    public final String ownerName2;
	    public final Date date;
	    public final long refID;
	    public final int refTypeID;

	    private WalletEntries(long charID, double amount, String ownerName1, String ownerName2, Date date, long refID, int refTypeID) {
	    	this.charID = charID;
	        this.amount = amount;
	        this.ownerName1 = ownerName1;
	        this.ownerName2 = ownerName2;
	        this.date = date;
	        this.refID = refID;
	        this.refTypeID = refTypeID;
	    }
	}
	public static class UpcomingEvents implements Serializable{
		public final long charID;
		public final long eventID;
	    public final String eventTitle;
	    public final Date eventDate;

	    private UpcomingEvents(long charID, long eventID, String eventTitle, Date eventDate) {
	    	this.charID = charID;
	    	this.eventID = eventID;
	        this.eventTitle = eventTitle;
	        this.eventDate = eventDate;
	    }
	}
	public static class Orders implements Serializable{
		public final long charID;
	    public final long orderID;
	    public final Date issued;
	    public final int bid;
	    public final int duration;
	    public final int orderState;
	    public final int volEntered;
	    public final int volRemaining;
	    public final long typeID;

	    private Orders(long charID, long orderID, Date issued, int bid, int duration, int orderState, int volEntered, int volRemaining, long typeID) {
	    	this.charID = charID;
	        this.orderID = orderID;
	        this.issued = issued;
	        this.bid = bid;
	        this.duration = duration;
	        this.orderState = orderState;
	        this.volEntered = volEntered;
	        this.volRemaining = volRemaining;
	        this.typeID = typeID;
	    }
	}
	public static class Research implements Serializable{
		public final long charID;
	    public final long agentID;
	    

	    private Research(long charID, long agentID) {
	    	this.charID = charID;
	        this.agentID = agentID;
	    }
	}
	public static class Jobs implements Serializable{
		public final long charID;
	    public final long outputLocationID;
	    public final long outputTypeID;
	    public final int completed;
	    public final int completedSuccessfully;
	    public final long jobID;
	    public final Date endProductionTime;
	    public final String pauseProductionTime;
	    public final int completedStatus;

	    private Jobs(long charID, long outputLocationID, long outputTypeID, int completed, int completedSuccessfully, long jobID, Date endProductionTime, String pauseProductionTime, int completedStatus) {
	    	this.charID = charID;
	        this.outputLocationID = outputLocationID;
	        this.outputTypeID = outputTypeID;
	        this.completed = completed;
	        this.completedSuccessfully = completedSuccessfully;
	        this.jobID = jobID;
	        this.endProductionTime = endProductionTime;
	        this.pauseProductionTime = pauseProductionTime;
	        this.completedStatus = completedStatus;
	    }
	}
	public static class Messages implements Serializable{
		public final long charID;
	    public final String title;
	    public final long messageID;
	    public final long senderID;
	    public final Date sentDate;
	    public boolean shouldAlert = false;

	    private Messages(long charID, long messageID, long senderID, Date sentDate, String title) {
	    	this.charID = charID;
	        this.title = title;
	        this.messageID = messageID;
	        this.senderID = senderID;
	        this.sentDate = sentDate;
	    }
	}
	public static class Notifications implements Serializable{
		public final long charID;
	    public final long notificationID;
	    public final long typeID;
	    public final long senderID;
	    public final Date sentDate;
	    public boolean shouldAlert = false;

	    private Notifications(long charID, long notificationID, long typeID, Date sentDate, long senderID) {
	    	this.charID = charID;
	        this.notificationID = notificationID;
	        this.typeID = typeID;
	        this.senderID = senderID;
	        this.sentDate = sentDate;
	    }
	}
	
	public static class NotificationTexts implements Serializable{
		public final long charID;
		public final long notificationID;
		public final String cdata;
		
		private NotificationTexts(long charID, long notificationID, String cdata){
			this.charID = charID;
			this.notificationID = notificationID;
			this.cdata = cdata;
		}
	}
	
	public static class MailBodies implements Serializable{
		public final long charID;
		public final long messageID;
		public final String cdata;
		
		private MailBodies(long charID, long messageID, String cdata){
			this.charID = charID;
			this.messageID = messageID;
			this.cdata = cdata;
		}
	}
	
	public static class Skills implements Serializable{
		public final long charID;
		public final long typeID;
		public  int level;
		
		private Skills(long charID, long typeID, int level){
			this.charID = charID;
			this.typeID=typeID;
			this.level=level;
		}
	}
	
	public static class CharInfo implements Serializable{
		public final long charID;
		public final long skillPoints;
		
		private CharInfo(long charID, long skillPoints){
			this.charID = charID;
			this.skillPoints = skillPoints;
		}
	}
	
	public static class Clone implements Serializable{
		public final long charID;
		public final long skillPoints;
		
		private Clone(long charID, long skillPoints){
			this.charID = charID;
			this.skillPoints = skillPoints;
		}
	}
	
	/*public static class SkillInTraining {
		public final long charID;
		public final Date trainingEndTime;
		public final long trainingTypeID;
		public final int trainingToLevel;
		
		private SkillInTraining(long charID, Date trainingEndTime, long trainingTypeID, int trainingToLevel){
			this.charID = charID;
			this.trainingEndTime = trainingEndTime;
			this.trainingTypeID = trainingTypeID;
			this.trainingToLevel = trainingToLevel;
		}
	}*/
	


	@Override
	protected void onHandleIntent(Intent intent) {
		try{
		if (isRunning==false){
			isRunning=true;
			boolean vibrate = false;
			boolean sound = false;
			SharedPreferences settings =  getSharedPreferences("evewatch",0);
			
			//Toast.makeText(this, "just ran", Toast.LENGTH_LONG).show();
			try {
				checkForNewAlerts();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String vibrateString = settings.getString("vibrateIDs", "");
			String soundString = settings.getString("soundIDs", "");
			for (int i = 0; i < newAlertIDs.size(); i++){
				if (vibrateString.contains(newAlertIDs.get(i))){
					vibrate = true;
				}
				if (soundString.contains(newAlertIDs.get(i))){
					sound = true;
				}
			}
			
			try {
				sendNotification((ArrayList<String>)newAlertIDs.clone(),vibrate,sound);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newAlertIDs = new ArrayList<String>();
			//mark charIDsBaseLoaded here
			if (clearCharIDBaseLoadedHM){
				ClearCharIDBaseLoaded();
			} else {
				if (removeCharFromHM || addCharToHM){
					try {
						ExecuteHashMapActions(this);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				
				for (int i = 0; i < activeCharIDs.size(); i++){
					charIDBaseLoadedHM.put(activeCharIDs.get(i).characterID,charIDBaseLoadedHM.get(activeCharIDs.get(i).characterID) + toBeLoadedHM.get(activeCharIDs.get(i).characterID));
					toBeLoadedHM.put(activeCharIDs.get(i).characterID,"");
				}
				
			}
			
			
			
		}
		} catch (Exception e){
			//nothing
		} finally {
			stopSelf();
		}
	}
	
	
	
	/*public static void ClearAlerts(){
		
		
		archivedAlerts.addAll(0,(Collection<? extends Alert>) alerts.clone());
		alerts.clear();
		
	}
	
	public static void ClearArchivedAlerts(){
		archivedAlerts.clear();
	}*/
	
	public static void ClearCharIDBaseLoaded(){
		charIDBaseLoadedHM.clear();
		toBeLoadedHM.clear();
		cachedUntil.clear();
		
		curOrders.clear();
		curResearch.clear();
		curJobs.clear();
		curWalletEntries.clear();
		curUpcomingEvents.clear();
		curNotifications.clear();
		curMessages.clear();
		curSkills.clear();
		curCharInfo.clear();
		curCloneInfo.clear();
		curSkillQueue.clear();
		
		prevSkillQueue.clear();
		prevCharInfo.clear();
		prevCloneInfo.clear();
		prevWalletEntries.clear();
		prevResearch.clear();
		prevOrders.clear();
		clearCharIDBaseLoadedHM = false;
	}
	public static void MarkCharIDBaseLoadedForClearing(){
		clearCharIDBaseLoadedHM = true;
	}
	public static void RemoveCharIDBaseLoaded(long charID){
		charIDBaseLoadedHM.put(charID,"");
		toBeLoadedHM.put(charID,"");
	}
	public static void AddCharToHashMaps(Character ch, Context context) throws IOException{
		//addCharToHM=false;
		ArrayList<Character> availChars = Character.readAvailableCharacterIDs(context);
		ArrayList<Character> activeChars = Character.readActiveCharacterIDs(context);
		//for (int i = 0; i < charsMarkedForAddition.size();i++){
			charIDBaseLoadedHM.put(ch.characterID,"");
			toBeLoadedHM.put(ch.characterID,"");
			cachedUntil.put(ch.characterID, new HashMap<String, Date>());
			curOrders.put(ch.characterID, new ArrayList<Orders>());
			curResearch.put(ch.characterID, new ArrayList<Research>());
			curJobs.put(ch.characterID, new ArrayList<Jobs>());
			curWalletEntries.put(ch.characterID, new ArrayList<WalletEntries>());
			curUpcomingEvents.put(ch.characterID, new ArrayList<UpcomingEvents>());
			curNotifications.put(ch.characterID, new ArrayList<Notifications>());
			curMessages.put(ch.characterID, new ArrayList<Messages>());
			curSkills.put(ch.characterID, new ArrayList<Skills>());
			curCharInfo.put(ch.characterID, new ArrayList<CharInfo>());
			curCloneInfo.put(ch.characterID, new ArrayList<Clone>());
			curSkillQueue.put(ch.characterID, new ArrayList<SkillQueue>());
			
			prevSkillQueue.put(ch.characterID, new ArrayList<SkillQueue>());
			prevCharInfo.put(ch.characterID, new ArrayList<CharInfo>());
			prevCloneInfo.put(ch.characterID, new ArrayList<Clone>());
			prevWalletEntries.put(ch.characterID, new ArrayList<WalletEntries>());
			prevResearch.put(ch.characterID, new ArrayList<Research>());
			prevOrders.put(ch.characterID, new ArrayList<Orders>());
			

			boolean availUpdated = false;
			boolean activeUpdated = false;
			for (int j = availChars.size()-1; j >=0; j--){
				if (availChars.get(j).characterID == ch.characterID){
					availChars.get(j).keyID = ch.keyID;
					availChars.get(j).vCode = ch.vCode;
					availChars.get(j).characterName = ch.characterName;
					availUpdated = true;
				}
			}
			for (int j = activeChars.size()-1; j >=0; j--){
				if (activeChars.get(j).characterID == ch.characterID){
					activeChars.get(j).keyID = ch.keyID;
					activeChars.get(j).vCode = ch.vCode;
					activeChars.get(j).characterName = ch.characterName;
					activeUpdated = true;
				}
			}
			if (!availUpdated){
				availChars.add(new Character(ch.characterID, ch.keyID, ch.vCode, ch.characterName));
			}
			if (!activeUpdated){
				activeChars.add(new Character(ch.characterID, ch.keyID, ch.vCode, ch.characterName));
			}
			
		//}
		Character.writeAvailableCharacterIDs(availChars, context);
		Character.writeActiveCharacterIDs(activeChars, context);
	}
	public static void RemoveCharFromHashMaps(Character ch, Context context) throws IOException{
		ArrayList<Character> availChars = Character.readAvailableCharacterIDs(context);
		ArrayList<Character> activeChars = Character.readActiveCharacterIDs(context);
		//for (int i = 0; i < charIDsMarkedForRemoval.size(); i ++){
			charIDBaseLoadedHM.remove(ch.characterID);
			toBeLoadedHM.remove(ch.characterID);
			for (int j = availChars.size()-1; j >=0; j--){
				if (availChars.get(j).characterID == ch.characterID){
					availChars.remove(j);
				}
			}
			for (int j = activeChars.size()-1; j >=0; j--){
				if (activeChars.get(j).characterID == ch.characterID){
					activeChars.remove(j);
				}
			}
		//}
		Character.writeAvailableCharacterIDs(availChars, context);
		Character.writeActiveCharacterIDs(activeChars, context);
		//removeCharFromHM = false;
		//charIDsMarkedForRemoval.clear();
		
		
	}
	
	public static void ExecuteHashMapActions(Context context) throws IOException{
		for (int i = 0; i < hmas.size(); i++){
			if (hmas.get(i).addOrRemove==true){
				AddCharToHashMaps(hmas.get(i).ch,context);
			} else{
				RemoveCharFromHashMaps(hmas.get(i).ch,context);
			}
		}
		hmas.clear();
		charIDsMarkedForRemoval.clear();
		charsMarkedForAddition.clear();
		
		removeCharFromHM = false;
		addCharToHM = false;
	}
	
	public static void AddCharAsActive(long charID, Context context) throws IOException{
		ArrayList<Character> availChars = Character.readAvailableCharacterIDs(context);
		ArrayList<Character> activeChars = Character.readActiveCharacterIDs(context);
		
			for (int j = availChars.size()-1; j >=0; j--){
				if (availChars.get(j).characterID==charID){
					activeChars.add(new Character(charID,availChars.get(j).keyID, availChars.get(j).vCode,availChars.get(j).characterName));
				}
			}
		
		Character.writeActiveCharacterIDs(availChars, context);
	}
	
	public static void RemoveCharFromActive(long charID, Context context) throws IOException{
		
		ArrayList<Character> activeChars = Character.readActiveCharacterIDs(context);
		
			for (int j = activeChars.size()-1; j >=0; j--){
				if (activeChars.get(j).characterID == charID){
					activeChars.remove(j);
				}
			}
		

		Character.writeActiveCharacterIDs(activeChars, context);
		
	}
	
	public static void MarkCharForHashMapRemoval(Character ch){
		removeCharFromHM = true;
		hmas.add(new HashMapAction(ch,false));
	}
	
	public static void MarkCharForHMAddition(Character ch){
		addCharToHM=true;
		hmas.add(new HashMapAction(ch,true));
	}
	public  void InitializeHashMaps(){
		for (int i = 0; i < charIDs.size();i++){
			charIDBaseLoadedHM.put(charIDs.get(i).characterID, "");
			toBeLoadedHM.put(charIDs.get(i).characterID, "");
			cachedUntil.put(charIDs.get(i).characterID, new HashMap<String, Date>());
			curOrders.put(charIDs.get(i).characterID, new ArrayList<Orders>());
			curResearch.put(charIDs.get(i).characterID, new ArrayList<Research>());
			curJobs.put(charIDs.get(i).characterID, new ArrayList<Jobs>());
			curWalletEntries.put(charIDs.get(i).characterID, new ArrayList<WalletEntries>());
			curUpcomingEvents.put(charIDs.get(i).characterID, new ArrayList<UpcomingEvents>());
			curNotifications.put(charIDs.get(i).characterID, new ArrayList<Notifications>());
			curMessages.put(charIDs.get(i).characterID, new ArrayList<Messages>());
			curSkills.put(charIDs.get(i).characterID, new ArrayList<Skills>());
			curCharInfo.put(charIDs.get(i).characterID, new ArrayList<CharInfo>());
			curCloneInfo.put(charIDs.get(i).characterID, new ArrayList<Clone>());
			curSkillQueue.put(charIDs.get(i).characterID, new ArrayList<SkillQueue>());
			
			prevSkillQueue.put(charIDs.get(i).characterID, new ArrayList<SkillQueue>());
			prevCharInfo.put(charIDs.get(i).characterID, new ArrayList<CharInfo>());
			prevCloneInfo.put(charIDs.get(i).characterID, new ArrayList<Clone>());
			prevWalletEntries.put(charIDs.get(i).characterID, new ArrayList<WalletEntries>());
			prevResearch.put(charIDs.get(i).characterID, new ArrayList<Research>());
			prevOrders.put(charIDs.get(i).characterID, new ArrayList<Orders>());
		}
		apiCallErrors.put("Orders",0);
		apiCallErrors.put("CharacterInfo",0);
		apiCallErrors.put("CharacterSheet",0);
		apiCallErrors.put("Clone",0);
		apiCallErrors.put("Research",0);
		apiCallErrors.put("Jobs",0);
		apiCallErrors.put("WalletEntries",0);
		apiCallErrors.put("UpcomingEvents",0);
		apiCallErrors.put("Notifications",0);
		apiCallErrors.put("Messages",0);
		apiCallErrors.put("SkillQueue",0);


	}
	
	private static class HashMapAction {
		public final Character ch;
		public final boolean addOrRemove;

		
		private HashMapAction(Character ch, boolean addOrRemove){
			this.ch = ch;
			this.addOrRemove = addOrRemove;
		}
	}
}




