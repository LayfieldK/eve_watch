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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class About extends Activity {

	    
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		
        
        

        setContentView(R.layout.about_layout);
        
		
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
			intent.putExtra("activity", "about");
	    	startActivity(intent);
	    	
			return true;
		case R.id.action_menu:
			intent = new Intent(this, MainActivity.class);
	    	startActivity(intent);
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
		default:
			return true;
		}
		//return super.onOptionsItemSelected(item);
	}

}
