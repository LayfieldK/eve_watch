package net.evewatch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import net.evewatch.R;

import android.R.color;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewAlerts extends Activity {
	
	public static Context context;
	private static DataBaseHelper dbHelper = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    StarterService.VersionUpgrade(this);
	    setContentView(R.layout.new_alerts_layout);
	    
	    context = this;
		SharedPreferences settings =  getSharedPreferences("evewatch",0);
		//String charIDString = settings.getString("charIDs","");
		
		Intent tintent = getIntent();
		String activity = tintent.getStringExtra("activity");
		
		if (dbHelper == null){
			dbHelper = new DataBaseHelper(this);
			try {
				 
				dbHelper.createDataBase();
	 
			} catch (IOException ioe) {
	 
				throw new Error("Unable to create database");
	 
			}
	 
		}
		AppRater.app_launched(this);
		if (Character.readAvailableCharacterIDs(this).size() > 0){
			//if(APIPoller.isRunning == false && APIPoller.monitoringScheduled==false){
				//APIPoller.monitoringScheduled=true;
				
				
				
				
				//Toast.makeText(MainActivity.this, "In", Toast.LENGTH_LONG).show();
				
				
				
				

				/*Intent myIntent = new Intent(context, APIPoller.class);

			  pendingIntent = PendingIntent.getService(context, 0, myIntent, 0);
				//PendingIntent pi = PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);


		       AlarmManager alertManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		       Calendar calendar = Calendar.getInstance();
		
		       calendar.setTimeInMillis(System.currentTimeMillis());
		
		       alertManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 600000, pendingIntent);*/
				Intent intent = new Intent(context, StarterService.class);
	    		context.startService(intent);

			//}
		} else {
			Intent intent = new Intent(this, APISettings.class);
	    	startActivity(intent);

			
		}
	    
	    HashMap<Long,Bitmap> portraits = new HashMap<Long,Bitmap>();
	    ArrayList<Character> characters = Character.readAvailableCharacterIDs(this);
	    for (int i = 0; i < characters.size();i++){
	    	InputStream is;
			try {
				is = openFileInput(String.valueOf(characters.get(i).characterID));
			
	    		Bitmap b = BitmapFactory.decodeStream(is);
	    		portraits.put(characters.get(i).characterID, b);
	    		is.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				
			}
	    }
	    
	    ArrayList<Alert> alerts = new ArrayList<Alert>();
	    ArrayList<Alert> archivedAlerts = new ArrayList<Alert>();
	    int newAlertCount=0;
	    try {
		    
			
			alerts = Alert.getAlertsArrayList(this);
			archivedAlerts = Alert.getArchivedAlertsArrayList(this);
			newAlertCount = alerts.size();
			archivedAlerts.addAll(0,alerts);
			alerts = archivedAlerts;
			Alert.writeArchivedAlertsToFile(alerts, this);
			Alert.writeAlertsToFile(new ArrayList<Alert>(), this);
	    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    if (alerts.size() > 0){
	    	LinearLayout noAlerts = (LinearLayout)findViewById(R.id.noalerts);
	    	noAlerts.setVisibility(View.GONE);
	    }
	    
		int count=0;
		for(int i = 0;i < alerts.size(); i++){
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
		    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
		    ll.setWeightSum(6.0f);
		    boolean newAlert = false;
		    if (i < newAlertCount){
		    	//61D7A4
		    	ll.setBackgroundColor(Color.rgb(167, 255, 138));
		    	newAlert=true;
		    }
		    
		    ImageView img = new ImageView(this);
		    final float scale = this.getResources().getDisplayMetrics().density;
			int pixels = (int) (64 * scale + 0.5f);
		    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pixels, pixels);
		    img.setLayoutParams(layoutParams);
		    
		    //int imageResource = R.drawable.ic_launcher;
		    //Drawable image = getResources().getDrawable(imageResource);
		    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT,.1f );
		    llp.setMargins(2,0,2,0);
		    LinearLayout.LayoutParams llp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT,5.9f );
		    llp1.setMargins(2,0,2,0);
		    //img.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT,2.0f ));
		    //img.setImageDrawable(image);
		   
		    
	    	img.setImageBitmap(portraits.get(alerts.get(i).charID)); 
			
			LinearLayout ll1 = new LinearLayout(this);
			ll1.setOrientation(LinearLayout.VERTICAL);
			
		    //ll1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ,3.0f));
		    
		    TextView txt = new TextView(this);
		    txt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
		    txt.setText(alerts.get(i).title);
		    txt.setTypeface(null, Typeface.BOLD);
		    if (!newAlert){
		    	txt.setTextColor(Color.rgb(248, 248, 255));
		    }
		    
		    TextView det = new TextView(this);
		    det.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    	det.setVisibility(View.VISIBLE);
	    	det.setText("Detected at: " + alerts.get(i).detectTime.toString());
	    	if (!newAlert){
		    	det.setTextColor(Color.rgb(248, 248, 255));
		    }
		    
		    TextView cat = new TextView(this);
		    cat.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
		    cat.setVisibility(View.GONE);
		    if (!newAlert){
		    	cat.setTextColor(Color.rgb(248, 248, 255));
		    }
		    if (alerts.get(i).category != ""){
		    	cat.setVisibility(View.VISIBLE);
		    	cat.setText(alerts.get(i).category);
		    	cat.setTypeface(null, Typeface.ITALIC);
		    }
		    
		    TextView expand = null;
		    if (alerts.get(i).alertID.equals("messageReceived")){
		    	ll1.setLayoutTransition(new LayoutTransition());
		    	expand = new TextView(this);
		    	expand.setId(1);
		    	expand.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
		    	expand.setVisibility(View.GONE);
		    	if (!newAlert){
			    	expand.setTextColor(Color.rgb(248, 248, 255));
			    }
			    if (alerts.get(i).message != ""){
			    	expand.setVisibility(View.VISIBLE);
			    	expand.setText("Tap to see message.");
			    }
		    }
		    if (expand == null){
		    	try {
		    		Integer.parseInt(alerts.get(i).alertID);
		    		ll1.setLayoutTransition(new LayoutTransition());
		    		expand = new TextView(this);
			    	expand.setId(1);
			    	expand.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
			    	expand.setVisibility(View.GONE);
			    	if (!newAlert){
				    	expand.setTextColor(Color.rgb(248, 248, 255));
				    }
				    if (alerts.get(i).message != ""){
				    	expand.setVisibility(View.VISIBLE);
				    	expand.setText("Tap to see semi-raw data.");
				    }
		    	} catch (Exception e){
		    		//do nothing
		    	}
		    }
		    
		    TextView msg = new TextView(this);
		    msg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
		    msg.setVisibility(View.GONE);
		    msg.setId(2);
		    if (!newAlert){
		    	msg.setTextColor(Color.rgb(248, 248, 255));
		    }
		    if (alerts.get(i).message != ""){
		    	if (expand == null){
		    		msg.setVisibility(View.VISIBLE);
		    	}
		    	msg.setText(Html.fromHtml(alerts.get(i).message).toString());
		    }
		    
		    ll.addView(img,llp);
		    ll1.addView(txt);
		    ll1.addView(det);
		    ll1.addView(cat);
		    if (expand != null){
		    	ll1.addView(expand);
		    } 
		    ll1.addView(msg);
		    ll.addView(ll1,llp1);
		    
		     
		    
		    if (expand != null){
			    ll.setOnClickListener(new View.OnClickListener() {
	
		            @Override
		            public void onClick(View v) {
		            	View m = v.findViewById(2);
		            	View e = v.findViewById(1);
		            	if (e.getVisibility() == View.VISIBLE ){
		            		
			            	e.setVisibility(View.GONE );
			            	m.setVisibility(View.VISIBLE );
			            	
		            	} else if (e.getVisibility() == View.GONE){
		            		
			            	e.setVisibility(View.VISIBLE );
			            	m.setVisibility(View.GONE );
			            	
		            	}
		             }
		            });
		    }
		    
		    LinearLayout.LayoutParams llp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT ,LinearLayout.LayoutParams.WRAP_CONTENT );
		    llp2.setMargins(2,10,2,10);
		    LinearLayout mainLayout = (LinearLayout)findViewById(R.id.alertsList);
		    mainLayout.addView(ll,llp2);
			count++;
		}
		//Toast.makeText(this, ""+count, Toast.LENGTH_LONG).show();
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		
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
			intent = new Intent(this, MainActivity.class);
			intent.putExtra("activity", "alerts");
	    	startActivity(intent);
			return true;
		case R.id.action_settings:
			intent = new Intent(this, APISettings.class);
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
		case R.id.action_menu:
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void clearArchivedAlerts(View view) throws IOException {
		
		Alert.writeAlertsToFile(new ArrayList<Alert>(), this);
		Alert.writeArchivedAlertsToFile(new ArrayList<Alert>(), this);
		LinearLayout mainLayout = (LinearLayout)findViewById(R.id.alertsList);
		mainLayout.setVisibility(View.GONE);
		LinearLayout text = (LinearLayout)findViewById(R.id.noalerts);
		text.setVisibility(View.VISIBLE);
		
	}

}
