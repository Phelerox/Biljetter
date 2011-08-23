/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
 
public class Settings extends Activity implements OnClickListener
{
	SharedPreferences sharedPreferences = Biljetter.getSharedPreferences();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		((Button)findViewById(R.id.btnSave)).setOnClickListener(this);
		
		((CheckBox)findViewById(R.id.chkSilence)).setChecked(sharedPreferences.getBoolean("silencesms", false));
		((CheckBox)findViewById(R.id.chkNotification)).setChecked(sharedPreferences.getBoolean("shownotification", true));
	}
	
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnSave:
				Editor e = sharedPreferences.edit();
				e.putBoolean("silencesms", ((CheckBox)findViewById(R.id.chkSilence)).isChecked());
				e.putBoolean("shownotification", ((CheckBox)findViewById(R.id.chkNotification)).isChecked());
				e.commit();

				finish();
				break;
		}
	}
}
