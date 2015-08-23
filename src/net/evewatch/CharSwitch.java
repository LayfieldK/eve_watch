package net.evewatch;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Switch;

public class CharSwitch extends Switch{
 
	public long charID;
	public CharSwitch(long charID, Context context) {
		super(context);
		this.charID = charID;
		//setChecked(true);
		// TODO Auto-generated constructor stub
	}

}
