package net.evewatch;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;

public class CharDelete extends Button{
	public long charID;
	public long keyID;
	public String vCode;
	public String name;
	public LinearLayout parentLayout;
	public CharDelete(long charID, long keyID, String vCode, String name, Context context, LinearLayout parentLayout) {
		super(context);
		this.charID = charID;
		this.keyID = keyID;
		this.vCode= vCode;
		this.name = name;
		this.parentLayout = parentLayout;
		//setChecked(true);
		// TODO Auto-generated constructor stub
	}
}
