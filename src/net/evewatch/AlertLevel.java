package net.evewatch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import net.evewatch.MainActivity;
import net.evewatch.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlertLevel extends Activity {

	    
	private static DataBaseHelper dbHelper = null;
	private static SQLiteDatabase db =null;
	SharedPreferences settings;
	public Context context;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        
        if (dbHelper == null){
			dbHelper = new DataBaseHelper(this);
			
			try {
	 
				db = dbHelper.openDataBase();
				//db = dbHelper.getReadableDatabase();
				
			}catch(SQLException sqle){
	 
				throw sqle;
	  
			}
		}
        
        Intent intent = getIntent();
        long groupID = intent.getLongExtra("groupID",-1);
       // String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        

        setContentView(R.layout.alert_level_layout);
        
		String table ="";
		String columns[] = {"_id","description"};
		String select="";
		if (groupID < 10){
			 table = "notifications";
			 
			 select = "group_id=" + groupID;
		} else {
			table = "wallettransactiontypes";
			
		}
		
		settings = getSharedPreferences("evewatch",0);
		Cursor c = db.query(table,columns,select,null,null,null,null);
		if (c != null ) {
		    if  (c.moveToFirst()) {
		    	TextView title = (TextView)findViewById(R.id.textView1);
		            //name = c.getString(c.getColumnIndex("name"));
		            //locationID = c.getInt(c.getColumnIndex("location_id"));
		    	switch((int)groupID){
		    	case 1:
		    		title.setText("Basic Character/Wallet/Misc");
		    		addButton("skillComplete", "Skill Training Complete");
	            	addButton("skillQueueEmpty", "Skill Queue Empty");
	            	addButton("roomInSkillQueue", "Room in Skill Queue");
	            	addButton("orderBought", "Market Order Purchased");
	            	addButton("orderSold", "Market Order Sold");
	            	addButton("orderExpired", "Market Order Expired");
	            	addButton("jobDelivered", "Industrial Job Delivered");
	            	addButton("jobWorkComplete", "Industrial Job Work Completed");
	            	//addButton("researchComplete", "Research Complete");
	            	addButton("walletEntry", "Wallet Transaction");
	            	addWalletTransactionTypesMenu("Wallet Transaction Settings");
	            	addButton("messageReceived", "Message Received");
	            	addButton("upcomingEvent", "Upcoming Calendar Event");
	            	addButton("cloneOutOfDate", "Clone Out of Date");
	            	addButton("APICallProblems", "Problems Contacting API Servers");
		    		break;
	    		case 2:
	    			title.setText("Corporation/Alliance");
		    		break;
	    		case 3:
	    			title.setText("War");
	    			break;
	    		case 4:
	    			title.setText("Bills/Insurance/Clones");
	    			break;
	    		case 5:
	    			title.setText("Bounties/Kill Rights");
	    			break;
	    		case 6:
	    			title.setText("Recruitment");
	    			break;
	    		case 7:
	    			title.setText("Sovereignty and Structures");
	    			break;
	    		case 8:
	    			title.setText("Faction Warfare");
	    			break;
	    		case 9:
	    			title.setText("Agents");
	    			break;
	    		case 10:
	    			title.setText("Wallet Transactions");

		    		
		    	}
	            if (groupID == 1){
	            	
	            	
	            }
		    	do {
				
			        addButton(c.getString(c.getColumnIndex("_id")),c.getString(c.getColumnIndex("description")));
			    
		    	} while (c.moveToNext());
		    }
		}
		c.close();
    }
	
	private void addWalletTransactionTypesMenu(String buttonText){
		LinearLayout mainLayout = (LinearLayout)findViewById(R.id.alerts_layout);
		Button b = new Button(this); 
		final float scale = this.getResources().getDisplayMetrics().density;
		int pixels = (int) (45 * scale + 0.5f);
		b.setText(buttonText);
		b.setTextColor(Color.rgb(248, 248, 255));
		b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT ,pixels ));
		b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(context, WalletTransactionAlertLevel.class);
            	intent.putExtra("groupID", 10);
            	startActivity(intent);
             }
            });
	    mainLayout.addView(b);
	}
	
	private void addButton(String _id, String description){
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
	    
        
        
        LinearLayout ll1 = new LinearLayout(this);
		ll1.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		layoutParams.setMargins(0, 30, 0, 0);
	    
	    
	    TextView txt = new TextView(this);
	    txt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    txt.setText(description);
	    txt.setTextColor(Color.rgb(248, 248, 255));
	    
	    LinearLayout ll2 = new LinearLayout(this);
		ll2.setOrientation(LinearLayout.HORIZONTAL);
		ll2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
        
        
	    
	    
	    ll2.setWeightSum(3.0f);
	    ll2.setGravity(Gravity.CENTER_HORIZONTAL);
	    
	    LinearLayout left = new LinearLayout(this);
	    left.setOrientation(LinearLayout.VERTICAL);
	    left.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f ));
	    left.setGravity(Gravity.CENTER_HORIZONTAL);
	    
	    LinearLayout middle = new LinearLayout(this);
	    middle.setOrientation(LinearLayout.VERTICAL);
	    middle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f ));
	    middle.setGravity(Gravity.CENTER_HORIZONTAL);
	    
	    LinearLayout right = new LinearLayout(this);
	    right.setOrientation(LinearLayout.VERTICAL);
	    right.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f ));
	    right.setGravity(Gravity.CENTER_HORIZONTAL);
	    
	    TextView alertText = new TextView(this);
	    alertText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    alertText.setText("Alert");
	    alertText.setTextColor(Color.rgb(248, 248, 255));
	    
	    TextView vibrateText = new TextView(this);
	    vibrateText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    vibrateText.setText("Vibrate");
	    vibrateText.setTextColor(Color.rgb(248, 248, 255));
	    
	    TextView soundText = new TextView(this);
	    soundText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    soundText.setText("Sound");
	    soundText.setTextColor(Color.rgb(248, 248, 255));
	    
	    
	    AlertSwitch vibrateSwitch = new AlertSwitch(_id,"vibrate",this);
	    vibrateSwitch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    if (settings.getString("vibrateIDs", "").contains(" " + _id + ",")){
	    	vibrateSwitch.setChecked(true);
	    } else {
	    	vibrateSwitch.setChecked(false);
	    }
	    AlertSwitch soundSwitch = new AlertSwitch(_id,"sound",this);
	    soundSwitch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    if (settings.getString("soundIDs", "").contains(" " + _id + ",")){
	    	soundSwitch.setChecked(true);
	    } else {
	    	soundSwitch.setChecked(false);
	    }
	    
	    AlertSwitch alertSwitch = new AlertSwitch(_id,"alert",this, vibrateSwitch, soundSwitch);
	    alertSwitch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    if (settings.getString("alertIDs", "").contains(" " + _id + ",")){
	    	alertSwitch.setChecked(true);
	    } else {
	    	alertSwitch.setChecked(false);
	    	vibrateSwitch.setVisibility(View.INVISIBLE);
	    	soundSwitch.setVisibility(View.INVISIBLE);
	    }
	    
	    alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	@Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked){
            	SharedPreferences settings =  getSharedPreferences("evewatch",0);
            	SharedPreferences.Editor ed = settings.edit();
            	
            	String alertIDs = settings.getString("alertIDs", "");
            	
                if (isChecked){
                	if (!alertIDs.contains(" " + String.valueOf(((AlertSwitch) v).alertID) + ",")){
                		alertIDs += " " + String.valueOf(((AlertSwitch) v).alertID) + ",";
                		((AlertSwitch) v).vib.setVisibility(View.VISIBLE);
                		((AlertSwitch) v).sound.setVisibility(View.VISIBLE);
                	}
                } else if (!isChecked){
                	if (alertIDs.contains(" " + String.valueOf(((AlertSwitch) v).alertID) + ",")){
                		String rep = " " + String.valueOf(((AlertSwitch) v).alertID) + ",";
                		alertIDs = alertIDs.replace(rep, "");
                		((AlertSwitch) v).vib.setVisibility(View.INVISIBLE);
                		((AlertSwitch) v).sound.setVisibility(View.INVISIBLE);
                	}
                }
                ed.putString("alertIDs", alertIDs);
            	ed.apply();
             }
            });
	    
	    vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	@Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked){
            	SharedPreferences settings =  getSharedPreferences("evewatch",0);
            	SharedPreferences.Editor ed = settings.edit();
            	
            	String vibrateIDs = settings.getString("vibrateIDs", "");
            	
                if (isChecked){
                	if (!vibrateIDs.contains(" " + String.valueOf(((AlertSwitch) v).alertID) + ",")){
                		vibrateIDs += " " + String.valueOf(((AlertSwitch) v).alertID) + ",";
                	}
                } else if (!isChecked){
                	if (vibrateIDs.contains(" " + String.valueOf(((AlertSwitch) v).alertID) + ",")){
                		String rep = " " + String.valueOf(((AlertSwitch) v).alertID) + ",";
                		vibrateIDs = vibrateIDs.replace(rep, "");
                	}
                }
                ed.putString("vibrateIDs", vibrateIDs);
            	ed.apply();
             }
            });
	    
	    soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	@Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked){
            	SharedPreferences settings =  getSharedPreferences("evewatch",0);
            	SharedPreferences.Editor ed = settings.edit();
            	
            	String soundIDs = settings.getString("soundIDs", "");
            	
                if (isChecked){
                	if (!soundIDs.contains(" " + String.valueOf(((AlertSwitch) v).alertID) + ",")){
                		soundIDs += " " + String.valueOf(((AlertSwitch) v).alertID) + ",";
                	}
                } else if (!isChecked){
                	if (soundIDs.contains(" " + String.valueOf(((AlertSwitch) v).alertID) + ",")){
                		String rep = " " + String.valueOf(((AlertSwitch) v).alertID) + ",";
                		soundIDs = soundIDs.replace(rep, "");
                	}
                }
                ed.putString("soundIDs", soundIDs);
            	ed.apply();
             }
            });
	    
	    
	    
	    ll1.addView(txt);
	    
	    left.addView(alertText);
	    left.addView(alertSwitch);
	    
	    middle.addView(vibrateText);
	    middle.addView(vibrateSwitch);
	    
	    right.addView(soundText);
	    right.addView(soundSwitch);
	    
	    ll2.addView(left);
	    ll2.addView(middle);
	    ll2.addView(right);
	    
	    //ll.addView(ll1);
	    ll.addView(ll2);
	    
	    LinearLayout mainLayout = (LinearLayout)findViewById(R.id.alerts_layout);
	    mainLayout.addView(ll1, layoutParams);
	    mainLayout.addView(ll);
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
			intent.putExtra("activity", "alertLevel");
	    	startActivity(intent);
	    	break;
		case R.id.action_menu:
			intent = new Intent(this, MainActivity.class);
	    	startActivity(intent);
			break;
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
		}
		return super.onOptionsItemSelected(item);
	}

}
