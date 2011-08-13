package se.rebootit.android.tagbiljetter;

import android.app.*;
import android.content.*;

public class Biljetter extends Application
{
	static Context context;
	
	@Override
	public void onCreate() {
		this.context = this;
	}
	
	public static Context getContext() {
		return context;
	}
	
	public static SharedPreferences getSharedPreferences() {
		return context.getSharedPreferences("Biljetter", Context.MODE_WORLD_READABLE);
	}
}
