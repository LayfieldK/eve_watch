package net.evewatch; 
/*package net.evewatch;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlertRow extends LinearLayout {

	Context context;
	String alertID;
	
	public AlertRow(Context context, String alertID){
		super(context);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
	    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
	    
	    ImageView img = new ImageView(this);
	    //int imageResource = R.drawable.ic_launcher;
	    //Drawable image = getResources().getDrawable(imageResource);
	    img.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    //img.setImageDrawable(image);
	    
	    InputStream is;
		try {
			is = openFileInput(String.valueOf(alerts.get(i).charID));
		
		Bitmap b = BitmapFactory.decodeStream(is);
    	img.setImageBitmap(b); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    TextView txt = new TextView(this);
	    txt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ));
	    txt.setText(alerts.get(i).title);
	    if (alerts.get(i).message != ""){
	    	txt.setText(txt.getText() + alerts.get(i).message);
	    }
	    
	    ll.addView(img);
	    ll.addView(txt);
	    
	    LinearLayout mainLayout = (LinearLayout)findViewById(R.id.main_layout);
	    mainLayout.addView(ll);
	}
}*/
