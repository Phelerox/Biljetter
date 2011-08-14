package se.rebootit.android.tagbiljetter;

import java.util.*;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
/**
 * Donate is the class that shows the Flattr button for this project
 * 
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
public class Donate extends Activity implements OnClickListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donate);
		
		((Button)findViewById(R.id.btnFlattr)).setOnClickListener(this);
	}
	
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btnFlattr:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://flattr.com/thing/371293"));
				startActivity(browserIntent);
				break;
		}
	}
}
