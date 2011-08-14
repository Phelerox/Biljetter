package se.rebootit.android.tagbiljetter;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */

public class Settings extends Activity implements OnClickListener
{
	Ticket ticket;
	SharedPreferences sharedPreferences = Biljetter.getSharedPreferences();
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		((Button)findViewById(R.id.btnSave)).setOnClickListener(this);
		((Button)findViewById(R.id.btnClearCache)).setOnClickListener(this);

		((CheckBox)findViewById(R.id.chkShowResplus)).setChecked(sharedPreferences.getBoolean("pref_show_RESPLUS", true));
		((CheckBox)findViewById(R.id.chkShowSJ)).setChecked(sharedPreferences.getBoolean("pref_show_SJ", true));
		((CheckBox)findViewById(R.id.chkShowSkanetrafiken)).setChecked(sharedPreferences.getBoolean("pref_show_SKANETRAFIKEN", true));
		((CheckBox)findViewById(R.id.chkShowSL)).setChecked(sharedPreferences.getBoolean("pref_show_SL", true));
		((CheckBox)findViewById(R.id.chkShowVarmlandstrafiken)).setChecked(sharedPreferences.getBoolean("pref_show_VARMLANDSTRAFIKEN", true));
		((CheckBox)findViewById(R.id.chkShowVasttrafik)).setChecked(sharedPreferences.getBoolean("pref_show_VASTTRAFIK", true));
    }

	public void onClick(View v)
	{
		Editor e = sharedPreferences.edit();
		Intent intent = getIntent();
		
		switch(v.getId())
		{
			case R.id.btnSave:
				e.putBoolean("pref_show_RESPLUS", ((CheckBox)findViewById(R.id.chkShowResplus)).isChecked());
				e.putBoolean("pref_show_SJ", ((CheckBox)findViewById(R.id.chkShowSJ)).isChecked());
				e.putBoolean("pref_show_SKANETRAFIKEN", ((CheckBox)findViewById(R.id.chkShowSkanetrafiken)).isChecked());
				e.putBoolean("pref_show_SL", ((CheckBox)findViewById(R.id.chkShowSL)).isChecked());
				e.putBoolean("pref_show_VARMLANDSTRAFIKEN", ((CheckBox)findViewById(R.id.chkShowVarmlandstrafiken)).isChecked());
				e.putBoolean("pref_show_VASTTRAFIK", ((CheckBox)findViewById(R.id.chkShowVasttrafik)).isChecked());
				
				intent.putExtra("reload", true);
				setResult(RESULT_OK, intent);
				
				finish();
				
				break;
				
			case R.id.btnClearCache:
				e.putLong("lastmessage", 0);
				intent.putExtra("clearcache", true);
				
				setResult(RESULT_OK, intent);
				
				finish();
				
				break;
		}
		e.commit();
	}
}
