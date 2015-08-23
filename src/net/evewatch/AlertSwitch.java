package net.evewatch;
 
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Switch;

public class AlertSwitch extends Switch{

	public String alertID;
	public String alertMode;
	public AlertSwitch vib;
	public AlertSwitch sound;
	public AlertSwitch(String alertID, String alertMode, Context context) {
		super(context);
		this.alertID = alertID;
		this.alertMode = alertMode;
		this.setText("");
		// TODO Auto-generated constructor stub
	}
	
	public AlertSwitch(String alertID, String alertMode, Context context, AlertSwitch vibrateSwitch, AlertSwitch soundSwitch) {
		super(context);
		this.alertID = alertID;
		this.alertMode = alertMode;
		this.vib= vibrateSwitch;
		this.sound = soundSwitch;
		this.setText("");
		// TODO Auto-generated constructor stub
	}

}
