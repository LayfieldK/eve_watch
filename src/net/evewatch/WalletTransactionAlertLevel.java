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
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WalletTransactionAlertLevel extends Activity {

	   
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
        //long groupID = intent.getLongExtra("groupID",-1);
       // String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        

        setContentView(R.layout.wallet_transaction_layout);
        
		String table ="";
		String columns[] = {"_id","description"};
		
		table = "wallettransactiontypes";
		
		
		settings = getSharedPreferences("evewatch",0);
		Cursor c = db.query(table,columns,null,null,null,null,"description asc");
		if (c != null ) {
		    if  (c.moveToFirst()) {
		    	TextView title = (TextView)findViewById(R.id.textView1);
		            //name = c.getString(c.getColumnIndex("name"));
		            //locationID = c.getInt(c.getColumnIndex("location_id"));
		    	
		    		title.setText("Wallet Transaction Settings");

		    	do {
				
			        addButton(c.getString(c.getColumnIndex("_id")),c.getString(c.getColumnIndex("description")));
			    
		    	} while (c.moveToNext());
		    }
		}
		c.close();
		
		EditText field = (EditText)findViewById(R.id.editText1);
		field.setText(settings.getString("minIsk","0.0"));
    }

	
	private void addButton(String _id, String description){
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
	    
        
        
        LinearLayout ll1 = new LinearLayout(this);
		ll1.setOrientation(LinearLayout.VERTICAL );
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		layoutParams.setMargins(0, 20, 0, 0);
	    
	    
	    TextView txt = new TextView(this);
	    txt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    txt.setText(description);
	    txt.setTextColor(Color.rgb(248, 248, 255));
	    
	    
	    AlertSwitch alertSwitch = new AlertSwitch(_id,"alert",this);
	    LinearLayout.LayoutParams llp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT );
	    llp1.setMargins(0,0,0,0);
	    //alertSwitch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    if (settings.getString("walletTransactionTypeIDs", "").contains(" " + _id + ",")){
	    	alertSwitch.setChecked(true);
	    } else {
	    	alertSwitch.setChecked(false);

	    }
	    
	    alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	@Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked){
            	SharedPreferences settings =  getSharedPreferences("evewatch",0);
            	SharedPreferences.Editor ed = settings.edit();
            	
            	String walletTransactionTypeIDs = settings.getString("walletTransactionTypeIDs", "");
            	
                if (isChecked){
                	if (!walletTransactionTypeIDs.contains(" " + String.valueOf(((AlertSwitch) v).alertID))){
                		walletTransactionTypeIDs += " " + String.valueOf(((AlertSwitch) v).alertID) + ",";

                	}
                } else if (!isChecked){
                	if (walletTransactionTypeIDs.contains(" " + String.valueOf(((AlertSwitch) v).alertID))){
                		String rep = " " + String.valueOf(((AlertSwitch) v).alertID) + ",";
                		walletTransactionTypeIDs = walletTransactionTypeIDs.replace(rep, "");

                	}
                }
                ed.putString("walletTransactionTypeIDs", walletTransactionTypeIDs);
            	ed.apply();
             }
            });
	    
	    ll1.setGravity(Gravity.CENTER_HORIZONTAL);
	    ll1.addView(txt);
	    ll1.addView(alertSwitch,llp1);
	    
	    LinearLayout mainLayout = (LinearLayout)findViewById(R.id.transaction_type_layout);
	    mainLayout.addView(ll1, layoutParams);
	    mainLayout.addView(ll);
	}
	
	public void setMinIskThreshold(View v){
		EditText field = (EditText)findViewById(R.id.editText1);
		String settingVal;
		double realVal;
		try{
			realVal = Double.parseDouble(field.getText().toString());
		} catch (Exception e){
			realVal = -1;
			Toast.makeText(WalletTransactionAlertLevel.this, "Invalid isk amount. Enter a decimal value with no commas.", Toast.LENGTH_LONG).show();
			field.setText(settings.getString("minIsk", "0.0"));
		}
		if (realVal > -1){
			settingVal = String.valueOf(realVal);
			SharedPreferences settings =  getSharedPreferences("evewatch",0);
        	SharedPreferences.Editor ed = settings.edit();
			ed.putString("minIsk", settingVal);
         	ed.apply();
         	Toast.makeText(WalletTransactionAlertLevel.this, "You will now only be alerted to transactions if they exceed an amount of " + settingVal + " isk.", Toast.LENGTH_LONG).show();
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
			intent = new Intent(this, AlertLevel.class);
			long val = 1;
			intent.putExtra("groupID", val);
			try {
	    	startActivity(intent);
			} catch (Exception e){
				//Log.d("debugging","back isn't working");
			}
			//NavUtils.navigateUpTo(this, intent);
			return true;
		case R.id.action_settings:
			intent = new Intent(this, APISettings.class);
	    	startActivity(intent);
			return true;
		case R.id.action_alerts:
			intent = new Intent(this, NewAlerts.class);
	    	startActivity(intent);
			return true;
		case R.id.action_about:
			intent = new Intent(this, About.class);
			startActivity(intent);
			return true;
		case R.id.action_menu:
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			return true;
		default:
			return true;
		}
		//return super.onOptionsItemSelected(item);
	}

}
