package net.evewatch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.text.Html;
import android.util.Log;

public class Alert {

	public String alertID;
	public long charID; 
	public String title;
	public String category;
	public String message;
	public Date detectTime;
	
	public final static String alertDel = ",-,-,-,";
	public final static String alertBreak = ":-:-:-:";
	
	public Alert(String alertID, long charID, String title, String category, String message, Date detectTime){
		this.alertID = alertID;
		this.charID = charID;
		this.title = title;
		this.category = category;
		this.message = message;
		this.detectTime = detectTime;
	}
	
	public static ArrayList<Alert> getAlertsArrayList(Context context) throws IOException, ParseException{
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		File f = new File(context.getFilesDir()+File.separator+"Alerts.txt");
		f.createNewFile();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String read;
		
		
		while((read = bufferedReader.readLine()) != null){
			String[] params = read.split(alertDel);
			String alertID="";
			long charID=-1;
			String title="";
			String category="";
			String message="";
			Date detectTime=new Date();
			for (int i = 0; i < params.length; i++){
				if (params[i].startsWith("alertID")){
					alertID = params[i].substring(params[i].indexOf(":")+1);
				} else if (params[i].startsWith("charID")){
					charID =  Long.parseLong(params[i].substring(params[i].indexOf(":")+1));
				} else if (params[i].startsWith("title")){
					title =  params[i].substring(params[i].indexOf(":")+1);
				} else if (params[i].startsWith("category")){
					category =  params[i].substring(params[i].indexOf(":")+1);
				} else if (params[i].startsWith("message")){
					message =  params[i].substring(params[i].indexOf(":")+1);
				} else if (params[i].startsWith("detectTime")){
					SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", Locale.ENGLISH);
		        	
					detectTime =  df.parse(params[i].substring(params[i].indexOf(":")+1));
				}
			}
			alerts.add(new Alert(alertID,charID,title,category,message,detectTime));
		}
		//Log.d("Output", builder.toString());
		bufferedReader.close();
		
		return alerts;
		
	}
	
	public static ArrayList<Alert> getArchivedAlertsArrayList(Context context) throws IOException, ParseException{
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		File f = new File(context.getFilesDir()+File.separator+"ArchivedAlerts.txt");
		f.createNewFile();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String read;
		
		
		while((read = bufferedReader.readLine()) != null){
			String[] params = read.split(alertDel);
			String alertID="";
			long charID=-1;
			String title="";
			String category="";
			String message="";
			Date detectTime=new Date();
			for (int i = 0; i < params.length; i++){
				if (params[i].startsWith("alertID")){
					alertID = params[i].substring(params[i].indexOf(":")+1);
				} else if (params[i].startsWith("charID")){
					charID =  Long.parseLong(params[i].substring(params[i].indexOf(":")+1));
				} else if (params[i].startsWith("title")){
					title =  params[i].substring(params[i].indexOf(":")+1);
				} else if (params[i].startsWith("category")){
					category =  params[i].substring(params[i].indexOf(":")+1);
				} else if (params[i].startsWith("message")){
					message =  params[i].substring(params[i].indexOf(":")+1);
				} else if (params[i].startsWith("detectTime")){
					SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", Locale.ENGLISH);
		        	
					detectTime =  df.parse(params[i].substring(params[i].indexOf(":")+1));
				}
			}
			alerts.add(new Alert(alertID,charID,title,category,message,detectTime));
		}
		//Log.d("Output", builder.toString());
		bufferedReader.close();
		
		return alerts;
	}
	
	public static void writeAlertsToFile(ArrayList<Alert> alerts,Context context) throws IOException{
		File f = new File(context.getFilesDir()+File.separator+"Alerts.txt");
		f.createNewFile();
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
		
		for (int i = 0; i < alerts.size(); i++){
			//write String representation of alert
			bufferedWriter.write("alertID" + ":" + alerts.get(i).alertID + alertDel);
			bufferedWriter.write("charID" + ":" + alerts.get(i).charID + alertDel);
			bufferedWriter.write("title" + ":" + alerts.get(i).title + alertDel);
			bufferedWriter.write("category" + ":" + alerts.get(i).category + alertDel);
			bufferedWriter.write("message" + ":" + alerts.get(i).message + alertDel);
			bufferedWriter.write("detectTime" + ":" + alerts.get(i).detectTime + alertDel);
			bufferedWriter.newLine();
		}
		
		bufferedWriter.close();
	}
	
	public static void writeArchivedAlertsToFile(ArrayList<Alert> archivedAlerts,Context context) throws IOException{
		
		File f = new File(context.getFilesDir()+File.separator+"ArchivedAlerts.txt");
		f.createNewFile();
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
		
		for (int i = 0; i < archivedAlerts.size(); i++){
			//write String representation of alert
			bufferedWriter.write("alertID" + ":" + archivedAlerts.get(i).alertID + alertDel);
			bufferedWriter.write("charID" + ":" + archivedAlerts.get(i).charID + alertDel);
			bufferedWriter.write("title" + ":" + archivedAlerts.get(i).title + alertDel);
			bufferedWriter.write("category" + ":" + archivedAlerts.get(i).category + alertDel);
			bufferedWriter.write("message" + ":" + archivedAlerts.get(i).message + alertDel);
			bufferedWriter.write("detectTime" + ":" + archivedAlerts.get(i).detectTime + alertDel);
			bufferedWriter.newLine();
		}
		
		bufferedWriter.close();
	}
}
