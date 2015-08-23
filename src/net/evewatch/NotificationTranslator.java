package net.evewatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import net.evewatch.APIPoller.CharInfo;
import net.evewatch.APIPoller.Clone;
import net.evewatch.APIPoller.Jobs;
import net.evewatch.APIPoller.MailBodies;
import net.evewatch.APIPoller.Messages;
import net.evewatch.APIPoller.NotificationTexts;
import net.evewatch.APIPoller.Notifications;
import net.evewatch.APIPoller.Orders;
import net.evewatch.APIPoller.Research;
import net.evewatch.APIPoller.SkillQueue;
import net.evewatch.APIPoller.Skills;
import net.evewatch.APIPoller.UpcomingEvents;
import net.evewatch.APIPoller.WalletEntries;

 
public class NotificationTranslator {
	
	ArrayList<String> toBeReplaced = new ArrayList<String>();
	ArrayList<String> replacer = new ArrayList<String>();
	long keyID;
	String vCode;
	
	public NotificationTranslator(long keyID, String vCode){
		this.keyID=keyID;
		this.vCode=vCode;
	}
	
	public String getNameFromID(long id){
		String rv="an unknown source (currently unavailable in API)";
		try{
			new DownloadWebpageText("CharacterNames").doInBackground("https://api.eveonline.com/eve/CharacterName.xml.aspx?keyId=" + String.valueOf(keyID) + "&vCode=" + vCode + "&IDs=" + id);
			rv = replacer.get(0);
		} catch (Exception e){
			//do nothing
		}
		
		return rv;
	}
	
	public String translate(String yaml) throws InterruptedException{
		String result = yaml.toString();
		try {
			String[] lines= new String[100];
			lines = yaml.split("<br>");
			String characterNameXMLInput="";
			boolean translateDone = false;
			
			
			for (int i =0; i <lines.length;i++){
				String[] parts = new String[2];
				parts = lines[i].split(": ");
				
				if (parts[0].contains("date") || parts[0].contains("Date") || parts[0].endsWith("Time") || parts[0].endsWith("time")){
					try{
					Long fileTimeMilli = Long.parseLong(parts[1]);
					long javaTime = fileTimeMilli - 0x19db1ded53e8000L;
					 
					// convert UNITS from (100 nano-seconds) to (milliseconds)
					javaTime /= 10000;
					Date val = new Date(javaTime);
					//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		        	//df.setTimeZone(TimeZone.getTimeZone("GMT"));
		        	//String formattedDate = df.format(val);
					replacer.add(val.toString());
					toBeReplaced.add(parts[1]);
					
					} catch (Exception e){}
					
				}
				if (parts[0].contains("typeID") || parts[0].contains("TypeID")){
					try{
					String val;
					val = APIPoller.getItemName(Long.parseLong(parts[1]));
					replacer.add(val);
					toBeReplaced.add(parts[1]);
					
					} catch (Exception e){}
				}
				
				if (parts[0].contains("solarSystemID") || parts[0].contains("SolarSystemID")){
					try{
					String val;
					val = APIPoller.getSystemName(Long.parseLong(parts[1]));
					replacer.add(val);
					toBeReplaced.add(parts[1]);
					
					} catch (Exception e){}
				}

				if (parts[0].contains("locationID") || parts[0].contains("LocationID")){
					try{
					String val;
					val = APIPoller.getSystemName(Long.parseLong(parts[1]));
					replacer.add(val);
					toBeReplaced.add(parts[1]);
					
					} catch (Exception e){}
					
					try{
						String val;
						val = APIPoller.getStationName(Long.parseLong(parts[1]));
						replacer.add(val);
						toBeReplaced.add(parts[1]);
						
					} catch (Exception e){}
				}
				
				if (parts[0].contains("againstID") || parts[0].contains("declaredByID")
						|| parts[0].contains("characterID") || parts[0].contains("charID") || parts[0].contains("CharID") || parts[0].contains("aggressorID")
						|| parts[0].contains("corporationID") || parts[0].contains("corpID") || parts[0].contains("CorpID")
						|| parts[0].contains("AllianceID") || parts[0].contains("allianceID")
						){
					characterNameXMLInput += parts[1] + ",";
					
					
				}
					
			}
			if (characterNameXMLInput.length() > 0){
				try{
				characterNameXMLInput = characterNameXMLInput.substring(0, characterNameXMLInput.length()-1);
				new DownloadWebpageText("CharacterNames").doInBackground("https://api.eveonline.com/eve/CharacterName.xml.aspx?keyId=" + String.valueOf(keyID) + "&vCode=" + vCode + "&IDs=" + characterNameXMLInput);
				} catch (Exception e){}
			}
			//while (translateDone == false){
			//		Thread.sleep(1000);
			//}
			
			if (toBeReplaced.size() == replacer.size()){
				for (int i = 0; i < toBeReplaced.size();i++){
					result=result.replace(": " + toBeReplaced.get(i) + "<br>", ": " + replacer.get(i) + "<br>");
				}
			}
			else {
				//Log.d("debugging","toBeReplaced.size() did not equal replacer.size()");
			}
			if (result.equals("")){
				result = yaml.toString();
			}
			return result;
		} catch (Exception e) {
			return yaml.toString();
		}
	}
	
	public String getSenderName(long id) throws InterruptedException{
		String name = String.valueOf(id);
		try{
			
			new DownloadWebpageText("CharacterNames").doInBackground("https://api.eveonline.com/eve/CharacterName.xml.aspx?keyId=" + String.valueOf(keyID) + "&vCode=" + vCode + "&IDs=" + name);
			name = replacer.get(0);
			} catch (Exception e){}
			
		return name;
	}
	
	private class DownloadWebpageText  {
		String xmlFileName;
		

		private DownloadWebpageText(String xmlName){
			xmlFileName = xmlName;
			
		}
        //@Override
        protected ArrayList<CharacterName> readURL(String... urls) {
              //Log.d("debugging","readURL");
              ArrayList<CharacterName> results = new ArrayList<CharacterName>();
            // params comes from the execute() call: params[0] is the url.
            try {
            	InputStream temp = downloadUrl(urls[0]);
            	if (temp != null){
            		results = parse(temp); 
            	
	            	
	            	
            	}
            } catch (IOException e) {
            	return results;
                //return "Unable to retrieve web page. URL may be invalid.";
            } catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return results;
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Log.d("debugging",e.getMessage());
				return results;
			} catch (Exception e){
				return results;
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
		

		
		public ArrayList<CharacterName> parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","parse");
			ArrayList<CharacterName> results = null;
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
		
		private ArrayList<CharacterName> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
		    
			//Log.d("debugging","readFeed");
		    parser.require(XmlPullParser.START_TAG, null, "eveapi");
		    ArrayList<CharacterName> result = null;
		    boolean finishedParse = false;
		    try{
		    while (parser.next() != XmlPullParser.END_DOCUMENT) {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        String name = parser.getName();
		        // Starts by looking for the entry tag
		        if (name.equals("result")) {
		            result = readResult(parser);
		        } else if (name.equals("cachedUntil")) {
		            finishedParse = true;
		        } else {
		            skip(parser);
		        }
		    }  
		    } catch (Exception e){
		    	return null;
		    }
		    return result;
		    
		}
		
		private ArrayList<CharacterName> readResult(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","readResult");
		    parser.require(XmlPullParser.START_TAG, null, "result");
		    ArrayList<CharacterName> rows = null;
		    while (parser.next() != XmlPullParser.END_TAG) {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        String name = parser.getName();
		        
		        if (name.equals("rowset")) {
		           rows = readRowset(parser);
		            
		        } else{
		        	
		            skip(parser);
		        }
		    }
		   return rows;
		}
		
		private ArrayList<CharacterName> readRowset(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			//Log.d("debugging","readRowset");
		    parser.require(XmlPullParser.START_TAG, null, "rowset");
		    
		    String rowsetName = parser.getAttributeValue(null,"name");
		    ArrayList<CharacterName> rows = new ArrayList();
		    
		    while ( parser.getEventType() != XmlPullParser.END_TAG || parser.getName().equals("row")) {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		        	parser.nextTag();
		            continue;
		        }
		        String name = parser.getName();
		       
		        
		        if (name.equals("row")) {
		        	if (xmlFileName.equals("CharacterNames")){
		        		rows.add(readCharacterNames(parser));
		        	} 
		        	
		            
		        } else {
		            //skip(parser);
		        }
		        parser.nextTag();
		    }
		    
		    
		    return rows;
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
		
		private CharacterName readCharacterNames(XmlPullParser parser) throws XmlPullParserException, IOException {
			//Log.d("debugging","readCharacterNames");
		    parser.require(XmlPullParser.START_TAG, null, "row");
		    String id="";
		    String name = "";
		    
		   
		        	id = parser.getAttributeValue(null, "characterID");
		        	name = parser.getAttributeValue(null, "name");	
		        	
		        	CharacterName cn = new CharacterName(id,name);
		        	return cn;
		        
		    
		}
		
		protected void doInBackground(String... url) {
			// TODO Auto-generated method stub
			
				ArrayList<CharacterName> cn = readURL(url);
				onPostExecute(cn);

		}
		
		
		protected void onPostExecute(ArrayList<CharacterName> result){
			try{
			for (int i = 0 ; i < result.size(); i++){
				replacer.add(((CharacterName)result.get(i)).name);
				toBeReplaced.add(((CharacterName)result.get(i)).ID);
				
			}
			}catch(Exception e){}
		}
    }
	
	public static class CharacterName {
		public final String ID;
	    public final String name;


	    private CharacterName(String ID, String name) {
	    	this.ID = ID;
	        this.name = name;

	    }
	}

}

