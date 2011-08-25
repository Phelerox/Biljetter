/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import java.util.*;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.text.method.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
public class Wizard extends Activity implements OnClickListener
{
	SharedPreferences sharedPreferences = Biljetter.getSharedPreferences();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wizard);
		
		((Button)findViewById(R.id.btnClose)).setOnClickListener(this);
	}

	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btnClose:
				finish();
				break;
		}
	}
}
