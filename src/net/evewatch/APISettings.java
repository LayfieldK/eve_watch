package net.evewatch;
 
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import net.evewatch.APIPoller.Skills;
import net.evewatch.R;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class APISettings extends Activity {
	
	boolean charactersRetrieved = true;
	public static ArrayList<Integer> characterIDs = new ArrayList<Integer>();
	public static HashMap<Long,Bitmap> characterPortraits = new HashMap<Long,Bitmap>();
	private static DataBaseHelper dbHelper = null;
	private static SQLiteDatabase db =null;
	private static Context context;
	public static boolean overrideReschedule = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		context = this;
	    super.onCreate(savedInstanceState);
	 // TODO Auto-generated method stub
	    setContentView(R.layout.settings);
	    SharedPreferences settings =  getSharedPreferences("evewatch",0);
	    SharedPreferences.Editor ed = settings.edit();
	    
	    Switch s = (Switch)findViewById(R.id.switch1);
	    
	    s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	@Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked){
	    		BootUpBehaviorChanged(isChecked);
	    	}
	    });
	    
	    Switch s2 = (Switch)findViewById(R.id.CellularSwitch);
	    
	    s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	@Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked){
	    		NetworkBehaviorChanged(isChecked);
	    	}
	    });
	    
	    
	    int freq = settings.getInt("frequency",900001);
	    RadioButton b=(RadioButton)findViewById(R.id.radio15);
	    switch (freq){
	    case 900001:
	    	b = (RadioButton)findViewById(R.id.radio15);
	    	break;
	    case 1800001:
	    	b = (RadioButton)findViewById(R.id.radio30);
	    	break;
	    case 3600001:
	    	b = (RadioButton)findViewById(R.id.radio60);
	    	break;
	    case 7200001:
	    	b = (RadioButton)findViewById(R.id.radio2);
	    	break;
	    case 14400001:
	    	b = (RadioButton)findViewById(R.id.radio4);
	    	break;
	    case 28800001:
	    	b = (RadioButton)findViewById(R.id.radio8);
	    	break;
	    }
	    b.setChecked(true);
	    RadioGroup radGrp = (RadioGroup) findViewById(R.id.frequencyGroup1);

	    radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
	      public void onCheckedChanged(RadioGroup arg0, int id) {
	    	SharedPreferences settings =  getSharedPreferences("evewatch",0);
	  	    SharedPreferences.Editor ed = settings.edit();
	        switch (id) {
	        case R.id.radio15:
	          ed.putInt("frequency", 900001);
	          break;
	        case R.id.radio30:
	        	 ed.putInt("frequency", 1800001);
	          break;
	        case R.id.radio60:
	        	 ed.putInt("frequency", 3600001);
	          break;
	        case R.id.radio2:
	        	 ed.putInt("frequency", 7200001);
	          break;
	        case R.id.radio4:
	        	 ed.putInt("frequency", 14400001);
		          break;
	        case R.id.radio8:
	        	 ed.putInt("frequency", 28800001);
		          break;
	        
	        }
	        ed.apply();
	        overrideReschedule=true;
	        Intent intent = new Intent(context, StarterService.class);
    		context.startService(intent);
	        
	      }
	    });
	    
	    if (dbHelper == null){
			dbHelper = new DataBaseHelper(this);
			
			try {
	 
				db = dbHelper.openDataBase();
				//db = dbHelper.getReadableDatabase();
				
			}catch(SQLException sqle){
	 
				try {
					dbHelper.createDataBase();
					db = dbHelper.openDataBase();
				} catch(SQLException e){
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 
			}
		}
	
	    
	    
	    
	    if (!settings.contains("StartOnBootUp")){
	    	
	    	ed.putBoolean("StartOnBootUp", true);
	     	ed.apply();
	    } else {
	    	Switch sw = (Switch)findViewById(R.id.switch1);
	    	
	    	sw.setChecked(settings.getBoolean("StartOnBootUp", true));
	    	
	    }
	    
	    if (!settings.contains("AllowCellularData")){
	    	
	    	ed.putBoolean("AllowCellularData", true);
	     	ed.apply();
	    } else {
	    	Switch sw = (Switch)findViewById(R.id.CellularSwitch);
	    	
	    	sw.setChecked(settings.getBoolean("AllowCellularData", true));
	    	
	    }
	    
	    try {
			loadCharacters(Character.readAvailableCharacterIDs(this),this, false);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    try{
	    	Uri data = getIntent().getData();
	    	String scheme = data.getScheme(); // "http"
	    	String host = data.getHost(); // "twitter.com"

	    	String keyID = data.getQueryParameter("keyID");
	    	String vCode = data.getQueryParameter("vCode");
	    	
	    	//Toast.makeText(APISettings.this, keyID, Toast.LENGTH_LONG).show();
	    	//Toast.makeText(APISettings.this, vCode, Toast.LENGTH_LONG).show();
	    	
	    	EditText field1 = (EditText)findViewById(R.id.editText1);
		    String tempString = "";
		    
		    tempString = keyID;
		    
			field1.setText(tempString);
			EditText field2 = (EditText)findViewById(R.id.editText2);
			field2.setText(vCode);
	    	
			retrieveAPIInfo(null);
	    } catch (Exception e){
	    	
	    
	    
	    
	    //EditText field1 = (EditText)findViewById(R.id.editText1);
	    //String tempString = "";
	    //if (settings.getInt("KeyID",0) != 0){
	    	//tempString = String.valueOf(settings.getInt("KeyID",0));
	    //}
		//field1.setText(tempString);
		//EditText field2 = (EditText)findViewById(R.id.editText2);
		//field2.setText(settings.getString("vCode",""));
	    }
		
	}
	
	
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  EditText field1 = (EditText)findViewById(R.id.editText1);
	  EditText field2 = (EditText)findViewById(R.id.editText2);
	  if (savedInstanceState.getString("KeyID") != null){
		  field1.setText(savedInstanceState.getString("KeyID"));
	  }
	  if (savedInstanceState.getString("vCode") != null){
		  field2.setText(savedInstanceState.getString("vCode"));
	  }
	  
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			intent = new Intent(this, NewAlerts.class);
			intent.putExtra("activity", "settings");
	    	startActivity(intent);
			return true;
		case R.id.action_settings:
			intent = new Intent(this, APISettings.class);
	    	startActivity(intent);
			break;
		case R.id.action_menu:
			intent = new Intent(this, MainActivity.class);
	    	startActivity(intent);
			break;
		case R.id.action_alerts:
			intent = new Intent(this, NewAlerts.class);
	    	startActivity(intent);
			break;
		case R.id.action_about:
			intent = new Intent(this, About.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void retrieveAPIInfo(View view) {
		charactersRetrieved = false;
		EditText KeyID = (EditText)findViewById(R.id.editText1);
		EditText vCode = (EditText)findViewById(R.id.editText2);
		SharedPreferences settings =  getSharedPreferences("evewatch",0);
    	SharedPreferences.Editor ed = settings.edit();
    	long keyIDVal;
    	String vCodeVal;
    	try{
    	keyIDVal = Long.parseLong(KeyID.getText().toString());
    	vCodeVal =  vCode.getText().toString();
    	} catch (Exception e){
    		Toast.makeText(APISettings.this, "An error occurred while attempting to load characters with the given API credentials.", Toast.LENGTH_LONG).show();
    		ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner);
        	spinner.setVisibility(View.GONE);
        	return;
    	}
    	ed.apply();
    	
    	ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner);
    	spinner.setVisibility(View.VISIBLE);
    	Button button = (Button)findViewById(R.id.button1);
    	button.setClickable(false);
    	new DownloadWebpageText("Characters",this,keyIDVal,vCodeVal).execute("https://api.eveonline.com/account/Characters.xml.aspx?keyId=" + String.valueOf(KeyID.getText().toString()) + "&vCode=" + vCode.getText().toString());
    	
    	
	}
	
	
	private class DownloadWebpageText extends AsyncTask<String,Void,ArrayList<Character>> {
			String xmlFileName;
			//String url;
			Context pContext;
			long keyID;
			String vCode;
			private DownloadWebpageText( String xmlName, Context pContext, long keyID, String vCode){
				xmlFileName = xmlName;
				
				this.pContext = pContext;
				this.keyID = keyID;
				this.vCode = vCode;
			}
	        //@Override
	        protected ArrayList<Character> readURL(String... urls) {
	              //Log.d("debugging","readURL");
	              ArrayList results = new ArrayList();
	            // params comes from the execute() call: params[0] is the url.
	            try {
	            	InputStream temp = downloadUrl(urls[0]);
	            	results = parse(temp); 
	            	
	            } catch (IOException e) {
	            	
	                //return "Unable to retrieve web page. URL may be invalid.";
	            } catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//Log.d("debugging",e.getMessage());
					
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
			    int len = 10000;
			        
			    
			        URL url = new URL(myurl);
			        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			        conn.setReadTimeout(10000 /* milliseconds */);
			        conn.setConnectTimeout(15000 /* milliseconds */);
			        conn.setRequestMethod("GET");
			        conn.setDoInput(true);
			        // Starts the query
			        conn.connect();
			        int response = conn.getResponseCode();
			        //Log.d("HttpExample", "The response is: " + response);
			        is = conn.getInputStream();

			        // Convert the InputStream into a string
			        //String contentAsString = readIt(is,len);
			        return is;
			        
			}
			

			
			public ArrayList<Character> parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
				//Log.d("debugging","parse");
				ArrayList<Character> results = new ArrayList<Character>();
		        try {
		            XmlPullParser parser = Xml.newPullParser();
		            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

		            parser.setInput(in, null);
		            parser.nextTag();
		            results = readFeed(parser);
		            
		        } catch (Exception e){
		        	//Log.d("debugging",e.toString());
		        	
		        } finally {
		            in.close();
		        }
		        return results;
		    }
			
			private ArrayList<Character> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			    
				//Log.d("debugging","readFeed");
			    parser.require(XmlPullParser.START_TAG, null, "eveapi");
			    ArrayList<Character> result = new ArrayList<Character>();
			    while (parser.next() != XmlPullParser.END_TAG) {
			        if (parser.getEventType() != XmlPullParser.START_TAG) {
			            continue;
			        }
			        String name = parser.getName();
			        // Starts by looking for the entry tag
			        if (name.equals("result")) {
			            result = readResult(parser);
			        } else if (name.equals("error")){
			        	Toast.makeText(APISettings.this, "An error occurred while attempting to load characters with the given API credentials.", Toast.LENGTH_LONG).show();
			        } else {
			            skip(parser);
			        }
			    }  
			    return result;
			}
			
			private ArrayList<Character> readResult(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
				//Log.d("debugging","readResult");
			    parser.require(XmlPullParser.START_TAG, null, "result");
			    ArrayList<Character> rows = new ArrayList<Character>();
			    while (parser.next() != XmlPullParser.END_TAG) {
			        if (parser.getEventType() != XmlPullParser.START_TAG) {
			            continue;
			        }
			        String name = parser.getName();
			        
			        if (name.equals("rowset")) {
			            rows = readRowset(parser);
			       
			        } else {
			            skip(parser);
			        }
			    }
			    return rows;
			}
			
			private ArrayList<Character> readRowset(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
				//Log.d("debugging","readRowset");
			    parser.require(XmlPullParser.START_TAG, null, "rowset");
			    
			    String rowsetName = parser.getAttributeValue(null,"name");
			    ArrayList<Character> rows = new ArrayList<Character>();
			    while ( parser.getEventType() != XmlPullParser.END_TAG || parser.getName().equals("row")) {
			        if (parser.getEventType() != XmlPullParser.START_TAG) {
			        	parser.nextTag();
			            continue;
			        }
			        String name = parser.getName();
			       
			        
			        if (name.equals("row")) {
			        	if (xmlFileName == "Characters"){
			        		rows.add( readCharacters(parser));
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
			
			private Character readCharacters(XmlPullParser parser) throws XmlPullParserException, IOException {
				//Log.d("debugging","readCharacters");
			    parser.require(XmlPullParser.START_TAG, null, "row");
			    long characterID=-1;
			    String name  = "";
			   
			        	characterID = Integer.parseInt(parser.getAttributeValue(null, "characterID"));
			        	name = parser.getAttributeValue(null, "name");
			        		try {
					    	  
					    	  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL("http://image.eveonline.com/Character/" + characterID + "_64.jpg").getContent());
					    	  characterPortraits.put(characterID, bitmap);
					    	} catch (MalformedURLException e) {
					    	  e.printStackTrace();
					    	} catch (IOException e) {
					    	  e.printStackTrace();
					    	}
			        
			    return new Character(characterID,keyID, vCode, name);
			}
			@Override
			protected ArrayList<Character> doInBackground(String... url) {
				
				return readURL(url);
			}
			
			@Override
			protected void onPostExecute(ArrayList<Character> result){
				int count=0;
				String commaList ="";
				long keyIDVal=0;
				String vCodeVal="";
				 LinearLayout mainLayout = (LinearLayout)findViewById(R.id.charLayout);
				 //mainLayout.removeAllViews();
				 //APIPoller.MarkCharIDBaseLoadedForClearing();
				 if (result.size() == 0){
					 Toast.makeText(APISettings.this, "An error occurred while attempting to load characters with the given API credentials.", Toast.LENGTH_LONG).show();
					 ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner);
				    	spinner.setVisibility(View.GONE);
				    	
				    	
				    	
				    	Button button = (Button)findViewById(R.id.button1);
				    	button.setClickable(true);
					 return;
				 } else {
					 EditText KeyID = (EditText)findViewById(R.id.editText1);
					 EditText vCode = (EditText)findViewById(R.id.editText2);
					 keyIDVal = result.get(0).keyID;
					 vCodeVal = result.get(0).vCode;
					 KeyID.setText("");
					 vCode.setText("");
				 }
				try {
					/*String FILENAME = "characterIDs";
			    	

			    	FileOutputStream fos;
					fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
					fos.write(commaList.getBytes());
			    	fos.close();*/
					
					
			    	
			    	for (int j = 0; j < result.size();j++){
			    		String FILENAME = String.valueOf(result.get(j).characterID);
			    		FileOutputStream fos =  openFileOutput(FILENAME, Context.MODE_PRIVATE);
			    		characterPortraits.get(result.get(j).characterID).compress(Bitmap.CompressFormat.PNG,90,fos);
			    		fos.close();
			    		
			    		//loop through existing characters
			    		//remove if it matches character in results
			    		// re add with this version.
			    		int startingIndex =  mainLayout.getChildCount() -1;
						for( int m = startingIndex; m  >= 0; m--){
							boolean remove = false;
							for (int k = 0; k < ((LinearLayout)(mainLayout.getChildAt(m))).getChildCount(); k++){
								if (((LinearLayout)(mainLayout.getChildAt(m))).getChildAt(k) instanceof LinearLayout){
									LinearLayout p = (LinearLayout) ((LinearLayout)(mainLayout.getChildAt(m))).getChildAt(k);
									for (int n = 0; n < p.getChildCount(); n++){
										if (p.getChildAt(n) instanceof CharSwitch){
											if (((CharSwitch)p.getChildAt(n)).charID == result.get(j).characterID){
												remove = true;
												mainLayout.getChildAt(m).setVisibility(View.GONE);
											}
										}
									}
								}
							}
							if (remove){
								mainLayout.removeViewAt(m);
							}
						}
						
						APIPoller.MarkCharForHMAddition(new Character(result.get(j).characterID,result.get(j).keyID,result.get(j).vCode,result.get(j).characterName));
						if (!APIPoller.isRunning){
	                		try {
								APIPoller.ExecuteHashMapActions(APISettings.this);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                	}
			    	}
			    	
			    	/*FileInputStream fis;
			    	fis = openFileInput("characterIDs");

			    	InputStreamReader in = new InputStreamReader(fis);
			    	BufferedReader br = new BufferedReader(in);
			    	String data = br.readLine();
			    	//Log.d("debugging",data);
			    	
			    	for (int i=0; i<result.size(); i++) {        
			    		InputStream is = openFileInput(result.get(i).toString());
			    		Bitmap b = BitmapFactory.decodeStream(is);
			    		ImageView img = new ImageView(pContext);
				    	img.setImageBitmap(b); 
				    	LinearLayout mainLayout = (LinearLayout)findViewById(R.id.charLayout);
					    mainLayout.addView(img);
			    		// do whatever you need with b
			    		}*/
			    	
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					ArrayList<Character> characters = new ArrayList<Character>();
					for (int m=0;m<result.size();m++){
						characters.add(new Character(result.get(m).characterID, result.get(m).keyID, result.get(m).vCode,result.get(m).characterName));
					}
					commaList = loadCharacters(characters, pContext, true);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner);
		    	spinner.setVisibility(View.GONE);
		    	
		    	
		    	
		    	Button button = (Button)findViewById(R.id.button1);
		    	button.setClickable(true);
		    	
		    	
		    	/*SharedPreferences settings =  getSharedPreferences("evewatch",0);
		    	SharedPreferences.Editor ed = settings.edit();
		    	ed.putString("charIDs", commaList);
		    	ed.putString("activeCharIDs", commaList);
		    	ed.apply();*/
		    	
		    	//Character.writeAvailableCharacterIDs(characters, pContext)
		    	
		    	//if (!APIPoller.isRunning && APIPoller.monitoringScheduled==false){
		    		//APIPoller.monitoringScheduled=true;
			    	/*PendingIntent pendingIntent;
			    	Intent myIntent = new Intent(MainActivity.context , APIPoller.class);

					  pendingIntent = PendingIntent.getService(MainActivity.context , 0, myIntent, 0);
			
			
			
				       AlarmManager alertManager = (AlarmManager)getSystemService(ALARM_SERVICE);
				       alertManager.cancel(pendingIntent);
				       Calendar calendar = Calendar.getInstance();
				
				       calendar.setTimeInMillis(System.currentTimeMillis());
				
				       alertManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 600000, pendingIntent);*/
		    		Intent intent = new Intent(context, StarterService.class);
		    		context.startService(intent);
		    	//} else {
		    		//APIPoller.monitoringScheduled=true;
			    	/*PendingIntent pendingIntent;
			    	Intent myIntent = new Intent(MainActivity.context , APIPoller.class);

					  pendingIntent = PendingIntent.getService(MainActivity.context , 0, myIntent, 0);
			
			
			
				       AlarmManager alertManager = (AlarmManager)getSystemService(ALARM_SERVICE);
				       alertManager.cancel(pendingIntent);
				       Calendar calendar = Calendar.getInstance();
				
				       calendar.setTimeInMillis(System.currentTimeMillis());
				
				       alertManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 600000, pendingIntent);*/
		    		//Intent intent = new Intent(context, StarterService.class);
		    		//context.startService(intent);
		    	//}
		    	Toast.makeText(APISettings.this, "Characters loaded and event detection has started.", Toast.LENGTH_LONG).show();
				super.onPostExecute(result);
			}
	}
	
	private ArrayList commaStringToArrayList(String commaList){
		ArrayList charIDs = new ArrayList();
		
		String[] temp = commaList.split(",");
		if (commaList.indexOf(",") > -1){
			for (int i = 0; i < temp.length; i ++){
				charIDs.add(Long.parseLong(temp[i]));
			}
		}
		return charIDs;
	}
	
	private String loadCharacters(ArrayList<Character> result, Context pContext, boolean activateAll) throws FileNotFoundException{
		int count=0;
		String commaList ="";
		for(int i = 0;i <= result.size()-1; i++){
			LinearLayout mainLayout = (LinearLayout)findViewById(R.id.charLayout);
			//mainLayout.setLayoutTransition(new LayoutTransition());
			
			LinearLayout ll = new LinearLayout(pContext);
			ll.setOrientation(LinearLayout.VERTICAL);
		    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
		    
		    final float scale = this.getResources().getDisplayMetrics().density;
			int pixels = (int) (64 * scale + 0.5f);
		    ImageView img = new ImageView(pContext);
		    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pixels, pixels);
		    img.setLayoutParams(layoutParams);
		    
	    		InputStream is = openFileInput(String.valueOf(result.get(i).characterID));
	    		Bitmap b = BitmapFactory.decodeStream(is);
		    	img.setImageBitmap(b); 

	    		
 
	    	LinearLayout ll1 = new LinearLayout(pContext);
	    	ll1.setOrientation(LinearLayout.HORIZONTAL);
		    ll1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
		    TextView txt = new TextView(pContext);
		    txt.setText("Receive Alerts: ");
		    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT );
		    lp1.setMargins(5, 0, 5, 0);
		    txt.setTextColor(Color.rgb(248, 248, 255));
		    
		    TextView name = new TextView(pContext);
		    name.setText(result.get(i).characterName);
		    name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
		    //LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT );
		    //lp1.setMargins(5, 0, 5, 0);
		    name.setTextColor(Color.rgb(248, 248, 255));
		    
		    SharedPreferences settings =  getSharedPreferences("evewatch",0);
		    CharSwitch sw = new CharSwitch((Long)result.get(i).characterID,pContext);
		    sw.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
		    ArrayList<Character> activeChars = Character.readActiveCharacterIDs(pContext);
		    boolean match = false;
		    for (int j=0; j < activeChars.size(); j++){
		    	if (activeChars.get(j).characterID == result.get(i).characterID){
		    		match = true;
		    	}
		    }
		    if (activateAll || match){
		    	sw.setChecked(true);
		    } else {
		    	sw.setChecked(false);
		    }
		    
		    sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					                    @Override
					                    public void onCheckedChanged(CompoundButton v, boolean isChecked){
					                    	SharedPreferences settings =  getSharedPreferences("evewatch",0);
					                    	SharedPreferences.Editor ed = settings.edit();
					                    	
					                    	//String activeCharIDs = settings.getString("activeCharIDs", "");
					                    	ArrayList<Character> activeCharacters = Character.readActiveCharacterIDs(APISettings.this);
					                    	boolean match = false;
					                    	for (int j=0; j < activeCharacters.size(); j++){
					            		    	if (activeCharacters.get(j).characterID == ((CharSwitch) v).charID){
					            		    		match = true;
					            		    	}
					            		    }
					                    	
					                        if (isChecked){
					                        	if (!match){
					                        		try {
														APIPoller.AddCharAsActive(((CharSwitch) v).charID, APISettings.this);
													} catch (IOException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
					                        	}
					                        } 
					                        
					                        if (!isChecked){
					                        	if (match){
					                        		APIPoller.RemoveCharIDBaseLoaded(((CharSwitch) v).charID);
					                        		try {
														APIPoller.RemoveCharFromActive(((CharSwitch) v).charID, APISettings.this);
													} catch (IOException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
					                        	}
					                        }
					                        //d.putString("activeCharIDs", activeCharIDs);
					                    	//ed.apply();
					                     }
					                    });
		    
		    CharDelete cd = new CharDelete(result.get(i).characterID,result.get(i).keyID,result.get(i).vCode,result.get(i).characterName,pContext,ll);
		    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT );
		    lp2.setMargins(5, 0, 5, 0);
		    cd.setText("Remove Character");
		    cd.setTextColor(Color.rgb(248, 248, 255));
		    cd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                	APIPoller.MarkCharForHashMapRemoval(new Character(((CharDelete)v).charID,((CharDelete)v).keyID,((CharDelete)v).vCode,((CharDelete)v).name));
                	if (!APIPoller.isRunning){
                		try {
							APIPoller.ExecuteHashMapActions(APISettings.this);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                	
                	
                	((CharDelete)v).parentLayout.setVisibility(View.GONE);
                 }
                });
		    
		    LinearLayout.LayoutParams main = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		    main.setMargins(0, 25, 0, 25);
		    
		    
		    ll1.addView(txt);
		    ll1.addView(sw);
		    ll1.setGravity(Gravity.CENTER_HORIZONTAL);
		    ll.addView(ll1);
		    ll.addView(img);
		    if (!name.getText().equals("")){
		    	ll.addView(name);
		    }
		    
		    //ll.addView(txt,lp1);
		    //ll.addView(sw,lp1);
		    ll.addView(cd,lp2);
		    
		    ll.setGravity(Gravity.CENTER_HORIZONTAL);
		    mainLayout.addView(ll,main);
		   
			count++;
			
			commaList = commaList + result.get(i).characterID + ",";
		}
		return commaList;
	}
	
	public void BootUpBehaviorChanged(boolean isChecked){
		
		SharedPreferences settings =  getSharedPreferences("evewatch",0);
    	SharedPreferences.Editor ed = settings.edit();
    	
    	
    	if (isChecked){
    		ed.putBoolean("StartOnBootUp", true);
    	} else{
    		ed.putBoolean("StartOnBootUp", false);
    	}
    	
     	ed.apply();
	}
	
	public void NetworkBehaviorChanged(boolean isChecked){
		
		SharedPreferences settings =  getSharedPreferences("evewatch",0);
    	SharedPreferences.Editor ed = settings.edit();
    	
    	
    	if (isChecked){
    		ed.putBoolean("AllowCellularData", true);
    	} else{
    		ed.putBoolean("AllowCellularData", false);
    	}
    	
     	ed.apply();
	}
	
	
	
	public void linkToEveSupport(View v){
		Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://community.eveonline.com/support/api-key/ActivateInstallLinks?activate=true"));
		startActivity(browserIntent);
	}

}
